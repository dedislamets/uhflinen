package id.coba.kotlinpintar.Dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Delete {
    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("NO_TRANSAKSI")
    public String NO_TRANSAKSI;
    @SerializedName("error")
    public Boolean Error;
}
