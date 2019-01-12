package com.techart.wb.devotion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.techart.wb.CommentActivity;
import com.techart.wb.LikesActivity;
import com.techart.wb.R;
import com.techart.wb.constants.Constants;
import com.techart.wb.constants.FireBaseUtils;
import com.techart.wb.models.Devotion;
import com.techart.wb.utils.TimeUtils;
import com.techart.wb.viewholders.ArticleEditViewHolder;


public class ProfileDevotionsListActivity extends AppCompatActivity {
    private RecyclerView mPoemList;
    private String postTitle;
    private String postContent;
    private String author;
    private boolean mProcessLike = false;
    private static final int EDITOR_REQUEST_CODE = 1001;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabrecyclerviewer);
        author = FireBaseUtils.getAuthor();
        setTitle("Devotions");
        FireBaseUtils.mDatabaseLike.keepSynced(true);
        FireBaseUtils.mDatabaseDevotions.keepSynced(true);

        mPoemList = findViewById(R.id.poem_list);
        mPoemList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProfileDevotionsListActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mPoemList.setLayoutManager(linearLayoutManager);
        bindView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_storylist,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add:
                Intent editorIntent = new Intent(ProfileDevotionsListActivity.this, DevotionEditorActivity.class);
                startActivityForResult(editorIntent, EDITOR_REQUEST_CODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void bindView()
    {
        Query query = FireBaseUtils.mDatabaseDevotions.orderByChild(Constants.POST_AUTHOR).equalTo(author);
        FirebaseRecyclerAdapter<Devotion, ArticleEditViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Devotion, ArticleEditViewHolder>(
                Devotion.class, R.layout.item_row_del, ArticleEditViewHolder.class, query) {
            @Override
            protected void populateViewHolder(ArticleEditViewHolder viewHolder, final Devotion model, int position) {
                final String post_key = getRef(position).getKey();
                postTitle = model.getTitle();
                postContent = model.getDevotionText();
                viewHolder.post_title.setText(model.getTitle());
                if (model.getNumLikes() != null) {
                    viewHolder.numLikes.setText(model.getNumLikes().toString());
                }
                if (model.getNumLikes() != null) {
                    viewHolder.numComments.setText(model.getNumComments().toString());
                }
                if (model.getTimeCreated() != null) {
                    String time = TimeUtils.timeElapsed(TimeUtils.currentTime() - model.getTimeCreated());
                    viewHolder.tvTimeCreated.setText(time);
                }

                viewHolder.setLikeBtn(post_key);

                viewHolder.btEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent readIntent = new Intent(ProfileDevotionsListActivity.this, DevotionEditorOnlineActivity.class);
                        readIntent.putExtra(Constants.POST_KEY, post_key);
                        readIntent.putExtra(Constants.DEVOTION_TITLE, model.getTitle());
                        readIntent.putExtra(Constants.DEVOTION, model.getDevotionText());
                        startActivity(readIntent);
                    }
                });


                viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FireBaseUtils.deleteDevotion(post_key);
                        FireBaseUtils.deleteComment(post_key);
                        FireBaseUtils.deleteLike(post_key);
                    }
                });

                viewHolder.btnLiked.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProcessLike = true;
                        FireBaseUtils.mDatabaseLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mProcessLike) {
                                    if (dataSnapshot.child(post_key).hasChild(Constants.AUTHOR_URL)) {
                                        FireBaseUtils.mDatabaseLike.child(post_key).child(FireBaseUtils.getUiD()).removeValue();
                                        FireBaseUtils.onDevotionDisliked(post_key);
                                        mProcessLike = false;
                                    } else {
                                        FireBaseUtils.addDevotionLike(model, post_key);
                                        mProcessLike = false;
                                        FireBaseUtils.onDevotionLiked(post_key);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
                viewHolder.numLikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent likedPostsIntent = new Intent(ProfileDevotionsListActivity.this, LikesActivity.class);
                        likedPostsIntent.putExtra(Constants.POST_KEY, post_key);
                        startActivity(likedPostsIntent);
                    }
                });

                viewHolder.btnComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentIntent = new Intent(ProfileDevotionsListActivity.this, CommentActivity.class);
                        commentIntent.putExtra(Constants.POST_KEY, post_key);
                        commentIntent.putExtra(Constants.POST_TITLE, model.getTitle());
                        commentIntent.putExtra(Constants.POST_TYPE, Constants.DEVOTION_HOLDER);
                        startActivity(commentIntent);
                    }
                });
            }
        };
        mPoemList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }
}

