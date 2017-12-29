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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class EditorActivity extends AppCompatActivity
{
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String action;
    private EditText editor;
    private EditText title;
    private String noteFilter;
    private String oldText;
    private String poemTitle;
    private String newText;
    private String newTitle;
    private String poemUrl;

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

        Uri uri = intent.getParcelableExtra(WritersBlockContract.PoemEntry.CONTENT_ITEM_TYPE);
        //Checks if user is creating a new note or editing an existing one
        if (uri == null) {
            action = Intent.ACTION_INSERT;
            isPostEdited = false;
            poemTitle = "New";
            poemUrl = "null";
            setTitle(poemTitle);
            title.setHint("Poem title");
            editor.setHint("Tap to write poem");
            title.requestFocus();
        } else {
            action = Intent.ACTION_EDIT;
            noteFilter = WritersBlockContract.PoemEntry.POEM_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri,
                    WritersBlockContract.PoemEntry.ALL_COLUMNS, noteFilter, null, null);
            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(WritersBlockContract.PoemEntry.POEM_TEXT));
            poemTitle = cursor.getString(cursor.getColumnIndex(WritersBlockContract.PoemEntry.POEM_TITLE));
            poemUrl = cursor.getString(cursor.getColumnIndex(WritersBlockContract.PoemEntry.POEM_FIREBASEURL));
            setTitle("Editing " + poemTitle);
            editor.setText(oldText);
            title.setText(poemTitle);
            title.requestFocus();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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
            case Intent.ACTION_EDIT: updatePoem();
        }
    }

    private void deleteNote()
    {
        getContentResolver().delete(WritersBlockContract.PoemEntry.CONTENT_URI,
                noteFilter, null);
        Toast.makeText(this, getString(R.string.poem_deleted),
                Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void finishEditing()
    {
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
                else if (oldText.equals(newText) && poemTitle.equals(newTitle)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateNote();
                }
        }
        finish();
    }

    private void updateNote()
    {
        ContentValues values = new ContentValues();
        values.put(WritersBlockContract.PoemEntry.POEM_TITLE, newTitle);
        values.put(WritersBlockContract.PoemEntry.POEM_TEXT, newText);
        values.put(WritersBlockContract.PoemEntry.POEM_FIREBASEURL, poemUrl);
        getContentResolver().update(WritersBlockContract.PoemEntry.CONTENT_URI, values, noteFilter, null);
        Toast.makeText(this, getString(R.string.poem_updated), Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertNote()
    {
        ContentValues values = new ContentValues();
        values.put(WritersBlockContract.PoemEntry.POEM_TITLE, newTitle);
        values.put(WritersBlockContract.PoemEntry.POEM_TEXT, newText);
        values.put(WritersBlockContract.PoemEntry.POEM_FIREBASEURL, poemUrl);
        getContentResolver().insert(WritersBlockContract.PoemEntry.CONTENT_URI, values);
        setResult(RESULT_OK);
    }

    private void startPosting()
    {
        newText = editor.getText().toString().trim();
        newTitle = title.getText().toString().trim();
        if ( EditorUtils.isEmpty(this,newTitle, "title") && EditorUtils.validateMainText(this,editor.getLayout().getLineCount()))
        {
            postPoem();
        }
    }

    private void updatePoem()
    {
        mProgress.setMessage("Updating poem...");
        mProgress.show();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.POEM_KEY);
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.POEM,newText);
        values.put(Constants.POEM_TITLE,newTitle);
        FireBaseUtils.mDatabasePoems.child(poemUrl).updateChildren(values);
        mProgress.dismiss();
        Toast.makeText(getApplicationContext(),"Poem posted", Toast.LENGTH_LONG).show();
        finishEditing();
    }


    private void postPoem()
    {
        mProgress.setMessage("Posting poem...");
        mProgress.show();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.POEM_KEY);
        poemUrl = mDatabase.push().getKey();

        mDatabase.child(poemUrl).child(Constants.POEM_TITLE).setValue(newTitle);
        mDatabase.child(poemUrl).child(Constants.POEM).setValue(newText);
        mDatabase.child(poemUrl).child(Constants.IS_EDITED).setValue(isPostEdited);
        mDatabase.child(poemUrl).child(Constants.NUM_LIKES).setValue(0);
        mDatabase.child(poemUrl).child(Constants.NUM_COMMENTS).setValue(0);
        mDatabase.child(poemUrl).child(Constants.NUM_VIEWS).setValue(0);
        mDatabase.child(poemUrl).child(Constants.AUTHOR_URL).setValue(mAuth.getCurrentUser().getUid());
        mDatabase.child(poemUrl).child(Constants.POST_AUTHOR).setValue(getAuthor());
        mDatabase.child(poemUrl).child(Constants.TIME_CREATED).setValue(ServerValue.TIMESTAMP);
        mProgress.dismiss();
        Toast.makeText(getApplicationContext(),"Poem posted", Toast.LENGTH_LONG).show();
        finishEditing();
    }


    public String getAuthor()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getDisplayName();
    }

    @Override
    public void onBackPressed() {
        finishEditing();
        Toast.makeText(getApplicationContext(),poemTitle + " saved", Toast.LENGTH_LONG).show();
    }
}
