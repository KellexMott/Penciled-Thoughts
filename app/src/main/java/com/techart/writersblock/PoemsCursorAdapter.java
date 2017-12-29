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

public class PoemsCursorAdapter extends CursorAdapter {
    Context context;
    public PoemsCursorAdapter(Context context, Cursor c, int flags) {
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
                cursor.getColumnIndex(WritersBlockContract.PoemEntry.POEM_TITLE));
        String timeCreated = cursor.getString(
                cursor.getColumnIndex(WritersBlockContract.PoemEntry.POEM_CREATED));
        String isPosted = cursor.getString(
                cursor.getColumnIndex(WritersBlockContract.PoemEntry.POEM_FIREBASEURL));


        TextView tv = (TextView) view.findViewById(R.id.tvTitle);
        TextView tm = (TextView) view.findViewById(R.id.tv_timeCreated);
        ImageView im = (ImageView) view.findViewById(R.id.ivFile);
        if (isPosted.length() > 5)
        {

            im.setImageResource(R.drawable.ic_file_blue);
        }
        else
        {
            im.setImageResource(R.drawable.ic_file_grey);
        }
        tv.setText(noteTitle);
        tm.setText(timeCreated);
    }
}
