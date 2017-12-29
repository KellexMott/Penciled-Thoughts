package com.techart.writersblock;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.HashMap;
import java.util.Map;

public class AddChapterOnlineActivity extends AppCompatActivity {

    private ProgressDialog mProgress;
    private DatabaseReference mDatabaseChapters;
    private EditText editor;
    private EditText title;

    private String newText;
    private String newTitle;

    private String storyUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editor);
        editor = (EditText) findViewById(R.id.editText);
        title = (EditText)findViewById(R.id.editTitle);
        mProgress = new ProgressDialog(this);

        Intent intent = getIntent();
        storyUrl = intent.getStringExtra(Constants.STORY_REFID);
        setTitle("Adding Chapter");
        title.requestFocus();
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
        if (id == R.id.action_post) {
            startPosting();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void finishEditing() {
       newText = editor.getText().toString().trim();
       newTitle = title.getText().toString().trim();
        if (newText.length() == 0) {
            setResult(RESULT_CANCELED);
        } else
        {
            setResult(RESULT_OK);
        }
        finish();
    }

    private void startPosting() {
        newText = editor.getText().toString().trim();
        newTitle = title.getText().toString().trim();
        if (validate())
        {
            postStoryChapter();
        }
    }

    private boolean validate()
    {
        return EditorUtils.isEmpty(this,newTitle, "chapter title") &&
                EditorUtils.validateMainText(this,editor.getLayout().getLineCount());
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

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void postStoryChapter()
    {
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Posting ...");
        mProgress.show();
        mDatabaseChapters = FireBaseUtils.mDatabaseChapters.child(storyUrl);
        String  chapterUrl = mDatabaseChapters.push().getKey();
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.CHAPTER_CONTENT,newText);
        values.put(Constants.CHAPTER_TITLE,newTitle);
        mDatabaseChapters.child(chapterUrl).setValue(values);
        updateLibrary(storyUrl);
        mProgress.dismiss();
        Toast.makeText(getApplicationContext(),"Chapter Added", Toast.LENGTH_LONG).show();
        finishEditing();
    }

    public  void updateLibrary(String storyUrl) {
        FireBaseUtils.mDatabaseLibrary.child(FireBaseUtils.mAuth.getCurrentUser().getUid()).child(storyUrl).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Library library = mutableData.getValue(Library.class);
                if (library == null) {
                    return Transaction.success(mutableData);
                }
                library.setChaptersAdded(library.getChaptersAdded() + 1 );
                // Set value and report transaction success
                mutableData.setValue(library);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }
}
