package id.coba.kotlinpintar

import android.content.*
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.ConnectivityManager
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import id.coba.kotlinpintar.Rest.ApiClient
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.timerTask
import android.os.Looper
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import android.os.Handler
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.coba.kotlinpintar.InputDbHelper.*
import id.coba.kotlinpintar.Rest.ApiClient.BASE_WEB
import id.coba.kotlinpintar.Util.context
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var btnGoal : RelativeLayout
    private lateinit var btnFinance : RelativeLayout
    private lateinit var btnPromise : RelativeLayout
    private lateinit var btnSetting : RelativeLayout
    private lateinit var btnPengawasan : RelativeLayout
    private lateinit var btnPenilaian : RelativeLayout
    private lateinit var btnSoal: RelativeLayout
    private lateinit var toolbar: Toolbar
    private lateinit var btnNavigation: BottomNavigationView
    private lateinit var fragment : Fragment
    private lateinit var baseUrl : TextView
    private lateinit var baseUrl2 : TextView
    private lateinit var nama_user : TextView
    private lateinit var department : TextView
    private lateinit var mHelper: InputDbHelper
    private lateinit var pref : SharedPreferences
    private lateinit var menu_toolbar : Menu
    private lateinit var layout_penilaian : RelativeLayout
    private lateinit var layout_inspeksi : RelativeLayout
    private lateinit var layout_soal : RelativeLayout
    private lateinit var layout1 : RelativeLayout
    private lateinit var layout2 : RelativeLayout
    private lateinit var layout3 : RelativeLayout
    private var jumlah_badge_notifikasi : String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pref = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
        if(!isLogin()){
            val intentBiasa1 = Intent(applicationContext, Activity_Login::class.java)
            startActivity(intentBiasa1)
        }

        getSupportActionBar()?.setTitle("UHF Reader V1.2")
        btnGoal = findViewById(R.id.LayoutGoal)
        btnFinance = findViewById(R.id.LayoutFinance)
        btnPromise = findViewById(R.id.LayoutPromise)
        btnSetting = findViewById(R.id.LayoutSetting)
        btnPengawasan = findViewById(R.id.LayoutPengawasan)
        btnPenilaian = findViewById(R.id.LayoutPenilaian)
        btnSoal = findViewById(R.id.LayoutSoal)
        baseUrl = findViewById(R.id.textBaseUrl)
        baseUrl2 = findViewById(R.id.textBaseUrl2)
        nama_user = findViewById(R.id.nama_user)
        department = findViewById(R.id.department)
        layout_penilaian = findViewById(R.id.LayoutPenilaian)
        layout_inspeksi = findViewById(R.id.LayoutPengawasan)
        layout_soal = findViewById(R.id.LayoutSoal)
        layout1 = findViewById(R.id.lay1)
        layout2 = findViewById(R.id.lay2)
        layout3 = findViewById(R.id.lay3)

        nama_user.setText(pref.getString(NAMA_USER, "Nama User") )
        department.setText(pref.getString(DEPARTMENT, "Bagian"))

        btnNavigation = findViewById(R.id.bottomNavigationView)
        var menu : Menu = btnNavigation.menu
        btnNavigation.setOnNavigationItemSelectedListener {
                item: MenuItem ->  selectedMenu(item)

            false
        }

        if(department.text == "Pengawas") {
            layout_penilaian.visibility = View.GONE
            layout_inspeksi.visibility = View.VISIBLE
            layout_soal.visibility = View.GONE
            layout1.visibility = View.GONE
            layout2.visibility = View.GONE
            layout3.visibility = View.VISIBLE
            menu.removeItem(R.id.menuRequest)
        }else if(department.text == "Supervisor"){
            layout_penilaian.visibility = View.VISIBLE
            layout_inspeksi.visibility = View.GONE
            layout1.visibility = View.GONE
            layout2.visibility = View.GONE
            layout3.visibility = View.VISIBLE
            layout_soal.visibility = View.VISIBLE
        }else{
            layout1.visibility = View.VISIBLE
            layout2.visibility = View.VISIBLE
            layout3.visibility = View.GONE
            layout_inspeksi.visibility = View.GONE
            layout_penilaian.visibility = View.GONE
            layout_soal.visibility = View.GONE
        }

        btnGoal.setOnClickListener(this)
        btnFinance.setOnClickListener(this)
        btnPromise.setOnClickListener(this)
        btnSetting.setOnClickListener(this)
        btnPengawasan.setOnClickListener(this)
        btnPenilaian.setOnClickListener(this)
        btnSoal.setOnClickListener(this)

        mHelper = InputDbHelper(this)
        syncNotifikasi()
        var cursor : Cursor =  mHelper.getSetting()
        if (cursor.moveToFirst()) {
            val url = cursor.getString(cursor.getColumnIndex(BASE_URL))
            val web = cursor.getString(cursor.getColumnIndex(WEB_URL))
            baseUrl.setText(url.toString())
            baseUrl2.setText(url.toString())
            ApiClient.BASE_URL = url.toString()
            ApiClient.BASE_WEB = web
        }

        val handler = Handler(Looper.getMainLooper())
        var jml_request : Int = 0

        jumlah_badge_notifikasi  =  mHelper.hitungNotif().toString()
        val runnable = object : Runnable {
            override fun run() {

                val networkConnection = NetworkConnection(applicationContext,baseUrl.getText().toString())
                networkConnection.observe(this@MainActivity, Observer { isConnected ->
                    if(isConnected){
                        LayoutStatusDisconnected.visibility = View.GONE
                        LayoutStatusConnected.visibility = View.VISIBLE
                    }else{
                        LayoutStatusDisconnected.visibility = View.VISIBLE
                        LayoutStatusConnected.visibility = View.GONE
                    }
                })
                if (jml_request++ < 20) {
                    handler.postDelayed(this, 10000)
                }
            }
        }
        handler.postDelayed(runnable, 1000)

        registerReceiver(broadCastReceiver,IntentFilter("broadcast_notify"))
    }

