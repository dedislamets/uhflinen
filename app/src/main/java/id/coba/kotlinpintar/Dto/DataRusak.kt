package id.coba.kotlinpintar.Dto

import com.google.gson.annotations.SerializedName

data class DataRusak(
    @SerializedName("id")
    val id: Int,
    @SerializedName("NO_TRANSAKSI")
    val NO_TRANSAKSI: String = "",
    @SerializedName("TANGGAL")
    val TANGGAL: String = "",
    @SerializedName("PIC")
    val PIC: String = "",
    @SerializedName("CURRENT_INSERT")
    val CURRENT_INSERT: String = "",
    @SerializedName("CATATAN")
    val CATATAN: String = "",
    @SerializedName("DEFECT")
    val DEFECT: String = "",
    @SerializedName("detail")
    var detail: List<LinenRusakDetail> = ArrayList()
)
