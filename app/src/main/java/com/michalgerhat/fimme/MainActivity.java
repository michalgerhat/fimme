package com.michalgerhat.fimme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    TextView txtDisplayName;
    TextView txtDistance;
    TextView txtDirection;
    LocationObject myLocation;

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

        txtDisplayName.setText(customLoc.displayName);

        Compass compass = new Compass(getApplicationContext());
        compass.setListener(new Compass.CustomCompassListener()
        {
            @Override
            public void onSensorChanged(int azimuth)
            {
                if (myLocation != null)
                {
                    int bearing = myLocation.getBearing(customLoc);
                    int direction = (360 - azimuth + bearing) % 360;
                    txtDirection.setText("Direction: " + direction + " degrees");
                }
            }
        });

        LocationTracker tracker = new LocationTracker(getApplicationContext());
        tracker.setListener(new LocationTracker.CustomLocationListener()
        {
            @Override
            public void onLocationChanged(LocationObject location)
            {
                myLocation = location;
                int distance = myLocation.getDistance(customLoc);

                txtDistance.setText("Distance: " + distance + " meters");
            }
        });
    }
}
