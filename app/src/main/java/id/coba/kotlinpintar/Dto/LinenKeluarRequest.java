package id.coba.kotlinpintar.Dto;

import com.google.gson.annotations.SerializedName;

public class LinenKeluarRequest {
        @SerializedName("jenis")
        public String jenis;
        @SerializedName("qty")
        public Integer qty;
        @SerializedName("ready")
        public Integer ready;

        @SerializedName("qty_keluar")
        public Integer qty_keluar;
}
