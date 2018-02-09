package com.zconnect.mondiner.customer.models;

import android.net.Uri;

/**
 * Created by Ishaan on 11-01-2018.
 */


public class Menu {

    private String itemName;
    private String itemCuisine;
    private String itemPrice;
    private String vegNonVeg;
    private boolean availability;
    private String  itemQuantity;
    private String imageUri;

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public Menu(){

    }

    public Menu(String itemCuisine) {
        this.itemCuisine = itemCuisine;
    }

    public String getItemCuisine() {
        return itemCuisine;
    }

    public void setItemCuisine(String itemCuisine) {
        this.itemCuisine = itemCuisine;
    }

    public Menu(String itemName, String itemPrice, String vegNonVeg, boolean availability, String itemQuantity, String imageUri) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.vegNonVeg = vegNonVeg;
        this.availability = availability;
        this.itemQuantity = itemQuantity;
        this.imageUri = imageUri;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getVegNonVeg() {
        return vegNonVeg;
    }

    public void setVegNonVeg(String vegNonVeg) {
        this.vegNonVeg = vegNonVeg;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public String getItemQuantity() {
        if(itemQuantity!=null) {
            return itemQuantity;
        }
        else {
            return "0";
        }
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public void incrementQuantity() {
        int qty = Integer.parseInt(itemQuantity);
        qty++;
        this.itemQuantity = qty + "";
    }
}

