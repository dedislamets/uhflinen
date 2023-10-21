package id.coba.kotlinpintar.Rest

import id.coba.kotlinpintar.InputDbHelper.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiRegLinenRetrofit {
    val endpoint: ApiRegLinenEndPoint
    get() {
//        val interceptor = HttpLoggingInterceptor()
//        interceptor.level = HttpLoggingInterceptor.Level.BODY

//        val client = OkHttpClient.Builder()
//            .connectTimeout(5, TimeUnit.MINUTES)
//            .readTimeout(5, TimeUnit.MINUTES)
//            .writeTimeout(5, TimeUnit.MINUTES)
//            .addInterceptor(interceptor)
//            .build()
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        val okHttpClient = OkHttpClient.Builder()

        okHttpClient.addInterceptor(httpLoggingInterceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiRegLinenEndPoint::class.java)
    }

}