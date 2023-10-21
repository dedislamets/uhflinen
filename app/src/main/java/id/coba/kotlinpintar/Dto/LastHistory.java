package id.coba.kotlinpintar.Dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LastHistory {

    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<Datum> data = new ArrayList<>();

    public class Datum {
        @SerializedName("no_transaksi")
        public String no_transaksi;
        @SerializedName("epc")
        public String epc;
        @SerializedName("status")
        public String status;
        @SerializedName("CURRENT_INSERT")
        public String CURRENT_INSERT;
    }
}
