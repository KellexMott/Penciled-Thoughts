package com.techart.writersblock.utils;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.techart.writersblock.R;
import com.techart.writersblock.models.Devotion;
import com.techart.writersblock.models.Poem;
import com.techart.writersblock.models.Story;

import java.util.HashMap;
import java.util.Map;

/**
 * Has constants for Fire base variable names
 * Created by Kelvin on 11/09/2017.
 */

public final class FireBaseUtils {

   public static DatabaseReference mDatabaseStory = FirebaseDatabase.getInstance().getReference().child(Constants.STORY_KEY);
   public static DatabaseReference mDatabasePoems = FirebaseDatabase.getInstance().getReference().child(Constants.POEM_KEY);
   public static DatabaseReference mDatabaseDevotions = FirebaseDatabase.getInstance().getReference().child(Constants.DEVOTION_KEY);
   public static DatabaseReference mDatabaseChapters = FirebaseDatabase.getInstance().getReference().child(Constants.CHAPTER_KEY);
   public static DatabaseReference mDatabaseLike = FirebaseDatabase.getInstance().getReference().child(Constants.LIKE_KEY);
   public static DatabaseReference mDatabaseComment = FirebaseDatabase.getInstance().getReference().child(Constants.COMMENTS_KEY);
   public static DatabaseReference mDatabaseViews = FirebaseDatabase.getInstance().getReference().child(Constants.VIEWS_KEY);
   public static DatabaseReference mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child(Constants.USERS);
   public static DatabaseReference mDatabaseLibrary = FirebaseDatabase.getInstance().getReference().child(Constants.LIBRARY);
   public static DatabaseReference mSubscriptions = FirebaseDatabase.getInstance().getReference().child(Constants.SUBSCRIPTIONS_KEY);
   public static FirebaseAuth mAuth  = FirebaseAuth.getInstance();
   public static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public static StorageReference mStoragePhotos = FirebaseStorage.getInstance().getReference();


    private FireBaseUtils() {

    }

    public static void subscribeToNewPostNotification(){
        mSubscriptions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (FireBaseUtils.mAuth.getCurrentUser() != null && !dataSnapshot.hasChild(getUiD()))  {
                    mSubscriptions.child(getUiD()).setValue(Constants.NEW_POST_SUBSCRIPTION);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void isComplete(String status, DatabaseReference databaseReference) {
        if (status.equals("Complete")){
            databaseReference.keepSynced(false);
        }else{
            databaseReference.keepSynced(true);
        }
    }

    public static void subscribeTopic(final String postKey) {
        FirebaseMessaging.getInstance().subscribeToTopic(postKey);
    }

    public static String getAuthor() {
        return user.getDisplayName();
    }

    @NonNull
    public static String getUiD() {
        return mAuth.getCurrentUser().getUid();
    }

    public static void setLikeBtn(final String post_key, final ImageView btnLiked)
    {
        mDatabaseLike.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (FireBaseUtils.mAuth.getCurrentUser() != null && dataSnapshot.child(getUiD()).hasChild(Constants.AUTHOR_URL))
                {
                    btnLiked.setImageResource(R.drawable.ic_thumb_up_blue_24dp);
                }
                else
                {
                    btnLiked.setImageResource(R.drawable.ic_thumb_up_grey_24dp);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void setPostViewed(final String post_key, final ImageView btViewed)
    {
        mDatabaseViews.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (FireBaseUtils.mAuth.getCurrentUser() != null && dataSnapshot.child(getUiD()).hasChild(Constants.AUTHOR_URL))
                {
                    btViewed.setImageResource(R.drawable.ic_visibility_blue_24px);
                }
                else
                {
                    btViewed.setImageResource(R.drawable.ic_visibility_grey_24px);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void onStoryDisliked(String post_key) {
        mDatabaseStory.child(post_key).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Story story = mutableData.getValue(Story.class);
                if (story == null) {
                    return Transaction.success(mutableData);
                }
                story.setNumLikes(story.getNumLikes() - 1 );
                // Set value and report transaction success
                mutableData.setValue(story);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }

    public static void onStoryLiked(String post_key) {
        mDatabaseStory.child(post_key).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Story story = mutableData.getValue(Story.class);
                if (story == null) {
                    return Transaction.success(mutableData);
                }
                story.setNumLikes(story.getNumLikes() + 1 );
                // Set value and report transaction success
                mutableData.setValue(story);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {

            }
        });
    }



    public static void onDevotionDisliked(String post_key)
    {
        mDatabaseDevotions.child(post_key).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Devotion devotion = mutableData.getValue(Devotion.class);
                if (devotion == null) {
                    return Transaction.success(mutableData);
                }
                devotion.setNumLikes(devotion.getNumLikes() - 1 );

                // Set value and report transaction success
                mutableData.setValue(devotion);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {

            }
        });
    }

    public static void onDevotionLiked(String post_key) {
        mDatabaseDevotions.child(post_key).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Devotion devotion = mutableData.getValue(Devotion.class);
                if (devotion == null) {
                    return Transaction.success(mutableData);
                }
                devotion.setNumLikes(devotion.getNumLikes() + 1 );
                // Set value and report transaction success
                mutableData.setValue(devotion);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }

    public static void onPoemDisliked(String post_key)
    {
        mDatabasePoems.child(post_key).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Poem poem = mutableData.getValue(Poem.class);
                if (poem == null) {
                    return Transaction.success(mutableData);
                }
                poem.setNumLikes(poem.getNumLikes() - 1 );

                // Set value and report transaction success
                mutableData.setValue(poem);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {

            }
        });
    }

