package com.itu.software.ituhermes;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.itu.software.ituhermes.Wrapper.User;
import com.itu.software.ituhermes.connection.HTTPClient;

import org.json.JSONObject;

import java.util.Random;

public class FirebaseMessageService extends FirebaseMessagingService {
    private static final String TAG = "Firebase";
    private static final int NOTIFICATION_ID = 9721021;

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    public void sendRegistrationToServer(String token) {
        try {
            JSONObject body = new JSONObject();
            body.put("fbToken", token);
            String path = String.format("user/fbToken?token=%s", User.getCurrentUser().getToken());
            HTTPClient.put(path, body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {
            String type = remoteMessage.getData().get("type");
            String key = String.valueOf(new Random(NOTIFICATION_ID));
            JSONObject data = new JSONObject();
            switch (type) {
                case "post":
                    SharedPreferences sharedPosts = this.getSharedPreferences("PostNotification", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPosts.edit();
                    data.put("sender", remoteMessage.getData().get("sender"));
                    data.put("post", remoteMessage.getData().get("post"));
                    data.put("date", remoteMessage.getData().get("date"));
                    data.put("topicId", Integer.parseInt(remoteMessage.getData().get("topicId")));
                    editor.putString(key, data.toString());
                    editor.apply();
                    break;
                case "topic":
                    SharedPreferences sharedTopics = this.getSharedPreferences("TopicNotification", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = sharedTopics.edit();
                    data.put("tag", remoteMessage.getData().get("tag"));
                    data.put("title", remoteMessage.getData().get("title"));
                    data.put("topicId", Integer.parseInt(remoteMessage.getData().get("topicId")));
                    editor1.putString(key, data.toString());
                    editor1.apply();
                    break;
            }
            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID);
            Intent intent = new Intent(this, NotificationActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
            Notification notification = builder
                    .setContentTitle(this.getString(R.string.notification_title))
                    .setContentText(this.getString(R.string.notification_text))
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setSmallIcon(R.drawable.ic_launcher_foreground).build();
            notificationManager.notify(NOTIFICATION_ID, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
