package id.coba.kotlinpintar.Model;

import com.google.gson.annotations.SerializedName;

public class Login {
    @SerializedName("email")
    String email;
    @SerializedName("nama_user")
    String nama_user;
    @SerializedName("password")
    String password;
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getUsername() {
        return nama_user;
    }
    public void setUsername(String nama_user) {
        this.nama_user = nama_user;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        password = password;
    }
}
