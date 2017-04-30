package com.a461group5.utbuysell.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.a461group5.utbuysell.MessageActivity;
import com.a461group5.utbuysell.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by irfanhasan on 3/31/17.
 */

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";



    private int maxNotificationId = 0;
    HashMap<String, NotificationBundle> activeNotifications = new HashMap<String, NotificationBundle>();


    /**
     * This class keeps track of active notification's history of unseen messages.
     */
    private class NotificationBundle {
        int id; //notitication id
        //TODO: decide if we want to display more than 1 message on notification or not.
        final int NUM_MESSAGES = 1; //we will only display the 5 latest messages
        LinkedList<String> messages = new LinkedList<String>(); //body of messages
        NotificationBundle(int id) {
            this.id = id;
        }

        /* Shifting of last displayed messages after first 5 unread
         * msg1
         * msg2
         * msg3
         * msg4
         * msg5
         *
         * msg2
         * msg3
         * msg4
         * msg5
         * msg6
         */
        void addMessage(String message) {
           if (messages.size() < NUM_MESSAGES) {
               //less than 5 total messages have been added
               messages.addLast(message);
           } else {
               //more than 5 messages have been added, need to shift as shown above
               messages.removeFirst();
               messages.addLast(message);
           }
        }

        String getMsgList() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < messages.size(); i++) {
                sb.append(messages.get(i));
                if (i != messages.size() - 1) {
                    sb.append("\n");
                }
            }
            return sb.toString();
        }
        int getId() {
            return id;
        }
    }
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]


    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        MessagingService getService() {
            return MessagingService.this;
        }
    }

    public void deleteNotification(String chatId) {
        activeNotifications.remove(chatId);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        sendNotification(remoteMessage.getData());
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
//    private void scheduleJob() {
//        // [START dispatch_job]
//        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
//        Job myJob = dispatcher.newJobBuilder()
//                .setService(MyJobService.class)
//                .setTag("my-job-tag")
//                .build();
//        dispatcher.schedule(myJob);
//        // [END dispatch_job]
//    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     */
    private void sendNotification(Map<String, String>  payload) {
        /**
         * Payload structure:
         * notification: {
             title: `{senderName} sent you a message.`,
            chatId: `{chatKey}`,
            senderId: `{senderId}`,
            body: `{messageBody}`
         }
         */
        Intent intent = new Intent(this, MessageActivity.class);
        String chatId = payload.get("chatId").toString();
        String title = payload.get("title").toString();
        String body = payload.get("body").toString();

        int notificationId = 0;
        NotificationBundle notification = null;
        //Check if a notification for this specific chat already exists
        if(!activeNotifications.containsKey(chatId)) {
            notificationId = maxNotificationId++; //increment max id after assigning current notification
            notification = new NotificationBundle(notificationId);
            activeNotifications.put(chatId, notification);
            notification.addMessage(body);
        } else {
            notification = activeNotifications.get(chatId);
            notificationId = notification.getId();
            notification.addMessage(body);
        }
        intent.putExtra("CHAT_ID", chatId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(notification.getMsgList())
                .setSmallIcon(R.mipmap.ic_launcher) //TODO: make an actual notification icon
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());

    }


}
