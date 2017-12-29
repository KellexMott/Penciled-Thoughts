package com.techart.writersblock;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private ProgressDialog mProgress;

    private EditText mUsername;
    private EditText mPassWord;
    private Button mLogin;
    private Button mRegister;

    // Firebase references.
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FireBaseUtils.mDatabaseUsers;
        mUsername = (EditText)findViewById(R.id.loginUsername);
        mPassWord = (EditText)findViewById(R.id.loginPassword);
        mLogin = (Button)findViewById(R.id.btLogin);
        mRegister = (Button)findViewById(R.id.btSignUp);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validUserCredentials();
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveNetworkConnection())
                {
                    Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                    registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(registerIntent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No internet connection, kindly ensure you have internet", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void validUserCredentials() {
        mProgress = new ProgressDialog(LoginActivity.this);
        mProgress.setMessage("Logging in ...");
        mProgress.show();
        String email = mUsername.getText().toString().trim();
        String password = mPassWord.getText().toString().trim();
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty((password)))
        {
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    mProgress.dismiss();
                    if(task.isSuccessful())
                    {
                        validUsersExistance();
                    }else
                    {
                        if (task.getException() instanceof FirebaseAuthInvalidUserException)
                        {
                            Toast.makeText(getApplicationContext(),"Invalid email, enter the email you registered with", Toast.LENGTH_LONG).show();
                        }
                        else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                        {
                            Toast.makeText(getApplicationContext(),"Wrong password, enter the password you registered with", Toast.LENGTH_LONG).show();
                        }
                        else if(haveNetworkConnection())
                        {
                            Toast.makeText(getApplicationContext(),"Connected", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"No internet connection, Ensure you are connected and try again", Toast.LENGTH_LONG).show();
                        }

                    }
                }
            });
        }
        else
        {
            mProgress.dismiss();
            Toast.makeText(getApplicationContext(),"Text or Email field empty", Toast.LENGTH_LONG).show();
        }

    }

    private boolean haveNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null)
        {
            NetworkInfo netWorkInfo = cm.getActiveNetworkInfo();
            if (netWorkInfo != null && netWorkInfo.getState() == NetworkInfo.State.CONNECTED)
            {
                return true;
            }
        }
        return false;
    }

    private void validUsersExistance() {
        final String userId = mAuth.getCurrentUser().getUid();
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userId))
                {
                    Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }else
                {
                    Toast.makeText(getApplicationContext(),"You need to setup an Account", Toast.LENGTH_LONG).show();
                    Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                    registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(registerIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE)
                        {
                            finishAffinity();
                        }
                        if (button == DialogInterface.BUTTON_NEGATIVE)
                        {
                            dialog.dismiss();
                        }
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Exit application? ")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener)
                .show();
    }
}

