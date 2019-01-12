package com.techart.wb;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Will be available to admins for postings articles
 */
public class AboutActivity extends AppCompatActivity {
    private TextView tvAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar);
        tvAppVersion = findViewById(R.id.appVersion);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getAppVersion();
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
