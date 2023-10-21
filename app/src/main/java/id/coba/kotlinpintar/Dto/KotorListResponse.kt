package id.coba.kotlinpintar.Dto

import com.google.gson.annotations.SerializedName

data class KotorListResponse (
    val data: ArrayList<DataKotor>,
    val page: Int,
    @SerializedName("total_pages")
    val totalPages: Int
)