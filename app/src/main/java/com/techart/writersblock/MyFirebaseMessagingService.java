package com.techart.writersblock;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * For handling notifications
 * Created by kelvin on 1/24/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
       if(remoteMessage.getData().size() > 0){
           Map<String,String>payload = remoteMessage.getData();
           showNotifications(payload);
       }
    }

    private void showNotifications(Map<String,String> payload){
        Intent intent = new Intent(this,MainActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent  = taskStackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notificationBuilder = new   NotificationCompat.Builder(this)
                .setContentTitle(payload.get("title")) //the "title" value you sent in your notification
                .setContentText(payload.get("body")) //ditto
                .setAutoCancel(true) //dismisses the notification on click
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

}
