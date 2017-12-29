package com.techart.writersblock;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * Created by Kelvin on 10/08/2017.
 */

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);
        setTitle("Help");

        String infor = getString(R.string.help);
        WebView webView = (WebView)findViewById(R.id.wv_help);
        webView.loadDataWithBaseURL(null,infor,"text/html","utf-8",null);
    }
}
