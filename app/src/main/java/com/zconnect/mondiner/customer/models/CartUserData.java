package com.zconnect.mondiner.customer.models;

/**
 * Created by Ishaan on 21-01-2018.
 */

public class CartUserData {
    private String userName;
    private String userID;
    private String userQuantity;

    public CartUserData(String userName, String userID, String userQuantity) {
        this.userName = userName;
        this.userID = userID;
        this.userQuantity = userQuantity;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserQuantity() {
        return userQuantity;
    }

    public void setUserQuantity(String userQuantity) {
        this.userQuantity = userQuantity;
    }

    public CartUserData(){

    }
}
