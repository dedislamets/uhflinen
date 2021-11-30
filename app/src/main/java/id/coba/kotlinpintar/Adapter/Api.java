package id.coba.kotlinpintar.Adapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import id.coba.kotlinpintar.Model.Login;

public interface  Api {
    @GET("marvel")
    Call<List<Login>> getsuperHeroes();
}
