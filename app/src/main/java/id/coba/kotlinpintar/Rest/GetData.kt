package id.coba.kotlinpintar.Rest

import id.coba.kotlinpintar.RetroCrypto
import io.reactivex.Observable
import retrofit2.http.GET

interface GetData {
    @GET("prices?key=271f01eb75067e874ae5e17efa3f97f6600625b5")
    fun getData() : Observable<List<RetroCrypto>>
}