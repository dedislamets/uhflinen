package id.coba.kotlinpintar

import android.app.Activity
import android.app.DatePickerDialog
import android.content.*
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import id.coba.kotlinpintar.CustomizedListView.*
import kotlinx.android.synthetic.main.activity_list_kotor.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.xml.sax.SAXException
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.app.ComponentActivity.ExtraData
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.ConnectivityManager
import android.os.Handler
import android.util.Log
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import id.coba.kotlinpintar.Adapter.BersihAdapterKt
import id.coba.kotlinpintar.Adapter.HistoryBersihAdapterKt
import id.coba.kotlinpintar.Dto.BersihListResponse
import id.coba.kotlinpintar.InputContract.TaskEntry.NO_TRANSAKSI
import id.coba.kotlinpintar.InputDbHelper.*
import id.coba.kotlinpintar.Rest.ApiClient
import id.coba.kotlinpintar.Rest.ApiInterface
import kotlinx.android.synthetic.main.activity_list_bersih_recycleview.filter
import kotlinx.android.synthetic.main.activity_list_bersih_recycleview.layout_empty
import kotlinx.android.synthetic.main.activity_list_bersih_recycleview.progressBar
import kotlinx.android.synthetic.main.activity_list_bersih_recycleview.swipeRefresh
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback


