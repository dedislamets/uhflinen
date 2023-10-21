package id.coba.kotlinpintar.Dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LinenKotorDetail {
        @SerializedName("epc")
        public String epc;
        @SerializedName("ruangan")
        public String ruangan;
        @SerializedName("no_transaksi")
        public String no_transaksi;
        @SerializedName("item")
        public String item;
        @SerializedName("berat")
        public String berat;
}
