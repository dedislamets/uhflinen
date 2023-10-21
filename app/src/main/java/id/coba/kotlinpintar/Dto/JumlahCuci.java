package id.coba.kotlinpintar.Dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class JumlahCuci {

    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<Datum> data = new ArrayList<>();
    public class Datum {
        @SerializedName("jml")
        public String jml;
    }
}
