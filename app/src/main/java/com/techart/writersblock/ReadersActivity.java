package com.techart.writersblock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.techart.writersblock.constants.Constants;
import com.techart.writersblock.constants.FireBaseUtils;
import com.techart.writersblock.models.Users;
import com.techart.writersblock.utils.TimeUtils;

/**
 * Retrieves and displays list of people who have viewed a particular post
 */
public class ReadersActivity extends AppCompatActivity
{
    String title;
    private RecyclerView mPoemList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_like);
        setTitle("Readers");
        FireBaseUtils.mDatabaseUsers.keepSynced(true);
        mPoemList = findViewById(R.id.lv_notice);
        mPoemList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ReadersActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mPoemList.setLayoutManager(linearLayoutManager);
        bindView();
    }

    /**
     * Binds the view to a listview
     */
    private void bindView() {
        Query viewQuery = FireBaseUtils.mDatabaseUsers.orderByChild(Constants.SIGNED_IN_AS).equalTo("Reader");
        //ToDo fully implement class and method
        FirebaseRecyclerAdapter<Users,LikesActivity.NoticeViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, LikesActivity.NoticeViewHolder>(
                Users.class,R.layout.list_view,LikesActivity.NoticeViewHolder.class, viewQuery)
        {
            @Override
            protected void populateViewHolder(LikesActivity.NoticeViewHolder viewHolder, final Users model, int position) {
                if (model.getTimeCreated() != null){
                    String time = TimeUtils.timeElapsed(model.getTimeCreated());
                    viewHolder.tvTime.setText(time);
                }
                viewHolder.tvUser.setText(model.getName());

            }
        };
        mPoemList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }
}

