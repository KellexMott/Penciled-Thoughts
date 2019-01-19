package com.techart.wb;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.techart.wb.constants.Constants;
import com.techart.wb.constants.FireBaseUtils;
import com.techart.wb.devotion.AuthorsDevotionsListActivity;
import com.techart.wb.models.Users;
import com.techart.wb.poems.AuthorsPoemsListActivity;
import com.techart.wb.stories.AuthorsStoriesListActivity;


public class AuthorsProfileActivity extends AppCompatActivity
{
    private static String author;
    private static String authorUrl;

    TextView tvOverlayBiography;
    TextView tvIndependentBiography;

    private ImageView imProfilePicture;
    private String currentPhotoUrl;
    private boolean isAttached;

    private static final int EDITOR_REQUEST_CODE = 1001;
    private TextView tvFaceBook;
    private TextView tvLinkedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        author = getIntent().getStringExtra(Constants.POST_AUTHOR);
        authorUrl = getIntent().getStringExtra(Constants.AUTHOR_URL);
        setTitle(author);
        setContentView(R.layout.activity_author);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        loadProfilePicture();
        imProfilePicture = findViewById(R.id.ib_profile);

        RelativeLayout postedPoems = findViewById(R.id.rv_postedpoems);
        RelativeLayout postedSpirituals = findViewById(R.id.rv_postedspirituals);
        RelativeLayout postedStories = findViewById(R.id.rv_postedstories);
        tvFaceBook = findViewById(R.id.tv_facebook);
        tvLinkedIn = findViewById(R.id.tv_linked);
        tvOverlayBiography = findViewById(R.id.tv_biography);
        tvIndependentBiography = findViewById(R.id.tv_bio);

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

        tvFaceBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AuthorsProfileActivity.this,"redirects to writers page",Toast.LENGTH_LONG).show();
               /* Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://web.facebook.com/TechArtZambia/");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);*/
            }
        });

        tvLinkedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AuthorsProfileActivity.this,"redirects to writers page",Toast.LENGTH_LONG).show();
               /* Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://www.linkedin.com/in/kelvin-chiwele-b36224167");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);*/
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
        if (authorUrl != null){
            FireBaseUtils.mDatabaseUsers.child(authorUrl).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Users users = dataSnapshot.getValue(Users.class);
                    if (users != null && users.getImageUrl() != null && users.getImageUrl().length() > 7) {
                        currentPhotoUrl = users.getImageUrl();
                        setPicture(currentPhotoUrl);
                    } else {
                        Toast.makeText(getBaseContext(),"No image found",Toast.LENGTH_LONG).show();
                    }
                    tvOverlayBiography.setVisibility(View.GONE);
                    if (users.getBiography() != null){
                        tvIndependentBiography.setText(users.getBiography());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else {
            Toast.makeText(getBaseContext(),"Could not load image",Toast.LENGTH_LONG).show();
        }
    }


    private void setPicture(String url) {
        if (isAttached){
            Glide.with(this)
            .load(url)
            //.centerCrop()
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