package id.coba.kotlinpintar

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import id.coba.kotlinpintar.InputDbHelper.*
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet

class ActivityNotification : AppCompatActivity() {

    private lateinit var list: ListView
    private var listEpc: ArrayList<HashMap<String, String>>? = null
    private var epcSet: MutableSet<String>? = null
    private lateinit var mHelper: InputDbHelper
    private lateinit var adapter: NotifikasiAdapter
    private val notifList = ArrayList<HashMap<String, String>>()
    private lateinit var pref : SharedPreferences
    private lateinit var webView: WebView
    private lateinit var mHandler: Handler
    private lateinit var ftView: View
    private var isLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        pref = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
        if(!isLogin()){
            val intentBiasa1 = Intent(applicationContext, Activity_Login::class.java)
            startActivity(intentBiasa1)
        }
        getSupportActionBar()?.setDisplayShowTitleEnabled(false)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)

        list = findViewById(R.id.list)
        val empty = findViewById(R.id.layout_empty) as RelativeLayout
        list.setEmptyView(empty)

        list.setOnItemClickListener { parent, view, position, id ->

            val intentBiasa = Intent(this, Activity_Inspeksi::class.java)
            val selectedFromList = parent.getItemAtPosition(position)
            var vi = view
            val id = vi.findViewById(R.id.id_notifikasi) as TextView
            val page_url = vi.findViewById(R.id.page_url) as TextView

            mHelper.updateSudahDibaca(Integer.parseInt(id.text.toString()))
            adapter.notifyDataSetChanged()
            val b = Bundle()
            b.putString(PAGE_URL, page_url.text.toString())
            if(page_url.text == "pengawasan/penilaian"){
                b.putString("title", "")
            }else{
                b.putString("title", "Form Penerimaan Linen")
            }

            intentBiasa.putExtras(b)
            startActivity(intentBiasa)
            finish()

        }
        list.setOnScrollListener(object : AbsListView.OnScrollListener{
            var VisibleItem: Int = 0
            override fun onScroll(p0: AbsListView?, FirstVisibleItem: Int, i2: Int, i3: Int) {

                if(VisibleItem < FirstVisibleItem)
                {
//                    Toast.makeText(applicationContext,"Scrolling Down",Toast.LENGTH_SHORT).show()
                }
                if(VisibleItem >FirstVisibleItem)
                {
//                    Toast.makeText(applicationContext,"Scrolling Up",Toast.LENGTH_SHORT).show()
                }
                    VisibleItem=FirstVisibleItem

            }
            override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {
//                updateUI()
            }
        })


        updateUI()
    }
    private fun isLogin(): Boolean {
        return pref.getBoolean("isLogin", false)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {

        startActivity(Intent(this, MainActivity::class.java))
        this.finish()
    }

    private fun updateUI() {
        epcSet = HashSet<String>()
        listEpc = ArrayList<HashMap<String, String>>()

        mHelper = InputDbHelper(this)
        val myList = ArrayList<String>()
        val db = mHelper.getReadableDatabase()

        val cursor_header = db.query(
            TABLE_NOTIFIKASI,
            arrayOf(
                ID_NOTIFIKASI,
                InputContract.TaskEntry.CURRENT_INSERT,
                SHORT_MSG,
                LONG_MSG,
                DIBACA,
                PAGE_URL
            ),
            null,
            null,
            null, null, InputContract.TaskEntry.CURRENT_INSERT + " DESC"
        )
        val ii = 0
        notifList.clear()
        while (cursor_header.moveToNext()) {
            val id_no_transaksi = cursor_header.getColumnIndex(ID_NOTIFIKASI)
            val id_tanggal = cursor_header.getColumnIndex(InputContract.TaskEntry.CURRENT_INSERT)
            val id_short = cursor_header.getColumnIndex(SHORT_MSG)
            val id_long = cursor_header.getColumnIndex(LONG_MSG)
            val id_dibaca = cursor_header.getColumnIndex(DIBACA)
            val id_url = cursor_header.getColumnIndex(PAGE_URL)

            val no_transaksi = cursor_header.getString(id_no_transaksi)

            val tanggal =cursor_header.getString(id_tanggal)

            val short = cursor_header.getString(id_short)
            val long = cursor_header.getString(id_long)
            val dibaca = cursor_header.getString(id_dibaca)
            val page_url = cursor_header.getString(id_url)

            val map = HashMap<String, String>()

            map["ID"] = no_transaksi
            map["SHORT"] = short
            map["LONG"] = long
            map["DIBACA"] = dibaca
            map["TANGGAL"]= tanggal
            map["URL"] = page_url

            notifList.add(map)

        }

        list = findViewById(R.id.list)

        adapter = NotifikasiAdapter(this, notifList)
        list.setAdapter(adapter)

        cursor_header.close()
        db.close()
    }

}

