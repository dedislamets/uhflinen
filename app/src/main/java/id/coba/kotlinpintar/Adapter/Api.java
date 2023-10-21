package id.coba.kotlinpintar.Adapter;

import java.util.List;

import id.coba.kotlinpintar.Dto.ListRuangan;
import retrofit2.Call;
import retrofit2.http.GET;
import id.coba.kotlinpintar.Model.Login;

public interface Api {
    @GET("ruangan")
    Call<ListRuangan> getRuangan();
}
