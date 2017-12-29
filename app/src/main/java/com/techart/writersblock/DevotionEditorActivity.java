package com.techart.writersblock;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class DevotionEditorActivity extends AppCompatActivity {

    private ProgressDialog mProgress;
    private FirebaseAuth mAuth;

    private String action;
    private EditText editor;
    private EditText title;
    private String noteFilter;
    private String oldText;
    private String oldTitle;

    private String newText;
    private String newTitle;

    private String devotionUrl;

    private Boolean isPostEdited;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mAuth = FirebaseAuth.getInstance();
        editor = (EditText) findViewById(R.id.editText);
        title = (EditText)findViewById(R.id.editTitle);
        mProgress = new ProgressDialog(this);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(WritersBlockContract.SpiritualEntry.CONTENT_ITEM_TYPE);
        //Checks if user is creating a new note or editing an existing one
        if (uri == null) {
            isPostEdited = false;
            devotionUrl = "null";
            oldTitle = "New";
            isPostEdited = false;
            action = Intent.ACTION_INSERT;
            setTitle(oldTitle);
            title.setHint("Devotion title");
            editor.setHint("Tap to write devotion");
            title.requestFocus();
        } else {
            action = Intent.ACTION_EDIT;
            noteFilter = WritersBlockContract.SpiritualEntry.SPIRITUAL_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri,
                    WritersBlockContract.SpiritualEntry.ALL_COLUMNS, noteFilter, null, null);
            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(WritersBlockContract.SpiritualEntry.SPIRITUAL_TEXT));
            oldTitle = cursor.getString(cursor.getColumnIndex(WritersBlockContract.SpiritualEntry.SPIRITUAL_TITLE));
            devotionUrl = cursor.getString(cursor.getColumnIndex(WritersBlockContract.SpiritualEntry.SPIRITUAL_FIREBASE_URL));

            setTitle("Editing " + oldTitle);
            editor.setText(oldText);
            title.setText(oldTitle);
            title.requestFocus();
        }
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
       newTitle = title.getText().toString().trim();
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_post:
                determineAction();
                break;
            case R.id.action_delete:
                deleteNote();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void determineAction()
    {
        switch (action)
        {
            case Intent.ACTION_INSERT: startPosting();
                break;
            case Intent.ACTION_EDIT: exitsDialog();
        }
    }

    private void updatePoem()
    {
        mProgress.setMessage("Updating devotion...");
        mProgress.show();

        Map<String,Object> values = new HashMap<>();
        values.put(Constants.DEVOTION,newText);
        values.put(Constants.DEVOTION_TITLE,newTitle);
        FireBaseUtils.mDatabasePoems.child(devotionUrl).updateChildren(values);
        mProgress.dismiss();
        Toast.makeText(getApplicationContext(),"Poem Updated", Toast.LENGTH_LONG).show();
        finishEditing();
    }


    private void deleteNote() {
        getContentResolver().delete(WritersBlockContract.SpiritualEntry.CONTENT_URI,
                noteFilter, null);
        Toast.makeText(this, getString(R.string.devotion_deleted),
                Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void finishEditing() {
       newText = editor.getText().toString().trim();
       newTitle = title.getText().toString().trim();
        switch (action) {
            case Intent.ACTION_INSERT:
                if (newText.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertNote();
                }
                break;
            case Intent.ACTION_EDIT:
                if (newText.length() == 0) {
                    deleteNote();
                }
                else if (oldText.equals(newText) && oldTitle.equals(newTitle)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateNote();
                }
        }
        finish();
    }

    private void updateNote() {
        ContentValues values = new ContentValues();
        values.put(WritersBlockContract.SpiritualEntry.SPIRITUAL_TITLE, newTitle);
        values.put(WritersBlockContract.SpiritualEntry.SPIRITUAL_TEXT, newText);
        values.put(WritersBlockContract.SpiritualEntry.SPIRITUAL_FIREBASE_URL, devotionUrl);
        getContentResolver().update(WritersBlockContract.SpiritualEntry.CONTENT_URI, values, noteFilter, null);
        Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertNote() {
        ContentValues values = new ContentValues();
        values.put(WritersBlockContract.SpiritualEntry.SPIRITUAL_TITLE, newTitle);
        values.put(WritersBlockContract.SpiritualEntry.SPIRITUAL_TEXT, newText);
        values.put(WritersBlockContract.SpiritualEntry.SPIRITUAL_FIREBASE_URL, devotionUrl);
        getContentResolver().insert(WritersBlockContract.SpiritualEntry.CONTENT_URI, values);
        setResult(RESULT_OK);
    }

    private void startPosting() {
        newText = editor.getText().toString().trim();
        newTitle = title.getText().toString().trim();
        if(!isEditorEmpty())
        {
            showErrorDialog("Error...! You have not written anything");
        }
        else if ( EditorUtils.isEmpty(this,newTitle, "title") && EditorUtils.validateMainText(this,editor.getLayout().getLineCount()))
        {
            postPoem();
        }
    }

    private void startUpdating() {
        newText = editor.getText().toString().trim();
        newTitle = title.getText().toString().trim();
        if(!isEditorEmpty())
        {
            showErrorDialog("Error...! You have not written anything");
        }
        else if ( EditorUtils.compareStrings(oldText,newText) || EditorUtils.compareStrings(oldTitle, newTitle))
        {
            updatePoem();
        }
        else
        {
            showErrorDialog("No changes detected");
        }
    }

    private boolean isEditorEmpty()
    {
        return TextUtils.isEmpty(newText) && !TextUtils.isEmpty(newTitle);
    }

    private void exitsDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Would you like to update existing post?")
                .setTitle("Devotion already exist");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                startUpdating();
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showErrorDialog(String errorMsg)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(errorMsg);
        builder.setPositiveButton("Stay in editor", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        })
                .setNegativeButton("Exit editor", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void postPoem()
    {
        mProgress.setMessage("Posting devotion...");
        mProgress.show();
        devotionUrl = FireBaseUtils.mDatabaseDevotions.push().getKey();
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.DEVOTION_TITLE,newTitle);
        values.put(Constants.DEVOTION,newText);
        values.put(Constants.IS_EDITED,isPostEdited);
        values.put(Constants.NUM_LIKES,0);
        values.put(Constants.NUM_COMMENTS,0);
        values.put(Constants.NUM_VIEWS,0);
        values.put(Constants.AUTHOR_URL,mAuth.getCurrentUser().getUid());
        values.put(Constants.POST_AUTHOR,getAuthor());
        values.put(Constants.TIME_CREATED,ServerValue.TIMESTAMP);
        FireBaseUtils.mDatabaseDevotions.child(devotionUrl).setValue(values);

        mProgress.dismiss();
        Toast.makeText(getApplicationContext(),"Devotion posted", Toast.LENGTH_LONG).show();
    }


    public String getAuthor()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getDisplayName();
    }

    @Override
    public void onBackPressed()
    {
        finishEditing();
        Toast.makeText(getApplicationContext(), oldTitle + " saved", Toast.LENGTH_LONG).show();
    }
}
