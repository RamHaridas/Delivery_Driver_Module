package com.whitehorse.deliverydriver.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.whitehorse.deliverydriver.OrderData;
import com.whitehorse.deliverydriver.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class MapFragment extends Fragment implements OnMapReadyCallback {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private MapView mapView;
    private int select;
    private Location loc;
    private GoogleMap googleMap;
    private View view;
    private MarkerOptions src,des;
    private Button details,complete;
    private PicassoMarker marker;
     LatLng driverLatLng;
    private double[] latLng = new double[2];
    private float start_rotation;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private GeoApiContext mGAPI;
    private Marker start,end;
    private boolean isContinue = false;
    private boolean isGPS = false;
    private StringBuilder stringBuilder;
    private String number;
    private String name;
    FirebaseFirestore firebaseFirestore;
    DocumentReference documentReference;
    Bundle args;
    public MapFragment(){ }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_map, container, false);
        details = view.findViewById(R.id.order_details);
        complete = view.findViewById(R.id.complete);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        args = getArguments();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        mapView = view.findViewById(R.id.mapView2);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(MapFragment.this);
        mapView.onResume();
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds
        //isContinue = true;
        new GpsUtils(view.getContext() ).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetailsFragment();
            }
        });
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completed(v);
            }
        });
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        if (!isContinue) {
                            //txtLocation.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));
                        } else {
                            stringBuilder.append(wayLatitude);
                            stringBuilder.append("-");
                            stringBuilder.append(wayLongitude);
                            stringBuilder.append("\n\n");
                            //txtContinueLocation.setText(stringBuilder.toString());
                            Log.i("LOCATION",location.getLatitude()+","+location.getLongitude());
                            updateLocation(location);
                        }
                        if (!isContinue && fusedLocationProviderClient != null) {
                            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };
        if (!isGPS) {
            Toast.makeText(view.getContext(), "Please turn on GPS", Toast.LENGTH_SHORT).show();
        }
        isContinue = true;
        stringBuilder = new StringBuilder();
        getLocation();
        getData();
        return view;
    }

    private void completed(View v) {
        //open a dialog fragment for confirmation
        String name = args.getString("id");
        String number = args.getString("number");

        CompletePopup completePopup = new CompletePopup(MapFragment.this);
        Bundle b = new Bundle();
        b.putString("id",name);
        b.putString("number",number);
        completePopup.setArguments(b);
        completePopup.show(this.getActivity().getSupportFragmentManager(),"complete");
    }

    private void openDetailsFragment() {
        NavController navController = NavHostFragment.findNavController(this);

        String name = args.getString("id");
        String number = args.getString("number");
        Bundle argument = new Bundle();
        argument.putString("id",name);
        argument.putString("number",number);

        navController.navigate(R.id.map_to_details,argument);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        //googleMap.setMyLocationEnabled(true);
        loc = HomeFragment.currLoc;
        if(loc != null && loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            src = new MarkerOptions().position(latLng);
            des = new MarkerOptions().position(new LatLng(21.172344, 79.034937));
            //start = googleMap.addMarker(src);
            marker = new PicassoMarker(googleMap.addMarker(new MarkerOptions().position(latLng)));
            Picasso.with(view.getContext()).load(R.mipmap.car).resize( 50,  50)
                    .into(marker);
        }else{
            Toast.makeText(getContext(),"Please Enable your location",Toast.LENGTH_SHORT).show();
        }
        if(mGAPI==null) {
            mGAPI = new GeoApiContext.Builder().apiKey(getString(R.string.map_key)).build();
        }
    }
    private void calculateDirections(){
        Log.d(TAG, "calculateDirections: calculating directions.");
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                end.getPosition().latitude,
                end.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGAPI);

        directions.alternatives(false);
        directions.origin(
                new com.google.maps.model.LatLng(
                        wayLatitude,
                        wayLongitude
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                addPolylinesToMap(result);
            }
            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );
            }
        });
    }
    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);
                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                    List<LatLng> newDecodedPath = new ArrayList<>();
                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){
//                        Log.d(TAG, "run: latlng: " + latLng.toString());
                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = googleMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    //polyline.setColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                    polyline.setColor(Color.rgb(95,66,200));
                    polyline.setClickable(true);
                }
            }
        });
    }
    private void getLocation() {
            if (isContinue) {
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        Log.i("LOCATION",location.getLatitude()+","+location.getLongitude());
                        //txtLocation.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));
                    } else {
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                });
            }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
    }
    public void updateLocation(Location location) {
        latLng[0] = location.getLatitude();
        latLng[1] = location.getLongitude();

        if(marker==null){
            marker = new PicassoMarker(googleMap.addMarker(new MarkerOptions().position(new LatLng(latLng[0], latLng[1]))));
            Picasso.with(view.getContext()).load(R.mipmap.car).resize( 50,  50)
                    .into(marker);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng[0], latLng[1]), 15.0f));
        }

        if ((latLng[0] != -1 && latLng[0] != 0) && (latLng[1] != -1 && latLng[1] != 0)) {
            //googleMapHomeFrag.moveCamera(CameraUpdateFactory.newLatLngZoom(driverLatLng, 12.0f));
            //float bearing = (float) bearingBetweenLocations(driverLatLng, new LatLng(location.getLatitude(), location.getLongitude()));
            if (marker != null) {
                moveVechile(marker.getmMarker(), location);
                rotateMarker(marker.getmMarker(), location.getBearing(), start_rotation);

            }
            driverLatLng = new LatLng(latLng[0], latLng[1]);
        } else {
            Toast.makeText(getContext(), "Location Not Found", Toast.LENGTH_LONG).show();
        }
       // calculateDirections();
    }

    public void moveVechile(final Marker myMarker, final Location finalPosition) {

        final LatLng startPosition = myMarker.getPosition();

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;
        final boolean hideMarker = false;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + (finalPosition.getLatitude()) * t,
                        startPosition.longitude * (1 - t) + (finalPosition.getLongitude()) * t);
                myMarker.setPosition(currentPosition);
                // myMarker.setRotation(finalPosition.getBearing());


                // Repeat till progress is completeelse
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                    // handler.postDelayed(this, 100);
                } else {
                    if (hideMarker) {
                        myMarker.setVisible(false);
                    } else {
                        myMarker.setVisible(true);
                    }
                }
            }
        });


    }


    public void rotateMarker(final Marker marker, final float toRotation, final float st) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = marker.getRotation();
        final long duration = 1555;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;


                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                start_rotation = -rot > 180 ? rot / 2 : rot;
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    public void getData(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        String name = args.getString("id");
        String number = args.getString("number");
        if(name == null || number == null){
            return;
        }
        try{
            DocumentReference documentReference = firebaseFirestore.collection("USERS")
                    .document(number).collection("ORDER").document(name);
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    OrderData orderData = documentSnapshot.toObject(OrderData.class);
                    addPickMarker(orderData.getPickup_location());
                    addDropMarker(orderData.getDrop_location());
                    GeoPoint geoPoint = orderData.getPickup_location();
                    des = new MarkerOptions().position(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()))
                            .icon(bitmapDescriptorFromVector(getContext(),R.drawable.ic_place_green));
                    end = googleMap.addMarker(des);
                    calculateDirections();
                }
            });
        }catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void addPickMarker(GeoPoint geoPoint){
        try {
            LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Pickup Point")
                    .icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_place_green));

            googleMap.addMarker(markerOptions);
        }catch (Exception e){}
    }
    public void addDropMarker(GeoPoint geoPoint){
        try {
            LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Drop Point")
                    .icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_place_red));

            googleMap.addMarker(markerOptions);
        }catch (Exception e){}
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorId){
        Drawable drawable = ContextCompat.getDrawable(context,vectorId);
        drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }
}
