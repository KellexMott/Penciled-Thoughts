package com.techart.writersblock;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


/**
 * Created by Kelvin on 30/05/2017.
 */

public class WritersBlockProvider extends ContentProvider {


    // Constant to identify the requested operation

    private static final int POEMS = 100;
    private static final int POEM_ID = 101;
    private static final int STORIES = 200;
    private static final int STORY_ID = 201;
    private static final int CHAPTERS = 300;
    private static final int CHAPTERS_ID = 301;
    private static final int SPIRITUALS = 400;
    private static final int SPIRITUALS_ID = 401;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private WritersBlockDBOpenHelper mOpenHelper;


    /**
     * Builds a UriMatcher that is used to determine witch database request is being made.
     */
    public static UriMatcher buildUriMatcher() {

       UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
       String content = WritersBlockContract.AUTHORITY;
        // All paths to the UriMatcher have a corresponding code to return
        // when a match is found (the ints above).
        matcher.addURI(content, WritersBlockContract.PATH_POEMS, POEMS);
        matcher.addURI(content, WritersBlockContract.PATH_POEMS + "/#", POEM_ID);
        matcher.addURI(content, WritersBlockContract.PATH_SPIRITUALS, SPIRITUALS);
        matcher.addURI(content, WritersBlockContract.PATH_SPIRITUALS + "/#", SPIRITUALS_ID);
        matcher.addURI(content, WritersBlockContract.PATH_STORIES, STORIES);
        matcher.addURI(content, WritersBlockContract.PATH_STORIES + "/#", STORY_ID);
        matcher.addURI(content, WritersBlockContract.PATH_CHAPTERS, CHAPTERS);
        matcher.addURI(content, WritersBlockContract.PATH_CHAPTERS + "/#", CHAPTERS_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper  = new WritersBlockDBOpenHelper(getContext());
        return true;
    }
    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch(match){
            case POEMS:
                return WritersBlockContract.PoemEntry.CONTENT_TYPE;
            case POEM_ID:
                return WritersBlockContract.PoemEntry.CONTENT_ITEM_TYPE;
            case SPIRITUALS:
                return WritersBlockContract.SpiritualEntry.CONTENT_TYPE;
            case SPIRITUALS_ID:
                return WritersBlockContract.SpiritualEntry.CONTENT_ITEM_TYPE;
            case STORIES:
                return WritersBlockContract.StoryEntry.CONTENT_TYPE;
            case STORY_ID:
                return WritersBlockContract.StoryEntry.CONTENT_ITEM_TYPE;
            case CHAPTERS:
                return WritersBlockContract.ChapterEntry.CONTENT_TYPE;
            case CHAPTERS_ID:
                return WritersBlockContract.ChapterEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri + "number " + match);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor retCursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case POEMS:
                retCursor = db.query(
                        WritersBlockContract.PoemEntry.TABLE_POEM,
                        projection,
                        selection,
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            case POEM_ID:
                retCursor = db.query(
                        WritersBlockContract.PoemEntry.TABLE_POEM,
                        projection,
                        selection,
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SPIRITUALS:
                retCursor = db.query(
                        WritersBlockContract.SpiritualEntry.TABLE_SPIRITUAL,
                        projection,
                        selection,
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            case SPIRITUALS_ID:
                retCursor = db.query(
                        WritersBlockContract.SpiritualEntry.TABLE_SPIRITUAL,
                        projection,
                        selection,
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            case STORIES:
                retCursor = db.query(
                        WritersBlockContract.StoryEntry.TABLE_STORIES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case STORY_ID:
                retCursor = db.query(
                        WritersBlockContract.StoryEntry.TABLE_STORIES,
                        projection,
                        selection,
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CHAPTERS:
                retCursor = db.query(
                        WritersBlockContract.ChapterEntry.TABLE_CHAPTER,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CHAPTERS_ID:
                retCursor = db.query(
                        WritersBlockContract.ChapterEntry.TABLE_CHAPTER,
                        projection,
                        selection,
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri + "number here " + match);
             /*   return database.query(DBOpenHelper.TABLE_POEM, DBOpenHelper.ALL_COLUMNS,
                        selection, null, null, null,
                        DBOpenHelper.POEM_CREATED + " DESC");*/
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long _id;
        Uri returnUri;

        switch(sUriMatcher.match(uri)){
            case POEMS:
                _id = db.insert(WritersBlockContract.PoemEntry.TABLE_POEM, null, values);
                if(_id > 0){
                    returnUri =  WritersBlockContract.PoemEntry.buildPoemUri(_id);
                } else{
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case SPIRITUALS:
                _id = db.insert(WritersBlockContract.SpiritualEntry.TABLE_SPIRITUAL, null, values);
                if(_id > 0){
                    returnUri =  WritersBlockContract.SpiritualEntry.buildPoemUri(_id);
                } else{
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case STORIES:
                _id = db.insert(WritersBlockContract.StoryEntry.TABLE_STORIES, null, values);
                if(_id > 0){
                    returnUri = WritersBlockContract.StoryEntry.buildStoryUri(_id);
                } else{
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case CHAPTERS:
                _id = db.insert(WritersBlockContract.ChapterEntry.TABLE_CHAPTER, null, values);
                if(_id > 0){
                    returnUri = WritersBlockContract.ChapterEntry.buildChapterUri(_id);
                } else{
                   throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Use this on the URI passed into the function to notify any observers that the uri has
        // changed.
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
        //return Uri.parse(PATH_POEMS + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rows; // Number of rows effected

        switch(sUriMatcher.match(uri)){
            case POEMS:
                rows = db.delete(WritersBlockContract.PoemEntry.TABLE_POEM,selection,selectionArgs);
                break;
            case SPIRITUALS:
                rows = db.delete(WritersBlockContract.SpiritualEntry.TABLE_SPIRITUAL,selection,selectionArgs);
                break;
            case STORIES:
                rows = db.delete(WritersBlockContract.StoryEntry.TABLE_STORIES,selection,selectionArgs);
                break;
            case CHAPTERS:
                rows = db.delete(WritersBlockContract.ChapterEntry.TABLE_CHAPTER, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because null could delete all rows:
        if(selection == null || rows != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rows;

        switch(sUriMatcher.match(uri)){
            case POEMS:
                rows = db.update(WritersBlockContract.PoemEntry.TABLE_POEM, values,selection,selectionArgs);
                break;
            case SPIRITUALS:
                rows = db.update(WritersBlockContract.SpiritualEntry.TABLE_SPIRITUAL, values,selection,selectionArgs);
                break;
            case STORIES:
                rows = db.update(WritersBlockContract.StoryEntry.TABLE_STORIES, values,selection,selectionArgs);
                break;
            case CHAPTERS:
                rows = db.update(WritersBlockContract.ChapterEntry.TABLE_CHAPTER, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rows != 0)
        {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rows;
    }
}
