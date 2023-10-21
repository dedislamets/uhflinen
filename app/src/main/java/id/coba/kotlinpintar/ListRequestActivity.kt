package id.coba.kotlinpintar

import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
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
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.widget.Toolbar
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import id.coba.kotlinpintar.InputContract.TaskEntry.CURRENT_INSERT
import id.coba.kotlinpintar.InputDbHelper.*
import id.coba.kotlinpintar.Rest.ApiClient.BASE_URL
import id.coba.kotlinpintar.Util.context
import org.json.JSONArray
import org.json.JSONObject
import javax.microedition.khronos.opengles.GL10


class ListRequestActivity : AppCompatActivity() , View.OnClickListener {

    private  val KEY_ID = "id"
    private val KEY_TITLE = "title"
    private val KEY_ARTIST = "artist"
    private val KEY_DURATION = "duration"
    private val KEY_THUMB_URL = "thumb_url"
    private val KEY_STATUS= "status_sync"

    private lateinit var list: ListView
    private lateinit var listView: ListView
    private lateinit var adapter: RequestAdapter
    private lateinit var adapterDetail: RequestDetailAdapter
    private var mapEpc: HashMap<String, String>? = null
    private var listEpc: ArrayList<HashMap<String, String>>? = null
    private var epcSet: MutableSet<String>? = null
    private lateinit var mHelper: InputDbHelper
    private val songsList = ArrayList<HashMap<String, String>>()
    private val detailList = ArrayList<HashMap<String, String>>()
    private lateinit var btnAdd: Button
    private lateinit var btnSync: Button
    private lateinit var tanggal: EditText
    private val myCalendar = Calendar.getInstance()
    private lateinit var toolbar: Toolbar
    private var NAME_SYNCED_WITH_SERVER : Int = 1
    private var MODE : Int = 0
    private var NAME_NOT_SYNCED_WITH_SERVER  : Int = 0
    val DATA_SAVED_BROADCAST = "id.coba.datasaved"
    private lateinit var broadcastReceiver : BroadcastReceiver
    private lateinit var btnReset: Button
    private lateinit var btnMode: Button
    private lateinit var txtCari: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_request)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        mHelper = InputDbHelper(this)

        btnAdd = findViewById(R.id.btnAdd) as Button
        btnSync = findViewById(R.id.btn_sync) as Button
        btnMode = findViewById(R.id.btnModeDetail) as Button
        list = findViewById(R.id.list) as ListView
        listView = findViewById(R.id.listView_data) as ListView
        btnReset = findViewById(R.id.reset) as Button
        txtCari = findViewById(R.id.txtCari) as EditText

        val empty = findViewById(R.id.layout_empty) as RelativeLayout
        list.setEmptyView(empty)
        listView.visibility = View.GONE

        btnAdd.setOnClickListener(this)
        btnSync.setOnClickListener(this)
        btnReset.setOnClickListener(this)
        btnMode.setOnClickListener(this)
        list.setOnItemClickListener { parent, view, position, id ->

            val intentBiasa = Intent(this, Activity_Request::class.java)
            val selectedFromList = parent.getItemAtPosition(position)
            var vi = view
            val title = vi.findViewById(R.id.title) as TextView

            val b = Bundle()
            b.putString("no_request", title.getText().toString())
            intentBiasa.putExtras(b)
            startActivity(intentBiasa)
            finish()

        }

        txtCari.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateUI(s.toString())
            }
        })

//        updateLabel()
        getSupportActionBar()?.setTitle("REQUEST LINEN")
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)


    }

    override fun onStart() {
        super.onStart()
        syncData(true)
//        registerReceiver(broadCastReceiver,IntentFilter(DATA_SAVED_BROADCAST))
//        registerReceiver(SyncKeluarState(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }
    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            updateUI()
        }
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
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//            updateLabel()

        }