//    override fun onStop() {
//        super.onStop()
//        unregisterReceiver(NetworkStateChecker())
//    }
    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
//            Toast.makeText(contxt, "Nama Action: "+intent?.action, Toast.LENGTH_LONG).show()
//            Toast.makeText(contxt, "Pesan: "+ intent?.getStringExtra("myBroadcast"), Toast.LENGTH_LONG).show()
            syncNotifikasi()
            val item: MenuItem = menu_toolbar.findItem(R.id.action_notification)
            val iv: ImageView= item.getActionView().findViewById(R.id.badge_icon_button)
            val count_badge : TextView =  item.getActionView().findViewById(R.id.badge_textView)
            jumlah_badge_notifikasi = (Integer.parseInt(count_badge.text.toString())+1).toString()
            count_badge.text = jumlah_badge_notifikasi
            if(jumlah_badge_notifikasi == "0"){
                count_badge.visibility= View.GONE
            }else{
                count_badge.visibility = View.VISIBLE
            }
            displayAlert()
        }
    }

    private fun displayAlert()
    {
        val builder : AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Anda menerima pemberitahuan baru..")
        val alert: AlertDialog = builder.create()
        alert.show()
    }
    private fun isLogin(): Boolean {
        return pref.getBoolean("isLogin", false)
    }

    private fun selectedMenu(item : MenuItem) {
        item.isChecked = true
        when(item.itemId) {
            R.id.menuHome -> {

            }
            R.id.menuMonitor -> {
                val alertDialog = MaterialAlertDialogBuilder(this)
                    .setTitle("Sedang dalam pengembangan..!!")
                    .setIcon(R.mipmap.ic_launcher)
                    .setNeutralButton("Tutup", null)
                    .show()
            }
            R.id.menuRequest -> {
                val intentBiasa = Intent(this, ListRequestActivity::class.java)
                startActivity(intentBiasa)
            }
            R.id.menuSettings -> {
                val intentBiasa1 = Intent(this, Activity_Setting::class.java)
                startActivity(intentBiasa1)
                finish()
            }
            R.id.action_notification -> {
                val intentBiasa1 = Intent(this, ActivityNotification::class.java)
                startActivity(intentBiasa1)
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu_toolbar =  menu;
        val inflater = menuInflater
        inflater.inflate(R.menu.notification_menu, menu)
        val item: MenuItem = menu.findItem(R.id.action_notification)
        val iv: ImageView= item.getActionView().findViewById(R.id.badge_icon_button)
        val count_badge : TextView =  item.getActionView().findViewById(R.id.badge_textView)
        count_badge.text= jumlah_badge_notifikasi
        if(jumlah_badge_notifikasi == "0"){
            count_badge.visibility= View.GONE
        }else{
            count_badge.visibility = View.VISIBLE
        }

        iv.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intentBiasa1 = Intent(applicationContext, ActivityNotification::class.java)
                startActivity(intentBiasa1)
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_notification -> {
                val intentBiasa1 = Intent(this, ActivityNotification::class.java)
                startActivity(intentBiasa1)
                return true
            }
            R.id.menuRequest -> {
                val intentBiasa = Intent(this, ListRequestActivity::class.java)
                startActivity(intentBiasa)
                return true
            }
            R.id.menuSettings -> {
                val intentBiasa1 = Intent(this, Activity_Setting::class.java)
                startActivity(intentBiasa1)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onClick(v: View) {
        when (v.id){
            R.id.LayoutGoal -> {
                    val intentBiasa = Intent(this, ListKotorActivity::class.java)
                    startActivity(intentBiasa)
                    finish()
            }
            R.id.LayoutFinance -> {
                val intentFinance = Intent(this, ListBersihActivity::class.java)
                startActivity(intentFinance)
            }
            R.id.LayoutPromise -> {
                val intentSearch = Intent(this, ListKeluarActivity::class.java)
                startActivity(intentSearch)
            }
            R.id.LayoutSetting -> {
//                val intentSetting = Intent(this, MultiColumnActivity::class.java)
                val intentSetting = Intent(this, ListRusakActivity::class.java)
                startActivity(intentSetting)
            }
            R.id.LayoutPenilaian -> {
                val intentSearch = Intent(this, Activity_Inspeksi::class.java)
                intentSearch.putExtra("title","Inspeksi")
                intentSearch.putExtra("url","")
                startActivity(intentSearch)
            }
            R.id.LayoutPengawasan -> {

                val browserIntent =  Intent(Intent.ACTION_VIEW, Uri.parse(BASE_WEB + "pengawasan/?user_id=" + pref.getInt(ID_USER, 0)))
                startActivity(browserIntent)
            }
            R.id.LayoutSoal -> {
                val intentSearch = Intent(this, Activity_Inspeksi::class.java)
                intentSearch.putExtra("url","soal")
                intentSearch.putExtra("title","Task")
                startActivity(intentSearch)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadCastReceiver)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(broadCastReceiver, IntentFilter("broadcast_notify"))
    }

    private fun syncNotifikasi(){
        val stringReq = StringRequest(
            Request.Method.GET, ApiClient.BASE_URL +"notifikasi?id=" + pref.getInt(ID_USER, 0),
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
                        var read : String = item.getString("read")
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
                }
            },
            Response.ErrorListener {  })
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringReq)
    }
}
