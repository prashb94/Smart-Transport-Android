package ch.ethz.vs.se.team7.carbonfootprintmonitor;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


import static android.content.Context.LOCATION_SERVICE;
import static android.support.v4.app.ActivityCompat.requestPermissions;

/**
 * Created by Prashanth on 10/23/2017.
 */

public class LocationAndSpeed extends AsyncTask<Void, Void, Void> {

    private LocationManager locationManager;

    private LocationListener locationListener;

    private Context mContext;

    private LastKnownLocationAndSpeed saveLocationAndSpeed;

    public LocationAndSpeed(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {

        Log.d("CUR_CONTEXT", "ASYNC_PRE_EXECUTE");

        saveLocationAndSpeed = new LastKnownLocationAndSpeed();

        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("CUR_CONTEXT", "LOCATIONCHANGED");
                saveLocationAndSpeed.setLastDetectedSpeed(String.valueOf(location.getSpeed()));
                saveLocationAndSpeed.setLastDetectedLocation(location.getLatitude() + "," + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(i);
            }
        };
        if (!isCancelled()) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                            , 10);
                }
                Log.d("PERMISSION_GPS", "PERMISSION_DENIED_GPS");
                return;
            }
            locationManager.requestLocationUpdates("gps", MainActivity.MEASUREMENT_INTERVAL, 0, locationListener);
        }
        else
        {
            Log.d("CUR_CONTEXT", "ASYNC_ISCANCELLED");
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    protected Void doInBackground(Void... aCoupleOfVoids) {

        Log.d("CUR_CONTEXT", "ASYNC_DO_IN_BACKGROUND");
        while(!isCancelled()){

        }
        return null;
    }

    @Override
    protected void onCancelled(Void aVoid) {
        Log.d("CUR_CONTEXT", "ASYNC_ONCANCELLED_RESULT");
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onCancelled() {
        Log.d("CUR_CONTEXT", "ASYNC_ONCANCELLED");
        locationManager.removeUpdates(locationListener);
    }
}