    public static void onPoemLiked(String post_key) {
        mDatabasePoems.child(post_key).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Poem poem = mutableData.getValue(Poem.class);
                if (poem == null) {
                    return Transaction.success(mutableData);
                }
                poem.setNumLikes(poem.getNumLikes() + 1 );
                // Set value and report transaction success
                mutableData.setValue(poem);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }


    public static void updateStatus(String status, String post_key) {
        mDatabaseStory.child(post_key).child(Constants.STORY_STATUS).setValue(status);
    }

    public static void updateCategory(String category, String post_key) {
        mDatabaseStory.child(post_key).child(Constants.STORY_CATEGORY).setValue(category);
    }

    public static void addStoryLike(Story model, String post_key) {
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.AUTHOR_URL,getUiD());
        values.put(Constants.USER, FireBaseUtils.getAuthor());
        values.put(Constants.POST_TITLE, model.getTitle());
        values.put(Constants.POEM_KEY, post_key);
        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
        mDatabaseLike.child(post_key).child(getUiD()).setValue(values);
    }

    public static void addDevotionLike(Devotion model, String post_key) {
        Map<String, Object> values = new HashMap<>();
        values.put(Constants.AUTHOR_URL, getUiD());
        values.put(Constants.USER, getAuthor());
        values.put(Constants.POST_TITLE, model.getTitle());
        values.put(Constants.POEM_KEY, post_key);
        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
        mDatabaseLike.child(post_key).child(getUiD()).setValue(values);
    }

    public static void addPoemLike(Poem model, String post_key) {
        Map<String, Object> values = new HashMap<>();
        values.put(Constants.AUTHOR_URL, getUiD());
        values.put(Constants.USER, getAuthor());
        values.put(Constants.POST_TITLE, model.getTitle());
        values.put(Constants.POEM_KEY, post_key);
        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
        mDatabaseLike.child(post_key).child(getUiD()).setValue(values);
    }

    public static void onPoemViewed(String post_key) {
        mDatabasePoems.child(post_key).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Poem poem = mutableData.getValue(Poem.class);
                if (poem == null) {
                    return Transaction.success(mutableData);
                }
                poem.setNumViews(poem.getNumViews() + 1 );
                // Set value and report transaction success
                mutableData.setValue(poem);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }
     public static void onDevotionViewed(String post_key) {
            mDatabaseDevotions.child(post_key).runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Devotion devotion = mutableData.getValue(Devotion.class);
                    if (devotion == null) {
                        return Transaction.success(mutableData);
                    }
                    devotion.setNumViews(devotion.getNumViews() + 1 );
                    // Set value and report transaction success
                    mutableData.setValue(devotion);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b,
                                       DataSnapshot dataSnapshot) {
                }
            });
        }
     public static void onStoryViewed(String post_key) {
            mDatabaseStory.child(post_key).runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Story story = mutableData.getValue(Story.class);
                    if (story == null) {
                        return Transaction.success(mutableData);
                    }
                    story.setNumViews(story.getNumViews() + 1 );
                    // Set value and report transaction success
                    mutableData.setValue(story);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b,
                                       DataSnapshot dataSnapshot) {
                }
            });
     }


    public static void addStoryView(Story model, String post_key) {
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.AUTHOR_URL,getUiD());
        values.put(Constants.USER, FireBaseUtils.getAuthor());
        values.put(Constants.POST_TITLE, model.getTitle());
        values.put(Constants.POEM_KEY, post_key);
        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
        mDatabaseViews.child(post_key).child(getUiD()).setValue(values);
    }

    public static void addDevotionView(Devotion model, String post_key) {
        Map<String, Object> values = new HashMap<>();
        values.put(Constants.AUTHOR_URL, getUiD());
        values.put(Constants.USER, getAuthor());
        values.put(Constants.POST_TITLE, model.getTitle());
        values.put(Constants.POEM_KEY, post_key);
        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
        mDatabaseViews.child(post_key).child(getUiD()).setValue(values);
    }

    public static void addPoemView(Poem model, String post_key) {
        Map<String, Object> values = new HashMap<>();
        values.put(Constants.AUTHOR_URL, getUiD());
        values.put(Constants.USER, getAuthor());
        values.put(Constants.POST_TITLE, model.getTitle());
        values.put(Constants.POEM_KEY, post_key);
        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
        mDatabaseViews.child(post_key).child(getUiD()).setValue(values);
    }

    public static void deleteStory(final String post_key)
    {
        mDatabaseStory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(post_key))  {
                    mDatabaseStory.child(post_key).removeValue();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void deleteChapter(final String post_key)
    {
        mDatabaseChapters.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(post_key))  {
                    mDatabaseChapters.child(post_key).removeValue();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void deleteDevotion(final String post_key)
    {
        mDatabaseDevotions.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(post_key))  {
                    mDatabaseDevotions.child(post_key).removeValue();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void deletePoem(final String post_key)
    {
        mDatabasePoems.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(post_key))  {
                    mDatabasePoems.child(post_key).removeValue();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void deleteComment(final String post_key)
    {
        mDatabaseComment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(post_key).hasChild(Constants.COMMENT_TEXT))  {
                    mDatabaseComment.child(post_key).removeValue();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void deleteLike(final String post_key)
    {
        mDatabaseLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(post_key))  {
                    mDatabaseLike.child(post_key).removeValue();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void deleteFromLib(final String post_key) {
        mDatabaseLibrary.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    FireBaseUtils.mDatabaseLibrary.child(getUiD()).child(post_key).removeValue();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
