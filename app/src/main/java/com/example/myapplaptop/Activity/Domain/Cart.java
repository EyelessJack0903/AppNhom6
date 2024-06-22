package com.example.myapplaptop.Activity.Domain;

import java.util.Date;

public class Cart {

    private String ID_Cart; // Thay đổi kiểu thành String
    private int ID_Payment;
    private String ID_User; // Thay đổi kiểu thành String
    private double Total;
    private Date Date;
    private Date DateShip;
    private String Address;
    private String Name;
    private String SDT;

    public Cart () {}

    // Getters và Setters cho các thuộc tính

    public String getID_Cart() {
        return ID_Cart;
    }

    public void setID_Cart(String ID_Cart) {
        this.ID_Cart = ID_Cart;
    }

    public int getID_Payment() {
        return ID_Payment;
    }

    public void setID_Payment(int ID_Payment) {
        this.ID_Payment = ID_Payment;
    }

    public String getID_User() {
        return ID_User;
    }

    public void setID_User(String ID_User) {
        this.ID_User = ID_User;
    }

    public double getTotal() {
        return Total;
    }

    public void setTotal(double total) {
        Total = total;
    }

    public Date getDate() {
        return Date;
    }

    public void setDate(Date date) {
        Date = date;
    }

    public Date getDateShip() {
        return DateShip;
    }

    public void setDateShip(Date dateShip) {
        DateShip = dateShip;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSDT() {
        return SDT;
    }

    public void setSDT(String SDT) {
        this.SDT = SDT;
    }

    @Override
    public String toString() {
        return Address + SDT + Name;
    }

    public void setPaymentMethod(String name) {
    }
}
