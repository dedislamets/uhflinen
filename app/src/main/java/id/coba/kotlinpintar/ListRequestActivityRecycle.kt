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
import id.coba.kotlinpintar.Adapter.RequestAdapterKt
import id.coba.kotlinpintar.Dto.KeluarListResponse
import id.coba.kotlinpintar.Dto.KotorListResponse
import id.coba.kotlinpintar.Dto.RequestListResponse
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


class ListRequestActivityRecycle : AppCompatActivity() , SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private lateinit var list: RecyclerView
    private var mapEpc: HashMap<String, String>? = null
    private var listEpc: ArrayList<HashMap<String, String>>? = null
    private var epcSet: MutableSet<String>? = null
    private lateinit var mHelper: InputDbHelper
    private val songsList = ArrayList<HashMap<String, String>>()
    private lateinit var btnAdd: Button
    private lateinit var tanggal: EditText
    private val myCalendar = Calendar.getInstance()
    val DATA_SAVED_BROADCAST = "id.coba.datasaved"
    private lateinit var broadcastReceiver : BroadcastReceiver
    private lateinit var layoutManager: LinearLayoutManager
    private var page = 1
    private var totalPage: Int = 1
    private var isLoading = false
    private lateinit var requestadapter: RequestAdapterKt
    var apiInterface: ApiInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_request_recycleview)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        mHelper = InputDbHelper(this)
        btnAdd = findViewById<Button>(R.id.btnAdd)
        list = findViewById<RecyclerView>(R.id.rvKeluar)
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
        title?.text = "REQUEST LINEN"
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
                requestadapter.clear()
            }

            apiInterface?.getRequestLinen(parameters)?.enqueue(object : Callback<RequestListResponse> {
                override fun onFailure(call: Call<RequestListResponse>, t: Throwable) {
                    Toast.makeText(this@ListRequestActivityRecycle, "${t.message}", Toast.LENGTH_SHORT).show()
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
        requestadapter = RequestAdapterKt()
        rvKeluar.adapter = requestadapter

        requestadapter.onItemClick = { contact ->
            val intentBiasa = Intent(this, RequestActivity::class.java)
            val b = Bundle()
            b.putString("no_request", contact.no_request)
            intentBiasa.putExtras(b)
            startActivity(intentBiasa)
            finish()
        }
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
            //updateUI()
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnAdd -> {
                val intentBiasa = Intent(this, RequestActivity::class.java)
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
}


