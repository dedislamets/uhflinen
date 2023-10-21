package id.coba.kotlinpintar.Dto

import com.google.gson.annotations.SerializedName

data class BersihListResponse (
    val data: ArrayList<DataBersih>,
    val page: Int,
    @SerializedName("total_pages")
    val totalPages: Int
)