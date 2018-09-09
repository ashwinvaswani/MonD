package com.zconnect.mondiner.customer.models;

/**
 * Created by Ishaan on 03-02-2018.
 */

public class PrevOrders {
    private String orderId;
    private String restaurantName;
    private String date;
    private String totalAmount;

    public PrevOrders(){

    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public PrevOrders(String orderId) {
        this.orderId = orderId;
    }

    public PrevOrders(String restaurantName, String date, String totalAmount) {
        this.restaurantName = restaurantName;
        this.date = date;
        this.totalAmount = totalAmount;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
