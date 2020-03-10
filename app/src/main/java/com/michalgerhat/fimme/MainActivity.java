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

public class MainActivity extends AppCompatActivity {

    Button btnLocation;
    TextView txtLocation;
    Timer t = new Timer();
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);

        btnLocation = (Button)findViewById(R.id.btnLocation);
        txtLocation = (TextView)findViewById(R.id.txtLocation);

        btnLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TimerTask refresh = new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                GpsTracker g = new GpsTracker(getApplicationContext());
                                Location l = g.getLocation();
                                if (l != null) {
                                    double lat = l.getLatitude();
                                    double lon = l.getLongitude();
                                    txtLocation.setText("LAT: " + lat + " LON: " + lon);
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
