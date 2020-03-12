package com.michalgerhat.fimme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{
    TextView txtDisplayName;
    TextView txtDistance;
    TextView txtDirection;
    TextView txtBearing;
    Timer t = new Timer();
    final Handler handler = new Handler();

    final LocationObject customLoc = new LocationObject("Church of Saint Wenceslas", 50.073333, 14.404722, 0.0);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);

        txtDisplayName = (TextView)findViewById(R.id.txtDisplayName);
        txtDistance = (TextView)findViewById(R.id.txtDistance);
        txtDirection = (TextView)findViewById(R.id.txtDirection);
        txtBearing = (TextView)findViewById(R.id.txtBearing);

        txtDisplayName.setText(customLoc.displayName);

        double bearing = 0.0;
        Compass compass = new Compass(getApplicationContext(), txtDirection);

        TimerTask refresh = new TimerTask()
        {
            @Override
            public void run()
            {
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        LocationTracker g = new LocationTracker(getApplicationContext());
                        Location l = g.getLocation();
                        if (l != null)
                        {
                            LocationObject myLocation = new LocationObject(l);
                            int distance = (int)Math.round(myLocation.getDistance(customLoc));
                            txtDistance.setText(distance + " meters");

                            double bearing = myLocation.getBearing(customLoc);
                            txtBearing.setText(bearing + " degrees off the location");
                        }
                    }
                });
            }
        };
        t.scheduleAtFixedRate(refresh, 0, 2000);
    }
}
