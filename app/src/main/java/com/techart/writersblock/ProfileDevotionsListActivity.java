package com.techart.writersblock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class ProfileDevotionsListActivity extends AppCompatActivity {
    private RecyclerView mPoemList;
    private DatabaseReference mDatabaseDevotions;
    private DatabaseReference mDatabaseLike;
    private FirebaseRecyclerAdapter<Devotion,Tab1Poems.PoemViewHolder> firebaseRecyclerAdapter;
    private String postTitle;
    private String postContent;
    private String author;
    private boolean mProcessLike = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabrecyclerviewer);
        author = FireBaseUtils.getAuthor();
        setTitle("Devotions");
        mDatabaseDevotions =FireBaseUtils.mDatabaseDevotions;
        mDatabaseLike = FireBaseUtils.mDatabaseLike;
        mDatabaseLike.keepSynced(true);
        mDatabaseDevotions.keepSynced(true);

        mPoemList = (RecyclerView) findViewById(R.id.poem_list);
        mPoemList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProfileDevotionsListActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mPoemList.setLayoutManager(linearLayoutManager);
        bindView();
    }

    private void bindView()
    {
        Query query = mDatabaseDevotions.orderByChild(Constants.POST_AUTHOR).equalTo(author);
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Devotion, Tab1Poems.PoemViewHolder>(
                Devotion.class,R.layout.item_row_del,Tab1Poems.PoemViewHolder.class, query) {
            @Override
            protected void populateViewHolder(Tab1Poems.PoemViewHolder viewHolder, final Devotion model, int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.post_title.setText(model.getTitle());
                viewHolder.post_author.setText("By " + model.getAuthor());
                viewHolder.poemText.setText(model.getDevotionText());
                if (model.getNumLikes() != null)
                {
                    viewHolder.numLikes.setText(model.getNumLikes().toString());
                }
                if (model.getNumLikes() != null)
                {
                    viewHolder.numComments.setText(model.getNumComments().toString());
                }
                if (model.getTimeCreated() != null)
                {
                    String time = TimeUtils.timeElapsed(TimeUtils.currentTime() - model.getTimeCreated());
                    viewHolder.timeTextView.setText(time);
                }

                viewHolder.setLikeBtn(post_key);
                postTitle = model.getTitle();
                postContent = model.getDevotionText();


                viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FireBaseUtils.deleteDevotion(post_key);
                        FireBaseUtils.deleteComment(post_key);
                        FireBaseUtils.deleteLike(post_key);
                    }
                });
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent readPoemIntent = new Intent(ProfileDevotionsListActivity.this,ScrollingActivity.class);
                        readPoemIntent.putExtra(Constants.POST_CONTENT, postContent);
                        readPoemIntent.putExtra(Constants.POST_TITLE, postTitle);
                        startActivity(readPoemIntent);
                    }
                });

                viewHolder.post_author.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent readPoemIntent = new Intent(ProfileDevotionsListActivity.this,AuthorsProfileActivity.class);
                        readPoemIntent.putExtra(Constants.POST_AUTHOR, model.getAuthor());
                        startActivity(readPoemIntent);
                    }
                });

                viewHolder.btnLiked.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProcessLike = true;
                        mDatabaseLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mProcessLike) {
                                    if (dataSnapshot.child(post_key).hasChild(Constants.AUTHOR_URL)) {
                                        mDatabaseLike.child(post_key).removeValue();
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
                        Intent likedPostsIntent = new Intent(ProfileDevotionsListActivity.this,LikesActivity.class);
                        likedPostsIntent.putExtra(Constants.POST_KEY,post_key);
                        startActivity(likedPostsIntent);
                    }
                });

                viewHolder.btnComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentIntent = new Intent(ProfileDevotionsListActivity.this,CommentActivity.class);
                        commentIntent.putExtra(Constants.POST_KEY,post_key);
                        commentIntent.putExtra(Constants.POST_TITLE,model.getTitle());
                        commentIntent.putExtra(Constants.POST_TYPE,Constants.DEVOTION_HOLDER);
                        startActivity(commentIntent);
                    }
                });
            }
        };
        mPoemList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }
}

