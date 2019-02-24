package com.techart.wb;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.techart.wb.constants.Constants;
import com.techart.wb.constants.FireBaseUtils;
import com.techart.wb.models.Comment;
import com.techart.wb.models.ImageUrl;
import com.techart.wb.utils.TimeUtils;
import com.techart.wb.utils.UploadUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WritersChatRoomActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private RecyclerView mCommentList;
    private EditText mEtComment;
    private Boolean isSent;
    private String time;
    private ProgressBar progressBar;
    private static final int GALLERY_REQUEST = 1;
    private String postKey;
    private Uri uri;
    StorageReference filePath;
    private String currentMessage;

    private PopupMenu popupMenu;
    private final static int EDIT = 1;

    private final static int DELETE = 2;
    private final static int CANCEL = 3;
    private final static int NEW = 4;

    private int menuAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        setTitle("Writers' Chat Room");
        mCommentList = findViewById(R.id.comment_recyclerview);
        mCommentList.setHasFixedSize(true);
        progressBar = findViewById(R.id.pb_loading);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(WritersChatRoomActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mCommentList.setLayoutManager(linearLayoutManager);
        menuAction = NEW;
        init();
        initCommentSection();
    }

    private void initCommentSection() {
        FirebaseRecyclerAdapter<Comment, CommentHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment, CommentHolder>(
                Comment.class, R.layout.item_inbox, CommentHolder.class, FireBaseUtils.mDatabaseWritersChat) {
            @Override
            protected void populateViewHolder(final CommentHolder viewHolder, final Comment model, int position) {
                progressBar.setVisibility(View.GONE);
                final String post_key = getRef(position).getKey();
                final String current_message = model.getCommentText();
                if (model.getAuthorUrl() != null) {
                    setVisibility(model.getAuthorUrl(), viewHolder);
                }

                if (model.getAuthor() != null) {
                    viewHolder.authorTextView.setText(model.getAuthor());
                }

                if (model.getCommentText() != null) {
                    viewHolder.commentTextView.setText(model.getCommentText());
                    viewHolder.ivSample.setVisibility(View.GONE);
                } else {
                    viewHolder.setImage(getApplicationContext(), model.getImageUrl());
                    viewHolder.commentTextView.setVisibility(View.GONE);
                }

                if (model.getTimeCreated() != null) {
                    time = TimeUtils.timeElapsed(model.getTimeCreated());
                    viewHolder.timeTextView.setText(time);
                }

                viewHolder.ivSample.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (model.getImageUrl() != null) {
                            Intent intent = new Intent(WritersChatRoomActivity.this, FullImageActivity.class);
                            intent.putExtra(Constants.IMAGE_URL, model.getImageUrl());
                            startActivity(intent);
                        }
                    }
                });

                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (FireBaseUtils.getUiD() != null && FireBaseUtils.getUiD().equals(model.getAuthorUrl())) {
                            postKey = post_key;
                            currentMessage = current_message;
                            popupMenu = new PopupMenu(WritersChatRoomActivity.this, viewHolder.commentTextView);
                            popupMenu.getMenu().add(Menu.NONE, EDIT, Menu.NONE, "Edit");
                            popupMenu.getMenu().add(Menu.NONE, DELETE, Menu.NONE, "Delete");
                            popupMenu.getMenu().add(Menu.NONE, CANCEL, Menu.NONE, "Cancel");
                            popupMenu.setOnMenuItemClickListener(WritersChatRoomActivity.this);
                            popupMenu.show();
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
            }
        };
        mCommentList.setAdapter(firebaseRecyclerAdapter);
    }

    private void setVisibility(String url, CommentHolder viewHolder) {
        if (FireBaseUtils.getUiD() != null && FireBaseUtils.getUiD().equals(url)) {
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
                if (menuAction == NEW) {
                    sendComment();
                } else {
                    editMessage();
                }
                menuAction = NEW;
                break;
            case R.id.iv_image:
                Intent intent = new Intent(WritersChatRoomActivity.this, ImageActivity.class);
                postKey = getIntent().getStringExtra(Constants.POST_KEY);
                startActivityForResult(intent, GALLERY_REQUEST);
                break;
        }
    }

    private void sendComment() {
        final String comment = mEtComment.getText().toString().trim();
        isSent = false;
        if (!comment.isEmpty()) {
            final ProgressDialog progressDialog = new ProgressDialog(WritersChatRoomActivity.this);
            progressDialog.setMessage("Sending comment..");
            progressDialog.setCancelable(true);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
            FireBaseUtils.mDatabaseWritersChat.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!isSent) {
                        DatabaseReference newComment = FireBaseUtils.mDatabaseWritersChat.push();
                        Map<String, Object> values = new HashMap<>();
                        values.put(Constants.AUTHOR_URL, FireBaseUtils.getUiD());
                        values.put(Constants.POST_AUTHOR, FireBaseUtils.getAuthor());
                        values.put(Constants.COMMENT_TEXT, comment);
                        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
                        newComment.setValue(values);
                        FireBaseUtils.updateNotifications("writersInbox", "", ImageUrl.getInstance().getSignedAs(), "wrote a message in the writers chat room", newComment.getKey(), comment, ImageUrl.getInstance().getImageUrl());
                        isSent = true;
                        progressDialog.dismiss();
                        mEtComment.setText("");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(this, "Nothing to send", Toast.LENGTH_LONG).show();
        }
    }

    private void sendImage(DatabaseReference newComment, String imageUrl) {
        final ProgressDialog progressDialog = new ProgressDialog(WritersChatRoomActivity.this);
        progressDialog.setMessage("Sending answer...");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        Map<String, Object> values = new HashMap<>();
        values.put(Constants.AUTHOR_URL, FireBaseUtils.getUiD());
        values.put(Constants.POST_AUTHOR, FireBaseUtils.getAuthor());
        values.put(Constants.IMAGE_URL, imageUrl);
        values.put(Constants.POST_KEY, postKey);
        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
        newComment.setValue(values);
        progressDialog.dismiss();
        FireBaseUtils.updateNotifications("writersInbox", "", ImageUrl.getInstance().getSignedAs(), "sent an image in the writers chat room", newComment.getKey(), "", ImageUrl.getInstance().getImageUrl());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case EDIT:
                menuAction = EDIT;
                mEtComment.setText(currentMessage);
                break;
            case DELETE:
                deleteMessage();
                break;
            case CANCEL:
                popupMenu.dismiss();
                break;
        }
        return false;
    }

    private void deleteMessage() {
        FireBaseUtils.mDatabaseWritersChat.child(postKey).removeValue();
        Toast.makeText(this, "Message deleted", Toast.LENGTH_LONG).show();
    }

    private void editMessage() {
        final String comment = mEtComment.getText().toString().trim();
        Map<String, Object> values = new HashMap<>();
        values.put(Constants.COMMENT_TEXT, comment);
        FireBaseUtils.mDatabaseWritersChat.child(postKey).updateChildren(values);
        mEtComment.setText("");
        Toast.makeText(this, "Message updated", Toast.LENGTH_LONG).show();
    }

    public static class CommentHolder extends RecyclerView.ViewHolder {
        public TextView authorTextView;
        public TextView commentTextView;
        public TextView timeTextView;
        public ImageView ivSample;
        public View itemView;

        public CommentHolder(View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.tvAuthor);
            timeTextView = itemView.findViewById(R.id.tvTime);
            commentTextView = itemView.findViewById(R.id.tvComment);
            ivSample = itemView.findViewById(R.id.iv_sample);
            this.itemView = itemView;
        }

        public void setImage(Context context, String image) {
            Glide.with(context)
                    .load(image)
                    .into(ivSample);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            uri = Uri.parse(data.getStringExtra(Constants.URI));
            postKey = getIntent().getStringExtra(Constants.POST_KEY);
            upload();
        }
    }

    /**
     * Uploads image to cloud storage
     */
    private void upload() {
        final DatabaseReference newComment = FireBaseUtils.mDatabaseWritersChat.push();
        final String url = newComment.getKey();

        final ProgressDialog mProgress = new ProgressDialog(WritersChatRoomActivity.this);
        mProgress.setMessage("Uploading photo, please wait...");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
        filePath = FireBaseUtils.mStoragePhotos.child("writersInbox" + "/" + url);
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        //uploading the image
        UploadTask uploadTask = filePath.putBytes(data);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return filePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    sendImage(newComment, task.getResult().toString());
                    mProgress.dismiss();
                } else {
                    // Handle failures
                    UploadUtils.makeNotification("Image upload failed", WritersChatRoomActivity.this);
                }
            }
        });
    }
}
