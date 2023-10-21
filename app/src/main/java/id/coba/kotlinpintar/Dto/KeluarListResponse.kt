package id.coba.kotlinpintar.Dto

import com.google.gson.annotations.SerializedName

data class KeluarListResponse (
    val data: ArrayList<DataKeluar>,
    val page: Int,
    @SerializedName("total_pages")
    val totalPages: Int
)