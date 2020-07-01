package com.cb.softwares.doctorapp.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.cb.softwares.doctorapp.R;
import com.cb.softwares.doctorapp.activity.ChatMessageActivity;
import com.cb.softwares.doctorapp.activity.MainActivity;
import com.cb.softwares.doctorapp.sharedpreference.SharedPreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        Log.w("FCM", "token  : " + s);

        updateToken(s);

    }


    private void updateToken(String token) {


        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(this);
        Log.w("FCM", "update token called");
        sharedPreferenceManager.setFCMKEY(token);
      /*  DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(user.getUid()).setValue(token1);*/
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sented = remoteMessage.getData().get("sented");
        String type = remoteMessage.getData().get("type");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            if (type.equalsIgnoreCase("1")) {
                if (sented.equalsIgnoreCase(user.getUid())) {

                    sendNotification(remoteMessage, type);
                }
            } else {
                sendNotification(remoteMessage, type);
            }
        }
    }

    private void sendNotification(RemoteMessage remoteMessage, String type) {

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");


        RemoteMessage.Notification notification = remoteMessage.getNotification();

        String channelId = type;
        String channel = "default";

        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = null;

        if (type.equalsIgnoreCase("1")) {
            channel = "Chat";
            intent = new Intent(this, ChatMessageActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", user);
            bundle.putString("name", "Vijay");
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            channel = "appointment";
            intent = new Intent(this, MainActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(channelId, channel, NotificationManager.IMPORTANCE_DEFAULT);
            noti.createNotificationChannel(notificationChannel);
        }


        int i = 1;

        if (j > 0) {
            i = j;
        }
        noti.notify(i, builder.build());


    }


}
