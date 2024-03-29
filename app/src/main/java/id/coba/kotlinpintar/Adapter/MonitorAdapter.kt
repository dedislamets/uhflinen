package id.coba.kotlinpintar.Adapter

import android.content.DialogInterface
import android.content.Intent
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
import id.coba.kotlinpintar.Lihat_Ruangan
import id.coba.kotlinpintar.Model.NoteModel
import id.coba.kotlinpintar.R
import kotlinx.android.synthetic.main.adapter_note.view.*
import kotlinx.android.synthetic.main.row_layout.view.*
import org.json.JSONObject
import java.nio.file.Files.delete
import id.coba.kotlinpintar.Rest.ApiClient.BASE_URL
import id.coba.kotlinpintar.Activity_Monitor


class MonitorAdapter (
    private val notes : ArrayList<HashMap<String,String>>,
    private val listener: OnItemClickListener

) : RecyclerView.Adapter<MonitorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)= ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_note, parent, false)
    )

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: MonitorAdapter.ViewHolder, position: Int) {
        val data = notes[position]
        holder.textNote.text = data["ruangan"]
        holder.itemView.setOnClickListener {
            (holder.itemView.text_note.setTextColor(Color.CYAN))
//            val intentBiasa = Intent(holder.itemView.context, Lihat_Ruangan::class.java)
//            intentBiasa.putExtra("id",data.id)
//            holder.itemView.context.startActivity(intentBiasa)
//            Toast.makeText(holder.itemView.context,holder.itemView.text_note.text,Toast.LENGTH_LONG).show()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) , View.OnClickListener{
        val textNote = itemView.findViewById<TextView>(R.id.text_note)
        val imgDelete = itemView.findViewById<ImageView>(R.id.imgDelete)

        init {
            itemView.setOnClickListener { this }
            imgDelete.setOnClickListener(this)
            textNote.setOnClickListener(this)

        }

        override fun onClick(v: View?) {
            val position : Int = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }

//            when (v?.id) {
//                R.id.imgDelete -> {
//                    var id= notes[position].id
//                    val builder = AlertDialog.Builder(v.context)
//                    builder.setMessage("Yakin akan dihapus data $id ?")
//                        .setPositiveButton("Yakin..",
//                            DialogInterface.OnClickListener { dialog, id ->
//
//                                val stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
//                                    BASE_URL +"hapus_room?id=" + notes[position].id,
//                                    Response.Listener<String> { response ->
//                                        var jsonObject: JSONObject = JSONObject(response)
//                                        var status_kirim : String = jsonObject.getString("status")
//                                        if (status_kirim.equals("200")){
//                                            Toast.makeText(v.context, "Data terhapus", Toast.LENGTH_SHORT).show()
//                                            val intentBiasa = Intent(v.context, Activity_Monitor::class.java)
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
//                    intentBiasa.putExtra("id",notes[position].id)
//                    v.context.startActivity(intentBiasa)
//                }
//
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