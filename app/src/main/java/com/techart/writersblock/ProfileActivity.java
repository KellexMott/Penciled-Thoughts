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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ProfileActivity extends AppCompatActivity
{
    TextView btSetPhoto;
    private ProgressDialog mProgress;

    private String title;
    private RelativeLayout mypoems;
    private RelativeLayout myspirituals;
    private RelativeLayout mystories;

    private RelativeLayout postedPoems;
    private RelativeLayout postedSpirituals;
    private RelativeLayout postedStories;

    private ImageButton imProfilePicture;

    static String author;
    private FirebaseAuth mAuth;

    String currentPhotoUrl;

    private String userName = "My Users";
    private ImageView ibProfile;

    private static final int GALLERY_REQUEST = 1;

    private BottomNavigationView bottomNavigationView;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
       // addBadge();
        author = FireBaseUtils.getAuthor();

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            userName = user.getDisplayName();
            //userName = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            String uid = user.getUid();
        }
        setTitle(userName);
        loadProfilePicture();
        btSetPhoto = (TextView)findViewById(R.id.tv_setImage);
        imProfilePicture = (ImageButton)findViewById(R.id.ib_profile);
        mypoems = (RelativeLayout)findViewById(R.id.mypoems);
        myspirituals = (RelativeLayout)findViewById(R.id.rv_myspirituals);
        mystories = (RelativeLayout)findViewById(R.id.rv_mystories);
       // ibProfile = (ImageView)findViewById(R.id.ibProfile);
        postedPoems = (RelativeLayout) findViewById(R.id.rv_postedpoems);
        postedSpirituals = (RelativeLayout) findViewById(R.id.rv_postedspirituals);
        postedStories = (RelativeLayout) findViewById(R.id.rv_postedstories);

        btSetPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePrompt();

            }
        });
        imProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(),"log out", Toast.LENGTH_LONG).show();
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

    private void loadProfilePicture()
    {
        FireBaseUtils.mDatabaseUsers.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                if (users.getImageUrl() != null && users.getImageUrl().length() > 7)
                {
                    currentPhotoUrl = users.getImageUrl();
                    setPicture(currentPhotoUrl);
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

    private void startPosting()
    {
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Posting ...");
        mProgress.show();
        StorageReference filePath = FireBaseUtils.mStoragePhotos.child(uri.getLastPathSegment());
        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String downloadImageUrl = taskSnapshot.getDownloadUrl().toString();
                setPhoto(downloadImageUrl);
                btSetPhoto.setVisibility(View.INVISIBLE);
                mProgress.dismiss();
            }

        });
    }

    private void setPicture(String image)
    {
        ImageButton postImage = (ImageButton) findViewById(R.id.ib_profile);
        Picasso.with(getApplicationContext()).load(image).into(postImage);
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
                            startPosting();
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
    private void setPhoto(String downloadImageUrl)
    {
        String user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference current_user_db = FireBaseUtils.mDatabaseUsers.child(user_id);
        current_user_db.child("imageUrl").setValue(downloadImageUrl);
        loadProfilePicture();
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            if (data != null && data.getData() != null) {
                uri = data.getData();
                String realPath = ImageUtils.getRealPathFromUrl(this, uri);
                Uri uriFromPath = Uri.fromFile(new File(realPath));
                setImage(imProfilePicture,uriFromPath);
            }
        }
    }

    private void setImage(ImageButton image,Uri uriFromPath)
    {
        Picasso.with(this)
                .load(uriFromPath)
                .resize(300, 300)
                .centerCrop()
                .into(image);
        btSetPhoto.setVisibility(View.VISIBLE);
    }


    private void DeletePictureFromStorage()
    {
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentPhotoUrl);
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }


    private void logOut() {
        Toast.makeText(getApplicationContext(),"log out", Toast.LENGTH_LONG).show();
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
        builder.setMessage(getString(R.string.are_you_sure))
            .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
            .setNegativeButton(getString(android.R.string.no), dialogClickListener)
            .show();
    }
}
