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

public class DevotionCursorAdapter extends CursorAdapter {
    Context context;
    public DevotionCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.item_filelist, parent, false
        );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String noteTitle = cursor.getString(
                cursor.getColumnIndex(WritersBlockContract.SpiritualEntry.SPIRITUAL_TITLE));
        String timeCreated = cursor.getString(
                cursor.getColumnIndex(WritersBlockContract.SpiritualEntry.SPIRITUAL_CREATED));
        String isPosted = cursor.getString(
                cursor.getColumnIndex(WritersBlockContract.SpiritualEntry.SPIRITUAL_FIREBASE_URL));

        TextView title = (TextView) view.findViewById(R.id.tvTitle);
        title.setText(noteTitle);
        TextView time = (TextView) view.findViewById(R.id.tv_timeCreated);
        time.setText(timeCreated);
        ImageView iv = (ImageView) view.findViewById(R.id.ivFile);



        if (isPosted.length() > 5)
        {

            iv.setImageResource(R.drawable.ic_file_blue);
        }
        else
        {
            iv.setImageResource(R.drawable.ic_file_grey);
        }

    }
}
