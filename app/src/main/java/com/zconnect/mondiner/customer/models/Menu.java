package com.zconnect.mondiner.customer.models;

/**
 * Created by Ishaan on 11-01-2018.
 */


public class Menu {

    private String itemName;
    private double itemPrice;
    private int vegNonVeg;
    private boolean availability;
    private int itemQuantity;

    public Menu(){

    }

    public Menu(String itemName, double itemPrice, int vegNonVeg, boolean availability, int itemQuantity) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.vegNonVeg = vegNonVeg;
        this.availability = availability;
        this.itemQuantity = itemQuantity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getVegNonVeg() {
        return vegNonVeg;
    }

    public void setVegNonVeg(int vegNonVeg) {
        this.vegNonVeg = vegNonVeg;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }
}

