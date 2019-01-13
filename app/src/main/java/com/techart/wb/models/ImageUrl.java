package com.techart.wb.models;

public class ImageUrl {
    private static ImageUrl instance;
    private String imageUrl;
    private String status;

    public static synchronized ImageUrl getInstance() {
        if (instance == null) {
            instance = new ImageUrl();
        }
        return instance;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
