package com.techart.writersblock;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Kelvin on 30/05/2017.
 */

public class StoryCursorAdapter extends CursorAdapter {
    Context context;
    LayoutInflater inflater;
    public StoryCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.item_doclist, parent, false
        );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String storyTitle = cursor.getString(
                cursor.getColumnIndex(WritersBlockContract.StoryEntry.STORY_TITLE));

        String timeCreated = cursor.getString(
                cursor.getColumnIndex(WritersBlockContract.StoryEntry.STORY_CREATED));

        String hasUrl = cursor.getString(
                cursor.getColumnIndex(WritersBlockContract.StoryEntry.STORY_REFID));


        TextView tvStoryTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvStoryTitle.setText(storyTitle);

        TextView time = (TextView) view.findViewById(R.id.tv_timeCreated);
        time.setText(timeCreated);
        ImageView im = (ImageView) view.findViewById(R.id.ivFile);



        if (hasUrl.length() > 5)
        {

             im.setImageResource(R.drawable.ic_book_blue);
        }
        else
        {
             im.setImageResource(R.drawable.ic_book_grey);
        }

    }
}
