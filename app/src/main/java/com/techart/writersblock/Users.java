package com.techart.writersblock;

/**
 * User object
 * Created by Kelvin on 11/10/2017.
 */

public class Users {
    private String name;
    private String imageUrl;
    private String signedAs;
    public Users(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSignedAs() {
        return signedAs;
    }

    public void setSignedAs(String signedAs) {
        this.signedAs = signedAs;
    }
}
