package id.coba.kotlinpintar.Adapter

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import id.coba.kotlinpintar.InputDbHelper.*
import id.coba.kotlinpintar.Lihat_Ruangan
import id.coba.kotlinpintar.R
import kotlinx.android.synthetic.main.adapter_note.view.*
import kotlinx.android.synthetic.main.row_layout.view.*
import org.json.JSONObject
import java.nio.file.Files.delete
import id.coba.kotlinpintar.Rest.ApiClient.BASE_URL
import id.coba.kotlinpintar.ListRegisterLinen
import id.coba.kotlinpintar.Model.RegLinenModel
import id.coba.kotlinpintar.InputDbHelper


class ListRegLinenAdapter (
    private val notes : ArrayList<HashMap<String,String>>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ListRegLinenAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)= ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_reg_linen, parent, false)
    )

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = notes[position]
        holder.textNo.text = (position + 1).toString()
        holder.textNote.text = data["serial"]
        holder.textRuangan.text = "Ruang " + data["ruangan"]


        val db = InputDbHelper(holder.itemView.context)
        val kursor: Cursor = db.getData(TABLE_JENIS_BARANG, ID_JENIS + "="+ data["jenis"])

        while (kursor.moveToNext()) {
            val jenis = kursor.getString(kursor.getColumnIndex(JENIS))
            holder.textJenis.text = jenis
        }
        holder.itemView.setOnClickListener {
            (holder.itemView.text_note.setTextColor(Color.CYAN))

        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) , View.OnClickListener{
        val textNo = itemView.findViewById<TextView>(R.id.text_no)
        val textNote = itemView.findViewById<TextView>(R.id.text_note)
        val textRuangan = itemView.findViewById<TextView>(R.id.text_ruangan)
        val textJenis = itemView.findViewById<TextView>(R.id.text_jenis)
//        val imgDelete = itemView.findViewById<ImageView>(R.id.imgDelete)

        init {
            itemView.setOnClickListener { this }
//            imgDelete.setOnClickListener(this)
            textNote.setOnClickListener(this)
            textJenis.setOnClickListener(this)
            textRuangan.setOnClickListener(this)

        }

        override fun onClick(v: View?) {
//            val position : Int = adapterPosition
//            if(position != RecyclerView.NO_POSITION) {
//                listener.onItemClick(position)
//            }
//
//            when (v?.id) {
//                R.id.imgDelete -> {
//                    var id= notes[position].serial
//                    val builder = AlertDialog.Builder(v.context)
//                    builder.setMessage("Yakin akan dihapus data $id ?")
//                        .setPositiveButton("Yakin..",
//                            DialogInterface.OnClickListener { dialog, id ->
//
//                                val stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
//                                    BASE_URL +"hapus_linen?serial=" + notes[position].serial,
//                                    Response.Listener<String> { response ->
//                                        var jsonObject: JSONObject = JSONObject(response)
//                                        var status_kirim : String = jsonObject.getString("status")
//                                        if (status_kirim.equals("200")){
//
//                                            val mHelper = InputDbHelper(v.context)
//                                            val db =  mHelper.getWritableDatabase()
//                                            val values_header  = ContentValues()
//
//                                            values_header.put(SERIAL,   notes[position].serial)
//
//                                            db.delete(TABLE_BARANG,"SERIAL=?", arrayOf(notes[position].serial) )
//
//                                            Toast.makeText(v.context, "Data terhapus", Toast.LENGTH_SHORT).show()
//                                            val intentBiasa = Intent(v.context, ListRegisterLinen::class.java)
//                                            v.context.startActivity(intentBiasa)
//                                        }
//
//                                    }, Response.ErrorListener {
//                                        Toast.makeText(v.context, "Something wrong", Toast.LENGTH_SHORT).show()
//                                    }){}
//                                val requestQueue: RequestQueue = Volley.newRequestQueue(v.context)
//                                requestQueue.add(stringRequest)
//
//                            })
//                        .setNegativeButton(R.string.cancel,
//                            DialogInterface.OnClickListener { dialog, id ->
//                                // User cancelled the dialog
//                            })
//
//                    builder.create()
//                    builder.show()
//
//                }
//                R.id.text_note -> {
//                    val intentBiasa = Intent(v.context, Lihat_Ruangan::class.java)
//                    intentBiasa.putExtra("serial",notes[position].serial)
//                    v.context.startActivity(intentBiasa)
//                }
//                R.id.text_ruangan -> {
//                    val intentBiasa = Intent(v.context, Lihat_Ruangan::class.java)
//                    intentBiasa.putExtra("serial",notes[position].serial)
//                    v.context.startActivity(intentBiasa)
//                }
//            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    public fun setData(data: List<HashMap<String,String>>){
        notes.clear()
        notes.addAll(data)
        notifyDataSetChanged()
    }
}