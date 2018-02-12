package com.techart.writersblock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.techart.writersblock.utils.Constants;

/**
 * Presents the view for reading items
 */
public class ScrollingActivity extends AppCompatActivity {
    TextView tvPoem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        String postTitle = getIntent().getStringExtra(Constants.POST_TITLE);
        String postContent = getIntent().getStringExtra(Constants.POST_CONTENT);
        setTitle(postTitle);
        tvPoem = findViewById(R.id.tvPoem);
        tvPoem.setText(postContent);
    }
}
