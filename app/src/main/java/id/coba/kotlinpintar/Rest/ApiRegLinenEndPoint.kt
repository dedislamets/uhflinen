package id.coba.kotlinpintar.Rest

import id.coba.kotlinpintar.Model.NoteModel
import id.coba.kotlinpintar.Model.RegLinenModel
import retrofit2.Call
import retrofit2.http.GET

interface ApiRegLinenEndPoint {
    @GET("barang")
    fun data(): Call<RegLinenModel>
}