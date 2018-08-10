package com.techart.writersblock.constants;

/**
 * Created by brad on 2017/02/05.
 * Stores firebase node keys and other constants to prevent spelling mistakes in different part of
 * the apps
 */

public class Constants {
    public static final String POST_AUTHOR = "author";
    public static final String AUTHOR_URL = "authorUrl";
    public static final String USER = "user";
    public static final String TIME_CREATED = "timeCreated";
    public static final String NUM_LIKES = "numLikes";
    public static final String NUM_COMMENTS = "numComments";
    public static final String LAST_UPDATE = "lastUpdate";
    public static final String NUM_VIEWS = "numViews";
    public static final String LIKE_KEY = "Like";
    public static final String VIEWS_KEY = "View";
    public static final String LIBRARY = "Library";
    public static final String USERS = "Users";

    public static final String SIGNED_IN_AS = "signedAs";
    public static final String IMAGE_URL = "imageUrl";
    public static final String USER_NAME = "name";
    public static final String REPLIES = "replies";

    public static final String NEW_POST_SUBSCRIPTION = "all";

    public static final String CREATE_STORY = "Create Story";
    public static final String POEM_HOLDER = "poem";
    public static final String STORY_HOLDER = "story";
    public static final String DEVOTION_HOLDER = "devotion";
    public static final String POST_KEY = "postKey";
    public static final String COMMENT_KEY = "commentKey";
    public static final String POST_TITLE = "postTitle";
    public static final String POST_TYPE = "postType";
    public static final String POST_CONTENT = "postContent";
    public static final String CHAPTER_ADDED = "chaptersAdded";

    //Poems
    public static final String POEM_KEY = "Poem";
    public static final String POEM_TITLE = "title";
    public static final String POEM = "poemText";

    //Spiritual
    public static final String DEVOTION_KEY = "Devotion";
    public static final String COMMENT_TEXT = "commentText";


    public static final String SUBSCRIPTIONS_KEY  = "Subscriptions";

    //Comments
    public static final String COMMENTS_KEY = "Comment";
    public static final String REPLIES_KEY = "Replies";
    public static final String DEVOTION_TITLE = "title";
    public static final String DEVOTION = "devotionText";

    public static final String STORY_KEY = "Stori";

    public static final String STORY_TITLE = "title";
    public static final String STORY_CATEGORY = "category";
    public static final String STORY_STATUS = "status";
    public static final String STORY_DESCRIPTION = "description";
    public static final String STORY_CHAPTERCOUNT = "chapters";
    public static final String STORY_REFID = "storyRefId";

    public static final String CHAPTER_KEY = "Chapter";
    public static final String CHAPTER = "Chapter";
    public static final String CHAPTER_TITLE = "chapterTitle";
    public static final String CHAPTER_CONTENT = "Content";

    public static final String IS_EDITED = "isPostEdited";

    // Name of Notification Channel for verbose notifications of background work
    public static final CharSequence VERBOSE_NOTIFICATION_CHANNEL_NAME =
            "Verbose WorkManager Notifications";
    public static String VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
            "Shows notifications whenever work starts";
    public static final CharSequence NOTIFICATION_TITLE = "Progress report";
    public static final String CHANNEL_ID = "VERBOSE_NOTIFICATION" ;
    public static final int NOTIFICATION_ID = 1;

    public static final String SENT_FROM = "\nSent from Penciled Thoughts:\n http://play.google.com/store/apps/details?id=com.techart.writersblock\n";

}
