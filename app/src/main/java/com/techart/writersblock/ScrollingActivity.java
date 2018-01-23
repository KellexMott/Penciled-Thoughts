package com.techart.writersblock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Presents the view for reading items
 */
public class ScrollingActivity extends AppCompatActivity {
    TextView tvPoem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        String postKey = getIntent().getStringExtra(Constants.POST_KEY);
        String postTitle = getIntent().getStringExtra(Constants.POST_TITLE);
        String postContent = getIntent().getStringExtra(Constants.POST_CONTENT);
        setTitle(postTitle);
        FirebaseMessaging.getInstance().subscribeToTopic(postKey);
        tvPoem = (TextView)findViewById(R.id.tvPoem);
        tvPoem.setTypeface(EditorUtils.getTypeFace(this));
        tvPoem.setText(postContent);
    }

    /**
     * Handles action bar item clicks.
     * @param item item clicked by user
     * @return returns true if item exits
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handles action bar item clicks here.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }
}
