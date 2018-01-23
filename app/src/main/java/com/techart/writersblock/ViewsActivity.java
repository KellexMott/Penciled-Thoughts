package com.techart.writersblock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;

/**
 * Retrieves and displays list of people who have viewed a particular post
 */
public class ViewsActivity extends AppCompatActivity
{
    String title;
    String postKey;
    private RecyclerView mPoemList;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_like);
        postKey = getIntent().getStringExtra(Constants.POST_KEY);
        mAuth = FirebaseAuth.getInstance();
        setTitle("Viewers");
        FireBaseUtils.mDatabaseViews.child(postKey).keepSynced(true);
        mPoemList = (RecyclerView) findViewById(R.id.lv_notice);
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
        //ToDo fully implement class and method
        FirebaseRecyclerAdapter<Notice,LikesActivity.NoticeViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Notice, LikesActivity.NoticeViewHolder>(
                Notice.class,R.layout.list_view,LikesActivity.NoticeViewHolder.class, viewQuery)
        {
            @Override
            protected void populateViewHolder(LikesActivity.NoticeViewHolder viewHolder, final Notice model, int position) {
                String time = TimeUtils.timeElapsed(model.getTimeCreated());
                viewHolder.tvUser.setText(model.getUser());
                viewHolder.setTypeFace(ViewsActivity.this);
                viewHolder.tvTime.setText(time);
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //ToDo Call listview
                    }
                });
            }
        };
        mPoemList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }
}

