package com.yvonne.alumnicardfunctions;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private TableLayout tblLayout;
    public static final String NAME = "EVENT_NAME";
    public static final String LOCATION = "EVENT_LOCATION";
    public static final String RADIUS = "EVENT_RADIUS";
    public static final String SHARE_PREFERENCE = "SHARE_PREFERENCE";
    public static final String EVENT_NUMBER = "EVENT_NUMBER";

    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static Boolean mLocationPermissionGranted = false;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        final CustomLocationListener mLocationListener = new CustomLocationListener();

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


        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(SHARE_PREFERENCE, Context.MODE_PRIVATE);
        int eventNumber = sharedPref.getInt(EVENT_NUMBER,0);

        tblLayout = findViewById(R.id.Table_Layout);

        for (int i = eventNumber; i > 0; i--) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            TextView nameText = new TextView(this);
            nameText.setText("活動名稱：" + sharedPref.getString(NAME+i,"No Event Name"));
            row.addView(nameText);

            Button detailButton = new Button(this);
            detailButton.setText("查看細節");
            detailButton.setId(i);
            detailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Location location = mLocationListener.getLocation();
                    new LatLng(location.getLatitude(),location.getLongitude());

                    Intent intent = new Intent(getApplicationContext(), UserLocationActivity.class);
                    ArrayList<String> l = new ArrayList<String>();
                    l.add(String.valueOf(v.getId()));
                    l.add(String.valueOf(location.getLatitude()));
                    l.add(String.valueOf(location.getLongitude()));
                    intent.putStringArrayListExtra("content", l);
                    startActivity(intent);
                }
            });
            row.addView(detailButton);

            tblLayout.addView(row,i);
        }
    }

    public void checkLocation(View view){
        Intent intent = new Intent(this, UserLocationActivity.class);
        startActivity(intent);
    }

}
