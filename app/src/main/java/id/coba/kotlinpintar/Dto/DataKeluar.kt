package id.coba.kotlinpintar.Dto

import com.google.gson.annotations.SerializedName

data class DataKeluar(
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
    @SerializedName("RUANGAN")
    val RUANGAN: String = "",
    @SerializedName("NO_REFERENSI")
    val NO_REFERENSI: String = "",
    @SerializedName("penerima")
    val penerima: String = "",
    @SerializedName("signature")
    val signature: String = "",
    @SerializedName("tgl_terima")
    val tgl_terima: String = "",
    @SerializedName("detail")
    var detail: List<LinenKeluarDetail> = ArrayList(),
    @SerializedName("request")
    var request: List<LinenKeluarRequest> = ArrayList()

)
