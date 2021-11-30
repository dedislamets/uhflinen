package id.coba.kotlinpintar.Rest

import id.coba.kotlinpintar.Model.CobaModel
import retrofit2.Call
import retrofit2.http.GET

interface CobaInterface {
    @GET("room")
    fun getPosts(): Call<List<CobaModel>>
}