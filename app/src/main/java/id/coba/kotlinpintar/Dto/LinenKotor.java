package id.coba.kotlinpintar.Dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LinenKotor {

    @SerializedName("NO_TRANSAKSI")
    public String NO_TRANSAKSI;

    @SerializedName("TANGGAL")
    public String TANGGAL;

    @SerializedName("PIC")
    public String PIC;

    @SerializedName("STATUS")
    public String STATUS;

    @SerializedName("KATEGORI")
    public String KATEGORI;
    @SerializedName("F_INFEKSIUS")
    public String F_INFEKSIUS;

    @SerializedName("TOTAL_BERAT")
    public String TOTAL_BERAT;

    @SerializedName("TOTAL_BERAT_REAL")
    public String TOTAL_BERAT_REAL;

    @SerializedName("TOTAL_QTY")
    public String TOTAL_QTY;

    @SerializedName("detail")
    public List<LinenKotorDetail> detail = new ArrayList<>();

    @SerializedName("error")
    public Boolean Error;

    @SerializedName("message")
    public String Message;

}
