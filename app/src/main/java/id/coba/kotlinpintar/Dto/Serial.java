package id.coba.kotlinpintar.Dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Serial {

    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<Datum> data = new ArrayList<>();

    public class Datum {
        @SerializedName("serial")
        public String serial;
        @SerializedName("ruangan")
        public String ruangan;
        @SerializedName("jenis")
        public String jenis;
        @SerializedName("berat")
        public String berat;
    }
}
