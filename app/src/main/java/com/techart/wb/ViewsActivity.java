package com.techart.wb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.techart.wb.constants.Constants;
import com.techart.wb.constants.FireBaseUtils;
import com.techart.wb.models.Notice;
import com.techart.wb.utils.TimeUtils;

/**
 * Retrieves and displays list of people who have viewed a particular post
 */
public class ViewsActivity extends AppCompatActivity {
    private String postKey;
    private RecyclerView mPoemList;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_like);
        postKey = getIntent().getStringExtra(Constants.POST_KEY);
        count = getIntent().getIntExtra(Constants.NUM_VIEWS, 0);
        setTitle("Viewers");
        FireBaseUtils.mDatabaseViews.child(postKey).keepSynced(true);
        mPoemList = findViewById(R.id.lv_notice);
        progressBar = findViewById(R.id.pb_loading);
        tvEmpty = findViewById(R.id.tv_empty);
        tvEmpty.setText("No views yet, be the first to start reading");
        mPoemList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewsActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mPoemList.setLayoutManager(linearLayoutManager);
        if (count == 0) {
            progressBar.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
        bindView();
    }

    /**
     * Binds the view to a listview
     */
    private void bindView()
    {
        Query viewQuery = FireBaseUtils.mDatabaseViews.child(postKey).orderByChild(Constants.TIME_CREATED);
        FirebaseRecyclerAdapter<Notice,LikesActivity.NoticeViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Notice, LikesActivity.NoticeViewHolder>(
                Notice.class,R.layout.list_view,LikesActivity.NoticeViewHolder.class, viewQuery)
        {
            @Override
            protected void populateViewHolder(LikesActivity.NoticeViewHolder viewHolder, final Notice model, int position) {
                if (count != 0) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                }
                String time = TimeUtils.timeElapsed(model.getTimeCreated());
                viewHolder.tvUser.setText(model.getUser());
                viewHolder.tvTime.setText(time);
            }
        };
        mPoemList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }
}

