package com.anupamchugh.notificationvault;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class CustomService2 extends NotificationListenerService {

    String previousKey = "";
    NotificationManager mNotificationManager;
    Context context;
    public static final String CHANNEL_ID = "id";
    public static final int NOTIFICATION_ID = 20;
    public static final String NOTIFICATION_MODEL_EXTRA_KEY = "notification";
    public static final String INTENT_FROM_SERVICE = "com.anupamchugh.notificationvault.INTENT_FROM_SERVICE";

    @Override
    public void onCreate() {
        super.onCreate();


        Log.d("API123", "onCreate service");

        context = getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(CHANNEL_ID, "Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(notificationChannel);

            Intent notificationIntent = new Intent(context, MainActivity.class);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent intent = PendingIntent.getActivity(context, 0,
                    notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setAutoCancel(true)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText("Notification Guard is active")
                    .setContentIntent(intent)
                    .setSmallIcon(R.mipmap.app_icon).build();

            startForeground(NOTIFICATION_ID, notification);
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        Log.d("API123", "onNotification changed");

        String tempKey = sbn.getKey();


        if (tempKey != null) {
            if (!tempKey.equals(previousKey)) {
                previousKey = tempKey;


                Bundle bundle = sbn.getNotification().extras;

                long timestamp = sbn.getPostTime();
                String packageName = sbn.getPackageName();

                Icon icon = sbn.getNotification().getSmallIcon();

                CharSequence title = bundle.getCharSequence(Notification.EXTRA_TITLE);
                CharSequence body = bundle.getCharSequence(Notification.EXTRA_TEXT);

                if (title != null && packageName != null && body != null && icon != null) {


                    Intent intent = new Intent(INTENT_FROM_SERVICE);

                    NotificationModel notificationModel = new NotificationModel(title.toString(), body.toString(), timestamp, tempKey, packageName, sbn.getNotification().contentIntent);
                    intent.putExtra(NOTIFICATION_MODEL_EXTRA_KEY, notificationModel);


                    context.sendBroadcast(intent);
                }

            }
        }


    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);

        Log.d("API123", "onNotification Removed");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {

        super.onTaskRemoved(rootIntent);
        /*if (mNotificationManager != null) {
            mNotificationManager.cancel(NOTIFICATION_ID);
        }*/
    }

}
