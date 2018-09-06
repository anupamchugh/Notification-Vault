package com.anupamchugh.notificationvault;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CustomBroadcastReceiver extends BroadcastReceiver {


    public static final String PREFS_NOTIFICATION_LIST = "prefs_notification_list";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("API123", "onReceive");

        if (intent != null) {

            if (intent.getAction() != null) {

                String action = intent.getAction();

                if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {

                    Log.d("API123","onBOoot completeed");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        context.startForegroundService(new Intent(context, CustomService2.class));
                    } else {
                        Intent serviceIntent = new Intent(context, CustomService2.class);
                        context.startService(serviceIntent);


                    }

                } else if (action.equals(CustomService2.INTENT_FROM_SERVICE)) {
                    NotificationModel notificationModel = intent.getParcelableExtra(CustomService2.NOTIFICATION_MODEL_EXTRA_KEY);


                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    Gson gson = new Gson();
                    String json = prefs.getString(PREFS_NOTIFICATION_LIST, null);
                    Type type = new TypeToken<ArrayList<NotificationModel>>() {
                    }.getType();

                    boolean alreadyExists = false;
                    ArrayList<NotificationModel> notificationModelArrayList = gson.fromJson(json, type);

                    if (notificationModelArrayList == null) {
                        notificationModelArrayList = new ArrayList<>();
                    } else {

                        for (int i = 0; i < notificationModelArrayList.size(); i++) {

                            NotificationModel n = notificationModelArrayList.get(i);

                            if (n.sbnKey.equals(notificationModel.sbnKey)) {
                                alreadyExists = true;
                                break;
                            }

                            if (n.title.equals(notificationModel.title) && n.packageName.equals(notificationModel.packageName) && n.body.equals(notificationModel.body)) {
                                if (n.timeStamp == notificationModel.timeStamp) {
                                    alreadyExists = true;
                                    break;
                                } else {
                                    notificationModelArrayList.remove(n);
                                    break;
                                }
                            }
                        }
                    }

                    if (!alreadyExists) {

                        //saving ArrayList
                        notificationModelArrayList.add(0, notificationModel);
                        SharedPreferences.Editor editor = prefs.edit();
                        String string = new Gson().toJson(notificationModelArrayList);
                        editor.putString(PREFS_NOTIFICATION_LIST, string);
                        editor.apply();
                    }
                }
            }
        }

    }
}
