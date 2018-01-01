package com.techart.writersblock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class ProfileDevotionsListActivity extends AppCompatActivity {
    private RecyclerView mPoemList;
    private DatabaseReference mDatabaseDevotions;
    private DatabaseReference mDatabaseLike;
    private FirebaseRecyclerAdapter<Devotion,ArticleEditViewHolder> firebaseRecyclerAdapter;
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
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Devotion, ArticleEditViewHolder>(
                Devotion.class,R.layout.item_row_del,ArticleEditViewHolder.class, query) {
            @Override
            protected void populateViewHolder(ArticleEditViewHolder viewHolder, final Devotion model, int position) {
                final String post_key = getRef(position).getKey();
                postTitle = model.getTitle();
                postContent = model.getDevotionText();
                viewHolder.post_title.setText(model.getTitle());
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
                    viewHolder.tvTimeCreated.setText(time);
                }

                viewHolder.setLikeBtn(post_key);

                viewHolder.btEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent readIntent = new Intent(ProfileDevotionsListActivity.this,DevotionEditorOnlineActivity.class);
                        readIntent.putExtra(Constants.POST_KEY,post_key);
                        readIntent.putExtra(Constants.DEVOTION_TITLE,model.getTitle());
                        readIntent.putExtra(Constants.DEVOTION,model.getDevotionText());
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


    public static class ArticleEditViewHolder extends RecyclerView.ViewHolder
    {
        TextView post_title;
        TextView numLikes;
        TextView numComments;
        TextView tvNumViews;
        TextView tvTimeCreated;

        View mView;

        DatabaseReference mDatabaseLike;
        FirebaseAuth mAUth;

        Button btEdit;
        ImageButton btnLiked;
        ImageButton btnDelete;
        ImageButton btnComment;
        ImageButton btnViews;

        public ArticleEditViewHolder(View itemView) {
            super(itemView);
            post_title = (TextView)itemView.findViewById(R.id.post_title);
            btnLiked = (ImageButton)itemView.findViewById(R.id.likeBtn);
            btnDelete = (ImageButton)itemView.findViewById(R.id.im_del);
            btnComment = (ImageButton)itemView.findViewById(R.id.commentBtn);
            tvNumViews = (TextView) itemView.findViewById(R.id.tv_numviews);
            btnViews = (ImageButton)itemView.findViewById(R.id.bt_views);
            numLikes = (TextView) itemView.findViewById(R.id.tv_likes);
            numComments = (TextView) itemView.findViewById(R.id.tv_comments);

            tvTimeCreated = (TextView) itemView.findViewById(R.id.tvTime);
            btEdit = (Button) itemView.findViewById(R.id.bt_edit);

            this.mView = itemView;
            mDatabaseLike =FireBaseUtils.mDatabaseLike;
            mAUth = FirebaseAuth.getInstance();
            mDatabaseLike.keepSynced(true);
        }

        protected void setLikeBtn(String post_key) {
            FireBaseUtils.setLikeBtn(post_key,btnLiked);
        }
    }

}

