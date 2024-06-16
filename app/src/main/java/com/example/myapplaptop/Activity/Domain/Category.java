package com.example.myapplaptop.Activity.Domain;

public class Category {
    private int ID_TH;
    private String Name;
    private String Logo;

    @Override
    public String toString() {
        return Name;
    }

    public int getID_TH() {
        return ID_TH;
    }

    public void setID_TH(int ID_TH) {
        this.ID_TH = ID_TH;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLogo() {
        return Logo;
    }

    public void setLogo(String logo) {
        Logo = logo;
    }

    public Category(){

    }
}
