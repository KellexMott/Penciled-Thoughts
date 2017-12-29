package com.techart.writersblock;

/**
 * Created by Kelvin on 11/10/2017.
 */

public class Users {
    private String name;
    private String image;
    private String signedAs;
    public Users(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSignedAs() {
        return signedAs;
    }

    public void setSignedAs(String signedAs) {
        this.signedAs = signedAs;
    }
}
