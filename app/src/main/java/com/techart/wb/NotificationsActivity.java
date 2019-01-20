package com.techart.wb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.techart.wb.constants.Constants;
import com.techart.wb.constants.FireBaseUtils;
import com.techart.wb.models.Notice;
import com.techart.wb.models.Users;
import com.techart.wb.utils.TimeUtils;

import static android.widget.Toast.LENGTH_LONG;
import static com.techart.wb.constants.Constants.STAMP_KEY;

/**
 * Holds notification and news fragments
 */
public class NotificationsActivity extends AppCompatActivity {
    private RecyclerView rvNotice;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_activity);
        rvNotice = findViewById(R.id.rv_news);
        rvNotice.setHasFixedSize(true);
        int lastAccessedPage = getIntent().getIntExtra(Constants.STAMP_KEY, 0);
        setTimeAccessed(lastAccessedPage);

        tvEmpty = findViewById(R.id.tv_empty);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvNotice.setLayoutManager(linearLayoutManager);
        setView();
    }

    private void setView() {
        if (FirebaseAuth.getInstance().getCurrentUser()!= null ){
            FireBaseUtils.mDatabaseUsers.child(FireBaseUtils.getUiD()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Users user = dataSnapshot.getValue(Users.class);
                    if (user != null){
                        switch (user.getSignedAs().trim()) {
                            case "Writer": {
                                writerView();
                                break;
                            }
                            case "Reader": {
                                readersView();
                                break;
                            }
                            default:
                                Toast.makeText(NotificationsActivity.this, "Could not open notifications " + user.getSignedAs(), LENGTH_LONG).show();
                                break;
                        }
                    }else {
                        Toast.makeText(NotificationsActivity.this,"Error...! Try later",LENGTH_LONG).show();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            Toast.makeText(NotificationsActivity.this,"Still loading, try after a minute",LENGTH_LONG).show();
        }
    }

    private void setTimeAccessed(int lastAccessedPage) {
        SharedPreferences mPref = getSharedPreferences(String.format("%s", getString(R.string.app_name)), MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt(STAMP_KEY,lastAccessedPage);
        editor.apply();
    }

    private void readersView(){
        FirebaseRecyclerAdapter<Notice,NoticeViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Notice, NoticeViewHolder>(
                Notice.class,R.layout.item_notifications,NoticeViewHolder.class, FireBaseUtils.mDatabaseNotifications.orderByChild(Constants.SIGNED_IN_AS).equalTo("Reader")){
            @Override
            protected void populateViewHolder(NoticeViewHolder viewHolder, final Notice model, int position) {
                viewHolder.makePortionBold(model.getUser() + " " + model.getAction() + model.getPostTitle(), model.getUser());
                tvEmpty.setVisibility(View.GONE);
                if (model.getTimeCreated() != null) {
                    String time = TimeUtils.timeElapsed( model.getTimeCreated());
                    viewHolder.tvTime.setText(time);
                }

                if (model.getImageUrl() != null && !model.getImageUrl().equals("default")) {
                    viewHolder.setIvImage(NotificationsActivity.this, model.getImageUrl());
                } else {
                    viewHolder.setIvImage(NotificationsActivity.this, R.drawable.placeholder);
                }
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectIntent(model);
                    }
                });
            }
        };
        rvNotice.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    private void writerView(){
        FirebaseRecyclerAdapter<Notice,NoticeViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Notice, NoticeViewHolder>(
                Notice.class,R.layout.item_notifications,NoticeViewHolder.class, FireBaseUtils.mDatabaseNotifications){
            @Override
            protected void populateViewHolder(NoticeViewHolder viewHolder, final Notice model, int position) {
                viewHolder.makePortionBold(model.getUser() + " " + model.getAction() + model.getPostTitle(), model.getUser());
                tvEmpty.setVisibility(View.GONE);
                if (model.getTimeCreated() != null) {
                    String time = TimeUtils.timeElapsed( model.getTimeCreated());
                    viewHolder.tvTime.setText(time);
                }

                if (model.getImageUrl() != null && !model.getImageUrl().equals("default")) {
                    viewHolder.setIvImage(NotificationsActivity.this, model.getImageUrl());
                } else {
                    viewHolder.setIvImage(NotificationsActivity.this, R.drawable.placeholder);
                }
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectIntent(model);
                    }
                });
            }
        };
        rvNotice.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    /**
     * Resolves activity to start
     *
     * @param notice name of category clicked
     */
    private void selectIntent(Notice notice) {
        Intent readPoemIntent;
        if (notice.getAction().equals("commented")) {
            readPoemIntent = new Intent(NotificationsActivity.this, CommentActivity.class);
            readPoemIntent.putExtra(Constants.POST_KEY, notice.getPostKey());
            readPoemIntent.putExtra(Constants.POST_TITLE, notice.getPostKey());
            readPoemIntent.putExtra(Constants.POST_TYPE, notice.getPostType());
            startActivity(readPoemIntent);
        } else  if (notice.getPostType().equals("writersInbox")) {
            readPoemIntent = new Intent(NotificationsActivity.this, WritersChatRoomActivity.class);
            startActivity(readPoemIntent);
        } else  if (notice.getPostType().equals("generalInbox")) {
            readPoemIntent = new Intent(NotificationsActivity.this, GeneralChatRoomActivity.class);
            startActivity(readPoemIntent);
        } else {
            Toast.makeText(NotificationsActivity.this, "Could not open new screen", Toast.LENGTH_LONG).show();
        }
    }

    public static class NoticeViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivImage;
        TextView tvNotice;
        TextView tvTime;
        View mView;

        public NoticeViewHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvNotice = itemView.findViewById(R.id.tv_notifications);
            ivImage = itemView.findViewById(R.id.iv_disease);
            this.mView = itemView;
        }

        public void setIvImage(Context context, String image)  {
            Glide.with(context)
                    .load(image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivImage);
        }

        public void setIvImage(Context context, int image) {
            Glide.with(context)
                    .load(image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivImage);
        }

        void makePortionBold(String text, String spanText) {
            StyleSpan boldStyle = new StyleSpan(Typeface.BOLD);
            SpannableStringBuilder sb = new SpannableStringBuilder(text);
            int start = text.indexOf(spanText);
            int end = start + spanText.length();
            sb.setSpan(boldStyle, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            tvNotice.setText(sb);
        }
    }
}
