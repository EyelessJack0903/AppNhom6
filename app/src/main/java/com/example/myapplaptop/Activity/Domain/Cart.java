package com.example.myapplaptop.Activity.Domain;

import java.util.Date;
import java.util.List;

public class Cart {

    private String ID_Cart;
    private int ID_Payment;
    private String ID_User;
    private double Total;
    private Date Date;
    private Date DateShip;
    private String Address;
    private String Name;
    private String SDT;

    // Date components
    private int day;
    private int month;
    private int year;
    private int hours;
    private int minutes;
    private int seconds;

    private List<DetailCart> detailCartList;

    public List<DetailCart> getDetailCartList() {
        return detailCartList;
    }

    public void setDetailCartList(List<DetailCart> detailCartList) {
        this.detailCartList = detailCartList;
    }

    public Cart() {
    }

    // Getters and Setters for all properties

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

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    @Override
    public String toString() {
        return Name + " " + SDT + " " + Address;
    }

    public void setPaymentMethod(String name) {
    }
}
