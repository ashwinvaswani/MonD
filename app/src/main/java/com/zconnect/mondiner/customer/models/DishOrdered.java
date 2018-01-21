package com.zconnect.mondiner.customer.models;

/**
 * Created by Ishaan on 21-01-2018.
 */

public class DishOrdered {
    private String dishName;
    private String dishID;
    private String dishPrice;
    private String dishQuantity;

    public DishOrdered(){

    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getDishID() {
        return dishID;
    }

    public void setDishID(String dishID) {
        this.dishID = dishID;
    }

    public String getDishPrice() {
        return dishPrice;
    }

    public void setDishPrice(String dishPrice) {
        this.dishPrice = dishPrice;
    }

    public String getDishQuantity() {
        return dishQuantity;
    }

    public void setDishQuantity(String dishQuantity) {
        this.dishQuantity = dishQuantity;
    }

    public DishOrdered(String dishName, String dishID, String dishPrice, String dishQuantity) {
        this.dishName = dishName;
        this.dishID = dishID;
        this.dishPrice = dishPrice;
        this.dishQuantity = dishQuantity;
    }
}
