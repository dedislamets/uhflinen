package id.coba.kotlinpintar.Dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ListRuangan {

    @SerializedName("status")
    public String status;
    @SerializedName("data")
    public List<Datum> data = new ArrayList<>();

    public class Datum {
        @SerializedName("id")
        public Integer id;
        @SerializedName("ruangan")
        public String ruangan;
        @SerializedName("finfeksius")
        public String finfeksius;
        @SerializedName("total_bed")
        public Integer total_bed;

    }
}
