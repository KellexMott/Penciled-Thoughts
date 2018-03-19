package com.techart.writersblock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.techart.writersblock.models.Story;
import com.techart.writersblock.utils.Constants;
import com.techart.writersblock.utils.FireBaseUtils;
import com.techart.writersblock.utils.ImageUtils;
import com.techart.writersblock.utils.NumberUtils;
import com.techart.writersblock.utils.TimeUtils;
import com.techart.writersblock.viewholders.StoryViewHolder;
public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageView ibSearch;
    private RecyclerView rvSearchResults;
    private String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        etSearch = findViewById(R.id.et_search);
        rvSearchResults = findViewById(R.id.rv_search);
        rvSearchResults.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvSearchResults.setLayoutManager(linearLayoutManager);
        initSearch();
        etSearch.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                searchText = etSearch.getText().toString().trim();
                if (searchText.isEmpty()){
                    initSearch();
                } else {
                    firebaseSearch();
                }
            }
        });
    }

    private void initSearch() {
        FirebaseRecyclerAdapter<Story,StoryViewHolder> fireBaseRecyclerAdapter = new FirebaseRecyclerAdapter<Story, StoryViewHolder>(
                Story.class,R.layout.item_storyrow,StoryViewHolder.class, FireBaseUtils.mDatabaseStory)
        {
            @Override
            protected void populateViewHolder(StoryViewHolder viewHolder, final Story model, int position) {
                final String post_key = getRef(position).getKey();
                FireBaseUtils.mDatabaseLike.child(post_key).keepSynced(true);
                viewHolder.tvTitle.setText(model.getTitle());
                viewHolder.setTint(SearchActivity.this);
                viewHolder.tvCategory.setText(getString(R.string.post_category,model.getCategory()));
                viewHolder.tvStatus.setText(getString(R.string.post_status,model.getStatus()));
                viewHolder.tvChapters.setText(getString(R.string.post_chapters, NumberUtils.setPlurality(model.getChapters(),"Chapter")));
                viewHolder.setIvImage(SearchActivity.this, ImageUtils.getStoryUrl(model.getCategory().trim(),model.getTitle()));
                viewHolder.tvAuthor.setText(getString(R.string.post_author,model.getAuthor()));

                if (model.getNumLikes() != null) {
                    viewHolder.tvNumLikes.setText(String.format("%s",model.getNumLikes().toString()));
                }

                if (model.getNumComments() != null) {
                    viewHolder.tvNumComments.setText(String.format("%s",model.getNumComments().toString()));
                }

                if (model.getNumViews() != null) {
                    viewHolder.tvNumViews.setText(String.format("%s",model.getNumViews().toString()));
                }
                if (model.getTimeCreated() != null) {
                    String time = TimeUtils.timeElapsed(model.getTimeCreated());
                    viewHolder.tvTime.setText(time);
                }

                viewHolder.tvAuthor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent readPoemIntent = new Intent(SearchActivity.this,AuthorsProfileActivity.class);
                        readPoemIntent.putExtra(Constants.POST_AUTHOR, model.getAuthor());
                        startActivity(readPoemIntent);
                    }
                });
                viewHolder.setLikeBtn(post_key);
                viewHolder.setPostViewed(post_key);

                if (model.getLastUpdate() != null) {
                    Boolean t = TimeUtils.currentTime() - model.getLastUpdate() < TimeUtils.MILLISECONDS_DAY; //&& res;
                    viewHolder.setVisibility(t);
                }

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //showDescription(model.getDescription(),post_key,model);
                    }
                });
            }
        };
        rvSearchResults.setAdapter(fireBaseRecyclerAdapter);
        fireBaseRecyclerAdapter.notifyDataSetChanged();
    }

    private void firebaseSearch() {
       /* View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }*/
            FirebaseRecyclerAdapter<Story,StoryViewHolder> fireBaseRecyclerAdapter = new FirebaseRecyclerAdapter<Story, StoryViewHolder>(
                    Story.class,R.layout.item_storyrow,StoryViewHolder.class, searchIn(searchText))
            {
                @Override
                protected void populateViewHolder(StoryViewHolder viewHolder, final Story model, int position) {
                    final String post_key = getRef(position).getKey();
                    FireBaseUtils.mDatabaseLike.child(post_key).keepSynced(true);
                    viewHolder.tvTitle.setText(model.getTitle());
                    viewHolder.setTint(SearchActivity.this);
                    viewHolder.tvCategory.setText(getString(R.string.post_category,model.getCategory()));
                    viewHolder.tvStatus.setText(getString(R.string.post_status,model.getStatus()));
                    viewHolder.tvChapters.setText(getString(R.string.post_chapters, NumberUtils.setPlurality(model.getChapters(),"Chapter")));
                    viewHolder.setIvImage(SearchActivity.this, ImageUtils.getStoryUrl(model.getCategory().trim(),model.getTitle()));
                    viewHolder.tvAuthor.setText(getString(R.string.post_author,model.getAuthor()));

                    if (model.getNumLikes() != null) {
                        viewHolder.tvNumLikes.setText(String.format("%s",model.getNumLikes().toString()));
                    }

                    if (model.getNumComments() != null) {
                        viewHolder.tvNumComments.setText(String.format("%s",model.getNumComments().toString()));
                    }

                    if (model.getNumViews() != null) {
                        viewHolder.tvNumViews.setText(String.format("%s",model.getNumViews().toString()));
                    }
                    if (model.getTimeCreated() != null) {
                        String time = TimeUtils.timeElapsed(model.getTimeCreated());
                        viewHolder.tvTime.setText(time);
                    }

                    viewHolder.tvAuthor.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent readPoemIntent = new Intent(SearchActivity.this,AuthorsProfileActivity.class);
                            readPoemIntent.putExtra(Constants.POST_AUTHOR, model.getAuthor());
                            startActivity(readPoemIntent);
                        }
                    });
                    viewHolder.setLikeBtn(post_key);
                    viewHolder.setPostViewed(post_key);

                    if (model.getLastUpdate() != null) {
                        Boolean t = TimeUtils.currentTime() - model.getLastUpdate() < TimeUtils.MILLISECONDS_DAY; //&& res;
                        viewHolder.setVisibility(t);
                    }

                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //showDescription(model.getDescription(),post_key,model);
                        }
                    });
                }
            };
        rvSearchResults.setAdapter(fireBaseRecyclerAdapter);
            fireBaseRecyclerAdapter.notifyDataSetChanged();
    }

    private Query searchIn(String searchText){
        return FireBaseUtils.mDatabaseStory.orderByChild("title").startAt(searchText).endAt(searchText + "\uf8ff");
    }
}
