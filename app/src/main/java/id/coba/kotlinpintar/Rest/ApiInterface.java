package id.coba.kotlinpintar.Rest;

import java.util.HashMap;
import java.util.List;

import id.coba.kotlinpintar.Dto.BarangHeader;
import id.coba.kotlinpintar.Dto.BersihListResponse;
import id.coba.kotlinpintar.Dto.Delete;
import id.coba.kotlinpintar.Dto.JumlahCuci;
import id.coba.kotlinpintar.Dto.KeluarListResponse;
import id.coba.kotlinpintar.Dto.KotorListResponse;
import id.coba.kotlinpintar.Dto.LastHistory;
import id.coba.kotlinpintar.Dto.LinenBersih;
import id.coba.kotlinpintar.Dto.LinenKeluar;
import id.coba.kotlinpintar.Dto.LinenKotor;
import id.coba.kotlinpintar.Dto.LinenRusak;
import id.coba.kotlinpintar.Dto.ListRuangan;
import id.coba.kotlinpintar.Dto.Movie;
import id.coba.kotlinpintar.Dto.RegLinenListResponse;
import id.coba.kotlinpintar.Dto.RequestLinen;
import id.coba.kotlinpintar.Dto.RequestListResponse;
import id.coba.kotlinpintar.Dto.RusakListResponse;
import id.coba.kotlinpintar.Dto.Serial;
import id.coba.kotlinpintar.Model.CobaModel;
import id.coba.kotlinpintar.Model.PostPutDelRoom;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

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

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "rusak", hasBody = true)
    Call<Delete> DeleteRusak(@Field("NO_TRANSAKSI") String no_transaksi);
    @GET("ruangan")
    Call<ListRuangan> getRuangan();

    @GET("infoserial?")
    Call<Serial> infoSerial(@Query("serial") String serial);

    @POST("linen_kotor")
    Call<LinenKotor> saveLinenKotor(@Body LinenKotor kotor);
    @POST("linen_bersih")
    Call<LinenKotor> saveLinenBersih(@Body LinenBersih bersih);
    @POST("linen_keluar")
    Call<LinenKeluar> saveLinenKeluar(@Body LinenKeluar keluar);
    @POST("linen_rusak")
    Call<LinenRusak> saveLinenRusak(@Body LinenRusak rusak);
    @POST("barang_multi")
    Call<BarangHeader> saveBarang(@Body BarangHeader brg);

    @POST("request_linen")
    Call<RequestLinen> saveRequestLinen(@Body RequestLinen request);
    @GET("linen_kotor_all")
    Call<KotorListResponse> getLinenKotor(@QueryMap HashMap<String, String> parameters);
    @GET("register_linen_all")
    Call<RegLinenListResponse> getRegisterLinen(@QueryMap HashMap<String, String> parameters);
    @GET("linen_keluar_all")
    Call<KeluarListResponse> getLinenKeluar(@QueryMap HashMap<String, String> parameters);
    @GET("linen_bersih_all")
    Call<BersihListResponse> getLinenBersih(@QueryMap HashMap<String, String> parameters);
    @GET("linen_rusak_all")
    Call<RusakListResponse> getLinenRusak(@QueryMap HashMap<String, String> parameters);
    @GET("request_linen_all")
    Call<RequestListResponse> getRequestLinen(@QueryMap HashMap<String, String> parameters);
    @GET("kotor?")
    Call<KotorListResponse> getKotor(@Query("no") String no_transaksi);
    @GET("rusak?")
    Call<RusakListResponse> getRusak(@Query("no") String no_transaksi);
    @GET("bersih?")
    Call<BersihListResponse> getBersih(@Query("no") String no_transaksi);
    @GET("keluar?")
    Call<KeluarListResponse> getKeluar(@Query("no") String no_transaksi);
    @GET("request?")
    Call<RequestListResponse> getRequest(@Query("no") String no_transaksi);
    @GET("last_history?")
    Call<LastHistory> getLastHistory(@Query("epc") String epc);
    @GET("jml_cuci?")
    Call<JumlahCuci> getJumlahCuci(@Query("epc") String epc);
    @GET("volley_array.json")
    Call<List<Movie>> getMovies();
}
