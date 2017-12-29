package com.techart.writersblock;

/**
 * Created by brad on 2017/02/05.
 */

//I create this class to store firebase node keys and to prevent from having spelling mistakes in
    //different part of the apps(Which has happened to me more than once)
public class Constants {
    //I'll be creating all they node keys I'll be using in this app now
    //General
    public static final String POST_AUTHOR = "author";
    public static final String AUTHOR_URL = "authorUrl";
    public static final String USER = "user";
    public static final String TIME_CREATED = "timeCreated";
    public static final String NUM_LIKES = "numLikes";
    public static final String NUM_COMMENTS = "numComments";
    public static final String NUM_VIEWS = "numViews";
    public static final String LIKE_KEY = "Likes";
    public static final String VIEWS_KEY = "Views";
    public static final String LIBRARY = "Library";
    public static final String USERS = "Users";

    public static final String CREATE_STORY = "Create Story";
    public static final String POEM_HOLDER = "poem";
    public static final String STORY_HOLDER = "story";
    public static final String DEVOTION_HOLDER = "devotion";
    public static final String POST_KEY = "postKey";
    public static final String POST_TITLE = "postTitle";
    public static final String POST_TYPE = "postType";
    public static final String POST_CONTENT = "postContent";
    public static final String LAST_PAGE = "lastPage";
    public static final String CHAPTER_ADDED = "chaptersAdded";

    //Poems
    public static final String POEM_KEY = "Poems";
    public static final String POEM_TITLE = "title";
    public static final String POEM = "poemText";

    //Spiritual
    public static final String DEVOTION_KEY = "Devotions";
    public static final String COMMENT_TEXT = "commentText";


    //Comments
    public static final String COMMENTS_KEY = "Comments";
    public static final String DEVOTION_TITLE = "title";
    public static final String DEVOTION = "devotionText";

    public static final String STORY_KEY = "Stories";
    public static final String STORY_TITLE = "title";
    public static final String STORY_CATEGORY = "category";
    public static final String STORY_STATUS = "status";
    public static final String STORY_DESCRIPTION = "description";
    public static final String STORY_CHAPTERCOUNT = "chapters";
    public static final String STORY_REFID = "storyRefId";

    public static final String CHAPTER_KEY = "Chapters";
    public static final String CHAPTER_TITLE = "chapterTitle";
    public static final String CHAPTER_CONTENT = "Content";

    public static final String IS_EDITED = "isPostEdited";

    public static final String EPISODE = "Episode";
}
