package id.coba.kotlinpintar.Model;

public class MenuModel {
    String Title ;
    String Description;
    int Icon;
    Boolean IsValid;

    public MenuModel(String Title, String Description, int Icon, Boolean IsValid){
        this.Title = Title;
        this.Description = Description;
        this.Icon = Icon;
        this.IsValid = IsValid;
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

    public Boolean getIsValid(){
        return this.IsValid;
    }

    public Boolean setIsValid(Boolean value){ return this.IsValid = value; }
}
