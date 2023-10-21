package id.coba.kotlinpintar.Dto

import com.google.gson.annotations.SerializedName

data class RegLinenListResponse (
    val data: ArrayList<DataLinen>,
    val page: Int,
    @SerializedName("total_pages")
    val totalPages: Int
)