package com.techart.writersblock;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.techart.writersblock.devotion.MySpiritualsListActivity;
import com.techart.writersblock.devotion.ProfileDevotionsListActivity;
import com.techart.writersblock.models.Users;
import com.techart.writersblock.poems.MyPoemsListActivity;
import com.techart.writersblock.poems.ProfilePoemsListActivity;
import com.techart.writersblock.stories.MyStoriesListActivity;
import com.techart.writersblock.stories.ProfileStoriesListActivity;
import com.techart.writersblock.utils.FireBaseUtils;
import com.techart.writersblock.utils.ImageUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Displays users private content. Such as
 * 1. Posted items
 * 2. Locally stored Articles
 * 3. Action such as changing and setting of dps
 *
 */
public class ProfileActivity extends AppCompatActivity
{
    private TextView tvSetPhoto;
    private ProgressDialog mProgress;
    private RelativeLayout mypoems;
    private RelativeLayout myspirituals;
    private RelativeLayout mystories;

    private RelativeLayout postedPoems;
    private RelativeLayout postedSpirituals;
    private RelativeLayout postedStories;

    private ImageButton imProfilePicture;
    private static String author;
    private FirebaseAuth mAuth;
    private String currentPhotoUrl;
    private String userName;
    private boolean isAttached;

    private static final int GALLERY_REQUEST = 1;

    private BottomNavigationView bottomNavigationView;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        author = FireBaseUtils.getAuthor();

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            userName = user.getDisplayName();
        }
        setTitle(userName);
        loadProfilePicture();
        tvSetPhoto = findViewById(R.id.tv_setImage);
        imProfilePicture = findViewById(R.id.ib_profile);
        mypoems = findViewById(R.id.mypoems);
        myspirituals = findViewById(R.id.rv_myspirituals);
        mystories = findViewById(R.id.rv_mystories);
        postedPoems = findViewById(R.id.rv_postedpoems);
        postedSpirituals = findViewById(R.id.rv_postedspirituals);
        postedStories = findViewById(R.id.rv_postedstories);

        //Sets new DP buy first deleting existing one
        tvSetPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePrompt();
            }
        });
        //Handles on clicks which brings up a larger image than that displayed
        imProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo handle on image button clicks
            }
        });
        mypoems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MyPoemsListActivity.class);
                startActivity(intent);
            }
        });
        myspirituals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MySpiritualsListActivity.class);
                startActivity(intent);
            }
        });
        mystories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MyStoriesListActivity.class);
                startActivity(intent);
            }
        });

        postedPoems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfilePoemsListActivity.class);
                startActivity(intent);
            }
        });
        postedSpirituals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileDevotionsListActivity.class);
                startActivity(intent);
            }
        });

        postedStories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileStoriesListActivity.class);
                startActivity(intent);
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_home:
                                Intent ma = new Intent(ProfileActivity.this, MainActivity.class);
                                ma.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(ma);
                                break;
                            case R.id.navigation_create:
                                Intent dialogIntent = new Intent(ProfileActivity.this,  LibraryActivity.class);
                                startActivity(dialogIntent);
                                break;
                            case R.id.navigation_profile:
                                break;
                        }
                        return true;
                    }
                });
        //End bottom naviagtion


        //ToDO fully implement

        /*BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(1); // number of menu from left
        new QBadgeView(this).bindTarget(v)
                .setBadgeGravity(Gravity.CENTER)
                .setBadgeNumber(5);*/
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logOut();
        }else if (id == R.id.action_changedp)
        {
            Intent imageIntent = new Intent();
            imageIntent.setType("image/*");
            imageIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(imageIntent,GALLERY_REQUEST);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
    }

    private void loadProfilePicture(){
        FireBaseUtils.mDatabaseUsers.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                if (users.getImageUrl() != null && users.getImageUrl().length() > 7)
                {
                    currentPhotoUrl = users.getImageUrl();
                    setPicturePicture(currentPhotoUrl);
                }
                else
                {
                    Toast.makeText(getBaseContext(),"No image found",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setPicturePicture(String url)
    {
        if (isAttached){
            Glide.with(this)
                .load(url)
                .into(imProfilePicture);
        }
    }

    private void deletePrompt()
    {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE)
                        {
                            DeletePictureFromStorage();
                        }
                        if (button == DialogInterface.BUTTON_NEGATIVE)
                        {
                            dialog.dismiss();
                        }
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Current display picture will be permanently deleted")
                .setPositiveButton("Upload", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener)
                .show();
    }

    private void DeletePictureFromStorage()
    {
        if (currentPhotoUrl != null)
        {
            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentPhotoUrl);
            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    startPosting();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            });
        }else {
            startPosting();
        }
    }

    private void startPosting()
    {
        mProgress = new ProgressDialog(ProfileActivity.this);
        mProgress.setMessage("Uploading picture, please wait...");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
        StorageReference filePath = FireBaseUtils.mStoragePhotos.child(uri.getLastPathSegment());
        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            Toast.makeText(getApplicationContext(),"Profile picture changed successfully",Toast.LENGTH_LONG).show();
            currentPhotoUrl = taskSnapshot.getDownloadUrl().toString();
            Map<String,Object> values = new HashMap<>();
            values.put("imageUrl",taskSnapshot.getDownloadUrl().toString());
            FireBaseUtils.mDatabaseUsers.child(mAuth.getCurrentUser().getUid()).updateChildren(values);
            tvSetPhoto.setVisibility(View.INVISIBLE);
            mProgress.dismiss();
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null){
            uri = data.getData();
            if (uri != null){
                String realPath = ImageUtils.getRealPathFromUrl(this, uri);
                Uri uriFromPath = Uri.fromFile(new File(realPath));
                displaySelectedDp(imProfilePicture,uriFromPath);
            }
        }
    }

    private void displaySelectedDp(ImageButton image, Uri uriFromPath)
    {
        Picasso.with(this)
                .load(uriFromPath)
                .resize(300, 300)
                .centerCrop()
                .into(image);
        tvSetPhoto.setVisibility(View.VISIBLE);
    }

    private void logOut() {
        DialogInterface.OnClickListener dialogClickListener =
        new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
            if (button == DialogInterface.BUTTON_POSITIVE)
            {
                FirebaseAuth.getInstance().signOut();
            }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.are_you_sure))
            .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
            .setNegativeButton(getString(android.R.string.no), dialogClickListener)
            .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
