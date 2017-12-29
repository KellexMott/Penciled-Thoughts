package com.techart.writersblock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;


public class Tab3Devotion extends Fragment {
    private RecyclerView mPoemList;
    private FirebaseRecyclerAdapter<Devotion,Tab1Poems.PoemViewHolder> fireBaseRecyclerAdapter;
    private FirebaseAuth mAuth;
    private String postTitle;
    private String postContent;
    private boolean mProcessView = false;


    private boolean mProcessLike = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.tabrecyclerviewer, container, false);
        mAuth = FirebaseAuth.getInstance();
        FireBaseUtils.mDatabaseLike.keepSynced(true);
        FireBaseUtils.mDatabaseDevotions.keepSynced(true);

        mPoemList = (RecyclerView) rootView.findViewById(R.id.poem_list);
        mPoemList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mPoemList.setLayoutManager(linearLayoutManager);
        bindView();
        return rootView;
    }

    @Override
    public String toString()
    {
        String title="Devotion";
        return title;
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    private void bindView()
    {
        fireBaseRecyclerAdapter = new FirebaseRecyclerAdapter<Devotion, Tab1Poems.PoemViewHolder>(
                Devotion.class,R.layout.item_row,Tab1Poems.PoemViewHolder.class, FireBaseUtils.mDatabaseDevotions) {
            @Override
            protected void populateViewHolder(Tab1Poems.PoemViewHolder viewHolder, final Devotion model, int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.post_title.setText(model.getTitle());
                viewHolder.post_author.setText("By " + model.getAuthor());
                viewHolder.poemText.setText(model.getDevotionText());
                if (model.getNumLikes() != null)
                {
                    String count = NumberUtils.shortenDigit(model.getNumLikes());
                    viewHolder.numLikes.setText(count);
                }
                if (model.getNumComments() != null)
                {
                    String count = NumberUtils.shortenDigit(model.getNumComments());
                    viewHolder.numComments.setText(count);
                }
                if (model.getNumViews() != null)
                {
                    String count = NumberUtils.shortenDigit(model.getNumViews());
                    viewHolder.tvNumViews.setText(count);
                }
                if (model.getTimeCreated() != null)
                {
                    String time = com.techart.writersblock.TimeUtils.timeElapsed(currentTime() - model.getTimeCreated());
                    viewHolder.timeTextView.setText(time);
                }

                viewHolder.setLikeBtn(post_key);
                viewHolder.setPostViewed(post_key);
                postTitle = model.getTitle();
                postContent = model.getDevotionText();


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProcessView = true;
                        FireBaseUtils.mDatabaseViews.child(post_key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mProcessView) {
                                    if (!dataSnapshot.hasChild(FireBaseUtils.mAuth.getCurrentUser().getUid()))
                                    {
                                        FireBaseUtils.addDevotionView(model,post_key);
                                        mProcessView = false;
                                        FireBaseUtils.onDevotionViewed(post_key);
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        Intent readPoemIntent = new Intent(getContext(),ScrollingActivity.class);
                        readPoemIntent.putExtra(Constants.POST_CONTENT, postContent);
                        readPoemIntent.putExtra(Constants.POST_TITLE, postTitle);
                        startActivity(readPoemIntent);
                    }
                });

                viewHolder.post_author.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent readPoemIntent = new Intent(getContext(),AuthorsProfileActivity.class);
                        readPoemIntent.putExtra(Constants.POST_AUTHOR, model.getAuthor());
                        startActivity(readPoemIntent);
                    }
                });

                viewHolder.btnLiked.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProcessLike = true;
                        FireBaseUtils.mDatabaseLike.child(post_key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mProcessLike) {
                                    if (dataSnapshot.hasChild(FireBaseUtils.mAuth.getCurrentUser().getUid()))
                                    {
                                        FireBaseUtils.mDatabaseLike.child(post_key).child(FireBaseUtils.mAuth.getCurrentUser().getUid()).removeValue();
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
                        Intent likedPostsIntent = new Intent(getContext(),LikesActivity.class);
                        likedPostsIntent.putExtra(Constants.POST_KEY,post_key);
                        startActivity(likedPostsIntent);
                    }
                });

                viewHolder.btnComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentIntent = new Intent(getContext(),CommentActivity.class);
                        commentIntent.putExtra(Constants.POST_KEY,post_key);
                        commentIntent.putExtra(Constants.POST_TITLE,model.getTitle());
                        commentIntent.putExtra(Constants.POST_TYPE,Constants.DEVOTION_HOLDER);
                        startActivity(commentIntent);
                    }
                });

                viewHolder.btnViews.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent likedPostsIntent = new Intent(getContext(),ViewsActivity.class);
                        likedPostsIntent.putExtra(Constants.POST_KEY,post_key);
                        startActivity(likedPostsIntent);
                    }
                });
            }
        };
        mPoemList.setAdapter(fireBaseRecyclerAdapter);
        fireBaseRecyclerAdapter.notifyDataSetChanged();
    }

    private long currentTime()
    {
        Date date = new Date();
        return date.getTime();
    }

    public String getAuthor()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getDisplayName();
    }
}
