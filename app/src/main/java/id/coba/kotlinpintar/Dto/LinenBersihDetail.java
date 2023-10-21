package id.coba.kotlinpintar.Dto;

import com.google.gson.annotations.SerializedName;

public class LinenBersihDetail {
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
        @SerializedName("checked")
        public String checked;
        @SerializedName("keluar")
        public String keluar;

        @SerializedName("status_linen")
        public String status_linen;
}
