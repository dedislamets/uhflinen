package id.coba.kotlinpintar.Dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LinenKeluar {
    @SerializedName("NO_TRANSAKSI")
    public String NO_TRANSAKSI;
    @SerializedName("TANGGAL")
    public String TANGGAL;
    @SerializedName("PIC")
    public String PIC;
    @SerializedName("STATUS")
    public String STATUS;
    @SerializedName("RUANGAN")
    public String RUANGAN;
    @SerializedName("NO_REFERENSI")
    public String NO_REFERENSI;
    @SerializedName("detail")
    public List<LinenKeluarDetail> detail = new ArrayList<>();
    @SerializedName("request")
    public List<LinenKeluarRequest> request = new ArrayList<>();
    @SerializedName("error")
    public Boolean Error;
    @SerializedName("message")
    public String Message;

}
