package com.demo.java.hybrid;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseRoger extends FirebaseMessagingService
{
    @Override
    public void onMessageReceived (RemoteMessage remoteMessage)
    {
        Allo.i ("onMessageReceived " + getClass ());

        String link = "";
        String title = "";
        String message = "";
        String from = remoteMessage.getFrom ();

        if (0 < remoteMessage.getData ().size ())
        {
            link = remoteMessage.getData ().get ("link");
        }
        if (null != remoteMessage.getNotification ())
        {
            title = remoteMessage.getNotification().getTitle ();
            message = remoteMessage.getNotification().getBody ();
        }
        sendNotification (title, message, link);
    }

    private void sendNotification (final String title, final String message, final String link)
    {
        Allo.i ("sendNotification [" + title + "][" + message + "][" + link + "] " + getClass ());

        int notificationId = (int) System.currentTimeMillis ();
        String channelId = getApplicationContext ().getPackageName ();
        String channelName = getApplicationContext ().getPackageName ();

        Intent params = new Intent (getApplicationContext (), MainActivity.class);
        params.putExtra (Allo.CUBE_LINK, link);
        params.addFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity (getApplicationContext (), notificationId, params, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder (getApplicationContext (), channelId)
                        .setSmallIcon (R.mipmap.ic_launcher)
                        .setAutoCancel (false)
                        .setShowWhen (true)
                        .setWhen (System.currentTimeMillis ())
                        .setContentTitle (title)
                        .setContentText (message)
                        .setContentIntent (intent);
        NotificationManager notificationManager = (NotificationManager) getSystemService (Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            notificationManager.createNotificationChannel (new NotificationChannel (channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT));
        }
        notificationManager.notify (notificationId, notificationBuilder.build ());
    }
}
