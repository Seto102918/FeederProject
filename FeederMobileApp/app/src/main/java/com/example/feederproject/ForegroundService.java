package com.example.feederproject;

import static android.content.ContentValues.TAG;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ForegroundService extends Service {

    private FirebaseDatabase firebaseDatabase;
    private Integer State;
    private NotificationManager notificationManager;
    private Context context;
    private final int notificationID = 1;
    private final int notificationID2 = 2;
    private final String channelID = "100";
    private boolean isDestroyed = false;

    private int state;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        context = this;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = showNotification("Feeder Condition Updater Running On Foreground");
        startForeground(notificationID,notification);
        doTask();
        super.onCreate();
    }

    private Notification showNotification(String content){
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(
                new NotificationChannel(channelID,"Foreground Notification",
                        NotificationManager.IMPORTANCE_DEFAULT)
        );
        return new NotificationCompat.Builder(this,channelID)
                .setContentTitle("FeederApp")
                .setContentText(content)
                .setOnlyAlertOnce(true)
                .setOngoing(false)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Foreground", "onStartCommand: Started");
        ///
        return super.onStartCommand(intent,flags,startId);
    }

    private void doTask(){
        Log.d("Foreground", "doTask: Started");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++){
                    if(isDestroyed) {break;}
                    try{
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                firebasePortion();
                            }
                        });
                        Thread.sleep(60000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void  updateNotification(int ID,String isi){
        Log.d("Foreground", "updateNotificationStarted");
        Notification notification = showNotification(isi);
        notificationManager.notify(ID,notification);
    }

    private void firebasePortion(){
        Log.d("Foreground", "firebasePortion: Started");
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://feederproject-8a71b-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference ref = database.getReference("RefillAlert");
        DatabaseReference ref2 = database.getReference("waterlvl");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Double value = dataSnapshot.getValue(Double.class);
                Integer valueInt = value.intValue();
                Log.i("Foreground","valueInt: " + valueInt);

                StatusBarNotification[] notifications = notificationManager.getActiveNotifications();

                if(valueInt == 1){
                    for (StatusBarNotification notification : notifications) {
                        Log.d("Foreground", "onDataChange: " + notification.getId());
                        if (notification.getId() == notificationID2) {
                            Log.d("Foreground", "checkNotification: case 1");
                            return;
                        } else {
                            Log.d("Foreground", "checkNotification: case 2");
                            updateNotification(notificationID2,"Food Dispenser Is Empty! 🐶🐈");
                        }
                    }
                }else if (valueInt == 0){
                    for (StatusBarNotification notification : notifications) {
                        Log.d("Foreground", "onDataChange: " + notification.getId());
                        if (notification.getId() == notificationID2) {
                            Log.d("Foreground", "checkNotification: Remove Notification");
                            notificationManager.cancel(notificationID2);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Foreground Firebase", "Failed to read value.", error.toException());
            }
        });

        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double value = dataSnapshot.getValue(Double.class);
                Integer valueInt = value.intValue();
                Log.i("Foreground","valueInt: " + valueInt);
                StatusBarNotification[] notifications = notificationManager.getActiveNotifications();

                if(valueInt <= 10){
                    for (StatusBarNotification notification : notifications) {
                        Log.d("Foreground", "onDataChange: " + notification.getId());
                        if (notification.getId() == 3) {
                            Log.d("Foreground", "checkNotification: case 1");
                            return;
                        } else {
                            Log.d(TAG, "checkNotification: case 2");
                            updateNotification(3,"Water Dispenser Is Empty! 🐶🐈");
                        }
                    }
                }else if (valueInt > 10){
                    for (StatusBarNotification notification : notifications) {
                        Log.d("Foreground", "onDataChange: " + notification.getId());
                        if (notification.getId() == 3) {
                            Log.d("Foreground", "checkNotification: Remove Notification");
                            notificationManager.cancel(3);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Foreground", "Failed to read value.", error.toException());
            }

        });
    }

    @Override
    public void onDestroy() {
        Log.d("Foreground","Stop Foreground Service");
        stopForeground(true);
        isDestroyed = true;
        super.onDestroy();
    }
}

