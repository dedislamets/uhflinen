package id.coba.kotlinpintar.Dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LinenRusak {
    @SerializedName("NO_TRANSAKSI")
    public String NO_TRANSAKSI;
    @SerializedName("TANGGAL")
    public String TANGGAL;
    @SerializedName("PIC")
    public String PIC;
    @SerializedName("CATATAN")
    public String CATATAN;
    @SerializedName("DEFECT")
    public String DEFECT;
    @SerializedName("CURRENT_INSERT")
    public String CURRENT_INSERT;
    @SerializedName("detail")
    public List<LinenRusakDetail> detail = new ArrayList<>();
    @SerializedName("error")
    public Boolean Error;
    @SerializedName("message")
    public String Message;

}
