package com.pigs1493.g_forcemonitor;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.CarToast;
import androidx.car.app.Screen;
import androidx.car.app.ScreenManager;
import androidx.car.app.hardware.CarHardwareManager;
import androidx.car.app.hardware.common.CarValue;
import androidx.car.app.hardware.common.OnCarDataAvailableListener;
import androidx.car.app.hardware.info.Accelerometer;
import androidx.car.app.hardware.info.CarSensors;
import androidx.car.app.hardware.info.Compass;
import androidx.car.app.hardware.info.Gyroscope;
import androidx.car.app.model.Action;
import androidx.car.app.model.Pane;
import androidx.car.app.model.PaneTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.Template;
import androidx.car.app.utils.StringUtils;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.util.List;

public class gforceScreen extends Screen {

    private CarSensors carSensors;
    private List<Float> accel_data;
    private List<Float> gyro_data;
    private List<Float> compass_data;

    private int refresh_delay = 1000;

    private int init = 0;

    static gforceScreen create(@NonNull CarContext carContext) {
        return new gforceScreen(carContext);
    }

    public gforceScreen(CarContext carContext) {
        super(carContext);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                onRefresh();
                handler.postDelayed(this, refresh_delay);
            }
        }, refresh_delay);
        carSensors = getCarContext().getCarService(CarHardwareManager.class).getCarSensors();
        if (getCarContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            init = 1;
            startAccelerometer();
            startCompass();
            startGyro();
        }
    }

    @NonNull
    @Override
    public Template onGetTemplate() {
        Row row_x = new Row.Builder().setTitle("Accelerometer").addText(formatData(accel_data)).build();
        Row row_y = new Row.Builder().setTitle("Compass").addText(formatData(compass_data)).build();
        Row row_z = new Row.Builder().setTitle("Gyro").addText(formatData(gyro_data)).build();
        Action action_1 = new Action.Builder().setTitle("Refresh").setOnClickListener(this::onRefresh).build();
        Action action_2 = new Action.Builder().setTitle("Settings").setOnClickListener(this::loadSettings).build();

        Pane pane = new Pane.Builder().addRow(row_x).addRow(row_y).addRow(row_z).addAction(action_1).addAction(action_2).build();
        return new PaneTemplate.Builder(pane).setTitle("G-Force Monitor").build();
    }

    private void onRefresh() {
        if (getCarContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && init == 0) {
            init = 1;
            startAccelerometer();
            startCompass();
            startGyro();
        }
        this.invalidate();
    }

    private void loadSettings() {
        ScreenManager screenManager = getCarContext().getCarService(ScreenManager.class);
        screenManager.push(SettingsScreen.create(getCarContext()));
        this.finish();
    }

    private void startAccelerometer() {
        OnCarDataAvailableListener<Accelerometer> listener = (data) -> {
            if (data.getForces().getStatus() == CarValue.STATUS_SUCCESS) {
                accel_data = data.getForces().getValue();
            }
        };
        carSensors.addAccelerometerListener(CarSensors.UPDATE_RATE_NORMAL, getCarContext().getMainExecutor(), listener);
    }

    private void startCompass() {
        OnCarDataAvailableListener<Compass> listener = (data) -> {
            if (data.getOrientations().getStatus() == CarValue.STATUS_SUCCESS) {
                compass_data = data.getOrientations().getValue();
            }
        };
        carSensors.addCompassListener(CarSensors.UPDATE_RATE_NORMAL, getCarContext().getMainExecutor(), listener);
    }

    private void startGyro() {
        OnCarDataAvailableListener<Gyroscope> listener = (data) -> {
            if (data.getRotations().getStatus() == CarValue.STATUS_SUCCESS) {
                gyro_data = data.getRotations().getValue();
            }
        };
        carSensors.addGyroscopeListener(CarSensors.UPDATE_RATE_NORMAL, getCarContext().getMainExecutor(), listener);
    }

    private String formatData(List<Float> data) {
        float g_x, g_y, g_z;
        try {
            g_x = data.get(0);
            g_y = data.get(1);
            g_z = data.get(2);
        }
        catch (Exception e) {
            g_x = 0;
            g_y = 0;
            g_z = 0;
        }
        return String.format(java.util.Locale.US,"%.2f", g_x) + ", " + String.format(java.util.Locale.US,"%.2f", g_y) + ", " + String.format(java.util.Locale.US,"%.2f", g_z);
    }
}