//    private fun updateLabel() {
//        val myFormat = "dd/MM/yyyy" //In which you need put here
//        val sdf = SimpleDateFormat(myFormat, Locale.US)
//        tanggal.setText(sdf.format(myCalendar.time))
//
//        updateUI()
//    }

    private fun showTanggal() {
        DatePickerDialog(
            this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    fun syncData(startFlag : Boolean = false){
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

        if(startFlag){
            updateUI()
        }
    }
    private fun updateUI(text : String = "") {

        epcSet = HashSet<String>()
        listEpc = ArrayList<HashMap<String, String>>()

        val myList = ArrayList<String>()
        val db = mHelper.getReadableDatabase()

        val cursor_header = db.rawQuery(
            "SELECT DISTINCT A.* " +
                    "FROM request_linen A " +
                    "JOIN request_linen_detail B ON  A.no_request=B.no_request " +
                    "WHERE nama_ruangan like '%" + text + "%' " +
                    "OR A.no_request like '%" + text + "%' " +
                    "OR requestor like '%" + text + "%' " +
                    "OR jenis like '%" + text + "%'",
             null)
        val ii = 0
        songsList.clear()
        while (cursor_header.moveToNext()) {
            val id_id = cursor_header.getColumnIndex(InputContract.TaskEntry._ID)
            val id_no_request = cursor_header.getColumnIndex(NO_REQUEST)
            val id_tgl = cursor_header.getColumnIndex(TGL_REQUEST)
            val id_created = cursor_header.getColumnIndex(CURRENT_INSERT)
            val id_pic = cursor_header.getColumnIndex(REQUESTOR)
            val id_status = cursor_header.getColumnIndex(STATUS_REQUEST)
            val id_ruangan = cursor_header.getColumnIndex(NAMA_RUANGAN)
            val id_total_request = cursor_header.getColumnIndex(TOTAL_REQUEST)

            val no_transaksi = cursor_header.getString(id_no_request)
            val id_transaksi = cursor_header.getString(id_id)
            val tanggal = cursor_header.getString(id_created)
            val pic = cursor_header.getString(id_pic)
            val status = cursor_header.getString(id_status)
            val ruangan = cursor_header.getString(id_ruangan)
            val total_req = cursor_header.getString(id_total_request)

            val map = HashMap<String, String>()

            map[KEY_ID] = no_transaksi
            map[KEY_TITLE] = no_transaksi
            map[KEY_ARTIST] = pic
            map[KEY_DURATION] = tanggal
            map[KEY_STATUS] = status
            map["ruangan"] = ruangan

            songsList.add(map)

        }

//        list = findViewById(R.id.list) as ListView
        adapter = RequestAdapter(this, songsList)
        list.setAdapter(adapter)
        if(MODE == 0) {
            list.visibility = View.VISIBLE
        }else{
            list.visibility = View.GONE
        }

        cursor_header.close()

        val cursor_header_detail = db.rawQuery(
            "SELECT A.nama_ruangan,A.no_request,B.jenis,qty,status_request " +
                    "FROM request_linen A " +
                    "JOIN request_linen_detail B ON  A.no_request=B.no_request " +
                    "WHERE nama_ruangan like '%" + text + "%' " +
                    "OR A.no_request like '%" + text + "%' " +
                    "OR requestor like '%" + text + "%' " +
                    "OR jenis like '%" + text + "%' ORDER BY status_request,A.nama_ruangan,A.no_request DESC",
            null)
        detailList.clear()
        while (cursor_header_detail.moveToNext()) {
            val id_no_request = cursor_header_detail.getColumnIndex(NO_REQUEST)
            val id_jenis = cursor_header_detail.getColumnIndex(JENIS)
            val id_status = cursor_header_detail.getColumnIndex(STATUS_REQUEST)
            val id_ruangan = cursor_header_detail.getColumnIndex(NAMA_RUANGAN)
            val id_qty = cursor_header_detail.getColumnIndex(QTY)

            val no_request = cursor_header_detail.getString(id_no_request)
            val jenis = cursor_header_detail.getString(id_jenis)
            val status = cursor_header_detail.getString(id_status)
            val ruangan = cursor_header_detail.getString(id_ruangan)
            val qty = cursor_header_detail.getString(id_qty)

            val map = HashMap<String, String>()

            map[NO_REQUEST] = no_request
            map[JENIS] = jenis
            map[QTY] = qty
            map[KEY_STATUS] = status
            map[NAMA_RUANGAN] = ruangan
            map[STATUS_REQUEST] = status

            detailList.add(map)

        }

        adapterDetail = RequestDetailAdapter(this, detailList)
        listView.setAdapter(adapterDetail)
        if(MODE == 0) {
            listView.visibility = View.GONE
        }else{
            listView.visibility = View.VISIBLE
        }
        cursor_header_detail.close()

        db.close()
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnAdd -> {
                val intentBiasa = Intent(this, Activity_Request::class.java)
                startActivity(intentBiasa)
            }
            R.id.tanggal -> {
                showTanggal()
            }
            R.id.reset -> {
                txtCari.setText("")
            }
            R.id.btnModeDetail -> {
                if(MODE == 1){
                    MODE = 0
                    listView.visibility = View.GONE
                    list.visibility = View.VISIBLE
                    btnMode.text = "MODE LIST"
                }else{
                    MODE = 1
                    list.visibility = View.GONE
                    listView.visibility = View.VISIBLE
                    btnMode.text = "MODE DETAIL"
                }
            }
            R.id.btn_sync -> {
                syncData()
                finish()
                overridePendingTransition(0, 0)
                startActivity(getIntent())
                overridePendingTransition(0, 0)
            }
        }
    }



}


