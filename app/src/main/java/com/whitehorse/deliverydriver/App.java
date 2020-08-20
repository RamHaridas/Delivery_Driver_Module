package com.whitehorse.deliverydriver;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.widget.Toast;

public class App extends Application {
    public static final String CHANNEL_ID="Example";
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel servicechannel=new NotificationChannel(
                    CHANNEL_ID,
                    "Example Service",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(servicechannel);
        }
        Toast.makeText(this,"Welcome",Toast.LENGTH_LONG).show();
    }
}
