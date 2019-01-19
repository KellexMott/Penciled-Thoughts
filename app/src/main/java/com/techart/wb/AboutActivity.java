package com.techart.wb;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Will be available to admins for postings articles
 */
public class AboutActivity extends AppCompatActivity {
    private TextView tvAppVersion;
    private TextView tvFaceBook;
    private TextView tvLinkedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar);
        tvAppVersion = findViewById(R.id.appVersion);
        tvFaceBook = findViewById(R.id.tv_facebook);
        tvLinkedIn = findViewById(R.id.tv_linked);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getAppVersion();

        tvFaceBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://web.facebook.com/TechArtZambia/");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        tvLinkedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://www.linkedin.com/in/kelvin-chiwele-b36224167");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
    }

    private void getAppVersion() {
        try {
            String version = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            tvAppVersion.setText(getResources().getString(R.string.app_version,version));
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "Could not read app version", Toast.LENGTH_LONG).show();
        }
    }
}
