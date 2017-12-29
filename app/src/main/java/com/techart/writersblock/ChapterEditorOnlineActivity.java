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

public class ChapterEditorOnlineActivity extends AppCompatActivity {

    private ProgressDialog mProgress;
    private String chapterUrl;
    private EditText editor;
    private EditText editorTitle;
    private String oldText;
    private String oldTitle;

    private String storyUrl;
    private String newText;
    private String newTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        editor = (EditText) findViewById(R.id.editText);
        editorTitle = (EditText) findViewById(R.id.editTitle);

        Intent intent = getIntent();
        storyUrl = intent.getStringExtra(Constants.STORY_REFID);
        chapterUrl = intent.getStringExtra(Constants.POST_KEY);
        oldText = intent.getStringExtra(Constants.CHAPTER_CONTENT);
        oldTitle = intent.getStringExtra(Constants.CHAPTER_TITLE);

        setTitle("Editing");
        editor.setText(oldText);
        editorTitle.setText(oldTitle);
        editorTitle.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_onlineeditor, menu);
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
            case R.id.action_update:
                if ( EditorUtils.isEmpty(this,newTitle, "chapter title") && EditorUtils.validateMainText(this,editor.getLayout().getLineCount()))
                {
                    startPosting();
                }
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
        if (!storyUrl.equals("null") && chapterUrl != null){
            postChapter();
        }
    }

    private void finishEditing() {
        newText = editor.getText().toString().trim();
        newTitle = editorTitle.getText().toString().trim();
        finish();
    }

    private void postChapter() {
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Posting ...");
        mProgress.show();
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.CHAPTER_CONTENT,newText);
        values.put(Constants.CHAPTER_TITLE,newTitle);
        FireBaseUtils.mDatabaseChapters.child(storyUrl).child(chapterUrl).updateChildren(values);
        mProgress.dismiss();
        Toast.makeText(getApplicationContext(),"Chapter successfully posted", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        finishEditing();
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }
}
