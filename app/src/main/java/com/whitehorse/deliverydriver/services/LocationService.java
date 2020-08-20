package com.whitehorse.deliverydriver.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.Manifest;
import android.app.Notification;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.whitehorse.deliverydriver.DriverData;
import com.whitehorse.deliverydriver.MainActivity;
import com.whitehorse.deliverydriver.OrderData;
import com.whitehorse.deliverydriver.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.Map;
import static com.whitehorse.deliverydriver.App.CHANNEL_ID;

public class LocationService extends Service {

    SharedPreferences sharedPreferences;
    private static final String TAG = "LocationService";
    FirebaseFirestore firebaseFirestore;
    CollectionReference collectionReference;
    DocumentReference documentReference;
    private FusedLocationProviderClient mFusedLocationClient;
    private final static long UPDATE_INTERVAL = 4 * 1000;  /* 4 secs */
    private final static long FASTEST_INTERVAL = 2000; /* 2 sec */
    String phone_no;
    SharedPreferences status;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: called.");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        sharedPreferences = getSharedPreferences("phone",MODE_PRIVATE);
        phone_no = sharedPreferences.getString("phone","");
        //FirebaseApp.initializeApp(this);
        status = getSharedPreferences("status",MODE_PRIVATE);
        firebaseFirestore = FirebaseFirestore.getInstance();
/*

            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                    .setContentText("CODE SPHERE")
                    .setSmallIcon(R.drawable.ic_notifications)
                    .setAutoCancel(true)
                    .setContentText("NEW ORDER");
            Notification notification = builder
                    .setContentTitle("Service Running")
                    .setContentText("").build();
*/

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Service Running")
                .setContentText("Location Updates")
                .setSmallIcon(R.drawable.ic_notifications)
                .build();
            startForeground(1, notification);
        getLocation();
        check_orders();
        return START_NOT_STICKY;
    }

    private void getLocation() {
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocation: stopping the location service.");
            stopSelf();
            return;
        }
        Log.d(TAG, "getLocation: getting location information.");
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        Log.d(TAG, "onLocationResult: got location result.");
                        Location location = locationResult.getLastLocation();
                        if (location != null) {
                            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                            if(status.getBoolean("status",false)) {
                                saveUserLocation(geoPoint);
                            }else{
                                stopSelf();
                            }
                        }
                    }
                },
                Looper.myLooper());
    }

    private void saveUserLocation(GeoPoint geoPoint){
        try {
            Log.i("phone",phone_no);
            documentReference = firebaseFirestore.collection("DRIVERS").document(phone_no);
            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                Log.i("phone2",phone_no);
                DriverData driverData = documentSnapshot.toObject(DriverData.class);
                Log.i("phone",driverData.getVehicle_type());
                updateLocation(driverData.getVehicle_type(),geoPoint);
            });


        }catch (NullPointerException e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
        //stopSelf();
    }
    public void updateLocation(String name,GeoPoint geoPoint){
        String text = "ONLINE_"+name+"_DRIVERS";
        DocumentReference documentReference = firebaseFirestore.collection(text).document(phone_no);
        Map<String,Object> map = new HashMap<>();
        map.put("geoPoint",geoPoint);
        documentReference.set(map,SetOptions.merge());
    }
    public void check_orders(){

        SharedPreferences sharedPreferences = getSharedPreferences("phone",MODE_PRIVATE);
        String phone_no = sharedPreferences.getString("phone","");
        collectionReference = firebaseFirestore.collection("DRIVERS")
                .document(phone_no).collection("ORDERS");
        collectionReference.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if(e != null){
                return;
            }
            for(DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){
                switch (dc.getType()){
                    case ADDED:
                        OrderData orderData = dc.getDocument().toObject(OrderData.class);
                        if(!orderData.isCompleted()) {
                            create_notifications(dc.getDocument().getId(),orderData.getUser());
                        }
                    case MODIFIED:

                    case REMOVED:

                }
            }
        });
    }

    private void create_notifications(String id,String number){

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", "notify");
        intent.putExtra("ID", id);
        intent.putExtra("number",number);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("n","n", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"n")
                .setContentText("NOTIFICATION")
                .setSmallIcon(R.drawable.ic_notifications)
                .setAutoCancel(true)
                .setContentText("NEW ORDER")
                .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(999,builder.build());
    }
}
