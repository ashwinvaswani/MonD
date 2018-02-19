package com.zconnect.mondiner.customer.models;

/**
 * Created by grandiose on 17/2/18.
 */

public class Offers {
    private String offerName;
    private String imageUri;
    private String description;

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Offers(String offerName, String imageUri, String description) {
        this.offerName = offerName;
        this.imageUri = imageUri;
        this.description = description;
    }

    public Offers(){

    }
}
