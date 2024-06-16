package com.example.myapplaptop.Activity.Domain;

public class Model {

    private int ID_MD;
    private String Name;

    public Model (){

    }

    public int getID_MD() {
        return ID_MD;
    }

    @Override
    public String toString() {
        return Name;
    }

    public void setID_MD(int ID_MD) {
        this.ID_MD = ID_MD;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
