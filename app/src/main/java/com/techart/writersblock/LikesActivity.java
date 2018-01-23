package com.techart.writersblock;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;


public class LikesActivity extends AppCompatActivity
{
    String title;
    String postKey;
    private RecyclerView mLikeList;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_like);
        postKey = getIntent().getStringExtra(Constants.POST_KEY);
        mAuth = FirebaseAuth.getInstance();
        FireBaseUtils.mDatabaseLike.child(postKey).keepSynced(true);
        mLikeList = (RecyclerView) findViewById(R.id.lv_notice);
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
                String time = com.techart.writersblock.TimeUtils.timeElapsed(model.getTimeCreated());
                viewHolder.tvUser.setText(getString(R.string.liked,model.getUser(),model.getPostTitle()));
                viewHolder.tvTime.setText(time);
                viewHolder.setTypeFace(LikesActivity.this);
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //ToDo Call listview
                    }
                });
            }
        };
        mLikeList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    public static class NoticeViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvUser;
        TextView tvTime;
        View mView;

        FirebaseAuth mAUth;

        public NoticeViewHolder(View itemView) {
            super(itemView);
            tvUser = (TextView)itemView.findViewById(R.id.tv_user);
            tvTime = (TextView)itemView.findViewById(R.id.tv_time) ;

            this.mView = itemView;
            mAUth = FirebaseAuth.getInstance();
        }

        public void setTypeFace(Context context){
           Typeface typeface =  EditorUtils.getTypeFace(context);
           tvUser.setTypeface(typeface);
           tvTime.setTypeface(typeface);
        }

    }
}

