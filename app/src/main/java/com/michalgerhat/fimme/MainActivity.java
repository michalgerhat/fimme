package com.michalgerhat.fimme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity
{
    private int SELECT_PLACE = 1;

    private int currentDirection = 0;
    private int distance = 0;
    private SocketManager socket;
    private LocationObject myLocation;
    private LocationTracker tracker;
    private Compass compass;

    private FloatingActionButton fabExplore;
    private TextView lblStatus;
    private TextView txtDisplayName;
    private TextView txtDistance;
    private ImageView svgArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);

        lblStatus = findViewById(R.id.lblStatus);
        txtDisplayName = findViewById(R.id.txtDisplayName);
        txtDistance = findViewById(R.id.txtDistance);
        svgArrow = findViewById(R.id.svgArrow);
        fabExplore = findViewById(R.id.fabExplore);

        lblStatus.setText("Getting your location...");
        txtDisplayName.setText("");
        fabExplore.setEnabled(false);

        compass = new Compass(context);
        tracker = new LocationTracker(context);
        tracker.setListener(new LocationTracker.CustomLocationListener()
        {
            @Override
            public void onLocationChanged(LocationObject location)
            {
                if (myLocation == null)
                {
                    lblStatus.setText("Ready to track");
                    fabExplore.setEnabled(true);
                    fabExplore.show();

                    fabExplore.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Intent intent = new Intent(MainActivity.this, PlacesActivity.class);
                            Bundle locationBundle = new Bundle();
                            locationBundle.putSerializable("MY_LOCATION", myLocation);
                            intent.putExtras(locationBundle);
                            startActivityForResult(intent, SELECT_PLACE);
                        }
                    });
                }
                myLocation = location;
            }
        });

        socket = new SocketManager(context);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PLACE && resultCode == RESULT_OK)
        {
            Bundle locationBundle = data.getExtras();
            LocationObject target = (LocationObject)locationBundle.getSerializable("TARGET_LOCATION");
            setTargetLocation(target);
        }
    }

    public void setTargetLocation(final LocationObject target)
    {
        lblStatus.setText("Tracking");
        txtDisplayName.setText(target.displayName);
        distance = myLocation.getDistance(target);
        txtDistance.setText(distance + " m");

        compass.setListener(new Compass.CustomCompassListener()
        {
            @Override
            public void onSensorChanged(int azimuth)
            {
                if (myLocation != null)
                {
                    int bearing = myLocation.getBearing(target);
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

        tracker.setListener(new LocationTracker.CustomLocationListener()
        {
            @Override
            public void onLocationChanged(LocationObject location)
            {
                myLocation = location;
                distance = myLocation.getDistance(target);
                txtDistance.setText(distance + " m");
            }
        });
    }
}
