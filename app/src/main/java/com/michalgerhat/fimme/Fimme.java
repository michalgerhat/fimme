package com.michalgerhat.fimme;

import android.accounts.AccountManager;
import android.content.Context;

public class Fimme
{
    public interface TrackingListener
    {
        void onTracking();
    }

    private TrackingListener listener;

    void setListener(TrackingListener listener)
    {
        this.listener = listener;
    }

    private Context context;

    private SocketManager socket;
    private AccountManager accountManager;
    private AuthPreferences authPreferences;

    private LocationTracker tracker;
    private Compass compass;

    private int myAzimuth = 0;
    private LocationObject myLocation = null;
    private LocationObject targetLocation = null;

    Fimme(Context context)
    {
        this.context = context;
        this.socket = new SocketManager(this.context);
        this.authPreferences = new AuthPreferences(this.context);
        this.accountManager = AccountManager.get(this.context);
        this.compass = new Compass(this.context);
        this.tracker = new LocationTracker(this.context);

        tracker.setListener(new LocationTracker.CustomLocationListener()
        {
            @Override
            public void onLocationChanged(LocationObject location)
            {
                myLocation = location;
            }
        });

        compass.setListener(new Compass.CustomCompassListener()
        {
            @Override
            public void onSensorChanged(int azimuth)
            {
                myAzimuth = azimuth;
            }
        });
    }

    public void setTargetLocation(LocationObject target)
    {
        targetLocation = target;
    }

    public int distanceToTarget()
    {
        return (myLocation != null && targetLocation != null) ? myLocation.getDistance(targetLocation) : 0;
    }

    public int directionToTarget()
    {
        return (myLocation != null && targetLocation != null) ? (360 - myAzimuth + myLocation.getBearing(targetLocation)) % 360 : 0;
    }
}
