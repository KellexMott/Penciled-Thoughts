package com.techart.writersblock;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.techart.writersblock.models.Chapter;
import com.techart.writersblock.models.Library;
import com.techart.writersblock.utils.Constants;
import com.techart.writersblock.utils.FireBaseUtils;

import java.util.ArrayList;


public class LibraryActivity extends AppCompatActivity {
    private ListView mPoemList;
    private ArrayList<String> contents;
    private ArrayList<String> chapterTitles;
    private int pageCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items_listview);
        setTitle("Shelf");
        FireBaseUtils.mDatabaseLibrary.child(FireBaseUtils.getUiD()).keepSynced(true);
        mPoemList = findViewById(R.id.lvItems);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(LibraryActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        bindView();
    }

    private void bindView()
    {
        FirebaseListAdapter<Library> fireBaseRecyclerAdapter = new FirebaseListAdapter<Library>(
               this,Library.class,R.layout.item_library, FireBaseUtils.mDatabaseLibrary.child(FireBaseUtils.getUiD())
        ) {
            @Override
            protected void populateView(View v, Library model, int position) {
                final String post_key = model.getPostKey();
                ((TextView)v.findViewById(R.id.tv_title)).setText(model.getPostTitle());
                 v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // onStoryOpened(post_key);
                        storyExists(post_key);
                    }
                });
            }
        };
        mPoemList.setAdapter(fireBaseRecyclerAdapter);
    }


    private void storyExists(final String key)
    {
        FireBaseUtils.mDatabaseLibrary.child(FireBaseUtils.getUiD()).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(Constants.POST_KEY))
                {
                    loadChapters(key);
                }
                else
                {
                    storyDeleted(key);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void loadChapters(final String key) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading chapters");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        FireBaseUtils.mDatabaseChapters.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    contents = new ArrayList<>();
                    chapterTitles = new ArrayList<>();
                    pageCount = ((int) dataSnapshot.getChildrenCount());
                    for (DataSnapshot chapterSnapShot: dataSnapshot.getChildren()) {
                        Chapter chapter = chapterSnapShot.getValue(Chapter.class);
                        contents.add(chapter.getContent());
                        chapterTitles.add(chapter.getChapterTitle());
                    }
                    if (contents.size() == pageCount) {
                        progressDialog.dismiss();
                        Intent readIntent = new Intent(LibraryActivity.this,ActivityReadStory.class);
                        readIntent.putStringArrayListExtra(Constants.POST_CONTENT,contents);
                        readIntent.putStringArrayListExtra(Constants.POST_TITLE,chapterTitles);
                        startActivity(readIntent);
                    }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void storyDeleted(final String key)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Story was deleted by Author");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                FireBaseUtils.deleteFromLib(key);
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}