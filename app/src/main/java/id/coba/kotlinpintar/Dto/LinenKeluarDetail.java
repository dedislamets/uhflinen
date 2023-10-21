package id.coba.kotlinpintar.Dto;

import com.google.gson.annotations.SerializedName;

public class LinenKeluarDetail {
        @SerializedName("epc")
        public String epc;
        @SerializedName("no_transaksi")
        public String no_transaksi;
        @SerializedName("item")
        public String item;
        @SerializedName("berat")
        public String berat;

        @SerializedName("jenis")
        public String jenis;
}
