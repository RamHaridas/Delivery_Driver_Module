package com.whitehorse.deliverydriver.ui.home;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.whitehorse.deliverydriver.DriverData;
import com.whitehorse.deliverydriver.OnlineDriver;
import com.whitehorse.deliverydriver.R;
import com.whitehorse.deliverydriver.services.LocationService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationManager locationManager;
    private LocationRequest locationRequest;
    private boolean isGPS = false;

    public static final int ERROR_DIALOG_REQUEST = 9001, PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private static Intent serviceIntent;
    private static String vehicle_type;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private NavController navController;
    private Button search_button;
    private GoogleMap mMap;
    private Context context;
    private boolean gpsStatus;
    private int select;
    private LocationManager mLocationManager;
    static Location currLoc;
    private double lat_a, long_a;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private View view;
    private MapView mMapView;
    private Button status_button;
    private static boolean status = false;
    private static final int MULTIPLE_PERMISSIONS = 10;
    private String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final int REQUEST_LOCATION = 101;
    private SharedPreferences sharedPreferences;
    private String phone;
    private GeoPoint geoPoint;
    private SharedPreferences local_status;
    private SharedPreferences.Editor editor;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        local_status = this.getActivity().getSharedPreferences("status", Context.MODE_PRIVATE);
        editor = local_status.edit();

//        checkGpsStatus();
//        isServicesOK();
        new GpsUtils(getContext()).turnGPSOn(isGPSEnable -> {
            // turn on GPS
            isGPS = isGPSEnable;
        });
        if (!isGPS) {
            Toast.makeText(getContext(), "Please turn on GPS", Toast.LENGTH_SHORT).show();
        }
        sharedPreferences = this.getActivity().getSharedPreferences("phone", Context.MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", "");
        firebaseFirestore = FirebaseFirestore.getInstance();
        navController = NavHostFragment.findNavController(this);
        context = view.getContext();
        Places.initialize(view.getContext(), "AIzaSyDVpo7xTz7wk5tS4JMMRSXFwbu_6iZho-o");
        PlacesClient placesClient = Places.createClient(getContext());
        search_button = view.findViewById(R.id.rc);
        status_button = view.findViewById(R.id.status);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Handler handler = new Handler();
        checkPermissions();
        handler.postDelayed(() -> {
            select = 0;
            mMapView = view.findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);
            mMapView.getMapAsync(HomeFragment.this);
            mMapView.onResume();
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());
            fetchLastLocation();
        }, 2000);
        search_button.setOnClickListener(this::openOrderFragment);
        status_button.setOnClickListener(v -> {
            if (status) {
                resetOnlineData();
                stopLocationService();
                Log.d("PHONE", phone);
                status_button.setText("offline");
                status_button.setBackgroundResource(R.drawable.background_redroundedbutton);
                status = false;
                editor.putBoolean("status", status);
                editor.apply();
            } else {
                setOnlineStatus();
                startLocationService();
                Log.d("PHONE", phone);
                status_button.setText("online");
                status_button.setBackgroundResource(R.drawable.background_greenroundedbutton);
                status = true;
                editor.putBoolean("status", status);
                editor.apply();

            }
        });
        if (isLocationServiceRunning()) {
            status = true;
            //Log.d("PHONE",phone);
            status_button.setText("online");
            status_button.setBackgroundResource(R.drawable.background_greenroundedbutton);
        }
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (lat_a != 0.0 && long_a != 0.0) {
            geoPoint = new GeoPoint(lat_a, long_a);
            LatLng latLng = new LatLng(lat_a, long_a);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                checkPermissions();
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            rlp.setMargins(0, 180, 180, 0);
            PicassoMarker marker = new PicassoMarker(googleMap.addMarker(new MarkerOptions().position(latLng)));
            Picasso.with(view.getContext()).load(R.mipmap.car).resize( 50,  50)
                    .into(marker);
        }
    }
    private void fetchLastLocation() {
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if(location != null){
                currLoc = location;
                lat_a = location.getLatitude();
                long_a = location.getLongitude();
                Log.i("Location",location.toString());
                MapView mv =view.findViewById(R.id.mapView);
                mv.getMapAsync(HomeFragment.this);
            }
        });
    }
    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(getContext(),p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissionsList, int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:{
                if (grantResults.length > 0) {
                    String permissionsDenied = "";
                    for (String per : permissionsList) {
                        if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                            permissionsDenied += "\n" + per;
                        }
                    }
                }
                return;
            }
        }
    }

  /* public void printAddress(double lat, double lng){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        //lat = location.getLatitude();
        //lng = location.getLongitude();

        Log.e("latitude", "latitude--" + lat);

        try {
            Log.e("latitude", "inside latitude--" + lat);
            addresses = geocoder.getFromLocation(lat, lng, 1);

            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                /*String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                pick.setText(address);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context,int vectorId){
        Drawable drawable = ContextCompat.getDrawable(context,vectorId);
        drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }*/
