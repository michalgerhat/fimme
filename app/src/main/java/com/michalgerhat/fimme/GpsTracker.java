package com.michalgerhat.fimme;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class GpsTracker implements LocationListener
{
    // https://github.com/Shivakishore14/AndroidTutorial/tree/master/GPStut

    Context context;

    public GpsTracker(Context c)
    {
        context = c;
    }

    public Location getLocation()
    {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            Toast.makeText(context, "Permission not granted", Toast.LENGTH_SHORT).show();

        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean GpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (GpsEnabled)
        {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, this);
            Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return l;
        }
        else
        {
            Toast.makeText(context, "Please enable GPS", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}
}
