package com.techart.writersblock;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.techart.writersblock.models.Chapter;
import com.techart.writersblock.models.Library;
import com.techart.writersblock.utils.Constants;
import com.techart.writersblock.utils.FireBaseUtils;
import com.techart.writersblock.utils.NumberUtils;

import java.util.ArrayList;


public class LibraryActivity extends AppCompatActivity {
    private RecyclerView rvReadingList;
    private ArrayList<String> contents;
    private ArrayList<String> chapterTitles;
    private int pageCount;
    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_activity);
        setTitle(FireBaseUtils.getAuthor());
        FireBaseUtils.mDatabaseLibrary.child(FireBaseUtils.getUiD()).keepSynced(true);
        rvReadingList = findViewById(R.id.rv_libraryBook);
        rvReadingList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(LibraryActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvReadingList.setLayoutManager(linearLayoutManager);
        bindView();
    }

    private void bindView() {

        FirebaseRecyclerAdapter<Library,LibraryViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Library, LibraryViewHolder>(
                Library.class,R.layout.items_library,LibraryViewHolder.class, FireBaseUtils.mDatabaseLibrary.child(FireBaseUtils.getUiD()))
        {
            @Override
            protected void populateViewHolder(LibraryViewHolder viewHolder, final Library model, int position){
                final String post_key = model.getPostKey();
                viewHolder.tvTitle.setText(model.getPostTitle());
                mPref = getSharedPreferences(String.format("%s",getString(R.string.app_name)),MODE_PRIVATE);
                int lastAccessedPage = mPref.getInt(post_key,-1);
                int pageCount = mPref.getInt(post_key+1,-1);
                if (lastAccessedPage != -1 && pageCount != -1){
                    viewHolder.tvTime.setText(getString(R.string.reading_progess, NumberUtils.setPlurality(lastAccessedPage,"chapter"), pageCount));
                }
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // onStoryOpened(post_key);
                        storyExists(post_key);
                    }
                });
                viewHolder.tvRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        storyDeleted(post_key,"Remove story from reading list?");
                    }
                });
            }
        };
        rvReadingList.setAdapter(firebaseRecyclerAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logOut();
        } else if (id == R.id.action_edit_name) {
            Intent readIntent = new Intent(LibraryActivity.this,EditNameDialog.class);
            startActivity(readIntent);
        }
        return super.onOptionsItemSelected(item);
    }


    private void storyExists(final String key) {
        FireBaseUtils.mDatabaseLibrary.child(FireBaseUtils.getUiD()).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(Constants.POST_KEY))
                {
                    loadChapters(key);
                }
                else
                {
                    storyDeleted(key,"Story was deleted by Author");
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

    private void storyDeleted(final String key, String msg )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                FireBaseUtils.deleteFromLib(key);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
    public void onBackPressed() {
        finish();
    }

    public static class LibraryViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvTitle;
        TextView tvTime;
        TextView tvRemove;
        View mView;

        public LibraryViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvTime =  itemView.findViewById(R.id.tv_timeAdded);
            tvRemove =  itemView.findViewById(R.id.tv_remove);
            this.mView = itemView;
        }
    }
}