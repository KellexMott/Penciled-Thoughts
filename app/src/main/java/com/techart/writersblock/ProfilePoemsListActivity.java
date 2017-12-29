package com.techart.writersblock;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class ProfilePoemsListActivity extends AppCompatActivity {
    private RecyclerView mPoemList;
    private DatabaseReference mDatabasePoems;
    private DatabaseReference mDatabaseLike;
    String author;
    private String postTitle;
    private String postContent;

    private boolean mProcessLike = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabrecyclerviewer);

        author = FireBaseUtils.getAuthor();
        setTitle("Poems");
        mDatabasePoems = FireBaseUtils.mDatabasePoems;
        mDatabaseLike = FireBaseUtils.mDatabaseLike;
        mDatabaseLike.keepSynced(true);
        mDatabasePoems.keepSynced(true);

        mPoemList = (RecyclerView) findViewById(R.id.poem_list);
        mPoemList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProfilePoemsListActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mPoemList.setLayoutManager(linearLayoutManager);
        bindView();
    }

    private void bindView()
    {
        Query query = mDatabasePoems.orderByChild(Constants.POST_AUTHOR).equalTo(author);
        FirebaseRecyclerAdapter<Poem,Tab1Poems.PoemViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Poem, Tab1Poems.PoemViewHolder>(
                Poem.class,R.layout.item_row_del,Tab1Poems.PoemViewHolder.class, query)
        {
            @Override
            protected void populateViewHolder(Tab1Poems.PoemViewHolder viewHolder, final Poem model, int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.post_title.setText(model.getTitle());
                viewHolder.post_author.setText("By " + model.getAuthor());
                viewHolder.poemText.setText(model.getPoemText());
                if (model.getNumLikes() != null)
                {
                    viewHolder.numLikes.setText(model.getNumLikes().toString());
                }
                if (model.getNumComments() != null)
                {
                    viewHolder.numComments.setText(model.getNumComments().toString());
                }
                if (model.getTimeCreated() != null)
                {
                    String time = TimeUtils.timeElapsed(TimeUtils.currentTime() - model.getTimeCreated());
                    viewHolder.timeTextView.setText(time);
                }
                viewHolder.setLikeBtn(post_key);
                postContent = model.getPoemText();
                postTitle = model.getTitle();

                viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FireBaseUtils.deletePoem(post_key);
                        FireBaseUtils.deleteComment(post_key);
                        FireBaseUtils.deleteLike(post_key);
                    }
                });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent readPoemIntent = new Intent(ProfilePoemsListActivity.this,ScrollingActivity.class);
                        readPoemIntent.putExtra(Constants.POST_CONTENT, postContent);
                        readPoemIntent.putExtra(Constants.POST_TITLE, postTitle);
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
                                        FireBaseUtils.onPoemDisliked(post_key);
                                        mProcessLike = false;
                                    } else {
                                        FireBaseUtils.addPoemLike(model, post_key);
                                        mProcessLike = false;
                                        FireBaseUtils.onPoemLiked(post_key);
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
                        Intent likedPostsIntent = new Intent(ProfilePoemsListActivity.this,LikesActivity.class);
                        likedPostsIntent.putExtra(Constants.POST_KEY,post_key);
                        startActivity(likedPostsIntent);
                    }
                });
                viewHolder.btnComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentIntent = new Intent(ProfilePoemsListActivity.this,CommentActivity.class);
                        commentIntent.putExtra(Constants.POST_KEY,post_key);
                        commentIntent.putExtra(Constants.POST_TITLE,model.getTitle());
                        commentIntent.putExtra(Constants.POST_TYPE,Constants.POEM_HOLDER);

                        startActivity(commentIntent);
                    }
                });
            }
        };
        mPoemList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }
}


