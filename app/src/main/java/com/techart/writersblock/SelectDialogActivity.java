package com.techart.writersblock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Kelvin on 30/07/2017.
 */

public class SelectDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_selecttype);
        ListView list = (ListView) findViewById(R.id.list);
        final String[] options = new String[] { "Devotion", "Poem", "Story" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, options);
        list.setAdapter(adapter);
        list.setStackFromBottom(true);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (options[position]) {
                    case "Devotion" :
                        Intent editorDevotionIntent = new Intent(SelectDialogActivity.this, DevotionEditorActivity.class);
                        startActivity(editorDevotionIntent);
                        break;
                    case "Poem":
                        Intent editorPoemIntent = new Intent(SelectDialogActivity.this, EditorActivity.class);
                        startActivity(editorPoemIntent);
                        break;
                    case "Story":
                        Intent editorStoryIntent = new Intent(SelectDialogActivity.this, StoryOverViewActivity.class);
                        startActivity(editorStoryIntent);
                        break;
                }
            }

        });
    }
}
