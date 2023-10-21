package id.coba.kotlinpintar.Dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Barang {
        @SerializedName("serial")
        public String serial;
        @SerializedName("tanggal_register")
        public String tanggal_register;
        @SerializedName("nama_ruangan")
        public String nama_ruangan;
        @SerializedName("id_jenis")
        public String id_jenis;
}
