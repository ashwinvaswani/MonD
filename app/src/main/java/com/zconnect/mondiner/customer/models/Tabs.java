package com.zconnect.mondiner.customer.models;

/**
 * Created by Ishaan on 20-01-2018.
 */

public class Tabs {

    public Tabs(String catID, String catName, String catPriority) {
        this.catID = catID;
        this.catName = catName;
        this.catPriority = catPriority;
    }

    private String catID;
    private String catName;
    private String catPriority;

    public Tabs(){

    }

    public String getCatID() {
        return catID;
    }

    public void setCatID(String catID) {
        this.catID = catID;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getCatPriority() {
        return catPriority;
    }

    public void setCatPriority(String catPriority) {
        this.catPriority = catPriority;
    }
}
