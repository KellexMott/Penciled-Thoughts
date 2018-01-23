package com.techart.writersblock;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class Tab1Poems extends Fragment {
    private RecyclerView mPoemList;
    private DatabaseReference mDatabasePoems;
    private DatabaseReference mDatabaseLike;
    private FirebaseAuth mAuth;

    RecyclerView.LayoutManager recyclerViewLayoutManager;

    private boolean mProcessView = false;

    private boolean mProcessLike = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tabrecyclerviewer, container, false);
        mDatabasePoems = FireBaseUtils.mDatabasePoems;
        mDatabaseLike = FireBaseUtils.mDatabaseLike;
        mAuth = FirebaseAuth.getInstance();
        mDatabaseLike.keepSynced(true);
        mDatabasePoems.keepSynced(true);

        mPoemList = (RecyclerView) rootView.findViewById(R.id.poem_list);
        mPoemList.setHasFixedSize(true);

        recyclerViewLayoutManager = new GridLayoutManager(getContext(),2);
        mPoemList.setLayoutManager(recyclerViewLayoutManager);
        bindView(mPoemList);
        return rootView;
    }

    @Override
    public String toString()
    {
        return "Poems";
    }

    private void bindView(RecyclerView mPoemList)
    {
        FirebaseRecyclerAdapter<Poem,PoemViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Poem, PoemViewHolder>(
                Poem.class,R.layout.item_row,PoemViewHolder.class, mDatabasePoems)
        {
            @Override
            protected void populateViewHolder(PoemViewHolder viewHolder, final Poem model, int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.post_title.setText(model.getTitle());

                viewHolder.post_author.setText(getString(R.string.article_author,model.getAuthor()));
                viewHolder.setIvImage(getContext(),ImageUtils.getPoemUrl(NumberUtils.getModuleOfTen(position)));
                viewHolder.setTypeFace(getContext());
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
                    viewHolder.tvNumViews.setText(getString(R.string.viewers,count));
                }
                if (model.getTimeCreated() != null)
                {
                    String time = TimeUtils.timeElapsed(model.getTimeCreated());
                    viewHolder.timeTextView.setText(time);
                }
                viewHolder.setLikeBtn(post_key);
                viewHolder.setPostViewed(post_key);

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
                                        FireBaseUtils.addPoemView(model,post_key);
                                        mProcessView = false;
                                        FireBaseUtils.onPoemViewed(post_key);
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        Intent readPoemIntent = new Intent(getContext(),ScrollingActivity.class);
                        readPoemIntent.putExtra(Constants.POST_CONTENT, model.getPoemText());
                        readPoemIntent.putExtra(Constants.POST_TITLE, model.getTitle());
                        readPoemIntent.putExtra(Constants.POST_KEY, post_key);
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
                        mDatabaseLike.child(post_key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mProcessLike) {
                                    if (dataSnapshot.hasChild(FireBaseUtils.mAuth.getCurrentUser().getUid()))
                                    {
                                        mDatabaseLike.child(post_key).child(FireBaseUtils.mAuth.getCurrentUser().getUid()).removeValue();
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
                        commentIntent.putExtra(Constants.POST_TYPE,Constants.POEM_HOLDER);

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

        mPoemList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    public String getAuthor()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getDisplayName();
    }

    public static class PoemViewHolder extends RecyclerView.ViewHolder
    {
        TextView post_title;
        TextView numLikes;
        TextView numComments;
        TextView tvNumViews;
        ImageView ivArticle;

        TextView timeTextView;
        View mView;

        DatabaseReference mDatabaseLike;
        FirebaseAuth mAUth;

        TextView post_author;
        ImageButton btnLiked;
        ImageButton btnDelete;
        ImageButton btnComment;
        ImageButton btnViews;

        public PoemViewHolder(View itemView) {
            super(itemView);
            post_title = (TextView)itemView.findViewById(R.id.post_title);
            post_author = (TextView)itemView.findViewById(R.id.post_author) ;

            ivArticle = (ImageView)itemView.findViewById(R.id.iv_news);

            timeTextView = (TextView) itemView.findViewById(R.id.tvTime);
            btnLiked = (ImageButton)itemView.findViewById(R.id.likeBtn);
            btnDelete = (ImageButton)itemView.findViewById(R.id.im_del);
            btnComment = (ImageButton)itemView.findViewById(R.id.commentBtn);
            tvNumViews = (TextView) itemView.findViewById(R.id.tv_numviews);
            btnViews = (ImageButton)itemView.findViewById(R.id.bt_views);
            numLikes = (TextView) itemView.findViewById(R.id.tv_likes);
            numComments = (TextView) itemView.findViewById(R.id.tv_comments);

            this.mView = itemView;
            mDatabaseLike =FireBaseUtils.mDatabaseLike;
            mAUth = FirebaseAuth.getInstance();
            mDatabaseLike.keepSynced(true);
        }


        protected void setTypeFace(Context context) {
            Typeface typeface = EditorUtils.getTypeFace(context);
            post_author.setTypeface(typeface);
            post_title.setTypeface(typeface);
            timeTextView.setTypeface(typeface);
            tvNumViews.setTypeface(typeface);
        }

        public void setIvImage(Context context, int image)
        {
            Glide.with(context)
                    .load(image)
                    .into(ivArticle);
        }
        protected void setLikeBtn(String post_key) {
            FireBaseUtils.setLikeBtn(post_key,btnLiked);
        }
        public void setPostViewed(String post_key) {
            FireBaseUtils.setPostViewed(post_key,btnViews);
        }
    }
}
