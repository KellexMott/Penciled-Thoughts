package com.techart.writersblock;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles updates to exiting devotions using the postUrl
 */
public class PoemEditorOnlineActivity extends AppCompatActivity {

    private ProgressDialog mProgress;
    private FirebaseAuth mAuth;

    private EditText editor;
    private EditText title;
    private String oldText;
    private String oldTitle;

    private String newText;
    private String newTitle;

    private String poemUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mAuth = FirebaseAuth.getInstance();
        editor = (EditText) findViewById(R.id.editText);
        title = (EditText)findViewById(R.id.editTitle);
        mProgress = new ProgressDialog(this);

        Intent intent = getIntent();
        poemUrl = intent.getStringExtra(Constants.POST_KEY);
        oldText = intent.getStringExtra(Constants.POEM);
        oldTitle = intent.getStringExtra(Constants.POEM_TITLE);

        title.setText(oldTitle);
        editor.setText(oldText);
        title.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * Handles action bar item clicks
     * @param item item that has been clicked
     * @return true if it was handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       newText = editor.getText().toString().trim();
       newTitle = title.getText().toString().trim();
        int id = item.getItemId();
        switch (id) {
            case R.id.action_post:
                startUpdating();
                break;
            case R.id.action_delete:
                clearComponents();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updatePoem() {
        mProgress.setMessage("Updating devotion...");
        mProgress.show();
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.POEM,newText);
        values.put(Constants.POEM_TITLE,newTitle);
        FireBaseUtils.mDatabasePoems.child(poemUrl).updateChildren(values);
        mProgress.dismiss();
        Toast.makeText(getApplicationContext(),"Poem Updated", Toast.LENGTH_LONG).show();
        finish();
    }


    /**
     * Clears EditTexts
     */
    private void clearComponents() {
        title.setText("");
        editor.setText("");
    }

    private boolean validate(){
        return EditorUtils.isEmpty(this,newTitle,"poem title") &&
                EditorUtils.validateMainText(this,editor.getLineCount());
    }

    private void startUpdating() {
        newText = editor.getText().toString().trim();
        newTitle = title.getText().toString().trim();
        if(validate()) {
            updatePoem();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
