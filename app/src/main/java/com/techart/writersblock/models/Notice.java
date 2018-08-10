package com.techart.writersblock.models;

/**
 * Object for notice
 * Created by Kelvin on 27/08/2017.
 */

public class Notice {
    private String postType;
    private String user;
    private String postTitle;
    private String authorUrl;
    private Long timeCreated;

    public Notice()
    {

    }

    public String  getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }


    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }
}
