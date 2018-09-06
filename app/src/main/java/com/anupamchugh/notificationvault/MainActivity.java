package com.anupamchugh.notificationvault;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.anupamchugh.notificationvault.CustomBroadcastReceiver.PREFS_NOTIFICATION_LIST;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ClickAdapterListener {

    Button button;
    SharedPreferences mSharedPreferences;
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    RecyclerView recyclerView;
    LinearLayout linearLayout;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<NotificationModel> notificationModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        button = findViewById(R.id.button);

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                //Log.d("API123", "onShared Preferences changed");

                if (key.equals(PREFS_NOTIFICATION_LIST)) {
                    Gson gson = new Gson();
                    String json = sharedPreferences.getString(PREFS_NOTIFICATION_LIST, null);
                    Type type = new TypeToken<ArrayList<NotificationModel>>() {
                    }.getType();

                    notificationModelArrayList = gson.fromJson(json, type);

                    if (notificationModelArrayList == null)
                        notificationModelArrayList = new ArrayList<>();


                    recyclerViewAdapter.setData(notificationModelArrayList);
                    toggleEmptyView();

                }
            }
        };


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        linearLayout = findViewById(R.id.linearLayout);

        Gson gson = new Gson();
        String json = mSharedPreferences.getString(PREFS_NOTIFICATION_LIST, null);
        Type type = new TypeToken<ArrayList<NotificationModel>>() {
        }.getType();

        notificationModelArrayList = gson.fromJson(json, type);

        if (notificationModelArrayList == null) {
            notificationModelArrayList = new ArrayList<>();
        }

        //Log.d("API123", "## " + notificationModelArrayList.size());

        recyclerViewAdapter = new RecyclerViewAdapter(notificationModelArrayList, this);
        recyclerView.setAdapter(recyclerViewAdapter);
        toggleEmptyView();


    }

    @Override
    protected void onResume() {
        super.onResume();


        if (mSharedPreferences == null) {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        }
        mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);

        if (!Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners").contains(getApplicationContext().getPackageName())) {
            showPermissionDialog();
        } else {
            button.setVisibility(View.GONE);

            if (!isMyServiceRunning(CustomService2.class)) {
                //Log.d("API123", "start service");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(this, CustomService2.class));
                } else {
                    startService(new Intent(this, CustomService2.class));
                }
            }
        }


    }

    public void showPermissionDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Please enable the notification access to allow guarding your notifications");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        getApplicationContext().startActivity(new Intent(
                                "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                button.setVisibility(View.VISIBLE);
            }
        });

        AlertDialog alert = builder1.create();
        alert.show();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {

        //Log.d("API123", "isMyService running");


        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //finishAndRemoveTask();
    }

    @Override
    public void onRowClicked(final NotificationModel model) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure you want to delete this notification from the guard?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        Gson gson = new Gson();
                        String json = mSharedPreferences.getString(PREFS_NOTIFICATION_LIST, null);
                        Type type = new TypeToken<ArrayList<NotificationModel>>() {
                        }.getType();

                        notificationModelArrayList = gson.fromJson(json, type);

                        if (notificationModelArrayList == null) {
                            notificationModelArrayList = new ArrayList<>();
                        }


                        for (int i = 0; i < notificationModelArrayList.size(); i++) {

                            NotificationModel n = notificationModelArrayList.get(i);
                            if (n.compareTo(model) == 0) {
                                boolean b = notificationModelArrayList.remove(n);

                                //Log.d("API123", "removed " + b);
                                break;
                            }
                        }
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        String string = new Gson().toJson(notificationModelArrayList);
                        editor.putString(PREFS_NOTIFICATION_LIST, string);
                        editor.apply();
                    }
                });

        builder1.setNegativeButton("No", null);

        AlertDialog alert = builder1.create();
        alert.show();


    }

    private void toggleEmptyView() {
        if (recyclerViewAdapter.getData().isEmpty()) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.GONE);
        }
    }

    public void buttonClicked(View view) {

        if (!Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners").contains(getApplicationContext().getPackageName())) {
            showPermissionDialog();
        } else {
            button.setVisibility(View.GONE);

            if (!isMyServiceRunning(CustomService2.class)) {
                //Log.d("API123", "start service");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(this, CustomService2.class));
                } else {
                    startService(new Intent(this, CustomService2.class));
                }
            }
        }

    }


}
