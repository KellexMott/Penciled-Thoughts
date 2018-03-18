package com.techart.writersblock;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.techart.writersblock.utils.Constants;
import com.techart.writersblock.utils.FireBaseUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity for editing title of posted story
 * Uses Story URL to map each story during an update
 * Created by Kelvin on 30/07/2017.
 */

public class EditNameDialog extends AppCompatActivity {

    private String newName;
    private EditText etDialogEditor;
    private TextView tvTitle;
    private TextView tvUpdate;
    private TextView tvCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_storyedit);
        etDialogEditor = findViewById(R.id.et_dialog_editor);
        tvTitle = findViewById(R.id.tv_title);
        tvUpdate = findViewById(R.id.tv_update);
        tvCancel = findViewById(R.id.tv_cancel);

        tvTitle.setText("Edit Name" );
        etDialogEditor.setText(FireBaseUtils.getAuthor());

        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newName = etDialogEditor.getText().toString().trim();
                if (validate(EditNameDialog.this,newName)) {
                    update();
                    Toast.makeText(getApplication(),"Update Successful...!",Toast.LENGTH_LONG).show();
                }
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

    private void update() {
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.USER_NAME, newName);
        FireBaseUtils.mDatabaseUsers.child(FireBaseUtils.getUiD()).updateChildren(values);
        finish();
    }

    public static boolean validate(Context context,String title)
    {
        if (title.isEmpty()) {
            Toast.makeText(context,"User name can not be empty",Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
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
