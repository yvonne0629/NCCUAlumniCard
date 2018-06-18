package com.yvonne.alumnicardfunctions;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

public class UserLocationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener{

    private GoogleMap mMap;

    private static final String NAME = "EVENT_NAME";
    private static final String LATITUDE = "EVENT_LATITUDE";
    private static final String LONGTITUDE = "EVENT_LONGTITUDE";
    private static final String RADIUS = "EVENT_RADIUS";
    private static final String SHARE_PREFERENCE = "SHARE_PREFERENCE";
    private static final String EVENT_NUMBER = "EVENT_NUMBER";
    private static final String ADDRESS = "EVENT_ADDRESS";
    private Button checkButton;

    private Marker marker;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_loacation);


        TextView mEventName = findViewById(R.id.user_location_activity_name);
        TextView mSearchText = findViewById(R.id.user_location_address);
        checkButton = findViewById(R.id.checkin_button);

        ArrayList<String> list = getIntent().getStringArrayListExtra("content");
        String eventId = list.get(0);


        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(SHARE_PREFERENCE, Context.MODE_PRIVATE);

        double radius = sharedPref.getInt(RADIUS + eventId ,0);

        mEventName.setText(sharedPref.getString(NAME + eventId ,"No Event Name"));
        mSearchText.setText(sharedPref.getString(ADDRESS + eventId, "No Address"));
        latLng = new LatLng(sharedPref.getFloat(LATITUDE + eventId ,0),
                             sharedPref.getFloat(LONGTITUDE + eventId ,0));



        double lat = Double.valueOf(list.get(1));
        double lnt = Double.valueOf(list.get(2));

        double d = SphericalUtil.computeDistanceBetween(latLng, new LatLng(lat,lnt));
        if(d > radius){
            checkButton.setEnabled(false);
        }else{
            checkButton.setEnabled(true);
        }



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        mMap.addMarker(new MarkerOptions().position(latLng).title("Event Loaction"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
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

    public void check(View view){
        final Context context = this;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);


        // set dialog message
        alertDialogBuilder
                .setMessage("成功！")
                .setCancelable(false).setPositiveButton("確定",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                checkButton.setEnabled(false);
            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }


}
