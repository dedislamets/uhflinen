package id.coba.kotlinpintar.Rest;

import id.coba.kotlinpintar.Model.LoginModel;
import id.coba.kotlinpintar.Model.Login;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface LoginInterface {
    @FormUrlEncoded
    @POST("login")
    Call<LoginModel> getLogin(@Field("email") String email, @Field("password") String password, @Field("company") String company);

}
