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
import id.coba.kotlinpintar.Adapter.MonitorAdapter
import id.coba.kotlinpintar.Model.NoteModel
import id.coba.kotlinpintar.Rest.ApiRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Activity_Monitor : AppCompatActivity(), MonitorAdapter.OnItemClickListener {

    private  val api by lazy { ApiRetrofit().endpoint}
    private lateinit var btnNavigation: BottomNavigationView
    private  lateinit var noteAdapter: MonitorAdapter
    private  lateinit var listNote: RecyclerView
    private  lateinit var fabCreate: FloatingActionButton
    private lateinit var btnCari: Button
    private lateinit var toolbar: Toolbar
    private lateinit var noteModel : ArrayList<NoteModel.Data>
    private lateinit var list: ArrayList<HashMap<String,String>>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_monitor)
        setupView()
        setupList()
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
        getSupportActionBar()?.setTitle("Master Ruangan")
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)
//        toolbar.title =  "Master Ruangan"
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {

        startActivity(Intent(this, Activity_Setting::class.java))
        this.finish()
    }

    override fun onStart() {
        super.onStart()
        getNote()
        setupList()
    }
    private fun setupView(){
        listNote =  findViewById(R.id.list_note)
        fabCreate =  findViewById(R.id.fab_create)
        btnCari = findViewById(R.id.btnCari)
    }
    private fun setupListener(){
//        fabCreate.setOnClickListener{
//            startActivity(Intent(this,CreateRoomActivity::class.java))
//        }
        btnCari.setOnClickListener {
            val intentBiasa = Intent(this, CariRoom::class.java)
            startActivity(intentBiasa)
        }
    }

    override fun onItemClick(position: Int) {
//        Toast.makeText(this,"item $position clicked",Toast.LENGTH_SHORT).show()
        noteAdapter.notifyItemChanged(position)
    }

    private fun setupList(){
        listNote =  findViewById(R.id.list_note)
        list = ArrayList<HashMap<String,String>>()
//        noteAdapter = MonitorAdapter(arrayListOf(),this)
//        listNote.adapter = noteAdapter
    }
    private fun getNote(){
//        api.data().enqueue(object: Callback<NoteModel> {
//            override fun onResponse(call: Call<NoteModel>, response: Response<NoteModel>) {
//                if(response.isSuccessful){
//                    var listdata = response.body()!!.data
//                    noteAdapter.setData( listdata)
//                }
//            }
//
//            override fun onFailure(call: Call<NoteModel>?, t: Throwable?) {
//                Log.e("Activity Monitor", t.toString())
//            }
//        })
        val db = InputDbHelper(this)
        val kursor: Cursor = db.getData(InputDbHelper.TABLE_RUANGAN,"1=1")
        while (kursor.moveToNext()) {
            val data = HashMap<String,String>()

            data["id"] = kursor.getString(kursor.getColumnIndex(InputDbHelper.ID_RUANGAN))
            data["ruangan"] = kursor.getString(kursor.getColumnIndex(InputDbHelper.NAMA_RUANGAN))
            list.add(data)
        }
        noteAdapter = MonitorAdapter(list,this)
        listNote.adapter = noteAdapter
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