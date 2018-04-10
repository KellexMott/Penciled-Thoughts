package com.techart.writersblock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.techart.writersblock.devotion.AuthorsDevotionsListActivity;
import com.techart.writersblock.models.Users;
import com.techart.writersblock.poems.AuthorsPoemsListActivity;
import com.techart.writersblock.stories.AuthorsStoriesListActivity;
import com.techart.writersblock.utils.Constants;
import com.techart.writersblock.utils.FireBaseUtils;


public class AuthorsProfileActivity extends AppCompatActivity
{
    private RelativeLayout postedPoems;
    private RelativeLayout postedSpirituals;
    private RelativeLayout postedStories;
    private FirebaseAuth mAuth;
    static String author;
    static String authorUrl;

    private ImageView imProfilePicture;
    private String currentPhotoUrl;
    private boolean isAttached;

    private ImageView ibProfile;
    private static final int EDITOR_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        author = getIntent().getStringExtra(Constants.POST_AUTHOR);
        authorUrl = getIntent().getStringExtra(Constants.AUTHOR_URL);
        setTitle(author);
        setContentView(R.layout.activity_author);
        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        loadProfilePicture();
        imProfilePicture = findViewById(R.id.ib_profile);

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
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttached = true;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttached = false;
    }

    private void loadProfilePicture(){
        FireBaseUtils.mDatabaseUsers.child(authorUrl).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                if (users.getImageUrl() != null && users.getImageUrl().length() > 7) {
                    currentPhotoUrl = users.getImageUrl();
                    setPicture(currentPhotoUrl);
                } else {
                    Toast.makeText(getBaseContext(),"No image found",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void setPicture(String url) {
        if (isAttached){
            Glide.with(this)
            .load(url)
            .centerCrop()
            .into(imProfilePicture);
            imProfilePicture.setColorFilter(ContextCompat.getColor(this, R.color.colorTint));
        }
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
