package com.github.chaitanyabhardwaj.lexi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LexiAlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TAGGED", "GOT THE BROADCAST");
        LexiNotifications lexiNotifications = new LexiNotifications(context);
        lexiNotifications.buildNotificationChannel();
        lexiNotifications.buildNotification();
    }

}
