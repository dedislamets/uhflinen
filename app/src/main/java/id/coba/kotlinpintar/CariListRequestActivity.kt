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
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
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
import id.coba.kotlinpintar.Adapter.RequestAdapterKt
import id.coba.kotlinpintar.Dto.RequestListResponse
import id.coba.kotlinpintar.InputContract.TaskEntry.CURRENT_INSERT
import id.coba.kotlinpintar.InputDbHelper.*
import id.coba.kotlinpintar.Rest.ApiClient
import id.coba.kotlinpintar.Rest.ApiClient.BASE_URL
import id.coba.kotlinpintar.Rest.ApiInterface
import id.coba.kotlinpintar.Util.context
import kotlinx.android.synthetic.main.activity_list_keluar_recycleview.filter
import kotlinx.android.synthetic.main.activity_list_keluar_recycleview.layout_empty
import kotlinx.android.synthetic.main.activity_list_keluar_recycleview.progressBar
import kotlinx.android.synthetic.main.activity_list_keluar_recycleview.rvKeluar
import kotlinx.android.synthetic.main.activity_list_keluar_recycleview.swipeRefresh
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import javax.microedition.khronos.opengles.GL10


class CariListRequestActivity : AppCompatActivity() , SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private  val KEY_ID = "id"
    private val KEY_TITLE = "title"
    private val KEY_ARTIST = "artist"
    private val KEY_DURATION = "duration"
    private val KEY_THUMB_URL = "thumb_url"
    private val KEY_STATUS= "status_sync"

    private lateinit var list: RecyclerView
    private lateinit var adapter: RequestAdapter
    private lateinit var adapterDetail: RequestDetailAdapter
    private var mapEpc: HashMap<String, String>? = null
    private var listEpc: ArrayList<HashMap<String, String>>? = null
    private var epcSet: MutableSet<String>? = null
    private lateinit var mHelper: InputDbHelper
    private val songsList = ArrayList<HashMap<String, String>>()

    private val myCalendar = Calendar.getInstance()
    val DATA_SAVED_BROADCAST = "id.coba.datasaved"
    private lateinit var broadcastReceiver : BroadcastReceiver
    private lateinit var btnReset: Button
    private lateinit var txtCari: EditText
    private lateinit var layoutManager: LinearLayoutManager
    private var page = 1
    private var totalPage: Int = 1
    private var isLoading = false
    private lateinit var requestadapter: RequestAdapterKt
    var apiInterface: ApiInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cari_list_request_recycleview)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        mHelper = InputDbHelper(this)
        //btnReset = findViewById(R.id.reset) as Button
        txtCari = findViewById<EditText>(R.id.txtCari)
        list = findViewById<RecyclerView>(R.id.rvKeluar)
        //btnReset.setOnClickListener(this)
        /*list.setOnItemClickListener { parent, view, position, id ->

            val intentBiasa = Intent()
            var vi = view
            val no_req = vi.findViewById(R.id.title) as TextView

            val b = Bundle()
            b.putString("no_request", no_req.getText().toString())
            intentBiasa.putExtras(b)
            setResult(RESULT_OK, intentBiasa)

            finish()

        }*/
        apiInterface = ApiClient.getClient().create(ApiInterface::class.java)
        layoutManager = LinearLayoutManager(this)
        swipeRefresh.setOnRefreshListener(this)
        setupRecyclerView()

        getUsers(false)
        list.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount = layoutManager.childCount
                val pastVisibleItem = layoutManager.findFirstVisibleItemPosition()
                val total  = requestadapter.itemCount
                if (!isLoading && page < totalPage){
                    if (visibleItemCount + pastVisibleItem>= total){
                        page++
                        getUsers(false)
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        txtCari.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //updateUI(s.toString())
            }
        })

        supportActionBar?.setCustomView(R.layout.abs_layout)
        supportActionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val title = supportActionBar?.customView?.findViewById<AppCompatTextView>(R.id.tvTitle)
        title?.text = "Pencarian"
    }
    override fun onRefresh() {
        requestadapter.clear()
        page = 1
        getUsers(true)
    }
    override fun onStart() {
        super.onStart()
    }
    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
//            updateUI()
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override fun onBackPressed() {
        startActivity(Intent(this, KeluarActivity::class.java))
        this.finish()
    }
    private var date: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // TODO Auto-generated method stub
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
            parameters["STATUS"] = "Pending"
            Log.d("PAGE", "$page")
            apiInterface?.getRequestLinen(parameters)?.enqueue(object :
                Callback<RequestListResponse> {
                override fun onFailure(call: Call<RequestListResponse>, t: Throwable) {
                    Toast.makeText(this@CariListRequestActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    isLoading = false
                    swipeRefresh.isRefreshing = false
                }

                override fun onResponse(
                    call: Call<RequestListResponse>?,
                    response: retrofit2.Response<RequestListResponse>?
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
                            requestadapter.addList(listResponse)
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
        requestadapter = RequestAdapterKt()
        rvKeluar.adapter = requestadapter

        requestadapter.onItemClick = { contact ->
            val intentBiasa = Intent()
            val b = Bundle()
            b.putString("no_request", contact.no_request)
            intentBiasa.putExtras(b)
            setResult(RESULT_OK, intentBiasa)

            finish()
        }
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
        }
    }



}


