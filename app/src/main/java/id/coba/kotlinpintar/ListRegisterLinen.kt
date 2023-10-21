package id.coba.kotlinpintar

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import id.coba.kotlinpintar.Adapter.KotorAdapter
import id.coba.kotlinpintar.Adapter.ListRegLinenAdapter
import id.coba.kotlinpintar.Adapter.RegisterLinenAdapter
import id.coba.kotlinpintar.Dto.KotorListResponse
import id.coba.kotlinpintar.Dto.RegLinenListResponse
import id.coba.kotlinpintar.InputDbHelper.*

import id.coba.kotlinpintar.Model.RegLinenModel
import id.coba.kotlinpintar.Rest.ApiClient
import id.coba.kotlinpintar.Rest.ApiInterface
import id.coba.kotlinpintar.Rest.ApiRegLinenRetrofit
import id.coba.kotlinpintar.Rest.ApiRetrofit
import kotlinx.android.synthetic.main.list_register_linen.country_search
import kotlinx.android.synthetic.main.list_register_linen.filter
import kotlinx.android.synthetic.main.list_register_linen.layout_empty
import kotlinx.android.synthetic.main.list_register_linen.progressBar
import kotlinx.android.synthetic.main.list_register_linen.swipeRefresh
import kotlinx.android.synthetic.main.list_register_linen.list_note
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ListRegisterLinen : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener, ListRegLinenAdapter.OnItemClickListener {

    private  val api by lazy { ApiRegLinenRetrofit().endpoint}
    private lateinit var btnNavigation: BottomNavigationView
    private  lateinit var noteAdapter: ListRegLinenAdapter
    private  lateinit var listNote: RecyclerView
    private  lateinit var fabCreate: FloatingActionButton
    private lateinit var btnCari: Button
    private lateinit var toolbar: Toolbar
    private lateinit var noteModel : ArrayList<RegLinenModel.Data>
    private  var mToast: Toast? = null
    private lateinit var list: ArrayList<HashMap<String,String>>

    private lateinit var layoutManager: LinearLayoutManager
    private var page = 1
    private var totalPage: Int = 1
    private var isLoading = false
    private lateinit var regLinenAdapter: RegisterLinenAdapter
    var apiInterface: ApiInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_register_linen)
        apiInterface = ApiClient.getClient().create(ApiInterface::class.java)
        layoutManager = LinearLayoutManager(this)
        swipeRefresh.setOnRefreshListener(this)
        setupRecyclerView()

        getUsers(false)
        list_note.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {Log.d("MainActivity", "onScrollChange: ")
                val visibleItemCount = layoutManager.childCount
                val pastVisibleItem = layoutManager.findFirstVisibleItemPosition()
                val total  = regLinenAdapter.itemCount
                if (!isLoading && page < totalPage){
                    if (visibleItemCount + pastVisibleItem>= total){
                        page++
                        getUsers(false)
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })

        setupView()
        setupListener()

        btnNavigation = findViewById(R.id.bottomNavigationView)
        var menu: Menu = btnNavigation.menu
        selectedMenu(menu.getItem(3))
        btnNavigation.setOnNavigationItemSelectedListener { item: MenuItem ->
            selectedMenu(item)

            false
        }

        supportActionBar?.setCustomView(R.layout.abs_layout)
        supportActionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val title = supportActionBar?.customView?.findViewById<AppCompatTextView>(R.id.tvTitle)
        title?.text = "MASTER LINEN"
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
            val parameters = java.util.HashMap<String, String>()
            parameters["page"] = page.toString()
            Log.d("PAGE", "$page")

            if (txtSearch !== ""){
                parameters["search"] = txtSearch
                if (page > 1){
                    parameters["page"] = "1";
                }
                regLinenAdapter.clear()
            }

            apiInterface?.getRegisterLinen(parameters)?.enqueue(object : Callback<RegLinenListResponse>{
                override fun onFailure(call: Call<RegLinenListResponse>, t: Throwable) {
                    Toast.makeText(this@ListRegisterLinen, "${t.message}", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    isLoading = false
                    swipeRefresh.isRefreshing = false
                }

                override fun onResponse(
                    call: Call<RegLinenListResponse>?,
                    response: retrofit2.Response<RegLinenListResponse>?
                ) {
                    totalPage = response?.body()?.totalPages!!
                    Log.d("PAGE", "totalPage: $totalPage")
                    val listResponse = response.body()?.data
                    if (listResponse != null){
                        Log.d("PAGE", "listResponse != null")
                        if(listResponse.isEmpty()){
                            filter.visibility = View.GONE
                            layout_empty.visibility = View.VISIBLE
                            list_note.visibility = View.GONE
                        }else{
                            layout_empty.visibility = View.GONE
                            filter.visibility = View.VISIBLE
                            list_note.visibility = View.VISIBLE
                            regLinenAdapter.addList(listResponse)
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
        list_note.setHasFixedSize(true)
        list_note.layoutManager = layoutManager
        regLinenAdapter = RegisterLinenAdapter()
        list_note.adapter = regLinenAdapter

        regLinenAdapter.onItemClick = { contact ->
            /*val intentBiasa = Intent(this, Activity_Register_Linen::class.java)
            val b = Bundle()
            b.putString("serial", contact.serial)
            intentBiasa.putExtras(b)
            startActivity(intentBiasa)
            finish()*/
        }
    }

    override fun onRefresh() {
        regLinenAdapter.clear()
        page = 1
        getUsers(true)
    }
    override fun onStart() {
        super.onStart()
        //getNote()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {

        startActivity(Intent(this, Activity_Setting::class.java))
        this.finish()
    }
    private fun setupView(){
        listNote =  findViewById(R.id.list_note)
        fabCreate =  findViewById(R.id.fab_create)
        country_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                getUsers(false, newText.toString())
                return false
            }
        })
        //btnCari = findViewById(R.id.btnCari)
    }
    private fun setupListener(){
        fabCreate.setOnClickListener{
            startActivity(Intent(this,Activity_Register_Linen::class.java))
        }
        /*btnCari.setOnClickListener {
            val intentBiasa = Intent(this, CariRoom::class.java)
            startActivity(intentBiasa)
        }*/
    }

    override fun onItemClick(position: Int) {
        noteAdapter.notifyItemChanged(position)
    }

    private fun getNote(){
        listNote =  findViewById(R.id.list_note)
        noteAdapter = ListRegLinenAdapter(arrayListOf(),this)

        list = ArrayList<HashMap<String,String>>()
        val db = InputDbHelper(applicationContext)
        val kursor: Cursor = db.getData(InputDbHelper.TABLE_BARANG,"1=1")


        while (kursor.moveToNext()) {
            val data = HashMap<String,String>()

            data["serial"] = kursor.getString(kursor.getColumnIndex(SERIAL))
            data["ruangan"] = kursor.getString(kursor.getColumnIndex(NAMA_RUANGAN))
            data["jenis"] = kursor.getString(kursor.getColumnIndex(ID_JENIS))
            list.add(data)
        }
        noteAdapter = ListRegLinenAdapter(list,this@ListRegisterLinen)
        listNote.adapter = noteAdapter

    }
    private fun showToast(info: String) {
        if (mToast == null)
            mToast = Toast.makeText(this, info, Toast.LENGTH_SHORT)
        else
            mToast!!.setText(info)
        mToast!!.show()
    }
    private fun selectedMenu(item: MenuItem) {
        item.isChecked = true
        when (item.itemId) {
            R.id.menuHome -> {
                val intentBiasa = Intent(this, MainActivity::class.java)
                startActivity(intentBiasa)
                finish()
            }
            R.id.menuMonitor -> {
                val alertDialog = MaterialAlertDialogBuilder(this)
                    .setTitle("Sedang dalam pengembangan..!!")
                    .setIcon(R.mipmap.ic_launcher)
                    .setNeutralButton("Tutup", null)
                    .show()
            }
            R.id.menuRequest -> {
                val intentBiasa = Intent(this, Activity_Request::class.java)
                startActivity(intentBiasa)
                finish()
            }
            R.id.menuSettings -> {
//                val intentBiasa1 = Intent(this, Activity_Setting::class.java)
//                startActivity(intentBiasa1)
//                finish()
            }
        }
    }



}