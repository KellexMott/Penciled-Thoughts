package com.techart.writersblock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.techart.writersblock.constants.Constants;
import com.techart.writersblock.constants.FireBaseUtils;
import com.techart.writersblock.models.Notice;
import com.techart.writersblock.utils.TimeUtils;

/**
 * Retrieves and displays list of people who have viewed a particular post
 */
public class ViewsActivity extends AppCompatActivity {
    private String postKey;
    private RecyclerView mPoemList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_like);
        postKey = getIntent().getStringExtra(Constants.POST_KEY);
        setTitle("Viewers");
        FireBaseUtils.mDatabaseViews.child(postKey).keepSynced(true);
        mPoemList = findViewById(R.id.lv_notice);
        mPoemList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewsActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mPoemList.setLayoutManager(linearLayoutManager);
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
                String time = TimeUtils.timeElapsed(model.getTimeCreated());
                viewHolder.tvUser.setText(model.getUser());
                viewHolder.tvTime.setText(time);
            }
        };
        mPoemList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }
}

