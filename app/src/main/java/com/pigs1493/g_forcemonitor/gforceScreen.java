package com.pigs1493.g_forcemonitor;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.ScreenManager;
import androidx.car.app.model.Action;
import androidx.car.app.model.Pane;
import androidx.car.app.model.PaneTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.Template;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import java.util.List;

public class gforceScreen extends Screen {

    private int init = 0;

    private sensorData SensorData;
    private int refresh_delay;

    static gforceScreen create(@NonNull CarContext carContext) {
        return new gforceScreen(carContext, 500);
    }
    static gforceScreen create(@NonNull CarContext carContext, int refresh_delay) {
        return new gforceScreen(carContext, refresh_delay);
    }

    public gforceScreen(CarContext carContext, int refresh_delay) {
        super(carContext);

        this.refresh_delay = refresh_delay;
        Log.i("Gforce Screen", String.valueOf(refresh_delay));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                onRefresh();
                handler.postDelayed(this, refresh_delay);
            }
        }, refresh_delay);

        SensorData = new sensorData(carContext);

        getLifecycle().addObserver(new Observer());
    }

    @NonNull
    @Override
    public Template onGetTemplate() {
        Row row_x = new Row.Builder().setTitle("Accelerometer").addText(formatData(SensorData.accel_data)).build();
        Row row_y = new Row.Builder().setTitle("Compass").addText(formatData(SensorData.compass_data)).build();
        Row row_z = new Row.Builder().setTitle("Gyro").addText(formatData(SensorData.gyro_data)).build();
        Action action_1 = new Action.Builder().setTitle("Refresh").setOnClickListener(this::onRefresh).build();
        Action action_2 = new Action.Builder().setTitle("Settings").setOnClickListener(this::loadSettings).build();

        Pane pane = new Pane.Builder().addRow(row_x).addRow(row_y).addRow(row_z).addAction(action_1).addAction(action_2).build();
        return new PaneTemplate.Builder(pane).setTitle("G-Force Monitor").build();
    }

    public void initSensors() {
        if (getCarContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        init = 1;
        SensorData.initSensors();
    }

    public void stopSensors() {
        SensorData.stopSensors();
    }

    private void onRefresh() {
        if (init == 0) {
            initSensors();
        }
        this.invalidate();
    }

    private void loadSettings() {
        ScreenManager screenManager = getCarContext().getCarService(ScreenManager.class);
        screenManager.push(SettingsScreen.create(getCarContext(), refresh_delay));
        this.finish();
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
        return String.format(java.util.Locale.US,"%.1f", g_x) + ", " + String.format(java.util.Locale.US,"%.1f", g_y) + ", " + String.format(java.util.Locale.US,"%.1f", g_z);
    }
}
