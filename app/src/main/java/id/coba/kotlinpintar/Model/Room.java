package id.coba.kotlinpintar.Model;

import com.google.gson.annotations.SerializedName;

public class Room {
    @SerializedName("id")
    private String id;
    @SerializedName("ruangan")
    private String ruangan;

    public Room(){}

    public Room(String id, String ruangan) {
        this.id = id;
        this.ruangan = ruangan;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoom() {
        return ruangan;
    }

    public void setRoom(String ruangan) {
        this.ruangan = ruangan;
    }

}
