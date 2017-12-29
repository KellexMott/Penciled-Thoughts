package com.techart.writersblock;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class ChapterEditorActivity extends AppCompatActivity {

    private ProgressDialog mProgress;
    private DatabaseReference mDatabaseChapters;
    private String chapterUrl;
    private EditText editor;
    private EditText editorTitle;
    private String pageFilter;
    private String oldText;
    private String oldTitle;
    private FirebaseAuth mAuth;
    private String storyFilter;
    private String status = "Ongoing";
    private String storyUrl;
    private String id;
    private String newText;
    private String newTitle;
    private Cursor cursor;
    private Uri uri;
    private String storyTitle;
    private String storyDescription;
    private String storyCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        editor = (EditText) findViewById(R.id.editText);
        editorTitle = (EditText) findViewById(R.id.editTitle);
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        uri = intent.getParcelableExtra(WritersBlockContract.ChapterEntry.CONTENT_ITEM_TYPE);

        pageFilter = WritersBlockContract.ChapterEntry.CHAPTER_ID + "=" + uri.getLastPathSegment();
        cursor = getContentResolver().query(uri,
                WritersBlockContract.ChapterEntry.CHAPTER_COLUMNS, pageFilter, null, null);
        cursor.moveToFirst();
        oldText = cursor.getString(cursor.getColumnIndex(WritersBlockContract.ChapterEntry.CHAPTER_CONTENT));
        oldTitle = cursor.getString(cursor.getColumnIndex(WritersBlockContract.ChapterEntry.CHAPTER_TITLE));
        chapterUrl = cursor.getString(cursor.getColumnIndex(WritersBlockContract.ChapterEntry.CHAPTER_URL));
        storyUrl = cursor.getString(cursor.getColumnIndex(WritersBlockContract.ChapterEntry.CHAPTER_FIREBASE_STORY_URL));
        id = cursor.getString(cursor.getColumnIndex(WritersBlockContract.ChapterEntry.CHAPTER_STORY_ID));
        setTitle("Editing");
        editor.setText(oldText);
        editorTitle.setText(oldTitle);
        editorTitle.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        newText = editor.getText().toString().trim();
        newTitle = editorTitle.getText().toString().trim();
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_post:
                if ( EditorUtils.isEmpty(this,newTitle, "chapter title") && EditorUtils.validateMainText(this,editor.getLayout().getLineCount()))
                {
                    startPosting();
                }
                break;
            case R.id.action_delete:
                deleteChapter();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
        if() checks if the story was posted, if not queries for the url from the story and then posts chapter
        else if () checks if the story & chapter were posted & then updates the chapter
        else posts the chapter
     */
    private void startPosting() {
        if (storyUrl.equals("null")) {
            queryStoryData();
            postStory();
            updateStory();
            postChapter();
        }
        else if (!storyUrl.equals("null") && chapterUrl != null){
            updatePostedChapter();
        }else {
            postChapter();
        }
    }

    /*
        post the story to the server
     */
    private void postStory() {
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Posting ...");
        mProgress.show();
        DatabaseReference newPost = FireBaseUtils.mDatabaseStory.push();
        storyUrl = newPost.getKey();
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.STORY_TITLE,storyTitle);
        values.put(Constants.STORY_DESCRIPTION,storyDescription);
        values.put(Constants.STORY_CATEGORY,storyCategory);
        values.put(Constants.STORY_STATUS,status);
        values.put(Constants.STORY_CHAPTERCOUNT,0);
        values.put(Constants.NUM_LIKES,0);
        values.put(Constants.NUM_COMMENTS,0);
        values.put(Constants.NUM_VIEWS,0);
        values.put(Constants.AUTHOR_URL,mAuth.getCurrentUser().getUid());
        values.put(Constants.POST_AUTHOR,getAuthor());
        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);

        FireBaseUtils.mDatabaseStory.child(storyUrl).setValue(values);
        mProgress.dismiss();
        Toast.makeText(getApplicationContext(),"Story successfully posted", Toast.LENGTH_LONG).show();
    }


    /*
        Queries the story from the database
     */
    private void queryStoryData() {
        storyFilter = WritersBlockContract.StoryEntry.STORY_ID + "=" + id;
        cursor = getContentResolver().query(WritersBlockContract.StoryEntry.CONTENT_URI,
                WritersBlockContract.StoryEntry.STORY_COLUMNS, storyFilter, null, null);
        cursor.moveToFirst();
        storyTitle = cursor.getString(cursor.getColumnIndex(WritersBlockContract.StoryEntry.STORY_TITLE));
        storyCategory = cursor.getString(cursor.getColumnIndex(WritersBlockContract.StoryEntry.STORY_CATEGORY));
        storyDescription = cursor.getString(cursor.getColumnIndex(WritersBlockContract.StoryEntry.STORY_DESCRIPTION));
    }

    /*
        Deletes chapter
     */
    private void deleteChapter() {
        getContentResolver().delete(WritersBlockContract.StoryEntry.CONTENT_URI,
                pageFilter, null);
        Toast.makeText(this, getString(R.string.chapter_deleted),
                Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void finishEditing() {
        newText = editor.getText().toString().trim();
        newTitle = editorTitle.getText().toString().trim();
        updateChapter();
        finish();
    }

    /*
        Updates the story in the database
     */
    private void updateStory() {
        ContentValues values = new ContentValues();
        values.put(WritersBlockContract.StoryEntry.STORY_REFID,storyUrl);
        getContentResolver().update(WritersBlockContract.StoryEntry.CONTENT_URI, values, storyFilter, null);
        Toast.makeText(this, "Story Posted", Toast.LENGTH_SHORT).show();
    }

    /*
        updates chapter in the database
     */
    private void updateChapter() {
        ContentValues values = new ContentValues();
        values.put(WritersBlockContract.ChapterEntry.CHAPTER_TITLE, newTitle);
        values.put(WritersBlockContract.ChapterEntry.CHAPTER_CONTENT, newText);
        values.put(WritersBlockContract.ChapterEntry.CHAPTER_URL, chapterUrl);
        getContentResolver().update(WritersBlockContract.ChapterEntry.CONTENT_URI, values, pageFilter, null);
        Toast.makeText(this, getString(R.string.chapter_updated), Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void postChapter() {
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Posting ...");
        mProgress.show();
        mDatabaseChapters = FireBaseUtils.mDatabaseChapters.child(storyUrl);
        chapterUrl = mDatabaseChapters.push().getKey();
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.CHAPTER_CONTENT,newText);
        values.put(Constants.CHAPTER_TITLE,newTitle);
        mDatabaseChapters.child(chapterUrl).updateChildren(values);
        updateChapter();
        mProgress.dismiss();
        Toast.makeText(getApplicationContext(),"Chapter successfully posted", Toast.LENGTH_LONG).show();
    }

    private void updatePostedChapter() {
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Posting ...");
        mProgress.show();
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.CHAPTER_CONTENT,newText);
        values.put(Constants.CHAPTER_TITLE,newTitle);
        FireBaseUtils.mDatabaseChapters.child(storyUrl).child(chapterUrl).setValue(values);
        updateChapter();
        mProgress.dismiss();
        Toast.makeText(getApplicationContext(),"Chapter successfully updated", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        finishEditing();
    }

    public String getAuthor() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getDisplayName();
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }
}
