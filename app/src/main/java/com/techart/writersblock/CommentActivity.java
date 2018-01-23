package com.techart.writersblock;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mCommentList;
    private DatabaseReference mDatabaseComment;
    private DatabaseReference mDatabasePoem;
    private DatabaseReference mDatabaseDevotions;
    private DatabaseReference mDatabaseStories;

    private FirebaseAuth mAuth;

    private EditText mEtComment;
    private String post_key;
    private String postName;
    private Boolean isSent;
    private String postType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        mDatabasePoem = FirebaseDatabase.getInstance().getReference().child(Constants.POEM_KEY);
        mDatabaseDevotions = FirebaseDatabase.getInstance().getReference().child(Constants.DEVOTION_KEY);
        mDatabaseStories = FirebaseDatabase.getInstance().getReference().child(Constants.STORY_KEY);

        mDatabaseComment = FirebaseDatabase.getInstance().getReference().child("Comments");
        post_key = getIntent().getStringExtra(Constants.POST_KEY);
        postName = getIntent().getStringExtra(Constants.POST_TITLE);
        postType = getIntent().getStringExtra(Constants.POST_TYPE);
        setTitle("Comments on "+ postName);
        mAuth = FirebaseAuth.getInstance();

        mCommentList = (RecyclerView) findViewById(R.id.comment_recyclerview);
        mCommentList.setHasFixedSize(true);
        mCommentList.setLayoutManager(new LinearLayoutManager(this));
        init();
        initCommentSection();
    }

    private void initCommentSection() {
        FirebaseRecyclerAdapter<Comment, CommentHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment, CommentHolder>(
                Comment.class, R.layout.item_comment, CommentHolder.class, mDatabaseComment.child(post_key)
        )
        {
            @Override
            protected void populateViewHolder(CommentHolder viewHolder, final Comment model, int position) {
                viewHolder.authorTextView.setText(model.getAuthor());
                viewHolder.commentTextView.setText(model.getCommentText());
                viewHolder.setTypeFace(CommentActivity.this);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, HH:mm:ss");
                String time = simpleDateFormat.format(model.getTimeCreated());
                viewHolder.timeTextView.setText(time);
            }
        };
        mCommentList.setAdapter(firebaseRecyclerAdapter);
    }

    private void init() {
        mEtComment = (EditText) findViewById(R.id.et_comment);
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
            mDatabaseComment = FirebaseDatabase.getInstance().getReference().child(Constants.COMMENTS_KEY).child(post_key);
            final ProgressDialog progressDialog = new ProgressDialog(CommentActivity.this);
            progressDialog.setMessage("Sending comment..");
            progressDialog.setCancelable(true);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
            mDatabaseComment.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!isSent)
                    {
                        DatabaseReference newComment = mDatabaseComment.push();
                        Map<String,Object> values = new HashMap<>();
                        values.put(Constants.USER,mAuth.getCurrentUser().getUid());
                        values.put(Constants.POST_AUTHOR,getAuthor());
                        values.put(Constants.COMMENT_TEXT,comment);
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
        switch (postType)
        {
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
        mDatabasePoem.child(post_key).runTransaction(new Transaction.Handler() {
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
        mDatabaseDevotions.child(post_key).runTransaction(new Transaction.Handler() {
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
        mDatabaseStories.child(post_key).runTransaction(new Transaction.Handler() {
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


    public String getAuthor()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getDisplayName();
    }

    public static class CommentHolder extends RecyclerView.ViewHolder {
        TextView authorTextView;
        TextView commentTextView;
        TextView timeTextView;

        public CommentHolder(View itemView) {
            super(itemView);
            authorTextView = (TextView) itemView.findViewById(R.id.tvAuthor) ;
            timeTextView = (TextView) itemView.findViewById(R.id.tvTime);
            commentTextView = (TextView) itemView.findViewById(R.id.tvComment);
        }

        protected void setTypeFace(Context context){
            Typeface typeface =  EditorUtils.getTypeFace(context);
            authorTextView.setTypeface(typeface);
            commentTextView.setTypeface(typeface);
            timeTextView.setTypeface(typeface);
        }
    }

}
