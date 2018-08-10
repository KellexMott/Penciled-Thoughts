package com.techart.writersblock;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.techart.writersblock.constants.Constants;
import com.techart.writersblock.constants.FireBaseUtils;
import com.techart.writersblock.models.Comment;
import com.techart.writersblock.models.Devotion;
import com.techart.writersblock.models.Poem;
import com.techart.writersblock.models.Story;
import com.techart.writersblock.utils.NumberUtils;
import com.techart.writersblock.utils.TimeUtils;

import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mCommentList;
    private EditText mEtComment;
    private String post_key;
    private Boolean isSent;
    private String postType;
    String postName;
    String time;
    TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        FireBaseUtils.mDatabaseComment.keepSynced(true);
        FireBaseUtils.mDatabaseStory.keepSynced(true);
        FireBaseUtils.mDatabaseDevotions.keepSynced(true);
        FireBaseUtils.mDatabasePoems.keepSynced(true);

        post_key = getIntent().getStringExtra(Constants.POST_KEY);
        postName = getIntent().getStringExtra(Constants.POST_TITLE);
        postType = getIntent().getStringExtra(Constants.POST_TYPE);
        setTitle("Comments on "+ postName);
        tvEmpty = findViewById(R.id.tv_empty);
        mCommentList = findViewById(R.id.comment_recyclerview);
        mCommentList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CommentActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mCommentList.setLayoutManager(linearLayoutManager);
        init();
        initCommentSection();
    }

    private void initCommentSection() {
        Query commentsQuery = FireBaseUtils.mDatabaseComment.child(post_key).orderByChild(Constants.TIME_CREATED);
        FirebaseRecyclerAdapter<Comment, CommentHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment, CommentHolder>(
                Comment.class, R.layout.item_comment, CommentHolder.class, commentsQuery)
        {
            @Override
            protected void populateViewHolder(CommentHolder viewHolder, final Comment model, int position) {
                final String comment_key = getRef(position).getKey();
                tvEmpty.setVisibility(View.GONE);
                if (model.getAuthorUrl() != null){
                    setVisibility(model.getAuthorUrl(),viewHolder);
                }
                viewHolder.authorTextView.setText(model.getAuthor());
                viewHolder.commentTextView.setText(model.getCommentText());
                time = TimeUtils.timeElapsed(model.getTimeCreated());
                viewHolder.timeTextView.setText(time);
                if (model.getReplies() != null && model.getReplies() != 0){
                    viewHolder.tvViewReplies.setVisibility(View.VISIBLE);
                    viewHolder.tvViewReplies.setText(getString(R.string.replies, NumberUtils.setUsualPlurality(model.getReplies(),"reply")));
                }
                viewHolder.tvReply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent replyIntent = new Intent(CommentActivity.this,ReplyActivity.class);
                        replyIntent.putExtra(Constants.POST_KEY,post_key);
                        replyIntent.putExtra(Constants.COMMENT_KEY,comment_key);
                        replyIntent.putExtra(Constants.POST_AUTHOR,model.getAuthor());
                        replyIntent.putExtra(Constants.COMMENT_TEXT,model.getCommentText());
                        replyIntent.putExtra(Constants.TIME_CREATED,time);
                        replyIntent.putExtra(Constants.POST_TYPE,postType);
                        startActivity(replyIntent);
                    }
                });
            }
        };
        mCommentList.setAdapter(firebaseRecyclerAdapter);
    }

    public void setVisibility(String url, CommentHolder viewHolder) {
        if (FireBaseUtils.getUiD() != null && FireBaseUtils.getUiD().equals(url)){
            viewHolder.commentTextView.setBackground(getResources().getDrawable(R.drawable.tv_circular_active_background));
        }
    }

    private void init() {
        mEtComment = findViewById(R.id.et_comment);
        findViewById(R.id.iv_send).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_send:
                sendComment();
        }
    }

    private void sendComment() {
        final String comment = mEtComment.getText().toString().trim();
        isSent = false;
        if (!comment.isEmpty())
        {
            final ProgressDialog progressDialog = new ProgressDialog(CommentActivity.this);
            progressDialog.setMessage("Sending comment..");
            progressDialog.setCancelable(true);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
            FireBaseUtils.mDatabaseComment.child(post_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!isSent)
                    {
                        DatabaseReference newComment = FireBaseUtils.mDatabaseComment.child(post_key).push();
                        Map<String,Object> values = new HashMap<>();
                        values.put(Constants.AUTHOR_URL,FireBaseUtils.getUiD());
                        values.put(Constants.POST_AUTHOR,FireBaseUtils.getAuthor());
                        values.put(Constants.COMMENT_TEXT,comment);
                        values.put(Constants.REPLIES,0);
                        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
                        newComment.setValue(values);
                        isSent = true;
                        progressDialog.dismiss();
                        onCommentSent();
                        mEtComment.setText("");
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            Toast.makeText(this,"Nothing to send",Toast.LENGTH_LONG ).show();
        }
    }

    private void onCommentSent() {
        switch (postType) {
            case Constants.POEM_HOLDER:
                poemCommentCount();
                break;
            case Constants.DEVOTION_HOLDER:
                devotionCommentCount();
                break;
            case Constants.STORY_HOLDER:
                storyCommentCount();
                break;
        }
    }

    private void poemCommentCount() {
        FireBaseUtils.mDatabasePoems.child(post_key).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Poem poem = mutableData.getValue(Poem.class);
                if (poem == null) {
                    return Transaction.success(mutableData);
                }
                poem.setNumComments(poem.getNumComments() + 1 );
                // Set value and report transaction success
                mutableData.setValue(poem);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }

    private void devotionCommentCount() {
        FireBaseUtils.mDatabaseDevotions.child(post_key).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Devotion devotion = mutableData.getValue(Devotion.class);
                if (devotion == null) {
                    return Transaction.success(mutableData);
                }
                devotion.setNumComments(devotion.getNumComments() + 1 );
                // Set value and report transaction success
                mutableData.setValue(devotion);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }
    private void storyCommentCount() {
        FireBaseUtils.mDatabaseStory.child(post_key).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Story story = mutableData.getValue(Story.class);
                if (story == null) {
                    return Transaction.success(mutableData);
                }
                story.setNumComments(story.getNumComments() + 1 );
                // Set value and report transaction success
                mutableData.setValue(story);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if (resultCode == RESULT_OK){
            post_key = data.getStringExtra(Constants.POST_KEY);
            postName = data.getStringExtra(Constants.POST_TITLE);
            postType = data.getStringExtra(Constants.POST_TYPE);
        }
    }

    public static class CommentHolder extends RecyclerView.ViewHolder {
        final LinearLayout llComment;
        final TextView authorTextView;
        final TextView commentTextView;
        final TextView timeTextView;
        final TextView tvReply;
        final TextView tvViewReplies;

        public CommentHolder(View itemView) {
            super(itemView);
            llComment = itemView.findViewById(R.id.ll_comment);
            authorTextView = itemView.findViewById(R.id.tvAuthor);
            timeTextView = itemView.findViewById(R.id.tvTime);
            tvReply = itemView.findViewById(R.id.tvReply);
            tvViewReplies = itemView.findViewById(R.id.tv_view_replies);
            commentTextView = itemView.findViewById(R.id.tvComment);
        }
    }

}
