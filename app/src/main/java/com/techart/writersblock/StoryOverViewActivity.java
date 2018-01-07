package com.techart.writersblock;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Presents a provision for adding Story Title, Category and Description
 * Invokes StoryEditorActivity
 */
public class StoryOverViewActivity extends AppCompatActivity {

    private Spinner spCategory;

    private int iTemPosition;
    private String category;

    private EditText etStoryTitle;
    private EditText etStoryDescription;

    private String title;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_storyoverview);
        setTitle(Constants.CREATE_STORY);

        final String[] categories = {"Select Category", "Action", "Drama ", "Fiction ","Romance "};
        spCategory = (Spinner) findViewById(R.id.categories);

        ArrayAdapter<String> pagesAdapter = new ArrayAdapter<>(StoryOverViewActivity.this, R.layout.spinnertxt, categories);
        pagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pagesAdapter.notifyDataSetChanged();
        spCategory.setAdapter(pagesAdapter);
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                iTemPosition = position;
                category = spCategory.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        etStoryTitle = (EditText)findViewById(R.id.editStoryTitle);
        etStoryDescription = (EditText)findViewById(R.id.editStoryDescription);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_nextbutton,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        setValues();
        switch (id) {
            case R.id.action_next:
                if (EditorUtils.validateEntry(this,iTemPosition,title, etStoryDescription))
                {
                    Intent intent = new Intent(StoryOverViewActivity.this, StoryEditorActivity.class);
                    intent.putExtra("Category",category);
                    intent.putExtra("Title",title);
                    intent.putExtra("Description", description);
                    startActivity(intent);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setValues()
    {
        title = etStoryTitle.getText().toString().trim();
        description = etStoryDescription.getText().toString().trim();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Create chapter to save changes");
        builder.setPositiveButton("Stay in editor", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        })
                .setNegativeButton("Exit editor", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
