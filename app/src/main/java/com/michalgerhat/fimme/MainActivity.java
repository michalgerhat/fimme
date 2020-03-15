package com.michalgerhat.fimme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.media.Image;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    TextView txtDisplayName;
    TextView txtDistance;
    ImageView svgArrow;
    int currentDirection = 0;
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
        svgArrow = (ImageView)findViewById(R.id.svgArrow);

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

                    // https://www.javacodegeeks.com/2013/09/android-compass-code-example.html

                    RotateAnimation pointArrow = new RotateAnimation(
                            currentDirection, direction,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                    pointArrow.setDuration(200);
                    pointArrow.setFillAfter(true);
                    svgArrow.startAnimation(pointArrow);
                    currentDirection = direction;
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

                txtDistance.setText(distance + " m");
            }
        });
    }
}
