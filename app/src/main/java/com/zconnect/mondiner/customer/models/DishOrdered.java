package com.zconnect.mondiner.customer.models;

/**
 * Created by Ishaan on 21-01-2018.
 */

public class DishOrdered {
    private String dishName;
    private String dishID;
    private String dishPrice;
    private Integer dishQuantity;
    private String dishAmount;

    public String getDishAmount() {
        return dishAmount;
    }

    public void setDishAmount(String dishAmount) {
        this.dishAmount = dishAmount;
    }

    public DishOrdered(String dishAmount) {
        this.dishAmount = dishAmount;
    }

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

    public int getDishQuantity() {
        if(dishQuantity!=null) {
            return dishQuantity;
        }
        else {
            return 0;
        }
    }

    public void setDishQuantity(int dishQuantity) {
        this.dishQuantity = dishQuantity;
    }

    public DishOrdered(String dishName, String dishID, String dishPrice, int dishQuantity) {
        this.dishName = dishName;
        this.dishID = dishID;
        this.dishPrice = dishPrice;
        this.dishQuantity = dishQuantity;
    }
}
