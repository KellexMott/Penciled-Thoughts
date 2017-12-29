package com.techart.writersblock;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etLogin;
    private EditText etPassword;
    private EditText etRepeatedPassword;

    private String firstPassword;
    private String repeatedPassword;
    private String name;
    private String email;


    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
//    private DatabaseReference mDatabaseUsers;


    private Button btRegister;
    private int iTemPosition = 0;
    private String signingInAs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(this);
        etUsername = (EditText) findViewById(R.id.et_username);
        etLogin = (EditText) findViewById(R.id.et_login);
        etPassword = (EditText) findViewById(R.id.et_password);
        etRepeatedPassword = (EditText) findViewById(R.id.et_repeatPassword);
        btRegister = (Button) findViewById(R.id.bt_register);
        btRegister.setClickable(true);

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if (validateCredentials())
            {
                startRegister();
            }
            }
        });

        /*final String[] options = new String[] {"Click me", "Reader", "Writer"};

        Spinner signUpAs = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String> pagesAdapter = new ArrayAdapter<String>(RegisterActivity.this, R.layout.tv_dropdown, options);
        pagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pagesAdapter.notifyDataSetChanged();

        signUpAs.setAdapter(pagesAdapter);
        signUpAs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                iTemPosition = position;
                signingInAs = options[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
    }

    public void onRadioButtonClicked(View view) {
        ((RadioButton) view).setChecked(((RadioButton) view).isChecked());
        signingInAs = ((RadioButton) view).getText().toString();
        Toast.makeText(this, "Story marked as " + signingInAs, Toast.LENGTH_LONG).show();
    }

    private void startRegister() {
        mProgress.setMessage("Signing Up  ...");
        mProgress.show();
        mAuth.createUserWithEmailAndPassword(email,firstPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                String user_id = mAuth.getCurrentUser().getUid();
               // DatabaseReference current_user_db = mDatabaseUsers.child(user_id);
                Map<String,Object> values = new HashMap<>();
                values.put("name",name);
                values.put("imageUrl","default");
                values.put("signedAs",signingInAs);
                /*
                current_user_db.child("name").setValue(name);
                current_user_db.child("imageUrl").setValue("default");
                */
                FireBaseUtils.mDatabaseUsers.child(user_id).setValue(values);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();
                if (user != null) {
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "User profile updated.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                    mProgress.dismiss();
                    Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                } else {
                    mProgress.dismiss();
                    Toast.makeText(RegisterActivity.this, "Error encountered, Try again later", Toast.LENGTH_LONG).show();

                }
            }
            else
            {
                if(task.getException() instanceof FirebaseAuthUserCollisionException)
                {
                    Toast.makeText(RegisterActivity.this,"User already exits, use another email address",Toast.LENGTH_LONG ).show();
                }
                else
                {
                    Toast.makeText(RegisterActivity.this,"Ensure that your internet is working",Toast.LENGTH_LONG ).show();
                }
            }
            }
        });
    }

    private boolean validateCredentials()
    {
        firstPassword =  etPassword.getText().toString().trim();
        repeatedPassword =  etRepeatedPassword.getText().toString().trim();
        name =  etUsername.getText().toString().trim();
        email = etLogin.getText().toString().trim();
        return EditorUtils.dropDownValidator(getApplicationContext(), signingInAs) &&
                EditorUtils.isEmpty(getApplicationContext(),name,"username") &&
                EditorUtils.isEmpty(getApplicationContext(),email,"email") &&
                EditorUtils.isEmpty(getApplicationContext(),firstPassword,"password") &&
                EditorUtils.doPassWordsMatch(getApplicationContext(),firstPassword,repeatedPassword);
    }
}

