package com.yvonne.alumnicardfunctions;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingLocationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, GoogleApiClient.OnConnectionFailedListener
{

    private EditText mEventName;
    private EditText mSearchText;
    private EditText mRadius;
    private Button currentLocationButton;
    private Button saveButton;
    private GoogleMap mMap;
    private static final float DEFAULT_ZOOM = 15f;



    private static final String TAG = "SettingLocationActivity";

    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static Boolean mLocationPermissionGranted = false;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private final CustomLocationListener mLocationListener = new CustomLocationListener();
    private Marker marker;
    private LatLng latLng;

    private static final String NAME = "EVENT_NAME";
    private static final String LATITUDE = "EVENT_LATITUDE";
    private static final String LONGTITUDE = "EVENT_LONGTITUDE";
    private static final String RADIUS = "EVENT_RADIUS";

    private static final String SHARE_PREFERENCE = "SHARE_PREFERENCE";
    private static final String EVENT_NUMBER = "EVENT_NUMBER";


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_location);

        mEventName = findViewById(R.id.activity_naming_field);
        mSearchText = findViewById(R.id.location_setting_field);
        mRadius = findViewById(R.id.radius_field);
        currentLocationButton = findViewById(R.id.current_location_button);
        saveButton = findViewById(R.id.set_button);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED)) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == (PackageManager.PERMISSION_GRANTED)) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
                mLocationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }

        initSearchLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (MainActivity.mLocationPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    public void getCurrentLocation(View view){
        Location location = mLocationListener.getLocation();
        latLng = new LatLng(location.getLatitude(),location.getLongitude());
        moveCamera(latLng,DEFAULT_ZOOM,"Current Location");
    }


    private void initSearchLocation(){

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == 5
                        ||actionId == EditorInfo.IME_ACTION_DONE
                        ||event.getAction() == KeyEvent.ACTION_DOWN
                        ||event.getAction() == KeyEvent.KEYCODE_ENTER){
                    //execute search method
                    geoLocate();
                }
                return false;
            }
        });
        hideSoftKeyboard();
    }

    private int getRadius(){
        int radius = Integer.parseInt(mRadius.getText().toString());
        return radius;


    }

    private void geoLocate(){
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(SettingLocationActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString,1);
        }catch (IOException e){
            Log.e(TAG,"geoLocate: IOException" + e.getMessage());
        }

        if(list.size()>0){
            Address address = list.get(0);
            Log.d(TAG,"geoLocate: found a location" + address.toString());
            latLng = new LatLng(address.getLatitude(),address.getLongitude());
            moveCamera(latLng,DEFAULT_ZOOM,address.getAddressLine(0));
            hideSoftKeyboard();
        }

    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if(null != marker){
            marker.remove();
        }

        if(!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions().position(latLng).title(title);
            marker = mMap.addMarker(options);
        }
    }

    public void search(View view){

    }

    public void setRange(View view){

    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void saveNewEvent(View view){
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                SHARE_PREFERENCE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        int eventNumber = sharedPref.getInt(EVENT_NUMBER,0) + 1;

        editor.putInt(EVENT_NUMBER, eventNumber);
        editor.putString(NAME + eventNumber, mEventName.getText().toString());
        editor.putFloat(LATITUDE + eventNumber, (float)latLng.latitude);
        editor.putFloat(LONGTITUDE + eventNumber, (float)latLng.longitude);
        editor.putInt(RADIUS + eventNumber, Integer.valueOf(mRadius.getText().toString()));
        editor.apply();
        finish();
    }



}



//
//    private void getDeviceLocation() {
//        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        try {
//            if (MainActivity.mLocationPermissionGranted) {
//                Task location = mfusedLocationProviderClient.getLastLocation();
//                location.addOnCompleteListener(new OnCompleteListener() {
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//                        if (task.isSuccessful() && task.getResult() != null) {
//                            Location currentLocation = (Location) task.getResult();
//                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM,"My Location");
//                        } else {
//                            Toast.makeText(SettingLocationActivity.this, "unable to get location", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        } catch (SecurityException e) {
//
//        }
//
//    }
