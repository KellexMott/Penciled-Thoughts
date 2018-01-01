package com.techart.writersblock;

/**
 * Object for a Story
 * Created by Kelvin on 05/06/2017.
 */

public class Story {
    private String title;
    private String author;
    private Long chapters;
    private String category;
    private String status;
    private String description;

    private Boolean isPostEdited;
    private Long numLikes;
    private Long numComments;
    private Long numViews;
    private Long timeCreated;


    public Story()
    {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public Boolean getPostEdited() {
        return isPostEdited;
    }

    public void setPostEdited(Boolean postEdited) {
        isPostEdited = postEdited;
    }

    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(Long numLikes) {
        this.numLikes = numLikes;
    }

    public Long getNumComments() {
        return numComments;
    }

    public void setNumComments(Long numComments) {
        this.numComments = numComments;
    }

    public Long getChapters() {
        return chapters;
    }

    public void setChapters(Long chapters) {
        this.chapters = chapters;
    }

    public Long getNumViews() {
        return numViews;
    }

    public void setNumViews(Long numViews) {
        this.numViews = numViews;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
