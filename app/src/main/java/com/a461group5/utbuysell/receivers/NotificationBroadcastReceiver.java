package com.a461group5.utbuysell.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.a461group5.utbuysell.services.MessagingService;

/**
 * Created by irfanhasan on 5/1/17.
 */

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        String id = intent.getStringExtra("chatId");
        if(action.equals("notification_cancelled"))
        {
            MessagingService.removeNotification(id);
        }
    }
}
