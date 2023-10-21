package id.coba.kotlinpintar.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.coba.kotlinpintar.Dto.DataKeluar
import id.coba.kotlinpintar.R
import kotlinx.android.synthetic.main.list_row.view.artist
import kotlinx.android.synthetic.main.list_row.view.duration
import kotlinx.android.synthetic.main.list_row.view.jam
import kotlinx.android.synthetic.main.list_row.view.status_sync
import kotlinx.android.synthetic.main.list_row.view.title
import kotlinx.android.synthetic.main.list_row_keluar.view.ruangan
import java.text.SimpleDateFormat
import javax.inject.Inject

class KeluarAdapterKt @Inject constructor(): RecyclerView.Adapter<KeluarAdapterKt.UsersViewHolder>(){
    private var list = ArrayList<DataKeluar>()
    var onItemClick: ((DataKeluar) -> Unit)? = null


    inner class UsersViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(data: DataKeluar){
            with(itemView){
                val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val outputDate = inputDateFormat.parse(data.CURRENT_INSERT)
                val outputDateFormat = SimpleDateFormat("dd MMM yyyy")
                val outputHourFormat = SimpleDateFormat("HH:mm")

                duration.text = outputDateFormat.format(outputDate)
                jam.text = outputHourFormat.format(outputDate)
                title.text = data.NO_TRANSAKSI
                artist.text= data.PIC
                ruangan.text= data.RUANGAN
                status_sync.text= data.STATUS
            }
        }
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(list[adapterPosition])
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_row_keluar, parent, false)
        return UsersViewHolder(view)
    }

    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bind(list[position])
    }
    fun addList(items: ArrayList<DataKeluar>){
        list.addAll(items)
        notifyDataSetChanged()
    }
    fun clear(){
        list.clear()
        notifyDataSetChanged()
    }
}