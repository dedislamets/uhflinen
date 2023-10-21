package id.coba.kotlinpintar.Dto;

import com.google.gson.annotations.SerializedName;

public class LinenRusakDetail {
        @SerializedName("epc")
        public String epc;
        @SerializedName("no_transaksi")
        public String no_transaksi;
        @SerializedName("item")
        public String item;
        @SerializedName("berat")
        public String berat;
        @SerializedName("jml_cuci")
        public String jml_cuci;
}
