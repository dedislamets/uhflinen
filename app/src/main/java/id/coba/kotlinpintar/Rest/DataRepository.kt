package id.coba.kotlinpintar.Rest

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DataRepository {
    fun create(): CobaInterface {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://192.168.2.156:8080/linen/api/")
            .build()
        return retrofit.create(CobaInterface::class.java)
    }
}