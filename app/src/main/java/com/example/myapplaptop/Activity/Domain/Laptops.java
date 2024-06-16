package com.example.myapplaptop.Activity.Domain;

public class Laptops {

    private int ID_Laptop;
    private int ID_LM;
    private int ID_MD;
    private int ID_TH;
    private String Image;
    private String Name;
    private String Description;
    private double Price;
    private int Quantity;
    private double Star;
    private boolean BestLaptop;

    @Override
    public String toString() {
        return Name;
    }
    public Laptops(){

    }

    public int getID_Laptop() {
        return ID_Laptop;
    }

    public void setID_Laptop(int ID_Laptop) {
        this.ID_Laptop = ID_Laptop;
    }

    public int getID_LM() {
        return ID_LM;
    }

    public void setID_LM(int ID_LM) {
        this.ID_LM = ID_LM;
    }

    public int getID_MD() {
        return ID_MD;
    }

    public void setID_MD(int ID_MD) {
        this.ID_MD = ID_MD;
    }

    public int getID_TH() {
        return ID_TH;
    }

    public void setID_TH(int ID_TH) {
        this.ID_TH = ID_TH;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public double getStar() {
        return Star;
    }

    public void setStar(double star) {
        Star = star;
    }

    public boolean isBestLaptop() {
        return BestLaptop;
    }

    public void setBestLaptop(boolean bestLaptop) {
        BestLaptop = bestLaptop;
    }
}
