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
import androidx.appcompat.widget.Toolbar
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import id.coba.kotlinpintar.InputContract.TaskEntry.NO_TRANSAKSI
import id.coba.kotlinpintar.InputDbHelper.*
import id.coba.kotlinpintar.Rest.ApiClient
import org.json.JSONArray
import org.json.JSONObject


class ListHistoryBersihActivity : AppCompatActivity() , View.OnClickListener {
    // All static variables
    internal val URI = "https://api.androidhive.info/music/music.xml"

    // XML node keys
    private  val KEY_ID = "id"
    private val KEY_TITLE = "title"
    private val KEY_ARTIST = "artist"
    private val KEY_DURATION = "duration"
    private val KEY_THUMB_URL = "thumb_url"
    private val KEY_STATUS= "status_sync"

    private lateinit var list: ListView
    private lateinit var adapter: BersihHistAdapter
    private var mapEpc: HashMap<String, String>? = null
    private var listEpc: ArrayList<HashMap<String, String>>? = null
    private var epcSet: MutableSet<String>? = null
    private lateinit var mHelper: InputDbHelper
    private val histList = ArrayList<HashMap<String, String>>()
    private val myCalendar = Calendar.getInstance()
    private lateinit var btnSync: Button
    private lateinit var toolbar: Toolbar
    val DATA_SAVED_BROADCAST = "id.coba.datasaved"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_history_bersih)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        mHelper = InputDbHelper(this)
        list = findViewById(R.id.list) as ListView
        btnSync = findViewById(R.id.button_sync) as Button
        btnSync.setOnClickListener(this)

        val empty = findViewById(R.id.layout_empty) as RelativeLayout
        list.setEmptyView(empty)

        list.setOnItemClickListener { parent, view, position, id ->

//            Toast.makeText(this, "Clicked item : $position",Toast.LENGTH_SHORT).show()
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

        }

        updateUI()
//        toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)
        getSupportActionBar()?.setTitle("HISTORY")

//        toolbar.title =  "LINEN BERSIH"
//        toolbar.setTitle("LINEN KOTOR")
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)

//        toolbar?.setNavigationOnClickListener {
//            startActivity(
//                Intent(
//                    applicationContext,
//                    MainActivity::class.java
//                )
//            )
//        }

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)

    }

    override fun onStart() {
        super.onStart()

        registerReceiver(broadCastReceiver, IntentFilter(DATA_SAVED_BROADCAST))
        registerReceiver(SyncBersihState(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }
    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            updateUI()
        }
    }

    override fun onPause() {
        unregisterReceiver(broadCastReceiver)
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
//        unregisterReceiver(broadCastReceiver)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {

        startActivity(Intent(this, ListBersihActivity::class.java))
        this.finish()
    }

    private var date: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // TODO Auto-generated method stub
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
        db.close()
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.button_sync -> {
                val stringReq = StringRequest(
                    Request.Method.GET, ApiClient.BASE_URL +"linen_bersih",
                    Response.Listener<String> { response ->
                        var jsonObject: JSONObject = JSONObject(response)
                        var status_kirim : String = jsonObject.getString("status")
                        var jsonArray: JSONArray = jsonObject.getJSONArray("data")
                        var jsonArray_detail: JSONArray = jsonObject.getJSONArray("data_detail")
                        if (status_kirim.equals("true")){
                            val db = mHelper.getWritableDatabase()
                            var cursor_del : Cursor = mHelper.deleteBersih()
                            cursor_del.moveToFirst()
                            cursor_del.close()
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

                                    values_header.put(NO_TRANSAKSI,   no_transaksi)
                                    values_header.put(InputContract.TaskEntry.TANGGAL,   tgl)
                                    values_header.put(InputContract.TaskEntry.STATUS,   status)
                                    values_header.put(InputContract.TaskEntry.SYNC,   1)
                                    values_header.put(InputContract.TaskEntry.PIC,   pic)
                                    values_header.put(KATEGORI,   kategori)
                                    values_header.put(TOTAL_QTY,   qty)
                                    values_header.put(TOTAL_BERAT,   berat)
                                    values_header.put(InputContract.TaskEntry.CURRENT_INSERT,   current_insert)

                                    db.insertWithOnConflict(TABLE_BERSIH, null, values_header, SQLiteDatabase.CONFLICT_FAIL)

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
                                    values.put(NO_TRANSAKSI,   no_transaksi)
                                    values.put(InputContract.TaskEntry.EPC,   epc)
                                    values.put(InputContract.TaskEntry.ITEM,   barang)
                                    values.put(InputContract.TaskEntry.ROOM,   ruangan)
                                    values.put(STATUS_LINEN,   status_linen)
                                    values.put(KELUAR,   keluar)
                                    values.put(BERAT,   item_detail.getString("berat"))
                                    values.put(CHECKED,   item_detail.getString("checked"))
                                    values.put(InputContract.TaskEntry.CURRENT_INSERT,   current_insert)

                                    db.insertWithOnConflict(TABLE_BERSIH_DETAIL, null, values, SQLiteDatabase.CONFLICT_FAIL)
                                }

                            }
                            finish()
                            overridePendingTransition(0, 0)
                            startActivity(getIntent())
                            overridePendingTransition(0, 0)
                            Toast.makeText(this,"Sync Tabel Linen Bersih sukses", Toast.LENGTH_SHORT).show()
                        }
                    },
                    Response.ErrorListener { Toast.makeText(this, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show() })
                val requestQueue = Volley.newRequestQueue(this)
                requestQueue.add(stringReq)
            }
        }
    }



}