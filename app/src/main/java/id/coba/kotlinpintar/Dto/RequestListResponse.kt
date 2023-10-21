package id.coba.kotlinpintar.Dto

import com.google.gson.annotations.SerializedName
data class RequestListResponse (
    val data: ArrayList<DataRequest>,
    val page: Int,
    @SerializedName("total_pages")
    val totalPages: Int
)