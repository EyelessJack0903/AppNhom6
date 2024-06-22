package com.example.myapplaptop.Activity.Domain;

public class DetailCart {

    private String ID_Detail; // Thay đổi kiểu thành String
    private String ID_Cart; // Thay đổi kiểu thành String
    private int ID_Laptop;
    private int Quantity;
    private double Total;
    private double Price;

    public DetailCart() {}

    // Getters và Setters cho các thuộc tính

    public String getID_Detail() {
        return ID_Detail;
    }

    public void setID_Detail(String ID_Detail) {
        this.ID_Detail = ID_Detail;
    }

    public String getID_Cart() {
        return ID_Cart;
    }

    public void setID_Cart(String ID_Cart) {
        this.ID_Cart = ID_Cart;
    }

    public int getID_Laptop() {
        return ID_Laptop;
    }

    public void setID_Laptop(int ID_Laptop) {
        this.ID_Laptop = ID_Laptop;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public double getTotal() {
        return Total;
    }

    public void setTotal(double total) {
        Total = total;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    // Method to get laptop name based on ID_Laptop
    public String getLaptopName() {
        // Implement logic to fetch laptop name based on ID_Laptop
        // This is just a placeholder method
        return "Laptop Name"; // Replace with actual logic to fetch laptop name
    }
}