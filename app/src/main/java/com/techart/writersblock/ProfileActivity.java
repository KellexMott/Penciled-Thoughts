package com.techart.writersblock;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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

import static com.techart.writersblock.utils.ImageUtils.hasPermissions;
/**
 * Displays users private content. Such as
 * 1. Posted items
 * 2. Locally stored Articles
 * 3. Action such as changing and setting of dps
 */
public class ProfileActivity extends AppCompatActivity {
    private TextView tvSetPhoto;
    private ProgressDialog mProgress;
    private RelativeLayout mypoems;
    private RelativeLayout myspirituals;
    private RelativeLayout mystories;

    private RelativeLayout postedPoems;
    private RelativeLayout postedSpirituals;
    private RelativeLayout postedStories;

    private ImageButton imProfilePicture;
    private String currentPhotoUrl;
    private boolean isAttached;
    // GALLERY_REQUEST is a constant integer
    private static final int GALLERY_REQUEST = 1;
    // The request code used in ActivityCompat.requestPermissions()
    // and returned in the Activity's onRequestPermissionsResult()
    private int PERMISSION_ALL = 1;
    private String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private BottomNavigationView bottomNavigationView;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle(FireBaseUtils.getAuthor());
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
                setPhoto();
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
        getMenuInflater().inflate(R.menu.menu_writer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logOut();
        } else if (id == R.id.action_changedp) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                onGetPermission();
            }  else {
                Intent imageIntent = new Intent();
                imageIntent.setType("image/*");
                imageIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(imageIntent,GALLERY_REQUEST);
            }
        }  else if (id == R.id.action_edit_name) {
            Intent readIntent = new Intent(ProfileActivity.this,EditNameDialog.class);
            startActivity(readIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(23)
    private void onGetPermission() {
        // only for MarshMallow and newer versions
        if(!hasPermissions(this, PERMISSIONS)){
            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                onPermissionDenied();
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLERY_REQUEST);
        }
    }

    // Trigger gallery selection for a photo
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLERY_REQUEST);
        } else {
            //do something like displaying a message that he did not allow the app to access gallery and you wont be able to let him select from gallery
            onPermissionDenied();
        }
    }

    private void onPermissionDenied() {
        DialogInterface.OnClickListener dialogClickListener =
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int button) {
                    if (button == DialogInterface.BUTTON_POSITIVE) {
                        ActivityCompat.requestPermissions(ProfileActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }
                    if (button == DialogInterface.BUTTON_NEGATIVE) {
                        dialog.dismiss();
                    }
                }
            };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("YOU NEED TO ALLOW ACCESS TO MEDIA STORAGE")
            .setMessage("Without this permission you can not upload an image")
            .setPositiveButton("ALLOW", dialogClickListener)
            .setNegativeButton("DENY", dialogClickListener)
            .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
    }

    private void loadProfilePicture(){
        FireBaseUtils.mDatabaseUsers.child(FireBaseUtils.getUiD()).addValueEventListener(new ValueEventListener() {
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
                .into(imProfilePicture);
        }
    }

    private void setPicture(Uri url) {
        Glide.with(this)
            .load(url)
            .centerCrop()
            .into(imProfilePicture);
        tvSetPhoto.setVisibility(View.VISIBLE);
    }

    private void deletePrompt() {
        DialogInterface.OnClickListener dialogClickListener =
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int button) {
                    if (button == DialogInterface.BUTTON_POSITIVE) {
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
                    }
                    if (button == DialogInterface.BUTTON_NEGATIVE) {
                        dialog.dismiss();
                    }
                }
            };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Current display picture will be permanently deleted")
                .setPositiveButton("UPLOAD", dialogClickListener)
                .setNegativeButton("CANCEL", dialogClickListener)
                .show();
    }

    private void setPhoto() {
        if (currentPhotoUrl != null) {
            deletePrompt();
        }else {
            startPosting();
        }
    }

    @NonNull
    private void startPosting() {
        mProgress = new ProgressDialog(ProfileActivity.this);
        mProgress.setMessage("Uploading photo, please wait...");
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
            FireBaseUtils.mDatabaseUsers.child(FireBaseUtils.getUiD()).updateChildren(values);
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
                setPicture(uriFromPath);
            }
        }
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
}
