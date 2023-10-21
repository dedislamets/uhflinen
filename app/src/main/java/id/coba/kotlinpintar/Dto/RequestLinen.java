package id.coba.kotlinpintar.Dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RequestLinen {
    @SerializedName("no_request")
    public String no_request;
    @SerializedName("tgl_request")
    public String tanggal;
    @SerializedName("total_request")
    public String total_request;
    @SerializedName("requestor")
    public String requestor;
    @SerializedName("created_date")
    public String created_date;
    @SerializedName("status_request")
    public String status_request;
    @SerializedName("ruangan")
    public String ruangan;
    @SerializedName("detail")
    public List<RequestDetail> detail = new ArrayList<>();
    @SerializedName("error")
    public Boolean Error;
    @SerializedName("message")
    public String Message;

}
