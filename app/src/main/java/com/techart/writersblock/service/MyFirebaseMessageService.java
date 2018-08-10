package com.techart.writersblock.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.techart.writersblock.MainActivity;
import com.techart.writersblock.constants.Constants;

import java.util.Map;

/**
 * Created by kelvin on 1/13/18.
 */

public class MyFirebaseMessageService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s){
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        if(remoteMessage.getData().size() > 0){
            Map<String,String> data = remoteMessage.getData();
            String header = data.get("header");
            String message = data.get("message");

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, Constants.CHANNEL_ID);
            mBuilder.setContentTitle(header);
            mBuilder.setContentText(message);

            Intent resultIntent = new Intent(this, MainActivity.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);

            taskStackBuilder.addNextIntent(resultIntent);
            PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, mBuilder.build());
        }
    }
}
