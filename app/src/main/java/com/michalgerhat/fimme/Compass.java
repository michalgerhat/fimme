package com.michalgerhat.fimme;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

public class Compass implements SensorEventListener
{
    // https://gist.github.com/nesquena/8265f057fef203a2c67e

    public interface CustomCompassListener
    {
        void onSensorChanged(int azimuth);
    }

    // https://www.wlsdevelop.com/index.php/en/blog?option=com_content&view=article&id=38

    private float[] rMat = new float[9];
    private float[] orientation = new float[3];
    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;

    private CustomCompassListener listener;

    void setListener(CustomCompassListener listener)
    {
        this.listener = listener;
    }

    Compass(Context context)
    {
        this.listener = new CustomCompassListener() {
            @Override
            public void onSensorChanged(int azimuth) {}
        };

        SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        if (sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null)
        {
            if ((sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null)
                    || (sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null))
            {
                Toast.makeText(context, "There are no sensors available.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Sensor accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                Sensor magnetometer = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
                sm.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
            }
        }
        else
        {
            Sensor rotation = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            sm.registerListener(this, rotation, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        int azimuth = 0;

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
        {
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            azimuth = (int)(Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.length);
            lastAccelerometerSet = true;
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.length);
            lastMagnetometerSet = true;
        }

        if (lastAccelerometerSet && lastMagnetometerSet)
        {
            SensorManager.getRotationMatrix(rMat, null, lastAccelerometer, lastMagnetometer);
            SensorManager.getOrientation(rMat, orientation);
            azimuth = (int)(Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        listener.onSensorChanged(azimuth);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
