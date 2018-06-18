package com.yvonne.alumnicardfunctions;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class CustomLocationListener implements LocationListener {
    private Location location;

    public Location getLocation(){
        if(null==location){
            location = new Location("Default Provider");
            location.setLatitude(24.9869);
            location.setLongitude(121.576);
        }
        return location;
    }

    @Override
    public void onLocationChanged(final Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
