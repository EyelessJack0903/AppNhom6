package com.example.myapplaptop.Activity.Domain;

public class Payment {
    private int ID_Payment;
    private String Name;

    public Payment() {
        // Default constructor required for Firebase
    }

    public int getID_Payment() {
        return ID_Payment;
    }

    public void setID_Payment(int ID_Payment) {
        this.ID_Payment = ID_Payment;
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
}
