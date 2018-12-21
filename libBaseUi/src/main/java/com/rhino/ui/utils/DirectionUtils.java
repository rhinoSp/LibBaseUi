package com.rhino.ui.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


/**
 * @author rhino
 * @since Create on 2018/10/11.
 */
public class DirectionUtils {

    /**
     * r
     */
    private float[] r = new float[9];
    /**
     * 用来保存最终的结果
     */
    private float[] values = new float[3];
    /**
     * 用来保存加速度传感器的值
     */
    private float[] gravity = new float[3];
    /**
     * 用来保存地磁传感器的值
     */
    private float[] geomagnetic = new float[3];
    /**
     * SensorManager
     */
    private SensorManager sensorManager;
    /**
     * 磁传感器
     */
    private Sensor magneticSensor;
    /**
     * 加速度计传感器
     */
    private Sensor accelerometerSensor;
    /**
     * 监听
     */
    private SensorEventListener mSensorEventListener;


    private static DirectionUtils instance;

    public static DirectionUtils getInstance(Context context) {
        if (instance == null) {
            instance = new DirectionUtils(context);
        }
        return instance;
    }


    public DirectionUtils(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void registerListener(SensorEventListener listener) {
        this.mSensorEventListener = listener;
        sensorManager.registerListener(listener, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterListener() {
        sensorManager.unregisterListener(mSensorEventListener);
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values;
            SensorManager.getRotationMatrix(r, null, gravity, geomagnetic);
            SensorManager.getOrientation(r, values);
        }
    }

    public int getAzimuth() {
        double azimuth = Math.toDegrees(values[0]);
        if (azimuth < 0) {
            azimuth = azimuth + 360;
        }
        return (int) azimuth;
    }

}
