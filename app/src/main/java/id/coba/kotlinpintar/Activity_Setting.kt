package id.coba.kotlinpintar

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import id.coba.kotlinpintar.Adapter.MenuAdapter
import id.coba.kotlinpintar.InputDbHelper.*
import id.coba.kotlinpintar.Model.MenuModel
import id.coba.kotlinpintar.Rest.ApiClient.BASE_URL
import org.json.JSONArray
import org.json.JSONObject
import android.widget.Toast
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Handler
import android.os.Message
import kotlinx.android.synthetic.main.activity_lihat__ruangan.*


class Activity_Setting : AppCompatActivity() {

    private lateinit var btnNavigation: BottomNavigationView
    private lateinit var listViewmenu: ListView
    private lateinit var listMenu: ArrayList<MenuModel>
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var toolbar: Toolbar
    private lateinit var mHelper: InputDbHelper

    private lateinit var progressBar: ProgressBar
    private lateinit var persentase: TextView
    private var Value: Int = 0
    var handler: Handler? = null
    var isStarted = false
    var primaryProgressStatus = 0
    var progressStatus = 0
    private lateinit var pref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_setup)

        pref = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
        if(!isLogin()){
            val intentBiasa1 = Intent(applicationContext, Activity_Login::class.java)
            startActivity(intentBiasa1)
        }

        btnNavigation = findViewById(R.id.bottomNavigationView)
        progressBar = findViewById(R.id.progress)
        persentase = findViewById(R.id.persentase)
        progressBar.setProgress(0) //Set Progress Dimulai Dari O

        handler = Handler(Handler.Callback {
//            if (isStarted) {
//                progressStatus++
//            }
//            progressBar.progress = progressStatus
            persentase.text = "${Value}/${progressBar.max}"
//            handler?.sendEmptyMessageDelayed(0, 100)
            if(Value == 100){
                Toast.makeText(getApplicationContext(), "Progress Completed", Toast.LENGTH_SHORT).show()
                persentase.text = "Sync Tabel COmpleted"
            }
            Value+= 5

            true
        })

        handler?.sendEmptyMessage(0)

        mHelper = InputDbHelper(this)
        var menu : Menu = btnNavigation.menu
        selectedMenu(menu.getItem(3))

        btnNavigation.setOnNavigationItemSelectedListener {
                item: MenuItem ->  selectedMenu(item)

            false
        }

        listViewmenu = findViewById(R.id.listMenuSetting)
        listMenu = ArrayList()

        listMenu.add(MenuModel("Master"))
        listMenu.add(MenuModel("Ruangan", "test", R.drawable.ic_room))
        listMenu.add(MenuModel("Barang", "test", R.drawable.ic_scanner_black_24dp))
        listMenu.add(MenuModel("Base API", "test", R.drawable.ic_settings))
        listMenu.add(MenuModel("Firebase Token", "test", R.drawable.ic_whatshot_black_24dp))
        listMenu.add(MenuModel("Pengaturan"))
        listMenu.add(MenuModel("Sync Tabel", "test", R.drawable.ic_sync_black_24dp))
        listMenu.add(MenuModel("Reset Data Transaksi", "test", R.drawable.ic_delete_black_24dp))
        listMenu.add(MenuModel("Alter Tabel", "test", R.drawable.ic_edit))
        listMenu.add(MenuModel("Logout", "test", R.drawable.ic_exit_to_app_black_24dp))


        menuAdapter = MenuAdapter(listMenu, getApplicationContext())
        listViewmenu.setAdapter(menuAdapter)
        listViewmenu.setOnItemClickListener( AdapterView.OnItemClickListener {
                parent, view, position, id ->
            run {
                var dataModel: MenuModel = listMenu.get(position)
                if (dataModel.title === "Ruangan"){
                    val intentBiasa = Intent(this, Activity_Monitor::class.java)
                    startActivity(intentBiasa)
                }else if (dataModel.title == "Barang"){
                    val intentBiasa = Intent(this, ListRegisterLinen::class.java)
                    startActivity(intentBiasa)
                }else if (dataModel.title == "Defect"){
                    val intentBiasa = Intent(this, ListRegisterLinen::class.java)
                    startActivity(intentBiasa)
                }else if (dataModel.title == "Firebase Token"){
                    val intentBiasa = Intent(this, FirebaseActivity::class.java)
                    startActivity(intentBiasa)
                }else if (dataModel.title == "Base API"){
                    val intentBiasa = Intent(this, SetupActivity::class.java)
                    startActivity(intentBiasa)
                }else if (dataModel.title == "Logout"){
                    pref.edit().remove("isLogin").commit()
                    val intentBiasa = Intent(this, Activity_Login::class.java)
                    startActivity(intentBiasa)
                }else if (dataModel.title == "Reset Data Transaksi"){
                    val db = mHelper.getWritableDatabase()
                    db.delete(InputContract.TaskEntry.TABLE,
                        null,
                        null)

                    db.delete(InputContract.TaskEntry.TABLE_DETAIL,
                        null,
                        null)

                    db.delete(TABLE_BERSIH,
                        null,
                        null)

                    db.delete(TABLE_BERSIH_DETAIL,
                        null,
                        null)

                    db.delete(TABLE_KELUAR,
                        null,
                        null)

                    db.delete(TABLE_KELUAR_DETAIL,
                        null,
                        null)

                    db.delete(TABLE_RUSAK,
                        null,
                        null)

                    db.delete(TABLE_RUSAK_DETAIL,
                        null,
                        null)

                    db.delete(TABLE_REQUEST,
                        null,
                        null)

                    db.delete(TABLE_REQUEST_DETAIL,
                        null,
                        null)
                    db.delete(TABLE_NOTIFIKASI,
                        null,
                        null)
                    Toast.makeText(this,"Reset Tabel Semua Linen Sukses", Toast.LENGTH_SHORT).show()
                    db.close()
                }else if (dataModel.title == "Alter Tabel"){
                    val db = InputDbHelper(this)
                    db.alterTabel()

                    Toast.makeText(this,"Alter Tabel Linen Bersih", Toast.LENGTH_SHORT).show()
                }else if (dataModel.title == "Sync Tabel"){
                    isStarted = true
                    Thread(Runnable {
                        while (primaryProgressStatus < 100) {
                            primaryProgressStatus += 5

                            try {
                                if(primaryProgressStatus == 10){
                                    val db = InputDbHelper(this)
                                    db.createRuangan()
                                    db.createLinen()
                                    db.createJenisBarang()
                                    db.createPIC()
                                    db.createLinenBersih()
                                    db.createKategori()
                                    db.createLinenKeluar()
                                    db.createLinenRusak()
                                    db.createLinenRequest()
                                    db.createDefect()
                                    db.createNotifikasi()
                                    db.createLogin()
                                }else if(primaryProgressStatus == 30) {
                                    syncRuangan()
                                }else if(primaryProgressStatus == 35) {
                                    syncNotifikasi()
                                }else if(primaryProgressStatus == 40) {
                                    syncLinen()
                                }else if(primaryProgressStatus == 50) {
                                    syncJenisBarang()
                                }else if(primaryProgressStatus == 60) {
                                    syncPIC()
                                }else if(primaryProgressStatus == 65) {
                                    syncKategori()
                                }else if(primaryProgressStatus == 70) {
                                    syncDefect()
                                }else if(primaryProgressStatus == 75) {
                                    syncKotor()
                                }else if(primaryProgressStatus == 80) {
                                    syncBersih()
                                }else if(primaryProgressStatus == 85) {
                                    syncKeluar()
                                }else if(primaryProgressStatus == 90) {
                                    syncRequest()
                                }else if(primaryProgressStatus == 95) {
                                    syncRusak()
                                }
                                progressBar.setProgress(primaryProgressStatus) // Memasukan Value pada ProgressBar

                                if (primaryProgressStatus == 100) {
                                    persentase.text = "All tasks completed"
                                    isStarted = false
                                }else{
                                    // Mengirim pesan dari handler, untuk diproses didalam thread
                                    handler?.sendMessage(handler?.obtainMessage())
                                    Thread.sleep(1000)
                                }
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }


                        }
                    }).start()
                }else{

                }

            }

        })

        getSupportActionBar()?.setTitle("Menu Master")

    }

    private fun isLogin(): Boolean {
        return pref.getBoolean("isLogin", false)
    }
    private fun syncKotor(){
        val stringReq = StringRequest(Request.Method.GET, BASE_URL +"linen_kotor_all",
            Response.Listener<String> { response ->
                var jsonObject: JSONObject = JSONObject(response)
                var status_kirim : String = jsonObject.getString("status")
                var jsonArray: JSONArray = jsonObject.getJSONArray("data")
                var jsonArray_detail: JSONArray = jsonObject.getJSONArray("data_detail")
                if (status_kirim.equals("true")){
                    val db = mHelper.getWritableDatabase()
                    var cursor_del : Cursor = mHelper.deleteKotorAll()
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

                        var cursor : Cursor =  mHelper.getKotor(no_transaksi)
                        if (!cursor.moveToFirst()) {
                            val values_header  = ContentValues()

                            values_header.put(InputContract.TaskEntry.NO_TRANSAKSI,   no_transaksi)
                            values_header.put(InputContract.TaskEntry.TANGGAL,   tgl)
                            values_header.put(InputContract.TaskEntry.PIC,   pic)
                            values_header.put(KATEGORI,   kategori)
                            values_header.put(TOTAL_QTY,   qty)
                            values_header.put(TOTAL_BERAT,   berat)
                            values_header.put(InputContract.TaskEntry.STATUS,   status)
                            values_header.put(InputContract.TaskEntry.SYNC,   1)
                            values_header.put(InputContract.TaskEntry.CURRENT_INSERT,   current_insert)

                            db.insertWithOnConflict(InputContract.TaskEntry.TABLE, null, values_header, SQLiteDatabase.CONFLICT_FAIL)

                        }


                    }

                    for (i in 0 until jsonArray_detail.length()) {
                        val item_detail = jsonArray_detail.getJSONObject(i)
                        var no_transaksi : String = item_detail.getString("no_transaksi")
                        var epc : String = item_detail.getString("epc")
                        var ruangan : String = item_detail.getString("ruangan")
                        var barang : String = item_detail.getString("item")
                        var current_insert : String = item_detail.getString("CURRENT_INSERT")


                        val db = mHelper.getWritableDatabase()
                        val values  = ContentValues()

                        var cursor_del : Cursor = mHelper.deleteDetailEpc(no_transaksi,epc)
                        cursor_del.moveToFirst()
                        cursor_del.close()

                        var cursor : Cursor =  mHelper.getDetailEpc(no_transaksi,epc)
                        if (!cursor.moveToFirst()) {
                            values.put(InputContract.TaskEntry.NO_TRANSAKSI,   no_transaksi)
                            values.put(InputContract.TaskEntry.EPC,   epc)
                            values.put(InputContract.TaskEntry.ITEM,   barang)
                            values.put(InputContract.TaskEntry.ROOM,   ruangan)
                            values.put(InputContract.TaskEntry.CURRENT_INSERT,   current_insert)

                            db.insertWithOnConflict(InputContract.TaskEntry.TABLE_DETAIL, null, values, SQLiteDatabase.CONFLICT_FAIL)
                        }

                    }
                    Toast.makeText(this,"Sync Tabel Linen Kotor sukses", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { Toast.makeText(this, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show() })
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringReq)
    }
    private fun syncBersih(){
        val stringReq = StringRequest(Request.Method.GET, BASE_URL +"linen_bersih_all",
            Response.Listener<String> { response ->
                var jsonObject: JSONObject = JSONObject(response)
                var status_kirim : String = jsonObject.getString("status")
                var jsonArray: JSONArray = jsonObject.getJSONArray("data")
                var jsonArray_detail: JSONArray = jsonObject.getJSONArray("data_detail")
                if (status_kirim.equals("true")){
                    val db = mHelper.getWritableDatabase()
                    var cursor_del : Cursor = mHelper.deleteBersihAll()
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

                            values_header.put(InputContract.TaskEntry.NO_TRANSAKSI,   no_transaksi)
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
                            values.put(InputContract.TaskEntry.NO_TRANSAKSI,   no_transaksi)
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
                    Toast.makeText(this,"Sync Tabel Linen Bersih sukses", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { Toast.makeText(this, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show() })
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringReq)
    }
    private fun syncRusak(){
        val stringReq = StringRequest(Request.Method.GET, BASE_URL +"linen_rusak_all",
            Response.Listener<String> { response ->
                var jsonObject: JSONObject = JSONObject(response)
                var status_kirim : String = jsonObject.getString("status")
                var jsonArray: JSONArray = jsonObject.getJSONArray("data")
                var jsonArray_detail: JSONArray = jsonObject.getJSONArray("data_detail")
                if (status_kirim.equals("true")){
                    val db = mHelper.getWritableDatabase()
                    var cursor_del : Cursor = mHelper.deleteRusakAll()
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

                    Toast.makeText(this,"Sync Tabel Linen Rusak sukses", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { Toast.makeText(this, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show() })
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringReq)
    }
    private fun syncKeluar(){
        val stringReq = StringRequest(Request.Method.GET, BASE_URL +"linen_keluar_all",
            Response.Listener<String> { response ->
                var jsonObject: JSONObject = JSONObject(response)
                var status_kirim : String = jsonObject.getString("status")
                var jsonArray: JSONArray = jsonObject.getJSONArray("data")
                var jsonArray_detail: JSONArray = jsonObject.getJSONArray("data_detail")
                if (status_kirim.equals("true")){
                    val db = mHelper.getWritableDatabase()

                    var cursor_del : Cursor = mHelper.deleteKeluarAll()
                    cursor_del.moveToFirst()
                    cursor_del.close()

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        var no_transaksi : String = item.getString("NO_TRANSAKSI")
                        var tgl : String = item.getString("TANGGAL")
                        var nama_ruangan : String = item.getString("RUANGAN")
                        var pic : String = item.getString("PIC")
                        var no_referensi : String = item.getString("NO_REFERENSI")
                        var status : String = item.getString("STATUS")
                        var current_insert : String = item.getString("CURRENT_INSERT")


                        var cursor : Cursor =  mHelper.getKeluar(no_transaksi)
                        if (!cursor.moveToFirst()) {
                            val values_header  = ContentValues()

                            values_header.put(InputContract.TaskEntry.NO_TRANSAKSI,   no_transaksi)
                            values_header.put(InputContract.TaskEntry.TANGGAL,   tgl)
                            values_header.put(InputContract.TaskEntry.PIC,   pic)
                            values_header.put(NAMA_RUANGAN,   nama_ruangan)
                            values_header.put(NO_REFERENSI,   no_referensi)
                            values_header.put(InputContract.TaskEntry.STATUS,   status)
                            values_header.put(InputContract.TaskEntry.SYNC,   1)
                            values_header.put(InputContract.TaskEntry.CURRENT_INSERT,   current_insert)

                            db.insertWithOnConflict(TABLE_KELUAR, null, values_header, SQLiteDatabase.CONFLICT_FAIL)

                        }


                    }

                    for (i in 0 until jsonArray_detail.length()) {
                        val item_detail = jsonArray_detail.getJSONObject(i)
                        var no_transaksi : String = item_detail.getString("no_transaksi")
                        var epc : String = item_detail.getString("epc")
                        var berat : String = item_detail.getString("berat")
                        var barang : String = item_detail.getString("item")
                        var current_insert : String = item_detail.getString("CURRENT_INSERT")

                        val db = mHelper.getWritableDatabase()
                        val values  = ContentValues()

                        var cursor_del : Cursor = mHelper.deleteDetailEpcKeluar(no_transaksi,epc)
                        cursor_del.moveToFirst()
                        cursor_del.close()

                        var cursor : Cursor =  mHelper.getDetailEpcKeluar(no_transaksi,epc)
                        if (!cursor.moveToFirst()) {
                            values.put(InputContract.TaskEntry.NO_TRANSAKSI,   no_transaksi)
                            values.put(InputContract.TaskEntry.EPC,   epc)
                            values.put(InputContract.TaskEntry.ITEM,   barang)
                            values.put(BERAT,   berat)
                            values.put(KOTOR,   item_detail.getString("kotor"))
                            values.put(InputContract.TaskEntry.CURRENT_INSERT,   current_insert)

                            db.insertWithOnConflict(TABLE_KELUAR_DETAIL, null, values, SQLiteDatabase.CONFLICT_FAIL)
                        }

                    }
                    Toast.makeText(this,"Sync Tabel Linen Keluar sukses", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { Toast.makeText(this, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show() })
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringReq)
    }
    private fun syncRequest(){
        val stringReq = StringRequest(Request.Method.GET, BASE_URL +"request_linen_all",
            Response.Listener<String> { response ->
                var jsonObject: JSONObject = JSONObject(response)
                var status_kirim : String = jsonObject.getString("status")
                var jsonArray: JSONArray = jsonObject.getJSONArray("data")
                var jsonArray_detail: JSONArray = jsonObject.getJSONArray("data_detail")
                if (status_kirim.equals("true")){
                    val db = mHelper.getWritableDatabase()

                    var cursor_del : Cursor = mHelper.deleteRequestAll()
                    cursor_del.moveToFirst()
                    cursor_del.close()

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
    private fun syncRuangan(){
        val stringReq = StringRequest(Request.Method.GET, BASE_URL +"room",
            Response.Listener<String> { response ->
                var jsonObject: JSONObject = JSONObject(response)
                var status_kirim : String = jsonObject.getString("status")
                var jsonArray: JSONArray  = jsonObject.getJSONArray("data")
                if (status_kirim.equals("true")){
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        var id_ruangan :Int = item.getInt("id")
                        var nama_ruangan : String = item.getString("ruangan")
                        var jenis_infeksius : String = item.getString("finfeksius")

                        val db = mHelper.getWritableDatabase()
                        val values_header  = ContentValues()

                        values_header.put(ID_RUANGAN,   id_ruangan)
                        values_header.put(NAMA_RUANGAN,   nama_ruangan)
                        values_header.put(JENIS_INFEKSIUS,   jenis_infeksius)

                        db.insertWithOnConflict(TABLE_RUANGAN, null, values_header, SQLiteDatabase.CONFLICT_REPLACE)
                    }
                    Toast.makeText(this,"Sync Tabel Ruangan sukses", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show() })
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringReq)
    }
    private fun syncNotifikasi(){
        val stringReq = StringRequest(Request.Method.GET, BASE_URL +"notifikasi",
            Response.Listener<String> { response ->
                var jsonObject: JSONObject = JSONObject(response)
                var status_kirim : String = jsonObject.getString("status")
                var jsonArray: JSONArray  = jsonObject.getJSONArray("data")
                if (status_kirim.equals("true")){
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        var id_notifikasi :Int = item.getInt("id")
                        var short_msg : String = item.getString("short_msg")
                        var long_msg : String = item.getString("long_msg")
                        var url : String = item.getString("url")
                        var current_insert : String = item.getString("insert_date")

                        var cursor : Cursor =  mHelper.getNotifikasi(id_notifikasi)
                        if (!cursor.moveToFirst()) {
                            val db = mHelper.getWritableDatabase()
                            val values_header  = ContentValues()

                            values_header.put(ID_NOTIFIKASI,   id_notifikasi)
                            values_header.put(SHORT_MSG,   short_msg)
                            values_header.put(LONG_MSG,   long_msg)
                            values_header.put(PAGE_URL,   url)
                            values_header.put(DIBACA,   0)
                            values_header.put(InputContract.TaskEntry.CURRENT_INSERT,   current_insert)

                            db.insertWithOnConflict(TABLE_NOTIFIKASI, null, values_header, SQLiteDatabase.CONFLICT_REPLACE)
                        }

                    }
                    Toast.makeText(this,"Sync Notifikasi sukses", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show() })
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringReq)
    }

    private fun syncLinen(){
        val stringReq = StringRequest(Request.Method.GET, BASE_URL +"barang",
            Response.Listener<String> { response ->
                var jsonObject: JSONObject = JSONObject(response)
                var status_kirim : String = jsonObject.getString("status")
                var jsonArray: JSONArray  = jsonObject.getJSONArray("data")
                if (status_kirim.equals("true")){
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        var serial :String = item.getString("serial")
                        var tanggal :String = item.getString("tanggal_register")
                        var nama_ruangan : String = item.getString("nama_ruangan")
                        var id_jenis : String = item.getString("id_jenis")

                        val db = mHelper.getWritableDatabase()
                        val values_header  = ContentValues()

                        values_header.put(InputDbHelper.SERIAL,   serial)
                        values_header.put(InputDbHelper.NAMA_RUANGAN,   nama_ruangan)
                        values_header.put(InputDbHelper.TANGGAL,   tanggal)
                        values_header.put(InputDbHelper.ID_JENIS,   id_jenis)

                        db.insertWithOnConflict(TABLE_BARANG, null, values_header, SQLiteDatabase.CONFLICT_REPLACE);
                    }
                    Toast.makeText(this,"Sync Tabel Linen Sukses", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show() })
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringReq)
    }

    private fun syncJenisBarang(){
        val stringReq = StringRequest(Request.Method.GET, BASE_URL +"jenis",
            Response.Listener<String> { response ->
                var jsonObject: JSONObject = JSONObject(response)
                var status_kirim : String = jsonObject.getString("status")
                var jsonArray: JSONArray  = jsonObject.getJSONArray("data")
                if (status_kirim.equals("true")){
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        var jenis :String = item.getString("jenis")
                        var berat :String = item.getString("berat")
                        var id_jenis : String = item.getString("id")
                        var harga : String = item.getString("harga")
                        var jenis_medis : String = item.getString("fmedis")
                        var spesifikasi : String = item.getString("spesifikasi")

                        val db = mHelper.getWritableDatabase()
                        val values_header  = ContentValues()

                        values_header.put(InputDbHelper.ID_JENIS,   id_jenis)
                        values_header.put(InputDbHelper.JENIS,   jenis)
                        values_header.put(InputDbHelper.BERAT,   berat)
                        values_header.put(HARGA,   harga)
                        values_header.put(JENIS_MEDIS,   jenis_medis)
                        values_header.put(SPESIFIKASI,   spesifikasi)

                        db.insertWithOnConflict(TABLE_JENIS_BARANG, null, values_header, SQLiteDatabase.CONFLICT_REPLACE)
                    }
                    Toast.makeText(this,"Sync Tabel Jenis Barang Sukses", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show() })
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringReq)
    }

    private fun syncPIC(){
        val stringReq = StringRequest(Request.Method.GET, BASE_URL +"pic",
            Response.Listener<String> { response ->
                var jsonObject: JSONObject = JSONObject(response)
                var status_kirim : String = jsonObject.getString("status")
                var jsonArray: JSONArray  = jsonObject.getJSONArray("data")
                if (status_kirim.equals("true")){
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        var nama_user :String = item.getString("nama_user")
                        var id_user : String = item.getString("id_user")

                        val db = mHelper.getWritableDatabase()
                        val values_header  = ContentValues()

                        values_header.put(ID_USER,   id_user)
                        values_header.put(NAMA_USER,   nama_user)

                        db.insertWithOnConflict(TABLE_PIC, null, values_header, SQLiteDatabase.CONFLICT_REPLACE)
                    }
                    Toast.makeText(this,"Sync Tabel PIC Sukses", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show() })
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringReq)
    }

    private fun syncDefect(){
        val stringReq = StringRequest(Request.Method.GET, BASE_URL +"defect",
            Response.Listener<String> { response ->
                var jsonObject: JSONObject = JSONObject(response)
                var status_kirim : String = jsonObject.getString("status")
                var jsonArray: JSONArray  = jsonObject.getJSONArray("data")
                if (status_kirim.equals("true")){
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        var defect :String = item.getString("defect")
                        var id_defect : String = item.getString("id")

                        val db = mHelper.getWritableDatabase()
                        val values_header  = ContentValues()

                        values_header.put(ID_DEFECT,   id_defect)
                        values_header.put(DEFECT,   defect)

                        db.insertWithOnConflict(TABLE_DEFECT, null, values_header, SQLiteDatabase.CONFLICT_REPLACE)
                    }
                    Toast.makeText(this,"Sync Tabel Defect Sukses", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show() })
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringReq)
    }

    private fun syncKategori(){
        val stringReq = StringRequest(Request.Method.GET, BASE_URL +"kategori",
            Response.Listener<String> { response ->
                var jsonObject: JSONObject = JSONObject(response)
                var status_kirim : String = jsonObject.getString("status")
                var jsonArray: JSONArray  = jsonObject.getJSONArray("data")
                if (status_kirim.equals("true")){
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        var id_kategori : String = item.getString("id")
                        var kategori :String = item.getString("kategori")

                        val db = mHelper.getWritableDatabase()
                        val values_header  = ContentValues()

                        values_header.put(ID_KATEGORI,   id_kategori)
                        values_header.put(KATEGORI,   kategori)

                        db.insertWithOnConflict(TABLE_KATEGORI, null, values_header, SQLiteDatabase.CONFLICT_REPLACE)
                    }
                    Toast.makeText(this,"Sync Tabel Kategori Sukses", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show() })
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringReq)
    }


    private fun selectedMenu(item : MenuItem) {
        item.isChecked = true
        when(item.itemId) {
            R.id.menuHome -> {
                val intentBiasa = Intent(this, MainActivity::class.java)
                startActivity(intentBiasa)
                finish()
            }
            R.id.menuMonitor -> {
                val intentBiasa1 = Intent(this, Activity_Monitor::class.java)
                startActivity(intentBiasa1)
                finish()
            }
            R.id.menuRequest -> {
                val intentBiasa1 = Intent(this, Activity_Request::class.java)
                startActivity(intentBiasa1)
                finish()
            }
            R.id.menuSettings -> {

            }
        }
    }
}