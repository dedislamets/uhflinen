package id.coba.kotlinpintar.Dto

import com.google.gson.annotations.SerializedName

data class DataBersih(
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
    @SerializedName("STATUS")
    val STATUS: String = "",
    @SerializedName("KATEGORI")
    val KATEGORI: String = "",
    @SerializedName("TOTAL_BERAT")
    val TOTAL_BERAT: String = "",
    @SerializedName("TOTAL_QTY")
    val TOTAL_QTY: String = "",
    @SerializedName("detail")
    var detail: List<LinenBersihDetail> = ArrayList()
)
