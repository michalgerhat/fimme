package com.michalgerhat.fimme;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class LocationTracker implements LocationListener
{
    public interface CustomLocationListener
    {
        void onLocationChanged(LocationObject location);
    }

     // https://www.thecodecity.com/2017/03/location-tracker-android-app-complete.html

    Context context;
    CustomLocationListener listener;
    LocationManager lm;
    final String denied = "Location permission denied. Please allow Fimme to access location services.";
    final String disabled = "Location disabled. Please enable location services.";

    public void setListener(CustomLocationListener listener)
    {
        this.listener = listener;
    }

    public LocationTracker(Context context)
    {
        this.context = context;
        this.listener = null;

        lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_DENIED ||
             ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_DENIED)
            Toast.makeText(context, denied, Toast.LENGTH_SHORT).show();
        else
        {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_HIGH);
            String provider = lm.getBestProvider(criteria, true);
            lm.requestLocationUpdates(provider, 2000, 2, this);
        }
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
            !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            Toast.makeText(context, disabled, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        listener.onLocationChanged(new LocationObject(location));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}
}
