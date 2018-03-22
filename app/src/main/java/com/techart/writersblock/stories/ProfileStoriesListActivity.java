package com.techart.writersblock.stories;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.techart.writersblock.CommentActivity;
import com.techart.writersblock.LikesActivity;
import com.techart.writersblock.R;
import com.techart.writersblock.models.Story;
import com.techart.writersblock.utils.Constants;
import com.techart.writersblock.utils.FireBaseUtils;
import com.techart.writersblock.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class ProfileStoriesListActivity extends AppCompatActivity {
    private RecyclerView mPoemList;
    private DatabaseReference mDatabaseStory;
    private DatabaseReference mDatabaseLike;
    private AlertDialog updateDialog;
    private ArrayList<String> contents = new ArrayList<>(Arrays.asList("Action", "Drama", "Fiction","Romance"));

    String[] categories = {"Action", "Drama", "Fiction","Romance"};
    private boolean mProcessLike = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabrecyclerviewer);
        setTitle("Stories");
        mDatabaseStory = FireBaseUtils.mDatabaseStory;
        mDatabaseLike = FireBaseUtils.mDatabaseLike;
        mDatabaseLike.keepSynced(true);
        mDatabaseStory.keepSynced(true);
        mPoemList = findViewById(R.id.poem_list);
        mPoemList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProfileStoriesListActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mPoemList.setLayoutManager(linearLayoutManager);
        bindView();
    }

    public void onToggleButtonClicked(View view, String  postKey) {
        ((ToggleButton) view).setChecked(((ToggleButton) view).isChecked());
        FireBaseUtils.updateStatus(((ToggleButton) view).getText().toString(),postKey);
        Toast.makeText(this,"Story marked as " + ((ToggleButton) view).getText(),Toast.LENGTH_LONG).show();
    }

    private void bindView() {
        Query query = mDatabaseStory.orderByChild(Constants.POST_AUTHOR).equalTo(FireBaseUtils.getAuthor());
        FirebaseRecyclerAdapter<Story,StoryViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Story, StoryViewHolder>(
                Story.class,R.layout.item_storyrow_del,StoryViewHolder.class, query) {
            @Override
            protected void populateViewHolder(StoryViewHolder viewHolder, final Story model, int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.tvTitle.setText(model.getTitle());
                viewHolder.tvCategory.setText(model.getCategory());
                viewHolder.tbStatus.setChecked(model.getStatus().equals("Complete"));
                viewHolder.tbStatus.setTextColor(setColor(model.getStatus().equals("Complete")));
                if (model.getNumLikes() != null)
                {
                    viewHolder.tvNumLikes.setText(getString(R.string.num_likes,model.getNumLikes()));
                }
                if (model.getNumComments() != null)
                {
                    viewHolder.tvNumComments.setText(getString(R.string.num_comments,model.getNumComments()));
                }
                if (model.getTimeCreated() != null)
                {
                    String time = TimeUtils.timeElapsed(TimeUtils.currentTime() - model.getTimeCreated());
                    viewHolder.tvTime.setText(time);
                }
                viewHolder.setLikeBtn(post_key);
                viewHolder.tvCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateStoryDialog(post_key, model.getCategory().trim());
                    }
                });
                viewHolder.tbStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onToggleButtonClicked(v,post_key);
                       // setStatus(model.getStatus());
                    }
                });
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });

                viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    deleteCaution(post_key);
                     //FireBaseUtils.deleteFromViews(post_key);
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
                                    if (dataSnapshot.child(post_key).hasChild(Constants.AUTHOR_URL))  {
                                        mDatabaseLike.child(post_key).child(FireBaseUtils.getUiD()).removeValue();
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
                        Intent likedPostsIntent = new Intent(ProfileStoriesListActivity.this,LikesActivity.class);
                        likedPostsIntent.putExtra(Constants.POST_KEY,post_key);
                        startActivity(likedPostsIntent);
                    }
                });

                viewHolder.btEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent likedPostsIntent = new Intent(ProfileStoriesListActivity.this,StoryDialogActivity.class);
                        likedPostsIntent.putExtra(Constants.POST_KEY,post_key);
                        likedPostsIntent.putExtra(Constants.STORY_CHAPTERCOUNT,model.getChapters().toString());
                        startActivity(likedPostsIntent);
                    }
                });

                viewHolder.btnComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentIntent = new Intent(ProfileStoriesListActivity.this,CommentActivity.class);
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

    private int setColor(boolean isChecked)
    {
        if (isChecked){
            return R.color.colorAccent;
        }else{
            return R.color.colorPrimary;
        }
    }

    public void updateStoryDialog(final String post_key, String category)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileStoriesListActivity.this);
        builder.setSingleChoiceItems(categories, contents.indexOf(category), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                FireBaseUtils.updateCategory(contents.get(item),post_key);
                updateDialog.dismiss();
                Toast.makeText(getBaseContext(),"Story Updated",Toast.LENGTH_LONG).show();
            }
        });
        updateDialog = builder.create();
        updateDialog.show();
    }

    public void deleteCaution(final String post_key)
    {
        DialogInterface.OnClickListener dialogClickListener =
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int button) {
                    if (button == DialogInterface.BUTTON_POSITIVE)
                    {
                        FireBaseUtils.deleteComment(post_key);
                        FireBaseUtils.deleteLike(post_key);
                        FireBaseUtils.deleteChapter(post_key);
                        FireBaseUtils.deleteStory(post_key);
                        FireBaseUtils.deleteFromLib(post_key);
                        dialog.dismiss();
                    }
                    if (button == DialogInterface.BUTTON_NEGATIVE)
                    {
                        dialog.dismiss();
                    }
                }
            };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Deletes all chapters, comments, likes & views related to this story")
                .setTitle("This action is irreversible!")
                .setPositiveButton("Delete", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener)
                .show();
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvTitle;
        Button tvCategory;
        Button btEdit;
        TextView tvNumLikes;
        TextView tvNumComments;
        TextView tvNumViews;
        TextView tvTime;
        View mView;

        DatabaseReference mDatabaseLike;
        FirebaseAuth mAUth;

        ImageButton btnDelete;
        ToggleButton tbStatus;
        ImageButton btnLiked;
        ImageButton btnComment;
        ImageButton btnViews;

        public StoryViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tbStatus = itemView.findViewById(R.id.tb_status);
            tvCategory = itemView.findViewById(R.id.tv_category);
            btEdit = itemView.findViewById(R.id.bt_edit);

            btnDelete = itemView.findViewById(R.id.im_delete);
            btnLiked = itemView.findViewById(R.id.likeBtn);
            btnComment = itemView.findViewById(R.id.commentBtn);
            btnViews = itemView.findViewById(R.id.bt_views);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvNumLikes = itemView.findViewById(R.id.tv_numlikes);
            tvNumComments = itemView.findViewById(R.id.tv_numcomments);
            tvNumViews = itemView.findViewById(R.id.tv_numviews);
            this.mView = itemView;
            mDatabaseLike = FireBaseUtils.mDatabaseLike;
            mAUth = FirebaseAuth.getInstance();
            mDatabaseLike.keepSynced(true);
        }
        public void setLikeBtn(String post_key) {
            FireBaseUtils.setLikeBtn(post_key,btnLiked);
        }
    }

}

