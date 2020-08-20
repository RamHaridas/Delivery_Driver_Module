package com.whitehorse.deliverydriver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.whitehorse.deliverydriver.ViewProfile.ViewProfile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    FirebaseFirestore firebaseFirestore;
    CollectionReference collectionReference;
    View headerview;
    public static DriverData static_userData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseFirestore = FirebaseFirestore.getInstance();
       // check_orders();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,R.id.nav_help,R.id.nav_details,R.id.nav_map)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.nav_logout){
                    //Toast.makeText(MainActivity.this,"LOGOUT",Toast.LENGTH_SHORT).show();
                    LogoutPopup logoutPopup = new LogoutPopup();
                    logoutPopup.show(getSupportFragmentManager(),"logout");
                }
                //This is for maintaining the behavior of the Navigation view
                NavigationUI.onNavDestinationSelected(item,navController);
                //This is for closing the drawer after acting on it
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        headerview = navigationView.getHeaderView(0);
        LinearLayout header = headerview.findViewById(R.id.header);
        headerview.setOnClickListener(v -> {
            Fragment fr = new ViewProfile();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.nav_default_pop_enter_anim,R.anim.nav_default_pop_exit_anim);
            transaction.replace(R.id.drawer_layout,fr);
            transaction.addToBackStack(null);
            transaction.commit();
            drawer.closeDrawer(GravityCompat.START);
        });
        Intent intent = getIntent();
        String val = intent.getStringExtra("data");
        if(val != null ){
            if(val.equals("notif y")) {
                Bundle args = new Bundle();
                args.putString("id",intent.getStringExtra("ID"));
                args.putString("number",intent.getStringExtra("number"));
                navController.navigate(R.id.action_home_to_details,args);
            }
        }
        getUserData();
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void switchContent(int id,Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(id,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
                .setContentText("CODE SPHERE")
                .setSmallIcon(R.drawable.ic_notifications)
                .setAutoCancel(true)
                .setContentText("NEW ORDER")
                .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(999,builder.build());
    }

    public void getUserData(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("phone",MODE_PRIVATE);

        try {
            String phone = sharedPreferences.getString("phone","");
            DocumentReference documentReference = firebaseFirestore.collection("DRIVERS").document(phone);
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    DriverData driverData = documentSnapshot.toObject(DriverData.class);
                    if(driverData != null){
                        static_userData = driverData;
                        ImageView imageView = headerview.findViewById(R.id.imageView);
                        TextView textView = headerview.findViewById(R.id.name_tv);
                        try{
                            textView.setText(driverData.getName());
                            Glide.with(getApplicationContext())
                                    .load(driverData.getProfile_url())
                                    .circleCrop()
                                    .placeholder(R.drawable.profile)
                                    .into(imageView);
                        }catch (Exception e){}
                    }
                }
            });
        }catch (Exception e){

        }
    }

}
