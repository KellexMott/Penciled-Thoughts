package com.techart.writersblock.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import com.techart.writersblock.AuthorsProfileActivity;
import com.techart.writersblock.CommentActivity;
import com.techart.writersblock.LikesActivity;
import com.techart.writersblock.R;
import com.techart.writersblock.ScrollingActivity;
import com.techart.writersblock.ViewsActivity;
import com.techart.writersblock.models.Devotion;
import com.techart.writersblock.utils.Constants;
import com.techart.writersblock.utils.FireBaseUtils;
import com.techart.writersblock.utils.ImageUtils;
import com.techart.writersblock.utils.NumberUtils;
import com.techart.writersblock.utils.TimeUtils;
import com.techart.writersblock.viewholders.ArticleViewHolder;

/**
 * Devotion fragment
 */
public class Tab3Devotion extends Fragment {
    private RecyclerView mPoemList;
    private FirebaseRecyclerAdapter<Devotion,ArticleViewHolder> fireBaseRecyclerAdapter;
    private FirebaseAuth mAuth;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
    private boolean mProcessView = false;
    private boolean mProcessLike = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tabrecyclerviewer, container, false);
        mAuth = FirebaseAuth.getInstance();
        FireBaseUtils.mDatabaseLike.keepSynced(true);
        FireBaseUtils.mDatabaseDevotions.keepSynced(true);
        FireBaseUtils.mDatabaseViews.keepSynced(true);
        FireBaseUtils.mDatabaseComment.keepSynced(true);

        mPoemList = rootView.findViewById(R.id.poem_list);
        mPoemList.setHasFixedSize(true);

        recyclerViewLayoutManager = new GridLayoutManager(getContext(),2);

        mPoemList.setLayoutManager(recyclerViewLayoutManager);
        bindView();
        return rootView;
    }

    @Override
    public String toString()
    {
        return "Devotion";
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    /**
     * Binds view to recycler view
     */
    private void bindView()
    {
        fireBaseRecyclerAdapter = new FirebaseRecyclerAdapter<Devotion, ArticleViewHolder>(
                Devotion.class,R.layout.item_article,ArticleViewHolder.class, FireBaseUtils.mDatabaseDevotions) {
            @Override
            protected void populateViewHolder(ArticleViewHolder viewHolder, final Devotion model, int position) {
                final String post_key = getRef(position).getKey();
                FireBaseUtils.mDatabaseLike.child(post_key).keepSynced(true);
                viewHolder.post_title.setText(model.getTitle());
                viewHolder.setTint(getContext());
                viewHolder.post_author.setText(getString(R.string.article_author,model.getAuthor()));
                viewHolder.setIvImage(getContext(), ImageUtils.getDevotionUrl(NumberUtils.getModuleOfTen(position)));
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
                                    if (!dataSnapshot.hasChild(FireBaseUtils.getUiD()))
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
                        readPoemIntent.putExtra(Constants.POST_CONTENT, model.getDevotionText());
                        readPoemIntent.putExtra(Constants.POST_TITLE, model.getTitle());
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
                                    if (dataSnapshot.hasChild(FireBaseUtils.getUiD()))
                                    {
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

    public String getAuthor()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getDisplayName();
    }
}
