package com.anupamchugh.notificationvault;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.anupamchugh.notificationvault.CustomBroadcastReceiver2.PREFS_NOTIFICATION_LIST;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ClickAdapterListener {

    Button button;
    SharedPreferences mSharedPreferences;
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    RecyclerView recyclerView;
    LinearLayout linearLayout;
    RecyclerViewAdapter mAdapter;
    ArrayList<NotificationModel> notificationModelArrayList;
    CoordinatorLayout coordinatorLayout;
    boolean undoWasClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        coordinatorLayout = findViewById(R.id.coordinator_layout);
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


                    mAdapter.setData(notificationModelArrayList);
                    recyclerView.scrollToPosition(0);
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

        mAdapter = new RecyclerViewAdapter(notificationModelArrayList, this);
        recyclerView.setAdapter(mAdapter);
        toggleEmptyView();


        swipeToDeleteFeature();


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


        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.d("API123", "isMyService running");
                return true;
            }
        }
        Log.d("API123", "isMyService NOT running");
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


        boolean applicationIntentAvailable = openApplication(this, model.packageName);


        if (!applicationIntentAvailable) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "This application does not allow launch.", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        /*AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
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
        alert.show();*/

    }

    private boolean openApplication(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();

        try {
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            if (intent == null)
                return false;

            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            return true;
        }
    }

    private void toggleEmptyView() {
        if (mAdapter.getData().isEmpty()) {
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

    public void swipeToDeleteFeature() {


        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                Log.d("API123", "SWIPED..........................................................");

                final ArrayList<NotificationModel> tempList = mAdapter.getData();
                final NotificationModel notificationModel = tempList.remove(position);

                mAdapter.notifyItemRemoved(position);


                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Notification removed from vault.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        undoWasClicked = true;
                        // undo is selected, restore the deleted item
                        boolean b = mAdapter.restoreItem(notificationModel, position);

                        if (b)
                            recyclerView.scrollToPosition(position);
                    }
                });
                snackbar.addCallback(new Snackbar.Callback() {

                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (!undoWasClicked) {
                            Log.d("API123", "onDismissed");
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            String string = new Gson().toJson(tempList);
                            editor.putString(PREFS_NOTIFICATION_LIST, string);
                            editor.apply();
                        }

                    }

                    @Override
                    public void onShown(Snackbar snackbar) {

                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }


}
