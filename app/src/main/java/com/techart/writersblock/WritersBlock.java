package com.techart.writersblock;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.firebase.client.Firebase;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Initializes Fire base context
 * Created by Kelvin on 31/05/2017.
 */

public class WritersBlock extends Application {
    @Override
    public void onCreate()
    {
        super.onCreate();
        Firebase.setAndroidContext(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        TypefaceUtil.overrideFont(getApplicationContext(),"SERIF","fonts/time-new-roman.ttf");
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
