package id.coba.kotlinpintar

import android.app.DatePickerDialog
import android.content.*
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import id.coba.kotlinpintar.CustomizedListView.*
import kotlinx.android.synthetic.main.activity_list_keluar_recycleview.*
import java.util.*
import android.net.ConnectivityManager
import android.os.Handler
import android.util.Log
import android.view.Menu
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.coba.kotlinpintar.Adapter.KeluarAdapterKt
import id.coba.kotlinpintar.Dto.KeluarListResponse
import id.coba.kotlinpintar.Dto.KotorListResponse
import id.coba.kotlinpintar.InputDbHelper.*
import id.coba.kotlinpintar.Rest.ApiClient
import id.coba.kotlinpintar.Rest.ApiClient.BASE_URL
import id.coba.kotlinpintar.Rest.ApiInterface
import kotlinx.android.synthetic.main.activity_list_keluar_recycleview.country_search
import kotlinx.android.synthetic.main.activity_list_keluar_recycleview.filter
import kotlinx.android.synthetic.main.activity_list_keluar_recycleview.layout_empty
import kotlinx.android.synthetic.main.activity_list_keluar_recycleview.progressBar
import kotlinx.android.synthetic.main.activity_list_keluar_recycleview.swipeRefresh
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback


class ListKeluarActivityRecycle : AppCompatActivity() , SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private  val KEY_ID = "id"
    private val KEY_TITLE = "title"
    private val KEY_ARTIST = "artist"
    private val KEY_DURATION = "duration"
    private val KEY_THUMB_URL = "thumb_url"
    private val KEY_STATUS= "status_sync"

    private lateinit var list: RecyclerView
    private var mapEpc: HashMap<String, String>? = null
    private var listEpc: ArrayList<HashMap<String, String>>? = null
    private var epcSet: MutableSet<String>? = null
    private lateinit var mHelper: InputDbHelper
    private val songsList = ArrayList<HashMap<String, String>>()
    private lateinit var btnAdd: Button
    private lateinit var searchView: SearchView
    private lateinit var tanggal: EditText
    private val myCalendar = Calendar.getInstance()
    private lateinit var toolbar: Toolbar
    private var NAME_SYNCED_WITH_SERVER : Int = 1
    private var NAME_NOT_SYNCED_WITH_SERVER  : Int = 0
    val DATA_SAVED_BROADCAST = "id.coba.datasaved"
    private lateinit var broadcastReceiver : BroadcastReceiver
    private lateinit var layoutManager: LinearLayoutManager
    private var page = 1
    private var totalPage: Int = 1
    private var isLoading = false
    private lateinit var keluaradapter: KeluarAdapterKt
    var apiInterface: ApiInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_keluar_recycleview)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        mHelper = InputDbHelper(this)

        btnAdd = findViewById<Button>(R.id.btnAdd)

        list = findViewById<RecyclerView>(R.id.rvKeluar)
        searchView = findViewById<SearchView>(R.id.country_search)

        btnAdd.setOnClickListener(this)
        apiInterface = ApiClient.getClient().create(ApiInterface::class.java)
        layoutManager = LinearLayoutManager(this)
        swipeRefresh.setOnRefreshListener(this)
        setupRecyclerView()

        getUsers(false)
        list.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount = layoutManager.childCount
                val pastVisibleItem = layoutManager.findFirstVisibleItemPosition()
                val total  = keluaradapter.itemCount
                if (!isLoading && page < totalPage){
                    if (visibleItemCount + pastVisibleItem>= total){
                        page++
                        getUsers(false)
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        //syncDataRequest()

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
        title?.text = "LINEN KELUAR"
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
            Log.d("PAGE", "$page")

            if (txtSearch !== ""){
                parameters["search"] = txtSearch
                keluaradapter.clear()
            }

            apiInterface?.getLinenKeluar(parameters)?.enqueue(object : Callback<KeluarListResponse> {
                override fun onFailure(call: Call<KeluarListResponse>, t: Throwable) {
                    Toast.makeText(this@ListKeluarActivityRecycle, "${t.message}", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    isLoading = false
                    swipeRefresh.isRefreshing = false
                }

                override fun onResponse(
                    call: Call<KeluarListResponse>?,
                    response: retrofit2.Response<KeluarListResponse>?
                ) {
                    totalPage = response?.body()?.totalPages!!
                    Log.d("PAGE", "totalPage: $totalPage")
                    val listResponse = response.body()?.data
                    if (listResponse != null){
                        Log.d("PAGE", "listResponse != null")
                        if(listResponse.isEmpty()){
                            filter.visibility = View.GONE
                            layout_empty.visibility = View.VISIBLE
                            rvKeluar.visibility = View.GONE
                        }else{
                            layout_empty.visibility = View.GONE
                            filter.visibility = View.VISIBLE
                            rvKeluar.visibility = View.VISIBLE
                            keluaradapter.addList(listResponse)
                        }

                        if (txtSearch !== ""){
                            filter.visibility = View.VISIBLE
                        }
                    }
                    if (page == totalPage){
                        progressBar.visibility = View.GONE
                    }else{
                        progressBar.visibility = View.INVISIBLE
                    }
                    isLoading = false
                    swipeRefresh.isRefreshing = false
                }
            })
        }, 1000)
    }

    private fun setupRecyclerView() {
        rvKeluar.setHasFixedSize(true)
        rvKeluar.layoutManager = layoutManager
        keluaradapter = KeluarAdapterKt()
        rvKeluar.adapter = keluaradapter

        keluaradapter.onItemClick = { contact ->
            val intentBiasa = Intent(this, KeluarActivity::class.java)
            val b = Bundle()
            b.putString("no_transaksi", contact.NO_TRANSAKSI)
            intentBiasa.putExtras(b)
            startActivity(intentBiasa)
            finish()
        }
    }
    override fun onRefresh() {
        keluaradapter.clear()
        page = 1
        getUsers(true)
    }

    override fun onStart() {
        super.onStart()

        /*registerReceiver(broadCastReceiver,IntentFilter(DATA_SAVED_BROADCAST))
        registerReceiver(SyncKeluarState(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        syncData()*/
    }
    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            //updateUI()
        }
    }

    override fun onResume() {
        if (searchView != null) {
//            searchView.setQuery("", false);
            searchView.clearFocus();
            //searchView.onActionViewCollapsed();
        }
        super.onResume()
    }

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

    private fun showTanggal() {
        DatePickerDialog(
            this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateUI() {
        /*
        epcSet = HashSet<String>()
        listEpc = ArrayList<HashMap<String, String>>()

        val myList = ArrayList<String>()
        val db = mHelper.getReadableDatabase()

        val whereClause = InputContract.TaskEntry.STATUS + "=?"
        val whereArgs = arrayOf("0")

        val cursor_header = db.query(
            TABLE_KELUAR,
            arrayOf(
                InputContract.TaskEntry._ID,
                InputContract.TaskEntry.NO_TRANSAKSI,
                InputContract.TaskEntry.CURRENT_INSERT,
                InputContract.TaskEntry.PIC,
                NO_REFERENSI,
                InputContract.TaskEntry.SYNC,
                NAMA_RUANGAN
            ),
            null,
            null,
            null, null, InputContract.TaskEntry.CURRENT_INSERT + " DESC"
        )
        val ii = 0
        songsList.clear()
        while (cursor_header.moveToNext()) {
            val id_id = cursor_header.getColumnIndex(InputContract.TaskEntry._ID)
            val id_no_transaksi = cursor_header.getColumnIndex(InputContract.TaskEntry.NO_TRANSAKSI)
            val id_tanggal = cursor_header.getColumnIndex(InputContract.TaskEntry.CURRENT_INSERT)
            val id_pic = cursor_header.getColumnIndex(InputContract.TaskEntry.PIC)
            val id_status = cursor_header.getColumnIndex(InputContract.TaskEntry.SYNC)
            val id_ruangan = cursor_header.getColumnIndex(NAMA_RUANGAN)
            val id_referensi = cursor_header.getColumnIndex(NO_REFERENSI)

            val no_transaksi = cursor_header.getString(id_no_transaksi)
            val id_transaksi = cursor_header.getString(id_id)
            val tanggal = cursor_header.getString(id_tanggal)
            val pic = cursor_header.getString(id_pic)
            val status = cursor_header.getString(id_status)
            val ruangan = cursor_header.getString(id_ruangan)
            val no_ref = cursor_header.getString(id_referensi)

            val map = HashMap<String, String>()

            map[KEY_ID] = no_transaksi
            map[KEY_TITLE] = no_transaksi
            map[KEY_ARTIST] = pic
            map[KEY_DURATION] = tanggal
            map[KEY_STATUS] = status
            map["ruangan"] = ruangan

            songsList.add(map)

        }

        list = findViewById(R.id.list) as ListView

        adapter = KeluarAdapter(this, songsList)
        list.setAdapter(adapter)

        cursor_header.close()
        db.close()*/
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnAdd -> {
                val intentBiasa = Intent(this, KeluarActivity::class.java)
                startActivity(intentBiasa)
            }
            R.id.tanggal -> {
                showTanggal()
            }
            R.id.button_eksport -> {
                val alertDialog = MaterialAlertDialogBuilder(this)
                    .setTitle("Sedang dalam pengembangan..!!")
                    .setIcon(R.mipmap.ic_launcher)
                    .setNeutralButton("Tutup", null)
                    .show()
            }
            R.id.btn_sync -> {

            }
        }
    }

    fun syncDataRequest(){
        val stringReq = StringRequest(Request.Method.GET, BASE_URL +"request_linen",
            Response.Listener<String> { response ->
                var jsonObject: JSONObject = JSONObject(response)
                var status_kirim : String = jsonObject.getString("status")
                var cursor_del : Cursor = mHelper.deleteRequest()
                cursor_del.moveToFirst()
                cursor_del.close()
                if (status_kirim.equals("true")){
                    val db = mHelper.getWritableDatabase()
                    var jsonArray: JSONArray = jsonObject.getJSONArray("data")
                    var jsonArray_detail: JSONArray = jsonObject.getJSONArray("data_detail")
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        var no_request : String = item.getString(NO_REQUEST)
                        var tgl : String = item.getString(TGL_REQUEST)
                        var nama_ruangan : String = item.getString("ruangan")
                        var requestor : String = item.getString(REQUESTOR)
                        var total_request : String = item.getString(TOTAL_REQUEST)
                        var status_request : String = item.getString(STATUS_REQUEST)
                        var current_insert : String = item.getString("created_date")


                        var cursor : Cursor =  mHelper.getRequest(no_request)
                        if (!cursor.moveToFirst()) {
                            val values_header  = ContentValues()

                            values_header.put(NO_REQUEST,   no_request)
                            values_header.put(TGL_REQUEST,   tgl)
                            values_header.put(REQUESTOR,   requestor)
                            values_header.put(NAMA_RUANGAN,   nama_ruangan)
                            values_header.put(TOTAL_REQUEST,   total_request)
                            values_header.put(STATUS_REQUEST,   status_request)
                            values_header.put(InputContract.TaskEntry.CURRENT_INSERT,   current_insert)

                            db.insertWithOnConflict(TABLE_REQUEST, null, values_header, SQLiteDatabase.CONFLICT_FAIL)

                        }


                    }

                    for (i in 0 until jsonArray_detail.length()) {
                        val item_detail = jsonArray_detail.getJSONObject(i)
                        var no_request : String = item_detail.getString("no_request")
                        var jenis : String = item_detail.getString("jenis")
                        var qty : String = item_detail.getString("qty")
                        var current_insert : String = item_detail.getString("CURRENT_INSERT")

                        val db = mHelper.getWritableDatabase()
                        val values  = ContentValues()

                        var cursor_del : Cursor = mHelper.deleteDetailEpcRequest(no_request,jenis)
                        cursor_del.moveToFirst()
                        cursor_del.close()

                        var cursor : Cursor =  mHelper.getDetailEpcRequest(no_request,jenis)
                        if (!cursor.moveToFirst()) {
                            values.put(NO_REQUEST,   no_request)
                            values.put(JENIS,   jenis)
                            values.put(QTY,   qty)
                            values.put(READY,   item_detail.getString("qty_keluar"))
                            values.put(FLAG_AMBIL,   item_detail.getString("flag_ambil"))
                            values.put(InputContract.TaskEntry.CURRENT_INSERT,   current_insert)

                            db.insertWithOnConflict(TABLE_REQUEST_DETAIL, null, values, SQLiteDatabase.CONFLICT_FAIL)
                        }

                    }

                    Toast.makeText(this,"Sync Tabel Request Linen sukses", Toast.LENGTH_SHORT).show()

                }
            },
            Response.ErrorListener { Toast.makeText(this, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show() })
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringReq)
    }
}


