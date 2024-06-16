package com.example.myapplaptop.Activity.Domain;

public class Type {
    private int ID_LM;
    private String Name;

    public int getID_LM() {
        return ID_LM;
    }

    public void setID_LM(int ID_LM) {
        this.ID_LM = ID_LM;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @Override
    public String toString() {
        return Name;
    }

    public Type (){

    }
}
