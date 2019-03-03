package com.techart.wb.chapters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.techart.wb.R;
import com.techart.wb.constants.Constants;
import com.techart.wb.constants.FireBaseUtils;
import com.techart.wb.models.Chapter;
import com.techart.wb.viewholders.ChapterViewHolder;


public class OnlineChapterListActivity extends AppCompatActivity {
    private RecyclerView mPoemList;
    private String storyUrl;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabrecyclerviewer);
        storyUrl = getIntent().getStringExtra(Constants.STORY_REFID);
        setTitle("Chapters");
        mPoemList = findViewById(R.id.poem_list);
        progressBar = findViewById(R.id.pb_loading);
        mPoemList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(OnlineChapterListActivity.this);
        mPoemList.setLayoutManager(linearLayoutManager);
        bindView();
    }

    private void bindView() {
        if(storyUrl != null){
            FirebaseRecyclerAdapter<Chapter, ChapterViewHolder> firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<Chapter, ChapterViewHolder>(
                    Chapter.class,R.layout.item_chapter,ChapterViewHolder.class, FireBaseUtils.mDatabaseChapters.child(storyUrl)) {
                @Override
                protected void populateViewHolder(ChapterViewHolder viewHolder, final Chapter model, int position) {
                    final String post_key = getRef(position).getKey();
                    progressBar.setVisibility(View.GONE);
                    viewHolder.tvTitle.setText(model.getChapterTitle());
                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent readIntent = new Intent(OnlineChapterListActivity.this,ChapterEditorOnlineActivity.class);
                            readIntent.putExtra(Constants.POST_KEY,post_key);
                            readIntent.putExtra(Constants.STORY_REFID,storyUrl);
                            readIntent.putExtra(Constants.CHAPTER_TITLE,model.getChapterTitle());
                            readIntent.putExtra(Constants.CHAPTER_CONTENT,model.getContent());
                            startActivity(readIntent);
                        }
                    });
                }
            };
            mPoemList.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this,"Kindly reload",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed(){
        setResult(RESULT_OK,getIntent());
        finish();
    }
}

