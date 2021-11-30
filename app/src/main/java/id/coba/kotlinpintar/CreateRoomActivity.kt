package id.coba.kotlinpintar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import id.coba.kotlinpintar.InputDbHelper.BASE_URL
import org.json.JSONObject

class CreateRoomActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var editNote: EditText
    private lateinit var btnCreate: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_room)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setTitle("My Title")

        toolbar.title =  "Input Ruangan"
        toolbar?.setNavigationOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    Activity_Monitor::class.java
                )
            )
        }

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)

        setupView()
        setupListener()
    }

    private fun setupView(){
        editNote = findViewById(R.id.nama_ruangan)
        btnCreate = findViewById(R.id.btnCreate)
    }
    private fun setupListener(){

        btnCreate.setOnClickListener{
            if(editNote.text.toString().isEmpty()){
                Toast.makeText(applicationContext,"Ruangan tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }else{

                val stringRequest: StringRequest = object :StringRequest(Request.Method.POST,
                     BASE_URL+"room",
                     Response.Listener<String> { response ->
                         var jsonObject: JSONObject = JSONObject(response)
                         var status_kirim : String = jsonObject.getString("status")
                         if (status_kirim.equals("200")){
                             Toast.makeText(this, "Data tersimpan", Toast.LENGTH_SHORT).show()
                         }
                         finish()
                     }, Response.ErrorListener {
                         Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show()
                     }){
                     override fun getParams(): Map<String, String> {
                         val params: MutableMap<String, String> = HashMap()
                         params["ruangan"] = editNote.text.toString()
                         return params
                     }
                 }
                val requestQueue: RequestQueue = Volley.newRequestQueue(this)
                requestQueue.add(stringRequest)
            }
            Log.e("Create Room", editNote.text.toString())
        }
    }
}
