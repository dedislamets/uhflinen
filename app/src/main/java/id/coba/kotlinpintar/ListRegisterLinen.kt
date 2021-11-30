package id.coba.kotlinpintar

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import id.coba.kotlinpintar.Adapter.ListRegLinenAdapter
import id.coba.kotlinpintar.InputDbHelper.*

import id.coba.kotlinpintar.Model.RegLinenModel
import id.coba.kotlinpintar.Rest.ApiRegLinenRetrofit
import id.coba.kotlinpintar.Rest.ApiRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ListRegisterLinen : AppCompatActivity(), ListRegLinenAdapter.OnItemClickListener {

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

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_register_linen)
        setupView()
        setupListener()

        btnNavigation = findViewById(R.id.bottomNavigationView)
        var menu: Menu = btnNavigation.menu
        selectedMenu(menu.getItem(3))
        btnNavigation.setOnNavigationItemSelectedListener { item: MenuItem ->
            selectedMenu(item)

            false
        }

//        toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)
        getSupportActionBar()?.setTitle("Master Linen")
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)
//        toolbar.title =  "Master Linen"

//        toolbar?.setNavigationOnClickListener {
//            startActivity(
//                Intent(
//                    applicationContext,
//                    Activity_Setting::class.java
//                )
//            )
//        }

    }

    override fun onStart() {
        super.onStart()
        getNote()
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
        btnCari = findViewById(R.id.btnCari)
    }
    private fun setupListener(){
        fabCreate.setOnClickListener{
            startActivity(Intent(this,Activity_Register_Linen::class.java))
        }
        btnCari.setOnClickListener {
            val intentBiasa = Intent(this, CariRoom::class.java)
            startActivity(intentBiasa)
        }
    }

    override fun onItemClick(position: Int) {
//        Toast.makeText(this,"item $position clicked",Toast.LENGTH_SHORT).show()
        noteAdapter.notifyItemChanged(position)
    }

    private fun getNote(){
        listNote =  findViewById(R.id.list_note)
        noteAdapter = ListRegLinenAdapter(arrayListOf(),this)

        list = ArrayList<HashMap<String,String>>()

//        api.data().enqueue(object: Callback<RegLinenModel> {
//            override fun onResponse(call: Call<RegLinenModel>, response: Response<RegLinenModel>) {
//                if(response.isSuccessful){
//                    var listdata = response.body()!!.data
//                    noteAdapter.setData( listdata)
//
//                    listNote.adapter = noteAdapter
//
//                }
//            }
//
//            override fun onFailure(call: Call<RegLinenModel>?, t: Throwable?) {
//
//
//            }
//        })
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