package com.techart.writersblock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.techart.writersblock.constants.Constants;
import com.techart.writersblock.constants.FireBaseUtils;
import com.techart.writersblock.models.Notice;
import com.techart.writersblock.utils.TimeUtils;


public class LikesActivity extends AppCompatActivity
{
    private String postKey;
    private RecyclerView mLikeList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_like);
        postKey = getIntent().getStringExtra(Constants.POST_KEY);
        FireBaseUtils.mDatabaseLike.child(postKey).keepSynced(true);
        mLikeList = findViewById(R.id.lv_notice);
        mLikeList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mLikeList.setLayoutManager(linearLayoutManager);
        bindView();
    }
    private void bindView()
    {
        Query likeQuery = FireBaseUtils.mDatabaseLike.child(postKey).orderByChild(Constants.TIME_CREATED);

        FirebaseRecyclerAdapter<Notice,LikesActivity.NoticeViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Notice, LikesActivity.NoticeViewHolder>(
                Notice.class,R.layout.list_view,LikesActivity.NoticeViewHolder.class, likeQuery)
        {
            @Override
            protected void populateViewHolder(LikesActivity.NoticeViewHolder viewHolder, final Notice model, int position) {
                String time = TimeUtils.timeElapsed(model.getTimeCreated());
                viewHolder.tvUser.setText(getString(R.string.liked,model.getUser(),model.getPostTitle()));
                viewHolder.tvTime.setText(time);
            }
        };
        mLikeList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    public static class NoticeViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvUser;
        TextView tvTime;

        public NoticeViewHolder(View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tv_user);
            tvTime = itemView.findViewById(R.id.tv_time);

        }

    }
}

