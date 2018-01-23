package com.techart.writersblock;

import android.app.ProgressDialog;
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

import java.util.ArrayList;


public class AuthorsStoriesListActivity extends AppCompatActivity {
    private RecyclerView mPoemList;

    private ArrayList<String> contents;
    private ArrayList<String> chapterTitles;
    private int pageCount;
    private String author;

    private boolean mProcessLike = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabrecyclerviewer);

        author = getIntent().getStringExtra("author");
        setTitle(author + "'s stories");
        FireBaseUtils.mDatabaseLike.keepSynced(true);
        FireBaseUtils.mDatabaseStory.keepSynced(true);

        mPoemList = (RecyclerView) findViewById(R.id.poem_list);
        mPoemList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AuthorsStoriesListActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mPoemList.setLayoutManager(linearLayoutManager);
        bindView();
    }

    private void bindView()
    {
        Query query = FireBaseUtils.mDatabaseStory.orderByChild(Constants.POST_AUTHOR).equalTo(author);
        FirebaseRecyclerAdapter<Story,Tab2Stories.StoryViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Story, Tab2Stories.StoryViewHolder>(
                Story.class,R.layout.item_storyrow,Tab2Stories.StoryViewHolder.class, query) {
            @Override
            protected void populateViewHolder(Tab2Stories.StoryViewHolder viewHolder, final Story model, int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.tvTitle.setText(model.getTitle());
                viewHolder.tvCategory.setText(getString(R.string.post_category,model.getCategory()));
                viewHolder.tvStatus.setText(model.getStatus());
                viewHolder.setIvImage(AuthorsStoriesListActivity.this, ImageUtils.getStoryUrl(model.getCategory().trim(),model.getTitle()));
                viewHolder.setTypeFace(AuthorsStoriesListActivity.this);
                viewHolder.tvAuthor.setText(getString(R.string.post_author,model.getAuthor()));
                if (model.getNumLikes() != null)
                {
                    viewHolder.tvNumLikes.setText(String.format("%s",model.getNumLikes().toString()));
                }
                if (model.getNumComments() != null)
                {
                    viewHolder.tvNumComments.setText(String.format("%s",model.getNumComments().toString()));
                }if (model.getNumViews() != null)
                {
                    viewHolder.tvNumViews.setText(String.format("%s",model.getNumViews().toString()));
                }
                if (model.getTimeCreated() != null)
                {
                    String time = com.techart.writersblock.TimeUtils.timeElapsed(TimeUtils.currentTime() - model.getTimeCreated());
                    viewHolder.tvTime.setText(time);
                }

                viewHolder.tvAuthor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent readPoemIntent = new Intent(AuthorsStoriesListActivity.this,AuthorsProfileActivity.class);
                        readPoemIntent.putExtra(Constants.POST_AUTHOR, model.getAuthor());
                        startActivity(readPoemIntent);
                    }
                });

                viewHolder.setLikeBtn(post_key);
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contents = new ArrayList<>();
                        chapterTitles = new ArrayList<>();
                        loadChapters(post_key);
                    }
                });

                viewHolder.btnLiked.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProcessLike = true;
                        FireBaseUtils.mDatabaseChapters.child(post_key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mProcessLike) {
                                    if (dataSnapshot.child(post_key).hasChild(Constants.AUTHOR_URL))  {
                                        FireBaseUtils.mDatabaseChapters.child(post_key).child(FireBaseUtils.mAuth.getCurrentUser().getUid()).removeValue();
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
                        Intent likedPostsIntent = new Intent(AuthorsStoriesListActivity.this,LikesActivity.class);
                        likedPostsIntent.putExtra(Constants.POST_KEY,post_key);
                        startActivity(likedPostsIntent);
                    }
                });
                viewHolder.btnComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentIntent = new Intent(AuthorsStoriesListActivity.this,CommentActivity.class);
                        commentIntent.putExtra(Constants.POST_KEY,post_key);
                        commentIntent.putExtra(Constants.POST_TITLE,model.getTitle());
                        commentIntent.putExtra(Constants.POST_TYPE,Constants.STORY_HOLDER);
                        startActivity(commentIntent);
                    }
                });
            }
        };
        mPoemList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }


    private void loadChapters(String post_key) {
        final ProgressDialog progressDialog = new ProgressDialog(AuthorsStoriesListActivity.this);
        progressDialog.setMessage("Loading chapters");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        FireBaseUtils.mDatabaseChapters.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    Intent readIntent = new Intent(AuthorsStoriesListActivity.this,ActivityReadStory.class);
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

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_OK,getIntent());
        finish();
    }
}

