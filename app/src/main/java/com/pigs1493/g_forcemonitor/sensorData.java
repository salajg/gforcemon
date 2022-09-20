package com.pigs1493.g_forcemonitor;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import androidx.car.app.CarContext;
import androidx.car.app.hardware.CarHardwareManager;
import androidx.car.app.hardware.common.CarValue;
import androidx.car.app.hardware.common.OnCarDataAvailableListener;
import androidx.car.app.hardware.info.Accelerometer;
import androidx.car.app.hardware.info.CarSensors;
import androidx.car.app.hardware.info.Compass;
import androidx.car.app.hardware.info.Gyroscope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class sensorData {

    private CarContext carContext;
    private CarSensors carSensors;
    private SensorManager deviceSensors;

    public List<Float> accel_data = new ArrayList<Float>(Collections.nCopies(3, 0F));
    public List<Float> gyro_data = new ArrayList<Float>(Collections.nCopies(3, 0F));
    public List<Float> compass_data = new ArrayList<Float>(Collections.nCopies(3, 0F));

    public int accel_present = 0;
    public int compass_present = 0;
    public int gyro_present = 0;

    public sensorData(CarContext carContext) {
        this.carContext = carContext;
        carSensors = carContext.getCarService(CarHardwareManager.class).getCarSensors();
        deviceSensors = (SensorManager)carContext.getSystemService(Context.SENSOR_SERVICE);
    }

    public void initSensors() {
        Log.i("Sensor Data", "Initializing Sensors");
        if (carContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startAccelerometer();
        startCompass();
        startGyro();
    }

    public void stopSensors() {
        Log.i("Sensor Data", "Stopping Sensors");
        if (carContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        stopAccelerometer();
        stopCompass();
        stopGyro();
    }

    private void startAccelerometer() {
        carSensors.addAccelerometerListener(CarSensors.UPDATE_RATE_NORMAL, carContext.getMainExecutor(), car_accel);
        if (accel_present == 0) {
            deviceSensors.registerListener(phone_accel, deviceSensors.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_UI);
        }
    }

    private void stopAccelerometer() {
        carSensors.removeAccelerometerListener(car_accel);
        if (accel_present == 0) {
            deviceSensors.unregisterListener(phone_accel);
        }
    }

    private void startCompass() {
        carSensors.addCompassListener(CarSensors.UPDATE_RATE_NORMAL, carContext.getMainExecutor(), car_compass);
        if (compass_present == 0) {
            deviceSensors.registerListener(phone_compass, deviceSensors.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
        }
    }

    private void stopCompass() {
        carSensors.removeCompassListener(car_compass);
        if (compass_present == 0) {
            deviceSensors.unregisterListener(phone_compass);
        }
    }

    private void startGyro() {
        carSensors.addGyroscopeListener(CarSensors.UPDATE_RATE_NORMAL, carContext.getMainExecutor(), car_gyro);
        if (gyro_present == 0) {
            deviceSensors.registerListener(phone_gyro, deviceSensors.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_UI);
        }
    }

    private void stopGyro() {
        carSensors.removeGyroscopeListener(car_gyro);
        if (gyro_present == 0) {
            deviceSensors.unregisterListener(phone_gyro);
        }
    }

    private final SensorEventListener phone_accel = new SensorEventListener() {
        public void onSensorChanged(SensorEvent e) {
            if (e.sensor.getType()== Sensor.TYPE_LINEAR_ACCELERATION && accel_present == 0) {
                accel_data.set(0, e.values[0]);
                accel_data.set(1, e.values[1]);
                accel_data.set(2, e.values[2]);
                for (int i = 0; i < 3; i++) {
                    if (accel_data.get(i) < 0.2f && accel_data.get(i) > -0.2f) {
                        accel_data.set(i, 0F);
                    }
                }

            }
        }
        @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    private final OnCarDataAvailableListener<Accelerometer> car_accel = (data) -> {
        if (data.getForces().getStatus() == CarValue.STATUS_SUCCESS) {
            accel_data = data.getForces().getValue();
            if (accel_present == 0){
                accel_present = 1;
                deviceSensors.unregisterListener(phone_accel);
            }
        }
    };

    private final SensorEventListener phone_compass = new SensorEventListener() {
        public void onSensorChanged(SensorEvent e) {
            if (e.sensor.getType()== Sensor.TYPE_ORIENTATION && compass_present == 0) {
                compass_data.set(0, e.values[0]);
                compass_data.set(1, e.values[1]);
                compass_data.set(2, e.values[2]);
            }
        }
        @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    private final OnCarDataAvailableListener<Compass> car_compass = (data) -> {
        if (data.getOrientations().getStatus() == CarValue.STATUS_SUCCESS) {
            compass_data = data.getOrientations().getValue();
            if (compass_present == 0){
                compass_present = 1;
                deviceSensors.unregisterListener(phone_compass);
            }
        }
    };

    private final SensorEventListener phone_gyro = new SensorEventListener() {
        public void onSensorChanged(SensorEvent e) {
            if (e.sensor.getType()== Sensor.TYPE_GYROSCOPE && gyro_present == 0) {
                gyro_data.set(0, e.values[0]);
                gyro_data.set(1, e.values[1]);
                gyro_data.set(2, e.values[2]);
            }
        }
        @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    private final OnCarDataAvailableListener<Gyroscope> car_gyro = (data) -> {
        if (data.getRotations().getStatus() == CarValue.STATUS_SUCCESS) {
            gyro_data = data.getRotations().getValue();
            if (gyro_present == 0){
                gyro_present = 1;
                deviceSensors.unregisterListener(phone_gyro);
            }
        }
    };

}
