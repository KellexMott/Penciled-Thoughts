package com.techart.writersblock;

/**
 * Created by Kelvin on 05/06/2017.
 */

public class Library {
    private String postTitle;
    private String postKey;
    private Integer chaptersAdded;

    public Library() {}

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public Integer getChaptersAdded() {
        return chaptersAdded;
    }

    public void setChaptersAdded(Integer chaptersAdded) {
        this.chaptersAdded = chaptersAdded;
    }
}