class ListHistoryBersihActivityRecycle : AppCompatActivity() , SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private lateinit var list: RecyclerView
    private lateinit var adapter: HistoryBersihAdapterKt
    private var mapEpc: HashMap<String, String>? = null
    private var listEpc: ArrayList<HashMap<String, String>>? = null
    private var epcSet: MutableSet<String>? = null
    private lateinit var mHelper: InputDbHelper
    private val histList = ArrayList<HashMap<String, String>>()
    private val myCalendar = Calendar.getInstance()
    private lateinit var btnSync: Button
    private lateinit var toolbar: Toolbar
    var apiInterface: ApiInterface? = null
    private var page = 1
    private var totalPage: Int = 1
    private var isLoading = false
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_history_bersih_recycleview)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        mHelper = InputDbHelper(this)
        list = findViewById<RecyclerView>(R.id.rvBersih)
        apiInterface = ApiClient.getClient().create(ApiInterface::class.java)

        /*list.setOnItemClickListener { parent, view, position, id ->

            val intentBiasa = Intent(this, BersihActivity::class.java)
            val selectedFromList = parent.getItemAtPosition(position)
            var vi = view
            val title = vi.findViewById(R.id.title) as TextView

            val b = Bundle()
            b.putString("no_transaksi", title.getText().toString())
            b.putString("history", "1")
            intentBiasa.putExtras(b)
            startActivity(intentBiasa)
            finish()

        }*/

        layoutManager = LinearLayoutManager(this)
        swipeRefresh.setOnRefreshListener(this)
        setupRecyclerView()
        getUsers(false)
        list.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount = layoutManager.childCount
                val pastVisibleItem = layoutManager.findFirstVisibleItemPosition()
                val total  = adapter.itemCount
                if (!isLoading && page < totalPage){
                    if (visibleItemCount + pastVisibleItem>= total){
                        page++
                        getUsers(false)
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })

        supportActionBar?.setCustomView(R.layout.abs_layout)
        supportActionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val title = supportActionBar?.customView?.findViewById<AppCompatTextView>(R.id.tvTitle)
        title?.text = "HISTORY LINEN BERSIH"

    }

    private fun getUsers(isOnRefresh: Boolean) {
        isLoading = true
        val translateAnimation = TranslateAnimation(0F,50F,0F,0F)
        translateAnimation.duration = 200
        translateAnimation.isFillEnabled = true
        translateAnimation.fillAfter = true
        progressBar.startAnimation(translateAnimation)
        if (!isOnRefresh) progressBar.visibility = View.VISIBLE
        Handler().postDelayed({
            val parameters = HashMap<String, String>()
            parameters["page"] = page.toString()
            apiInterface?.getLinenBersih(parameters)?.enqueue(object : Callback<BersihListResponse> {
                override fun onFailure(call: Call<BersihListResponse>, t: Throwable) {
                    Toast.makeText(this@ListHistoryBersihActivityRecycle, "${t.message}", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    isLoading = false
                    swipeRefresh.isRefreshing = false
                }

                override fun onResponse(
                    call: Call<BersihListResponse>?,
                    response: retrofit2.Response<BersihListResponse>?
                ) {
                    totalPage = response?.body()?.totalPages!!
                    Log.d("PAGE", "totalPage: $totalPage")
                    val listResponse = response.body()?.data
                    if (listResponse != null){
                        if(listResponse.isEmpty()){
                            filter.visibility = View.GONE
                            layout_empty.visibility = View.VISIBLE
                            list.visibility = View.GONE
                        }else{
                            layout_empty.visibility = View.GONE
                            filter.visibility = View.VISIBLE
                            list.visibility = View.VISIBLE
                            adapter.addList(listResponse)
                        }
                    }
                    if (page == totalPage){
                        progressBar.visibility = View.GONE
                    }else{
                        filter.visibility = View.GONE
                        progressBar.visibility = View.INVISIBLE
                    }
                    isLoading = false
                    swipeRefresh.isRefreshing = false
                }
            })
        }, 1000)}
    override fun onRefresh() {
        adapter.clear()
        page = 1
        getUsers(true)
    }

    override fun onStart() {
        super.onStart()
    }
    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        startActivity(Intent(this, ListBersihActivityRecycle::class.java))
        this.finish()
    }

    private var date: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }

    private fun updateUI() {
        /*epcSet = HashSet<String>()
        listEpc = ArrayList<HashMap<String, String>>()

        val myList = ArrayList<String>()
        val db = mHelper.getReadableDatabase()

        val cursor_header = db.query(
            TABLE_BERSIH,
            arrayOf(
                InputContract.TaskEntry._ID,
                InputContract.TaskEntry.NO_TRANSAKSI,
                InputContract.TaskEntry.CURRENT_INSERT,
                InputContract.TaskEntry.PIC,
                InputContract.TaskEntry.SYNC
            ),
            InputContract.TaskEntry.STATUS+"='BERSIH'",
            null,
            null, null, InputContract.TaskEntry.CURRENT_INSERT+" DESC"
        )
        val ii = 0
        histList.clear()
        while (cursor_header.moveToNext()) {
            val id_no_transaksi = cursor_header.getColumnIndex(InputContract.TaskEntry.NO_TRANSAKSI)
            val id_tanggal = cursor_header.getColumnIndex(InputContract.TaskEntry.CURRENT_INSERT)
            val id_pic = cursor_header.getColumnIndex(InputContract.TaskEntry.PIC)
            val id_status = cursor_header.getColumnIndex(InputContract.TaskEntry.SYNC)

            val no_transaksi = cursor_header.getString(id_no_transaksi)
            val tanggal = cursor_header.getString(id_tanggal)
            val pic = cursor_header.getString(id_pic)
            val status_sync = cursor_header.getString(id_status)

            val map = HashMap<String, String>()

            map[KEY_ID] = no_transaksi
            map[KEY_TITLE] = no_transaksi
            map[KEY_ARTIST] = pic
            map[KEY_DURATION] = tanggal
            map[KEY_STATUS] = status_sync

            val whereClause = NO_TRANSAKSI + "=?"
            val whereArgs = arrayOf<String>(no_transaksi)
            val cursor_detail = db.query(
                TABLE_BERSIH_DETAIL,
                null,
                whereClause,
                whereArgs,
                null, null, null
            )
            while (cursor_detail.moveToNext()) {
                val id_status = cursor_detail.getColumnIndex(STATUS_LINEN)
                val status_linen = cursor_detail.getString(id_status)

                if(status_linen.equals("RUSAK")){
                    map["rusak"] = status_linen
                }
                if(status_linen.equals("BARU")){
                    map["baru"] = status_linen
                }
                if(status_linen.equals("OK")){
                    map["ok"] = status_linen
                }
            }
            histList.add(map)

        }

        list = findViewById(R.id.list) as ListView

        adapter = BersihHistAdapter(this, histList)
        list.setAdapter(adapter)

        cursor_header.close()
        db.close()*/
    }

    private fun setupRecyclerView() {
        list.setHasFixedSize(true)
        list.layoutManager = layoutManager
        adapter = HistoryBersihAdapterKt()
        list.adapter = adapter

        adapter.onItemClick = { contact ->
            val intentBiasa = Intent(this, BersihActivity::class.java)
            val b = Bundle()
            b.putString("no_transaksi", contact.NO_TRANSAKSI)
            b.putString("history", "1")
            intentBiasa.putExtras(b)
            startActivity(intentBiasa)
            finish()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {

        }
    }



}