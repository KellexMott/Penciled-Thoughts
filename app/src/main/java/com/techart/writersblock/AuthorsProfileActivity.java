package com.techart.writersblock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;


public class AuthorsProfileActivity extends AppCompatActivity
{
    private RelativeLayout postedPoems;
    private RelativeLayout postedSpirituals;
    private RelativeLayout postedStories;
    private FirebaseAuth mAuth;
    static String author;

    private ImageView ibProfile;
    private static final int EDITOR_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        author = getIntent().getStringExtra(Constants.POST_AUTHOR);
        setTitle(author);
        setContentView(R.layout.activity_author);
        mAuth = FirebaseAuth.getInstance();

        postedPoems = findViewById(R.id.rv_postedpoems);
        postedSpirituals = findViewById(R.id.rv_postedspirituals);
        postedStories = findViewById(R.id.rv_postedstories);

        // ibProfile = (ImageView)findViewById(R.id.ibProfile);
        postedPoems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthorsProfileActivity.this, AuthorsPoemsListActivity.class);
                Bundle data = new Bundle();
                data.putString(Constants.POST_AUTHOR,author);
                intent.putExtras(data);
                startActivityForResult(intent,EDITOR_REQUEST_CODE);
            }
        });
        postedSpirituals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthorsProfileActivity.this, AuthorsDevotionsListActivity.class);
                intent.putExtra(Constants.POST_AUTHOR,author);
                startActivity(intent);
            }
        });

        postedStories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthorsProfileActivity.this, AuthorsStoriesListActivity.class);
                intent.putExtra(Constants.POST_AUTHOR,author);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK)
        {
          author =  data.getStringExtra(Constants.POST_AUTHOR);
        }
    }
}
