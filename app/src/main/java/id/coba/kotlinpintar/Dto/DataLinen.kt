package id.coba.kotlinpintar.Dto

import com.google.gson.annotations.SerializedName

data class DataLinen(
    @SerializedName("id")
    val id: Int,
    @SerializedName("serial")
    val serial: String = "",
    @SerializedName("tanggal_register")
    val tanggal_register: String = "",
    @SerializedName("nama_ruangan")
    val nama_ruangan: String = "",
    @SerializedName("jenis")
    val jenis: String = "",
    @SerializedName("spesifikasi")
    val spesifikasi: String = "",
    @SerializedName("fmedis")
    val fmedis: String = "",
    @SerializedName("berat")
    val berat: String = "",
    @SerializedName("harga")
    val harga: String = "",
    @SerializedName("jml_cuci")
    val jml_cuci: String = ""
)
