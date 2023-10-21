package id.coba.kotlinpintar.Dto

import com.google.gson.annotations.SerializedName

data class DataRequest(
    @SerializedName("id")
    val id: Int,
    @SerializedName("no_request")
    val no_request: String = "",
    @SerializedName("tgl_request")
    val tgl_request: String = "",
    @SerializedName("requestor")
    val requestor: String = "",
    @SerializedName("created_date")
    val created_date: String = "",
    @SerializedName("status_request")
    val status_request: String = "",
    @SerializedName("ruangan")
    val ruangan: String = "",
    @SerializedName("total_request")
    val total_request: String = "",
    @SerializedName("detail")
    var detail: List<RequestDetail> = ArrayList()
)
