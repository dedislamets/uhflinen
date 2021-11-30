package id.coba.kotlinpintar.Rest;

import id.coba.kotlinpintar.Model.CobaModel;
import id.coba.kotlinpintar.Model.PostPutDelRoom;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ApiInterface {
    @GET("room")
    Call<CobaModel> getRoom();
    @FormUrlEncoded
    @POST("room")
    Call<PostPutDelRoom> postRoom(@Field("ruangan") String ruangan);
    @FormUrlEncoded
    @PUT("room")
    Call<PostPutDelRoom> putRoom(@Field("id") String id,
                                     @Field("ruangan") String ruangan);
    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "room", hasBody = true)
    Call<PostPutDelRoom> deleteRoom(@Field("id") String id);
}
