package id.coba.kotlinpintar.Rest

import id.coba.kotlinpintar.Model.NoteModel
import retrofit2.Call
import retrofit2.http.GET

interface ApiEndPoint {
    @GET("room")
    fun data(): Call<NoteModel>
}