/*
    public void loadLocalDataSource(){
        if(MapFragment.sourcelatLng.latitude != 0.0){
            if(MapFragment.sourcelatLng.longitude != 0.0){
                LatLng latLng = MapFragment.sourcelatLng;
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Current Location").position(latLng)
                        .icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_green_dot));
                //mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                //googleMap.clear();
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("local",0);
                pick.setText(sharedPreferences.getString("srcadd","Pickup Location"));
                mMap.addMarker(markerOptions);
            }
        }
    }

    public void loadLocalDataDestination(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("local",0);

        if(MapFragment.deslatLng.latitude != 0.0){
            if(MapFragment.deslatLng.longitude != 0.0){
                LatLng latLng = MapFragment.deslatLng;
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Drop Location").position(latLng)
                        .icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_red_dot));
                //mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                //googleMap.clear();
                drop.setText(sharedPreferences.getString("desadd","Drop Location"));
                mMap.addMarker(markerOptions);
            }
        }
    }*/
  private void openOrderFragment(View view){
        navController.navigate(R.id.action_HomeFragment_to_HomeSecondFragment);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setOnlineStatus(){

        vehicle_type = "";
        documentReference = firebaseFirestore.collection("DRIVERS").document(phone);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            DriverData driverData = documentSnapshot.toObject(DriverData.class);
            Toast.makeText(getContext(),driverData.getVehicle_type(),Toast.LENGTH_SHORT).show();
            if(driverData.getVehicle_type() != null){
                vehicle_type = driverData.getVehicle_type();
                OnlineDriver onlineDriver = new OnlineDriver();
                onlineDriver.setAssigned(false);
                onlineDriver.setNumber(phone);
                onlineDriver.setGeoPoint(geoPoint);
                String name = "ONLINE_"+vehicle_type+"_DRIVERS";
                firebaseFirestore.collection(name).document(phone).set(onlineDriver);
            }
        });
    }

    private void resetOnlineData(){
        vehicle_type = "";
        documentReference = firebaseFirestore.collection("DRIVERS").document(phone);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            DriverData driverData = documentSnapshot.toObject(DriverData.class);
            Toast.makeText(getContext(),driverData.getVehicle_type(),Toast.LENGTH_SHORT).show();
            if(driverData.getVehicle_type() != null){
                vehicle_type = driverData.getVehicle_type();
                String name = "ONLINE_"+vehicle_type+"_DRIVERS";
                firebaseFirestore.collection(name).document(phone).delete();
            }
        });

    }
    private void startLocationService(){
        if (!isLocationServiceRunning()) {
            Intent serviceIntent = new Intent(getContext(), LocationService.class);
            ContextCompat.startForegroundService(getContext(), serviceIntent);
        }
    }
    private void stopLocationService(){
        if(isLocationServiceRunning()){
            try{
                getActivity().stopService(new Intent(getContext(),LocationService.class));
            }catch (NullPointerException e){
                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.example.deliverydriver.services.LocationService".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }
    /*
    public void checkGpsStatus(){
        mLocationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        gpsStatus = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(gpsStatus)
        {
            Toast.makeText(getContext(),"gps checked",Toast.LENGTH_SHORT).show();
        }else {
            buildAlertMessageNoGps();
        }
    }
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(getContext(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
                fetchLastLocation();
            }
        }
    }
}
