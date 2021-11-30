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
import androidx.appcompat.widget.Toolbar
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.coba.kotlinpintar.InputDbHelper.*
import id.coba.kotlinpintar.Rest.ApiClient.BASE_URL
import id.coba.kotlinpintar.Util.context
import org.json.JSONArray
import org.json.JSONObject


class ListRusakActivity : AppCompatActivity() , View.OnClickListener {

    private  val KEY_ID = "id"
    private val KEY_TITLE = "title"
    private val KEY_ARTIST = "artist"
    private val KEY_DURATION = "duration"
    private val KEY_THUMB_URL = "thumb_url"
    private val KEY_STATUS= "status_sync"

    private lateinit var list: ListView
    private lateinit var adapter: RusakAdapter
    private var mapEpc: HashMap<String, String>? = null
    private var listEpc: ArrayList<HashMap<String, String>>? = null
    private var epcSet: MutableSet<String>? = null
    private lateinit var mHelper: InputDbHelper
    private val songsList = ArrayList<HashMap<String, String>>()
    private lateinit var btnAdd: Button
    private lateinit var btnModeDetail: Button
    private lateinit var tanggal: EditText
    private val myCalendar = Calendar.getInstance()
    private lateinit var toolbar: Toolbar
    private var NAME_SYNCED_WITH_SERVER : Int = 1
    private var NAME_NOT_SYNCED_WITH_SERVER  : Int = 0
    val DATA_SAVED_BROADCAST = "id.coba.datasaved"
    private lateinit var broadcastReceiver : BroadcastReceiver
    private lateinit var btnSync: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_rusak)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        mHelper = InputDbHelper(this)

        btnAdd = findViewById(R.id.btnAdd) as Button
        btnModeDetail = findViewById(R.id.btnModeDetail)
        list = findViewById(R.id.list) as ListView
        btnSync = findViewById(R.id.button_simpan) as Button

        val empty = findViewById(R.id.layout_empty) as RelativeLayout
        list.setEmptyView(empty)

        btnAdd.setOnClickListener(this)
        btnSync.setOnClickListener(this)
        btnModeDetail.setOnClickListener(this)

        list.setOnItemClickListener { parent, view, position, id ->

//            Toast.makeText(this, "Clicked item : $position",Toast.LENGTH_SHORT).show()
            val intentBiasa = Intent(this, RusakActivity::class.java)
            val selectedFromList = parent.getItemAtPosition(position)
            var vi = view
            val title = vi.findViewById(R.id.title) as TextView

            val b = Bundle()
            b.putString("no_transaksi", title.getText().toString())
            intentBiasa.putExtras(b)
            startActivity(intentBiasa)
            finish()

        }
        updateUI()
