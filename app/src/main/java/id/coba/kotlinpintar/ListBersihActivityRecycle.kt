package id.coba.kotlinpintar

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
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
import android.os.Handler
import android.util.Log
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import id.coba.kotlinpintar.Adapter.BersihAdapterKt
import id.coba.kotlinpintar.Dto.KotorListResponse
import id.coba.kotlinpintar.InputDbHelper.KATEGORI
import id.coba.kotlinpintar.Rest.ApiClient
import id.coba.kotlinpintar.Rest.ApiInterface
import kotlinx.android.synthetic.main.activity_list_bersih_recycleview.filter
import kotlinx.android.synthetic.main.activity_list_bersih_recycleview.layout_empty
import kotlinx.android.synthetic.main.activity_list_bersih_recycleview.country_search
import kotlinx.android.synthetic.main.activity_list_kotor_recycleview.progressBar
import kotlinx.android.synthetic.main.activity_list_kotor_recycleview.rvKotor
import kotlinx.android.synthetic.main.activity_list_kotor_recycleview.swipeRefresh
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback


class ListBersihActivityRecycle : AppCompatActivity() , SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private  val KEY_ID = "id"
    private val KEY_TITLE = "title"
    private val KEY_ARTIST = "artist"
    private val KEY_DURATION = "duration"
    private val KEY_THUMB_URL = "thumb_url"

    private lateinit var list: RecyclerView
    private lateinit var adapter: BersihAdapterKt
    private var mapEpc: HashMap<String, String>? = null
    private var listEpc: ArrayList<HashMap<String, String>>? = null
    private var epcSet: MutableSet<String>? = null
    private lateinit var mHelper: InputDbHelper
    private val songsList = ArrayList<HashMap<String, String>>()
    private val myCalendar = Calendar.getInstance()
    private lateinit var toolbar: Toolbar
    private lateinit var btnhistory: TextView
    private lateinit var btnAdd: Button
    var apiInterface: ApiInterface? = null
    private var page = 1
    private var totalPage: Int = 1
    private var isLoading = false
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_bersih_recycleview)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        apiInterface = ApiClient.getClient().create(ApiInterface::class.java)

        mHelper = InputDbHelper(this)
        list = findViewById<RecyclerView>(R.id.list)
        btnhistory = findViewById<TextView>(R.id.history)
        btnAdd = findViewById<Button>(R.id.btnAdd)
        btnAdd.setOnClickListener(this)

        val emptyLayout = findViewById<LinearLayout>(R.id.layout_empty)
        /*list.setEmptyView(empty)

        list.setOnItemClickListener { parent, view, position, id ->

            val intentBiasa = Intent(this, BersihActivity::class.java)
            val selectedFromList = parent.getItemAtPosition(position)
            var vi = view
            val title = vi.findViewById(R.id.title) as TextView

            val b = Bundle()
            b.putString("no_transaksi", title.getText().toString())
            b.putString("history", "0")
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
                Log.d("MainActivity", "onScrollChange: ")
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

        btnhistory.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this,ListHistoryBersihActivityRecycle::class.java))
        })
        //syncData(this)
        //updateUI()

        country_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                getUsers(false, newText.toString())
                return false
            }
        })

        supportActionBar?.setCustomView(R.layout.abs_layout)
        supportActionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val title = supportActionBar?.customView?.findViewById<AppCompatTextView>(R.id.tvTitle)
        title?.text = "LINEN BERSIH"

    }
    private fun setupRecyclerView() {
        list.setHasFixedSize(true)
        list.layoutManager = layoutManager
        adapter = BersihAdapterKt()
        list.adapter = adapter

        adapter.onItemClick = { contact ->
            val intentBiasa = Intent(this, BersihActivity::class.java)
            val b = Bundle()
            b.putString("no_transaksi", contact.NO_TRANSAKSI)
            intentBiasa.putExtras(b)
            startActivity(intentBiasa)
            finish()
        }
    }
    override fun onRefresh() {
        adapter.clear()
        page = 1
        getUsers(true)
    }

    private fun getUsers(isOnRefresh: Boolean, txtSearch: String = "") {
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
            parameters["STATUS"] = "CUCI"

            if (txtSearch !== ""){
                parameters["search"] = txtSearch
                adapter.clear()
            }

            apiInterface?.getLinenKotor(parameters)?.enqueue(object : Callback<KotorListResponse> {
                override fun onFailure(call: Call<KotorListResponse>, t: Throwable) {
                    Toast.makeText(this@ListBersihActivityRecycle, "${t.message}", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    isLoading = false
                    swipeRefresh.isRefreshing = false
                }

                override fun onResponse(
                    call: Call<KotorListResponse>?,
                    response: retrofit2.Response<KotorListResponse>?
                ) {
                    totalPage = response?.body()?.totalPages!!
                    Log.d("PAGE", "totalPage: $totalPage")
                    val listResponse = response.body()?.data
                    if (listResponse != null){
                        Log.d("PAGE", "listResponse != null")
                        if(listResponse.isEmpty()){
                            filter.visibility = View.VISIBLE
                            layout_empty.visibility = View.VISIBLE
                            list.visibility = View.GONE
                        }else{
                            layout_empty.visibility = View.GONE
                            filter.visibility = View.VISIBLE
                            list.visibility = View.VISIBLE
                            adapter.addList(listResponse)
                        }
                        if (txtSearch !== ""){
                            filter.visibility = View.VISIBLE
                        }
                    }
                    if (page == totalPage){
                        progressBar.visibility = View.GONE
                    }else{
                        //filter.visibility = View.GONE
                        progressBar.visibility = View.INVISIBLE
                    }
                    isLoading = false
                    swipeRefresh.isRefreshing = false
                }
            })
        }, 1000)}
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        this.finish()
    }

    private var date: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }

    private fun updateUI() {
        epcSet = HashSet<String>()
        listEpc = ArrayList<HashMap<String, String>>()

        val myList = ArrayList<String>()
        val db = mHelper.getReadableDatabase()

        val cursor_header = db.query(
            InputContract.TaskEntry.TABLE,
            arrayOf(
                InputContract.TaskEntry._ID,
                InputContract.TaskEntry.NO_TRANSAKSI,
                InputContract.TaskEntry.CURRENT_INSERT,
                InputContract.TaskEntry.PIC,
                KATEGORI
            ),
            InputContract.TaskEntry.STATUS+"='CUCI'",
            null,
            null, null, null
        )
        val ii = 0
        while (cursor_header.moveToNext()) {
            val id_no_transaksi = cursor_header.getColumnIndex(InputContract.TaskEntry.NO_TRANSAKSI)
            val id_tanggal = cursor_header.getColumnIndex(InputContract.TaskEntry.CURRENT_INSERT)
            val id_pic = cursor_header.getColumnIndex(InputContract.TaskEntry.PIC)
            val id_kategori = cursor_header.getColumnIndex(KATEGORI)

            val no_transaksi = cursor_header.getString(id_no_transaksi)
            val tanggal = cursor_header.getString(id_tanggal)
            val pic = cursor_header.getString(id_pic)
            val kategori = cursor_header.getString(id_kategori)

            val map = HashMap<String, String>()

            map[KEY_ID] = no_transaksi
            map[KEY_TITLE] = no_transaksi
            map[KEY_ARTIST] = pic
            map[KEY_DURATION] = tanggal
            map["kategori"] = kategori
            val imageid = resources.getIdentifier("rfid", "drawable", packageName)
            map[KEY_THUMB_URL] = getResources().getResourceEntryName(imageid)
            songsList.add(map)
        }

        /*list = findViewById(R.id.list) as ListView
        adapter = BersihAdapter(this, songsList)
        list.setAdapter(adapter)
        cursor_header.close()
        db.close()*/
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnAdd -> {
                val intentBiasa = Intent(this, KotorActivity::class.java)
                startActivity(intentBiasa)
            }
        }
    }

    fun syncData(context: Context, startFlag : Boolean = false) {
        val stringReq = StringRequest(
            Request.Method.GET, ApiClient.BASE_URL +"linen_bersih",
            Response.Listener<String> { response ->
                var jsonObject: JSONObject = JSONObject(response)
                var status_kirim : String = jsonObject.getString("status")
                val db = mHelper.getWritableDatabase()
                var cursor_del : Cursor = mHelper.deleteBersih()
                cursor_del.moveToFirst()
                cursor_del.close()
                if (status_kirim.equals("true")){
                    var jsonArray: JSONArray = jsonObject.getJSONArray("data")
                    var jsonArray_detail: JSONArray = jsonObject.getJSONArray("data_detail")

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        var no_transaksi : String = item.getString("NO_TRANSAKSI")
                        var tgl : String = item.getString("TANGGAL")
                        var pic : String = item.getString("PIC")
                        var kategori : String = item.getString("KATEGORI")
                        var qty : String = item.getString("TOTAL_QTY")
                        var berat : String = item.getString("TOTAL_BERAT")
                        var status : String = item.getString("STATUS")
                        var current_insert : String = item.getString("CURRENT_INSERT")


                        var cursor : Cursor =  mHelper.getBersih(no_transaksi)
                        if (!cursor.moveToFirst()) {
                            val values_header  = ContentValues()

                            values_header.put(InputContract.TaskEntry.NO_TRANSAKSI,   no_transaksi)
                            values_header.put(InputContract.TaskEntry.TANGGAL,   tgl)
                            values_header.put(InputContract.TaskEntry.STATUS,   status)
                            values_header.put(InputContract.TaskEntry.SYNC,   1)
                            values_header.put(InputContract.TaskEntry.PIC,   pic)
                            values_header.put(KATEGORI,   kategori)
                            values_header.put(InputDbHelper.TOTAL_QTY,   qty)
                            values_header.put(InputDbHelper.TOTAL_BERAT,   berat)
                            values_header.put(InputContract.TaskEntry.CURRENT_INSERT,   current_insert)

                            db.insertWithOnConflict(InputDbHelper.TABLE_BERSIH, null, values_header, SQLiteDatabase.CONFLICT_FAIL)

                        }


                    }

                    for (i in 0 until jsonArray_detail.length()) {
                        val item_detail = jsonArray_detail.getJSONObject(i)
                        var no_transaksi : String = item_detail.getString("no_transaksi")
                        var epc : String = item_detail.getString("epc")
                        var ruangan : String = item_detail.getString("ruangan")
                        var barang : String = item_detail.getString("item")
                        var status_linen : String = item_detail.getString("status_linen")
                        var keluar : String = item_detail.getString("keluar")
                        var current_insert : String = item_detail.getString("CURRENT_INSERT")


                        val db = mHelper.getWritableDatabase()
                        val values  = ContentValues()

                        var cursor_del : Cursor = mHelper.deleteDetailEpcBersih(no_transaksi,epc)
                        cursor_del.moveToFirst()
                        cursor_del.close()

                        var cursor : Cursor =  mHelper.getDetailEpcBersih(no_transaksi,epc)
                        if (!cursor.moveToFirst()) {
                            values.put(InputContract.TaskEntry.NO_TRANSAKSI,   no_transaksi)
                            values.put(InputContract.TaskEntry.EPC,   epc)
                            values.put(InputContract.TaskEntry.ITEM,   barang)
                            values.put(InputContract.TaskEntry.ROOM,   ruangan)
                            values.put(InputDbHelper.STATUS_LINEN,   status_linen)
                            values.put(InputDbHelper.KELUAR,   keluar)
                            values.put(InputDbHelper.BERAT,   item_detail.getString("berat"))
                            values.put(InputDbHelper.CHECKED,   item_detail.getString("checked"))
                            values.put(InputContract.TaskEntry.CURRENT_INSERT,   current_insert)

                            db.insertWithOnConflict(InputDbHelper.TABLE_BERSIH_DETAIL, null, values, SQLiteDatabase.CONFLICT_FAIL)
                        }

                    }

                    Toast.makeText(context,"Sync Tabel Linen Bersih sukses", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { Toast.makeText(context, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show() })
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringReq)

    }
}