package id.coba.kotlinpintar.Model;

public class MenuModel {
    String Title ;
    String Description;
    int Icon;

    public MenuModel(String Title, String Description, int Icon){
        this.Title = Title;
        this.Description = Description;
        this.Icon = Icon;
    }

    public MenuModel(String Title){
        this.Title = Title;
    }

    public String getTitle(){
        return this.Title;
    }

    public String getDescription(){ return this.Description;  }

    public int getIcon(){
        return this.Icon;
    }
}
