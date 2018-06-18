package com.yvonne.alumnicardfunctions;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static Boolean mLocationPermissionGranted = false;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    public static final String NAME = "EVENT_NAME";
    public static final String LOCATION = "EVENT_LOCATION";
    public static final String RADIUS = "EVENT_RADIUS";
    public static final String SHARE_PREFERENCE = "SHARE_PREFERENCE";
    public static final String EVENT_NUMBER = "EVENT_NUMBER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLocationPermission();
    }

    public void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED)) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == (PackageManager.PERMISSION_GRANTED)) {
                mLocationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if (grantResults.length > 0){
                    for(int i = 0; i <grantResults.length;i++){
                        if (grantResults[i]!= PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;

                }
            }
        }
    }

    public void setNewLocation(View view){
        Intent intent = new Intent(this,SettingLocationActivity.class);
        startActivity(intent);
    }

    public void checkActivities(View view){
        Intent intent = new Intent(this,ListActivity.class);
        startActivity(intent);
    }






}
