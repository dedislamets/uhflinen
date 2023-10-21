package id.coba.kotlinpintar.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.coba.kotlinpintar.Dto.DataKotor
import id.coba.kotlinpintar.Dto.DataLinen
import id.coba.kotlinpintar.ListKotorActivityRecyle
import id.coba.kotlinpintar.R
import kotlinx.android.synthetic.main.adapter_reg_linen.view.*
import kotlinx.android.synthetic.main.item_layout.view.tvno_transaksi
import kotlinx.android.synthetic.main.item_layout.view.tvtanggal
import kotlinx.android.synthetic.main.list_row.view.artist
import kotlinx.android.synthetic.main.list_row.view.duration
import kotlinx.android.synthetic.main.list_row.view.status_sync
import kotlinx.android.synthetic.main.list_row.view.title
import java.text.SimpleDateFormat
import javax.inject.Inject

class RegisterLinenAdapter @Inject constructor(): RecyclerView.Adapter<RegisterLinenAdapter.UsersViewHolder>(){
    private var list = ArrayList<DataLinen>()
    var onItemClick: ((DataLinen) -> Unit)? = null

    inner class UsersViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(data: DataLinen, position: Int){
            with(itemView){
                val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                //val outputDate = inputDateFormat.parse(data.CURRENT_INSERT)
                val outputDateFormat = SimpleDateFormat("dd MMM yyyy HH:mm")

                text_no.text = (position+1).toString()
                text_note.text = data.serial
                text_jenis.text = data.jenis
                text_ruangan.text= data.nama_ruangan
                text_jml_cuci.text= data.jml_cuci
                //duration.text = outputDateFormat.format(outputDate)
            }
        }
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(list[adapterPosition])
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_reg_linen, parent, false)
        return UsersViewHolder(view)
    }

    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bind(list[position], position)
    }
    fun addList(items: ArrayList<DataLinen>){
        list.addAll(items)
        notifyDataSetChanged()
    }
    fun clear(){
        list.clear()
        notifyDataSetChanged()
    }
}