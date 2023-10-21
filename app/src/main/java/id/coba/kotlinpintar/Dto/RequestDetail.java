package id.coba.kotlinpintar.Dto;

import com.google.gson.annotations.SerializedName;
public class RequestDetail {
        @SerializedName("no_request")
        public String no_request;
        @SerializedName("jenis")
        public String jenis;
        @SerializedName("qty")
        public String qty;
        @SerializedName("qty_keluar")
        public String qty_keluar;
        @SerializedName("flag_ambil")
        public String flag_ambil;
}
