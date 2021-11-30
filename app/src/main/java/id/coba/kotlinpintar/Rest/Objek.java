package id.coba.kotlinpintar.Rest;

public class Objek {
    String ruangan="";
    Integer id;
    public Objek(Integer s_id, String s_ruangan){
        this.id=s_id;
        this.ruangan = s_ruangan;
    }

    public String getRuangan() {
        return ruangan;
    }

    public Integer getId() {
        return id;
    }
}
