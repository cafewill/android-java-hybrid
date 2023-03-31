package com.demo.java.hybrid;

import android.annotation.SuppressLint;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

@SuppressLint ({"OverrideAbstract", "NewApi"})
public class FirebaseService extends NotificationListenerService
{
    @Override
    public void onCreate ()
    {
        super.onCreate();
        Allo.i ("onCreate " + getClass ());
    }

    @Override
    public void onNotificationPosted (StatusBarNotification sbn)
    {
        super.onNotificationPosted (sbn);
        Allo.i ("onNotificationPosted [" + getApplicationContext ().getPackageName () + "][" + sbn.getPackageName () + "] " + getClass ());

        try
        {
            if (getApplicationContext ().getPackageName ().equals (sbn.getPackageName ()))
            {
                // (여기선 스킵요) NotificationListenerService 상속 클래스는 권한 획등용.
                // 푸시 알림 수신 관련 처리는 FirebaseMessagingService 상속된 클래스의 onMessageReceived () 쪽에서 처리함
            }
        } catch (Exception e) { e.printStackTrace (); }
    }
}
