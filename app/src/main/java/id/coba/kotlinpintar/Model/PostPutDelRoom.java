package id.coba.kotlinpintar.Model;

import com.google.gson.annotations.SerializedName;

public class PostPutDelRoom {
    @SerializedName("status")
    String status;
    @SerializedName("result")
    Room mRoom;
    @SerializedName("message")
    String message;
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Room getKontak() {
        return mRoom;
    }
    public void setKontak(Room Kontak) {
        mRoom = Kontak;
    }
}
