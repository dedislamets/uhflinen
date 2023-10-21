    package id.coba.kotlinpintar

    import android.content.*
    import android.os.Bundle
    import android.os.Handler
    import android.os.StrictMode
    import android.util.Log
    import android.view.Menu
    import android.view.View
    import android.view.animation.TranslateAnimation
    import android.widget.*
    import androidx.appcompat.app.ActionBar
    import androidx.appcompat.app.AppCompatActivity
    import androidx.appcompat.widget.AppCompatTextView
    import androidx.appcompat.widget.SearchView
    import androidx.appcompat.widget.Toolbar
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    import id.coba.kotlinpintar.Adapter.KotorAdapter
    import id.coba.kotlinpintar.CustomizedListView.*
    import id.coba.kotlinpintar.Dto.KotorListResponse
    import id.coba.kotlinpintar.InputDbHelper.*
    import id.coba.kotlinpintar.Rest.ApiClient
    import id.coba.kotlinpintar.Rest.ApiInterface
    import kotlinx.android.synthetic.main.activity_list_kotor_recycleview.filter
    import kotlinx.android.synthetic.main.activity_list_kotor_recycleview.layout_empty
    import kotlinx.android.synthetic.main.activity_list_kotor.*
    import kotlinx.android.synthetic.main.activity_list_kotor_recycleview.country_search
    import kotlinx.android.synthetic.main.activity_list_kotor_recycleview.progressBar
    import kotlinx.android.synthetic.main.activity_list_kotor_recycleview.rvKotor
    import kotlinx.android.synthetic.main.activity_list_kotor_recycleview.swipeRefresh
    import retrofit2.Call
    import retrofit2.Callback
    import java.util.*


    class ListKotorActivityRecyle : AppCompatActivity() , SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

        internal val URI = "https://api.androidhive.info/music/music.xml"
        private  val KEY_ID = "id"
        private val KEY_TITLE = "title"
        private val KEY_ARTIST = "artist"
        private val KEY_DURATION = "duration"
        private val KEY_THUMB_URL = "thumb_url"
        private val KEY_STATUS= "status_sync"
        private lateinit var list: ListView
        private lateinit var adapter: LazyAdapter
        private var mapEpc: HashMap<String, String>? = null
        private var listEpc: ArrayList<HashMap<String, String>>? = null
        private var epcSet: MutableSet<String>? = null
        private lateinit var mHelper: InputDbHelper
        private val songsList = ArrayList<HashMap<String, String>>()
        private lateinit var btnAdd: Button
        private lateinit var btnSync: Button
        private lateinit var tanggal: EditText
        private val myCalendar = Calendar.getInstance()
        private lateinit var toolbar: Toolbar
        private var NAME_SYNCED_WITH_SERVER : Int = 1
        private var NAME_NOT_SYNCED_WITH_SERVER  : Int = 0
        val DATA_SAVED_BROADCAST = "id.coba.datasaved"
        private lateinit var broadcastReceiver : BroadcastReceiver
        private lateinit var btnExport: Button

        private lateinit var layoutManager: LinearLayoutManager
        private var page = 1
        private var totalPage: Int = 1
        private var isLoading = false
        private lateinit var kotoradapter: KotorAdapter
        var apiInterface: ApiInterface? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_list_kotor_recycleview)

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            btnAdd = findViewById<Button>(R.id.btnAdd)

            apiInterface = ApiClient.getClient().create(ApiInterface::class.java)
            mHelper = InputDbHelper(this)
            btnAdd.setOnClickListener(this)

            layoutManager = LinearLayoutManager(this)
            swipeRefresh.setOnRefreshListener(this)
            setupRecyclerView()

            getUsers(false)
            rvKotor.addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {Log.d("MainActivity", "onScrollChange: ")
                    val visibleItemCount = layoutManager.childCount
                    val pastVisibleItem = layoutManager.findFirstVisibleItemPosition()
                    val total  = kotoradapter.itemCount
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
            //supportActionBar?.title = "LINEN KOTOR"
            supportActionBar?.setCustomView(R.layout.abs_layout)
            supportActionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            val title = supportActionBar?.customView?.findViewById<AppCompatTextView>(R.id.tvTitle)
            title?.text = "LINEN KOTOR"


        }

         override fun onStart() {
             super.onStart()
         }
         override fun onStop() {
             super.onStop()
    //         unregisterReceiver(NetworkStateChecker())
         }

         private fun setupRecyclerView() {
             rvKotor.setHasFixedSize(true)
             rvKotor.layoutManager = layoutManager
             kotoradapter = KotorAdapter()
             rvKotor.adapter = kotoradapter

             kotoradapter.onItemClick = { contact ->
                 val intentBiasa = Intent(this, KotorActivity::class.java)
                 val b = Bundle()
                 b.putString("no_transaksi", contact.NO_TRANSAKSI)
                 intentBiasa.putExtras(b)
                 startActivity(intentBiasa)
                 finish()
             }
         }

        override fun onRefresh() {
            kotoradapter.clear()
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

                 if (txtSearch !== ""){
                     parameters["search"] = txtSearch
                     kotoradapter.clear()
                 }

                 apiInterface?.getLinenKotor(parameters)?.enqueue(object : Callback<KotorListResponse>{
                     override fun onFailure(call: Call<KotorListResponse>, t: Throwable) {
                         Toast.makeText(this@ListKotorActivityRecyle, "${t.message}", Toast.LENGTH_SHORT).show()
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
                                 filter.visibility = View.GONE
                                 layout_empty.visibility = View.VISIBLE
                                 rvKotor.visibility = View.GONE
                             }else{
                                 layout_empty.visibility = View.GONE
                                 filter.visibility = View.VISIBLE
                                 rvKotor.visibility = View.VISIBLE
                                 kotoradapter.addList(listResponse)
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
             }, 1000)}
        override fun onSupportNavigateUp(): Boolean {
            onBackPressed()
            return true
        }

        override fun onBackPressed() {
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }

        override fun onClick(v: View) {
            when (v.id) {
                R.id.btnAdd -> {
                    val intentBiasa = Intent(this, KotorActivity::class.java)
                    startActivity(intentBiasa)
                }
            }
        }
    }


