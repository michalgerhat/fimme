package com.michalgerhat.fimme;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;
import android.widget.Toast;

public class Compass implements SensorEventListener
{
    // https://www.wlsdevelop.com/index.php/en/blog?option=com_content&view=article&id=38

    Context context;
    TextView outputField;
    private int azimuth;
    private SensorManager sm;
    private Sensor rotation, accelerometer, magnetometer;
    boolean haveSensor = false, haveSensor2 = false;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;

    public Compass(Context context, TextView outputField)
    {
        this.outputField = outputField;
        this.context = context;

        sm = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);

        if (sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null)
        {
            if ((sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null)
                    || (sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null))
            {
                Toast.makeText(context, "No sensors", Toast.LENGTH_SHORT).show();
            }
            else
            {
                accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                magnetometer = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                haveSensor = sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = sm.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
            }
        }
        else
        {
            rotation = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = sm.registerListener(this, rotation, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
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

        azimuth = Math.round(azimuth);
        outputField.setText(azimuth + " degrees off North");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
