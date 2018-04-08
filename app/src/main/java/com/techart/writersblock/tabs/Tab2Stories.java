package com.techart.writersblock.tabs;

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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.techart.writersblock.ActivityReadStory;
import com.techart.writersblock.AuthorsProfileActivity;
import com.techart.writersblock.CommentActivity;
import com.techart.writersblock.LikesActivity;
import com.techart.writersblock.R;
import com.techart.writersblock.ViewsActivity;
import com.techart.writersblock.models.Chapter;
import com.techart.writersblock.models.Story;
import com.techart.writersblock.utils.Constants;
import com.techart.writersblock.utils.FireBaseUtils;
import com.techart.writersblock.utils.ImageUtils;
import com.techart.writersblock.utils.NumberUtils;
import com.techart.writersblock.utils.TimeUtils;
import com.techart.writersblock.viewholders.StoryViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class Tab2Stories extends Fragment {
    private RecyclerView mStoryList;

    private DatabaseReference mDatabaseChapters;

    private boolean mProcessLike = false;
    private boolean mProcessView = false;
    private ArrayList<String> contents;
    private int pageCount;

    Long timeAccessed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.storyrecyclerviewer, container, false);
        FireBaseUtils.mDatabaseLike.keepSynced(true);
        FireBaseUtils.mDatabaseStory.keepSynced(true);
        FireBaseUtils.mDatabaseViews.keepSynced(true);
        FireBaseUtils.mDatabaseComment.keepSynced(true);
        mStoryList = rootView.findViewById(R.id.rv_story);
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
                Story.class,R.layout.item_storyrow,StoryViewHolder.class, FireBaseUtils.mDatabaseStory)
        {
            @Override
            protected void populateViewHolder(StoryViewHolder viewHolder, final Story model, int position) {
                final String post_key = getRef(position).getKey();
                FireBaseUtils.mDatabaseLike.child(post_key).keepSynced(true);
                viewHolder.tvTitle.setText(model.getTitle());
                viewHolder.setTint(getContext());
                viewHolder.tvCategory.setText(getString(R.string.post_category,model.getCategory()));
                viewHolder.tvStatus.setText(getString(R.string.post_status,model.getStatus()));
                viewHolder.tvChapters.setText(getString(R.string.post_chapters, NumberUtils.setPlurality(model.getChapters(),"Chapter")));
                if (model.getImageUrl() == null){
                    viewHolder.setIvImage(getContext(), ImageUtils.getStoryUrl(model.getCategory().trim(),model.getTitle()));
                } else {
                    viewHolder.setIvImage(getContext(),model.getImageUrl());
                }

                viewHolder.tvAuthor.setText(getString(R.string.post_author,model.getAuthor()));

                if (model.getNumLikes() != null) {
                    viewHolder.tvNumLikes.setText(String.format("%s",model.getNumLikes().toString()));
                }

                if (model.getNumComments() != null) {
                    viewHolder.tvNumComments.setText(String.format("%s",model.getNumComments().toString()));
                }

                if (model.getNumViews() != null) {
                    viewHolder.tvNumViews.setText(String.format("%s",model.getNumViews().toString()));
                }
                if (model.getTimeCreated() != null) {
                    String time = TimeUtils.timeElapsed(model.getTimeCreated());
                    viewHolder.tvTime.setText(time);
                }

                viewHolder.tvAuthor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent readPoemIntent = new Intent(getContext(),AuthorsProfileActivity.class);
                        readPoemIntent.putExtra(Constants.POST_AUTHOR, model.getAuthor());
                        readPoemIntent.putExtra(Constants.AUTHOR_URL, model.getAuthorUrl());
                        startActivity(readPoemIntent);
                    }
                });
                viewHolder.setLikeBtn(post_key);
                viewHolder.setPostViewed(post_key);

                if (model.getLastUpdate() != null) {
                    Boolean t = TimeUtils.currentTime() - model.getLastUpdate() < TimeUtils.MILLISECONDS_DAY; //&& res;
                    viewHolder.setVisibility(t);
                }

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToViews(model.getDescription(),post_key,model);
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
                                    if (dataSnapshot.hasChild(FireBaseUtils.getUiD())) {
                                        FireBaseUtils.mDatabaseLike.child(post_key).child(FireBaseUtils.getUiD()).removeValue();
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

    private void addToViews(final String Description, final String post_key, final Story model) {
        mProcessView = true;
        FireBaseUtils.mDatabaseViews.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mProcessView) {
                    if (dataSnapshot.hasChild(FireBaseUtils.getUiD())) {
                        mProcessView = false;
                        initializeChapters(post_key, model);
                    } else {
                        mProcessView = false;
                        showDescription(Description,post_key,model);
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
        addToLibrary(model,post_key);
        loadChapters(model.getCategory().trim(),post_key);
    }


    private void loadChapters(String status, final String post_key) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading chapters");
        progressDialog.setCancelable(true);
        progressDialog.show();

        FireBaseUtils.isComplete(status,mDatabaseChapters);
        mDatabaseChapters.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pageCount = ((int) dataSnapshot.getChildrenCount());
                for (DataSnapshot chapterSnapShot: dataSnapshot.getChildren())
                {
                    Chapter chapter = chapterSnapShot.getValue(Chapter.class);
                    contents.add(chapter.getContent());
                }
                if (contents.size() == pageCount) {
                    progressDialog.dismiss();
                    Intent readIntent = new Intent(getContext(),ActivityReadStory.class);
                    readIntent.putStringArrayListExtra(Constants.POST_CONTENT,contents);
                    readIntent.putExtra(Constants.POST_KEY,post_key);
                    startActivity(readIntent);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void addToLibrary(final Story model, final String post_key) {
        FireBaseUtils.mDatabaseLibrary.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(FireBaseUtils.getUiD()).hasChild(post_key))
                {
                    Map<String,Object> values = new HashMap<>();
                    values.put(Constants.POST_KEY,  post_key);
                    values.put(Constants.POST_TITLE, model.getTitle());
                    values.put(Constants.CHAPTER_ADDED, 0);
                    values.put("lastAccessed", timeAccessed);
                    FireBaseUtils.mDatabaseLibrary.child(FireBaseUtils.getUiD()).child(post_key).setValue(values);
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
                        FireBaseUtils.addStoryView(model,post_key);
                        FireBaseUtils.onStoryViewed(post_key);
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
}
