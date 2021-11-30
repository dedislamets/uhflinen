package id.coba.kotlinpintar.Model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetRoom {
    @SerializedName("status")
    String status;
    @SerializedName("result")
    List<Room> listDataRoom;
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
    public List<Room> getListDataRoom() {
        return listDataRoom;
    }
    public void setListDataRoom(List<Room> listDataRoom) {
        this.listDataRoom = listDataRoom;
    }
}
