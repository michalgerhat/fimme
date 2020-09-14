package com.michalgerhat.fimme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.accounts.AccountManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements MainActivityContext
{
    FimmeService fimme;
    boolean bound = false;

    private ServiceConnection serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            FimmeService.LocalBinder binder = (FimmeService.LocalBinder)service;
            fimme = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            bound = false;
        }
    };

    private static final int LOGIN = 1;
    private static final int SELECT_PLACE = 2;

    private LocationObject myLocation;
    private int currentDirection = 0;
    private int distance = 0;

    private Context context;

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
        this.context = getApplicationContext();

        Intent serviceIntent = new Intent(this, FimmeService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);

        lblStatus = findViewById(R.id.lblStatus);
        txtDisplayName = findViewById(R.id.txtDisplayName);
        txtDistance = findViewById(R.id.txtDistance);
        svgArrow = findViewById(R.id.svgArrow);
        fabExplore = findViewById(R.id.fabExplore);

        lblStatus.setText("Getting your location...");
        txtDisplayName.setText("");
        fabExplore.setEnabled(false);

        this.compass = new Compass(context);
        this.tracker = new LocationTracker(context);

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode)
        {
            case LOGIN:
                try
                {
                    JSONObject credentials = new JSONObject();
                    credentials.put("username", data.getStringExtra("username"));
                    credentials.put("password", data.getStringExtra("password"));
                    socket.sendMessage("request-login", credentials);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                break;

            case SELECT_PLACE:
                Bundle locationBundle = data.getExtras();
                LocationObject target = (LocationObject)locationBundle.getSerializable("TARGET_LOCATION");
                setTargetLocation(target);
                break;
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

    public void toast(final String message)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public SocketManager getSocketManager()
    {
        return socket;
    }
}
