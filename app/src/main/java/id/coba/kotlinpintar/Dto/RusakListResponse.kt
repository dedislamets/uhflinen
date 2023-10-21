package id.coba.kotlinpintar.Dto

import com.google.gson.annotations.SerializedName
data class RusakListResponse (
    val data: ArrayList<DataRusak>,
    val page: Int,
    @SerializedName("total_pages")
    val totalPages: Int
)