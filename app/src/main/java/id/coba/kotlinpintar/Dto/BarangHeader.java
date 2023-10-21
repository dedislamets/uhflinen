package id.coba.kotlinpintar.Dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BarangHeader {
    @SerializedName("detail")
    public List<Barang> detail = new ArrayList<>();
    @SerializedName("error")
    public Boolean Error;
    @SerializedName("message")
    public String Message;
}
