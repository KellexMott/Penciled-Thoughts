package com.techart.writersblock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

/**
 * Contains information about how to
 * Created by Kelvin on 10/08/2017.
 */

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);
        setTitle("Help");

        String information = getString(R.string.help);
        WebView webView = (WebView)findViewById(R.id.wv_help);
        webView.loadDataWithBaseURL(null,information,"text/html","utf-8",null);
    }
}
