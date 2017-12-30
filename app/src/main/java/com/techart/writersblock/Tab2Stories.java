package com.techart.writersblock;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Tab2Stories extends Fragment {
    private RecyclerView mStoryList;

    private DatabaseReference mDatabaseChapters;

    private boolean mProcessLike = false;
    private boolean mProcessView = false;
    private ArrayList<String> contents;
    private ArrayList<String> chapterTitles;
    private int pageCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tabrecyclerviewer, container, false);

        FireBaseUtils.mDatabaseLike.keepSynced(true);
        FireBaseUtils.mDatabaseStory.keepSynced(true);
        mStoryList = (RecyclerView) rootView.findViewById(R.id.poem_list);
        mStoryList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mStoryList.setLayoutManager(linearLayoutManager);
        bindView();
        return rootView;
    }

    private void bindView() {
        FirebaseRecyclerAdapter<Story,StoryViewHolder> fireBaseRecyclerAdapter = new FirebaseRecyclerAdapter<Story, StoryViewHolder>(
                Story.class,R.layout.item_storyrow,Tab2Stories.StoryViewHolder.class, FireBaseUtils.mDatabaseStory)
        {
            @Override
            protected void populateViewHolder(Tab2Stories.StoryViewHolder viewHolder, final Story model, int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.tvTitle.setText(model.getTitle());
                viewHolder.tvCategory.setText(model.getCategory());
                viewHolder.tvStatus.setText(model.getStatus());
                viewHolder.tvAuthor.setText(getString(R.string.post_author) + model.getAuthor());
                viewHolder.tvDescription.setText(model.getDescription());
                if (model.getNumLikes() != null)
                {
                    viewHolder.tvNumLikes.setText(model.getNumLikes().toString());
                }
                if (model.getNumComments() != null)
                {
                    viewHolder.tvNumComments.setText(model.getNumComments().toString());
                }if (model.getNumViews() != null)
                {
                    viewHolder.tvNumViews.setText(model.getNumViews().toString());
                }
                if (model.getTimeCreated() != null)
                {
                    String time = com.techart.writersblock.TimeUtils.timeElapsed(TimeUtils.currentTime() - model.getTimeCreated());
                    viewHolder.tvTime.setText(time);
                }

                viewHolder.tvAuthor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent readPoemIntent = new Intent(getContext(),AuthorsProfileActivity.class);
                        readPoemIntent.putExtra(Constants.POST_AUTHOR, model.getAuthor());
                        startActivity(readPoemIntent);
                    }
                });
                viewHolder.setLikeBtn(post_key);
                viewHolder.setPostViewed(post_key);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDescription(model.getDescription(),post_key,model);
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
                                        FireBaseUtils.mDatabaseLike.child(post_key).removeValue();
                                        FireBaseUtils.onStoryDisliked(post_key);
                                        mProcessLike = false;
                                    } else {
                                        FireBaseUtils.addStoryLike(model,post_key);
                                        mProcessLike = false;
                                        FireBaseUtils.onStoryLiked(post_key);
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                viewHolder.tvNumLikes.setOnClickListener(new View.OnClickListener() {
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
                        commentIntent.putExtra(Constants.POST_TYPE,Constants.STORY_HOLDER);
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
        mStoryList.setAdapter(fireBaseRecyclerAdapter);
        fireBaseRecyclerAdapter.notifyDataSetChanged();
    }

    private void addToViews(final String post_key, final Story model) {
        mProcessView = true;
        FireBaseUtils.mDatabaseViews.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mProcessView) {
                    if (!dataSnapshot.hasChild(FireBaseUtils.mAuth.getCurrentUser().getUid()))
                    {
                        FireBaseUtils.addStoryView(model,post_key);
                        mProcessView = false;
                        FireBaseUtils.onStoryViewed(post_key);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initializeChapters(String post_key, Story model) {
        mDatabaseChapters = FireBaseUtils.mDatabaseChapters.child(post_key);
        contents = new ArrayList<>();
        chapterTitles = new ArrayList<>();
        addToLibrary(model,post_key);
        loadChapters();
    }


    private void loadChapters()
    {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading chapters");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        mDatabaseChapters.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pageCount = ((int) dataSnapshot.getChildrenCount());
                for (DataSnapshot chapterSnapShot: dataSnapshot.getChildren())
                {
                    Chapter chapter = chapterSnapShot.getValue(Chapter.class);
                    contents.add(chapter.getContent());
                    chapterTitles.add(chapter.getChapterTitle());
                }
                if (contents.size() == pageCount) {
                    progressDialog.dismiss();
                    Intent readIntent = new Intent(getContext(),ActivityReadStory.class);
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

    private void addToLibrary(final Story model, final String post_key)
    {
        FireBaseUtils.mDatabaseLibrary.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(FireBaseUtils.mAuth.getCurrentUser().getUid()).hasChild(post_key))
                {
                    Map<String,Object> values = new HashMap<>();
                    values.put(Constants.POST_KEY,  post_key);
                    values.put(Constants.POST_TITLE, model.getTitle());
                    values.put(Constants.CHAPTER_ADDED, 0);
                    FireBaseUtils.mDatabaseLibrary.child(FireBaseUtils.mAuth.getCurrentUser().getUid()).child(post_key).setValue(values);
                    Toast.makeText(getContext(),model.getTitle() + " added to library",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public String toString() {
        return "Stories";
    }

    private void showDescription(String description, final String post_key, final Story model) {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE)
                        {
                            addToViews(post_key, model);
                            initializeChapters(post_key, model);
                        }
                        else
                        {
                            dialog.dismiss();
                        }
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(description)
                .setPositiveButton("Start Reading", dialogClickListener)
                .setNegativeButton("Back", dialogClickListener)
                .show();
    }


    public static class StoryViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvTitle;
        TextView tvAuthor;
        TextView tvDescription;
        TextView tvCategory;
        TextView tvStatus;
        TextView tvNumLikes;
        TextView tvNumComments;
        TextView tvNumViews;
        TextView tvTime;
        View mView;

        DatabaseReference mDatabaseLike;
        FirebaseAuth mAUth;

        ImageButton btnDelete;
        ImageButton btnLiked;
        ImageButton btnComment;
        ImageButton btnViews;

        public StoryViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView)itemView.findViewById(R.id.tv_title);
            tvAuthor = (TextView)itemView.findViewById(R.id.tv_author);
            tvStatus = (TextView)itemView.findViewById(R.id.tv_status);
            tvCategory = (TextView)itemView.findViewById(R.id.tv_category);
            tvDescription = (TextView)itemView.findViewById(R.id.tv_description);

            btnDelete = (ImageButton)itemView.findViewById(R.id.im_delete);
            btnLiked = (ImageButton)itemView.findViewById(R.id.likeBtn);
            btnComment = (ImageButton)itemView.findViewById(R.id.commentBtn);
            btnViews = (ImageButton)itemView.findViewById(R.id.bt_views);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvNumLikes = (TextView) itemView.findViewById(R.id.tv_numlikes);
            tvNumComments = (TextView) itemView.findViewById(R.id.tv_numcomments);
            tvNumViews = (TextView) itemView.findViewById(R.id.tv_numviews);
            this.mView = itemView;
            mDatabaseLike = FireBaseUtils.mDatabaseLike;
            mAUth = FirebaseAuth.getInstance();
            mDatabaseLike.keepSynced(true);
        }

        protected void setLikeBtn(String post_key) {
            FireBaseUtils.setLikeBtn(post_key,btnLiked);
        }
        private void setPostViewed(String post_key) {
            FireBaseUtils.setPostViewed(post_key,btnViews);
        }
    }
}

