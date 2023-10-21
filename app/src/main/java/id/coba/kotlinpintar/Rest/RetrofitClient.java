package id.coba.kotlinpintar.Rest;


import id.coba.kotlinpintar.Adapter.Api;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static id.coba.kotlinpintar.InputDbHelper.BASE_URL;


public class RetrofitClient {
    private static RetrofitClient instance = null;
    private Api myApi;

    private RetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        myApi = retrofit.create(Api.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public Api getMyApi() {
        return myApi;
    }
}
