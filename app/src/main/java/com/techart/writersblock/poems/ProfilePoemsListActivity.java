package com.techart.writersblock.poems;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.techart.writersblock.CommentActivity;
import com.techart.writersblock.LikesActivity;
import com.techart.writersblock.R;
import com.techart.writersblock.models.Poem;
import com.techart.writersblock.utils.Constants;
import com.techart.writersblock.utils.FireBaseUtils;
import com.techart.writersblock.utils.TimeUtils;
import com.techart.writersblock.viewholders.ArticleEditViewHolder;


public class ProfilePoemsListActivity extends AppCompatActivity {
    private RecyclerView mPoemList;
    private String author;
    private boolean mProcessLike = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabrecyclerviewer);

        author = FireBaseUtils.getAuthor();
        setTitle("Poems");
        FireBaseUtils.mDatabaseLike.keepSynced(true);
        FireBaseUtils.mDatabasePoems.keepSynced(true);

        mPoemList = findViewById(R.id.poem_list);
        mPoemList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProfilePoemsListActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mPoemList.setLayoutManager(linearLayoutManager);
        bindView();
    }

    private void bindView()
    {
        Query query = FireBaseUtils.mDatabasePoems.orderByChild(Constants.POST_AUTHOR).equalTo(author);
        FirebaseRecyclerAdapter<Poem,ArticleEditViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Poem, ArticleEditViewHolder>(
                Poem.class,R.layout.item_row_del,ArticleEditViewHolder.class, query)
        {
            @Override
            protected void populateViewHolder(ArticleEditViewHolder viewHolder, final Poem model, int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.post_title.setText(model.getTitle());
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
                    viewHolder.tvTimeCreated.setText(time);
                }
                viewHolder.setLikeBtn(post_key);

                viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FireBaseUtils.deletePoem(post_key);
                        FireBaseUtils.deleteComment(post_key);
                        FireBaseUtils.deleteLike(post_key);
                    }
                });

                viewHolder.btEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent readIntent = new Intent(ProfilePoemsListActivity.this,PoemEditorOnlineActivity.class);
                        readIntent.putExtra(Constants.POST_KEY,post_key);
                        readIntent.putExtra(Constants.POEM_TITLE,model.getTitle());
                        readIntent.putExtra(Constants.POEM,model.getPoemText());
                        startActivity(readIntent);
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


