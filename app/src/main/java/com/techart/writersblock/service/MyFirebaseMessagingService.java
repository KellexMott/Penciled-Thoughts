package com.techart.writersblock.service;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.techart.writersblock.NotifcationsActivity;
import com.techart.writersblock.R;
import com.techart.writersblock.constants.Constants;

import java.util.Map;

/**
 * For handling notifications
 * Created by kelvin on 1/24/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
       if(remoteMessage.getData().size() > 0){
           Map<String,String> payload = remoteMessage.getData();
           showNotifications(payload);
       }
    }

    private void showNotifications(Map<String,String> payload){
        Intent intent;
        /*if (payload.get("click_action") != null && payload.get("click_action").equals("Story_Notice")){
            intent = new Intent(this, OnStoryNotificationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("title",payload.get("title"));
        }else  {*/
        /*
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.POST_AUTHOR,FireBaseUtils.getAuthor());
        values.put(Constants.AUTHOR_URL,FireBaseUtils.getAuthor());
        values.put(Constants.POST_URL,FireBaseUtils.getAuthor());
        values.put(Constants.POST_TITLE,payload.get("title"));
        values.put(Constants.POST_TYPE,FireBaseUtils.getAuthor());
        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
        FireBaseUtils.mDatabaseNotifications.child(FireBaseUtils.getUiD()).push().setValue(values);*/

        intent = new Intent(this, NotifcationsActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent  = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new   NotificationCompat.Builder(this, Constants.CHANNEL_ID)
        .setContentTitle(payload.get("title")) //the "title" value you sent in your notification
        .setContentText(payload.get("body")) //ditto
        .setSmallIcon(R.mipmap.ic_launcher)
        .setAutoCancel(true) //dismisses the notification on click
                .setColor(getResources().getColor(R.color.colorPrimary))
        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
