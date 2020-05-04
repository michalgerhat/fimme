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
    // https://gist.github.com/nesquena/8265f057fef203a2c67e

    public interface CustomLocationListener
    {
        void onLocationChanged(LocationObject location);
    }

    // https://www.thecodecity.com/2017/03/location-tracker-android-app-complete.html

    private CustomLocationListener listener;

    void setListener(CustomLocationListener listener)
    {
        this.listener = listener;
    }

    LocationTracker(Context context)
    {
        this.listener = new CustomLocationListener() {
            @Override
            public void onLocationChanged(LocationObject location) {}
        };

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_DENIED ||
             ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_DENIED)
        {
            String denied = "Location permission denied. Please allow Fimme to access location services.";
            Toast.makeText(context, denied, Toast.LENGTH_LONG).show();
        }
        else if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            String disabled = "Location disabled. Please enable location services.";
            Toast.makeText(context, disabled, Toast.LENGTH_SHORT).show();
        }
        else
        {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_HIGH);
            String provider = lm.getBestProvider(criteria, true);
            lm.requestLocationUpdates(provider, 2000, 2, this);
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