//        updateLabel()
        getSupportActionBar()?.setTitle("LINEN RUSAK")
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)


    }

    override fun onStart() {
        super.onStart()
        try{
            registerReceiver(broadCastReceiver,IntentFilter(DATA_SAVED_BROADCAST))
            registerReceiver(SyncRusakState(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }catch (e: Throwable){ }
    }
    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            updateUI()
        }
    }

    override fun onStop() {
        super.onStop()

//        unregisterReceiver(SyncRusakState())
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

    private fun updateUI() {
        epcSet = HashSet<String>()
        listEpc = ArrayList<HashMap<String, String>>()

        val myList = ArrayList<String>()
        val db = mHelper.getReadableDatabase()

        val cursor_header = db.query(
            TABLE_RUSAK,
            arrayOf(
                InputContract.TaskEntry._ID,
                InputContract.TaskEntry.NO_TRANSAKSI,
                InputContract.TaskEntry.CURRENT_INSERT,
                InputContract.TaskEntry.PIC,
                InputContract.TaskEntry.SYNC,
                CATATAN,
                DEFECT
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
            val id_catatan = cursor_header.getColumnIndex(CATATAN)
            val id_defect = cursor_header.getColumnIndex(DEFECT)

            val no_transaksi = cursor_header.getString(id_no_transaksi)
            val id_transaksi = cursor_header.getString(id_id)
            val tanggal = cursor_header.getString(id_tanggal)
            val pic = cursor_header.getString(id_pic)
            val status = cursor_header.getString(id_status)
            val catatan = cursor_header.getString(id_catatan)
            val defect = cursor_header.getString(id_defect)

            val map = HashMap<String, String>()

            map[KEY_ID] = no_transaksi
            map[KEY_TITLE] = no_transaksi
            map[KEY_ARTIST] = pic
            map[KEY_DURATION] = tanggal
            map[KEY_STATUS] = status
            map["catatan"] = defect + " - " + catatan

            songsList.add(map)

        }

        list = findViewById(R.id.list) as ListView

        adapter = RusakAdapter(this, songsList)
        list.setAdapter(adapter)

        cursor_header.close()
        db.close()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnAdd -> {
                val intentBiasa = Intent(this, RusakActivity::class.java)
                startActivity(intentBiasa)
            }
            R.id.tanggal -> {
                showTanggal()
            }
            R.id.btnModeDetail -> {
                val alertDialog = MaterialAlertDialogBuilder(this)
                    .setTitle("Sedang dalam pengembangan..!!")
                    .setIcon(R.mipmap.ic_launcher)
                    .setNeutralButton("Tutup", null)
                    .show()
            }
            R.id.button_simpan -> {
                val stringReq = StringRequest(Request.Method.GET, BASE_URL +"linen_rusak",
                    Response.Listener<String> { response ->
                        var jsonObject: JSONObject = JSONObject(response)
                        var status_kirim : String = jsonObject.getString("status")
                        var jsonArray: JSONArray = jsonObject.getJSONArray("data")
                        var jsonArray_detail: JSONArray = jsonObject.getJSONArray("data_detail")
                        if (status_kirim.equals("true")){
                            val db = mHelper.getWritableDatabase()
                            var cursor_del : Cursor = mHelper.deleteRusak()
                            cursor_del.moveToFirst()
                            cursor_del.close()
                            for (i in 0 until jsonArray.length()) {
                                val item = jsonArray.getJSONObject(i)
                                var no_transaksi : String = item.getString("NO_TRANSAKSI")
                                var tgl : String = item.getString("TANGGAL")
                                var pic : String = item.getString("PIC")
                                var catatan : String = item.getString("CATATAN")
                                var defect : String = item.getString("DEFECT")
                                var current_insert : String = item.getString("CURRENT_INSERT")

                                var cursor : Cursor =  mHelper.getRusak(no_transaksi)
                                if (!cursor.moveToFirst()) {
                                    val values_header  = ContentValues()

                                    values_header.put(InputContract.TaskEntry.NO_TRANSAKSI,   no_transaksi)
                                    values_header.put(InputContract.TaskEntry.TANGGAL,   tgl)
                                    values_header.put(InputContract.TaskEntry.PIC,   pic)
                                    values_header.put(CATATAN,   catatan)
                                    values_header.put(DEFECT,   defect)
                                    values_header.put(InputContract.TaskEntry.SYNC,   1)
                                    values_header.put(InputContract.TaskEntry.CURRENT_INSERT,   current_insert)

                                    db.insertWithOnConflict(TABLE_RUSAK, null, values_header, SQLiteDatabase.CONFLICT_FAIL)

                                }


                            }

                            for (i in 0 until jsonArray_detail.length()) {
                                val item_detail = jsonArray_detail.getJSONObject(i)
                                var no_transaksi : String = item_detail.getString("no_transaksi")
                                var epc : String = item_detail.getString("epc")
                                var jml_cuci : String = item_detail.getString("jml_cuci")
                                var berat : String = item_detail.getString("berat")
                                var barang : String = item_detail.getString("item")
                                var current_insert : String = item_detail.getString("CURRENT_INSERT")


                                val db = mHelper.getWritableDatabase()
                                val values  = ContentValues()

                                var cursor_del : Cursor = mHelper.deleteDetailEpcRusak(no_transaksi,epc)
                                cursor_del.moveToFirst()
                                cursor_del.close()

                                var cursor : Cursor =  mHelper.getDetailEpcRusak(no_transaksi,epc)
                                if (!cursor.moveToFirst()) {
                                    values.put(InputContract.TaskEntry.NO_TRANSAKSI,   no_transaksi)
                                    values.put(InputContract.TaskEntry.EPC,   epc)
                                    values.put(InputContract.TaskEntry.ITEM,   barang)
                                    values.put(JML_CUCI,   jml_cuci)
                                    values.put(BERAT,   berat)
                                    values.put(InputContract.TaskEntry.CURRENT_INSERT,   current_insert)


                                    db.insertWithOnConflict(TABLE_RUSAK_DETAIL, null, values, SQLiteDatabase.CONFLICT_FAIL)
                                }

                            }
                            finish()
                            overridePendingTransition(0, 0)
                            startActivity(getIntent())
                            overridePendingTransition(0, 0)
                            Toast.makeText(this,"Sync Tabel Linen Rusak sukses", Toast.LENGTH_SHORT).show()
                        }
                    },
                    Response.ErrorListener { Toast.makeText(this, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show() })
                val requestQueue = Volley.newRequestQueue(this)
                requestQueue.add(stringReq)
            }
        }
    }



}


