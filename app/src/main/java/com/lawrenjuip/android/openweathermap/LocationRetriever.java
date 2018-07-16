package com.lawrenjuip.android.openweathermap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;

public class LocationRetriever {
    public static final int ACCESS_FINE_LOCATION_REQUEST = 0;
    private static final String TAG = "LocationRetriever";
    private LocationManager mLocationManager;
    private Location mLocation;
    private Context mContext;
    private ArrayList<LocationRetrieverListener> mListeners;

    public LocationRetriever(Context context){
        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        mListeners = new ArrayList<>();

        prepareLocationFromLocationManager();
    }

    public Location getLocation(){
        return mLocation;
    }

    //check if user has enabled location permissions. if so, obtain the last known location & return value.
    //if user hasn't enabled permissions, prompt for access. if user allows access, then obtain the location & return value.
    private void prepareLocationFromLocationManager(){
        if(hasLocationPermission()){
            getLocationFromLocationManager();
        }else{
            //Calls back to hosting Activity (mContext)'s onRequestPermissionsResult
            requestGPSPermission();

            if(hasLocationPermission()) {
                getLocationFromLocationManager();
            }
        }
    }

    public void getLocationAfterPermissionChanged(int[] grantResults){
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
           getLocationFromLocationManager();
        }
    }

    public boolean hasLocationPermission(){
        if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private void getLocationFromLocationManager(){
        try{
            if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Criteria locationCriteria = new Criteria();
                locationCriteria.setAccuracy(Criteria.ACCURACY_FINE);
                mLocationManager.requestSingleUpdate(locationCriteria, new LocationListener(){
                    @Override
                    public void onLocationChanged(Location location) {
                        mLocation = location;
                        for (LocationRetrieverListener listener : mListeners) {
                            listener.onLocationChanged(location);
                        }
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                }, null);
            }
        }catch(SecurityException e){
            Log.e(TAG, e.getMessage());
        }
    }

    public void requestGPSPermission(){
        ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST);
    }

    public void addListener(LocationRetrieverListener listener){
        mListeners.add(listener);
    }

    public interface LocationRetrieverListener {
        void onLocationChanged(Location location);
    }
}
