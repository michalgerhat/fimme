package com.michalgerhat.fimme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{
    Button btnLocation;
    TextView txtLocation;
    TextView txtDirection;
    Timer t = new Timer();
    final Handler handler = new Handler();

    final LocationObject churchStWenceslas = new LocationObject(50.073333, 14.404722, 0.0);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);

        btnLocation = (Button)findViewById(R.id.btnLocation);
        txtLocation = (TextView)findViewById(R.id.txtLocation);
        txtDirection = (TextView)findViewById(R.id.txtDirection);

        btnLocation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
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
                                GpsTracker g = new GpsTracker(getApplicationContext());
                                Location l = g.getLocation();
                                if (l != null)
                                {
                                    LocationObject myLocation = new LocationObject(l);
                                    int distance = (int)Math.round(myLocation.getDistance(churchStWenceslas));
                                    txtLocation.setText("Distance from the Church of St. Wenceslas:\n" + distance + " meters");
                                }
                            }
                        });
                    }
                };
                t.scheduleAtFixedRate(refresh, 0, 2000);
            }
        });
    }
}
