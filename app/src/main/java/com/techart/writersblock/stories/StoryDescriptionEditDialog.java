package com.techart.writersblock.stories;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.techart.writersblock.R;
import com.techart.writersblock.utils.Constants;
import com.techart.writersblock.utils.FireBaseUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity for editing Description of posted story
 * Uses Story URL to map each story during an update
 * Created by Kelvin on 30/07/2017.
 */

public class StoryDescriptionEditDialog extends AppCompatActivity {

    private String oldText;
    private String newText;
    private EditText etDialogEditor;
    private TextView tvTitle;
    private TextView tvUpdate;
    private TextView tvCancel;
    private String storyUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_storyedit);
        etDialogEditor = findViewById(R.id.et_dialog_editor);
        tvTitle = findViewById(R.id.tv_title);
        tvUpdate = findViewById(R.id.tv_update);
        tvCancel = findViewById(R.id.tv_cancel);

        storyUrl = getIntent().getStringExtra(Constants.STORY_REFID);
        oldText = getIntent().getStringExtra(Constants.STORY_DESCRIPTION);
        tvTitle.setText("Edit Description");
        etDialogEditor.setText(oldText);

        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
                Toast.makeText(getApplication(),"Update Successful...!",Toast.LENGTH_LONG).show();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(),"Update Cancelled...!",Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void update()
    {
        newText = etDialogEditor.getText().toString().trim();
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.STORY_DESCRIPTION,newText);
        FireBaseUtils.mDatabaseStory.child(storyUrl).updateChildren(values);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE)
                        {
                            finish();
                        }
                        if (button == DialogInterface.BUTTON_NEGATIVE)
                        {
                            dialog.dismiss();
                        }
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Changes will not be saved...!")
                .setPositiveButton("Exit", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener)
                .show();
    }
}
