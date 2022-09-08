package com.pigs1493.g_forcemonitor;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
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

public class SettingsScreen extends Screen {

    private CarSensors carSensors;
    private int accel_data = 0;
    private int gyro_data = 0;
    private int compass_data = 0;

    static SettingsScreen create(@NonNull CarContext carContext) {
        return new SettingsScreen(carContext);
    }

    public SettingsScreen(CarContext carContext) {
        super(carContext);
        carSensors = getCarContext().getCarService(CarHardwareManager.class).getCarSensors();
        startAccelerometer();
        startCompass();
        startGyro();
    }

    @NonNull
    @Override
    public Template onGetTemplate() {
        Row row_m = new Row.Builder().setTitle("Car API Value").addText(String.valueOf(getCarContext().getCarAppApiLevel())).build();
        Row row_x = new Row.Builder().setTitle("Accelerometer").addText(formatData(accel_data)).build();
        Row row_y = new Row.Builder().setTitle("Compass").addText(formatData(compass_data)).build();
        Row row_z = new Row.Builder().setTitle("Gyro").addText(formatData(gyro_data)).build();
        Action action_1 = new Action.Builder().setTitle("Refresh").setOnClickListener(this::onRefresh).build();
        Action action_2 = new Action.Builder().setTitle("Back").setOnClickListener(this::loadBack).build();

        Pane pane = new Pane.Builder().addRow(row_m).addRow(row_x).addRow(row_y).addRow(row_z).addAction(action_1).addAction(action_2).build();
        return new PaneTemplate.Builder(pane).setTitle("Settings").build();
    }

    private void onRefresh() {
        this.invalidate();
    }

    private void loadBack() {
        ScreenManager screenManager = getCarContext().getCarService(ScreenManager.class);
        screenManager.push(gforceScreen.create(getCarContext()));
        this.finish();
    }

    private void startAccelerometer() {
        OnCarDataAvailableListener<Accelerometer> listener = (data) -> {
            if (data.getForces().getStatus() == CarValue.STATUS_SUCCESS) {
                accel_data = 1;
                onRefresh();
            }
            else {
                accel_data = 0;
            }
        };
        carSensors.addAccelerometerListener(CarSensors.UPDATE_RATE_NORMAL, getCarContext().getMainExecutor(), listener);
    }

    private void startCompass() {
        OnCarDataAvailableListener<Compass> listener = (data) -> {
            if (data.getOrientations().getStatus() == CarValue.STATUS_SUCCESS) {
                compass_data = 1;
                onRefresh();
            }
            else {
                compass_data = 0;
            }
        };
        carSensors.addCompassListener(CarSensors.UPDATE_RATE_NORMAL, getCarContext().getMainExecutor(), listener);
    }

    private void startGyro() {
        OnCarDataAvailableListener<Gyroscope> listener = (data) -> {
            if (data.getRotations().getStatus() == CarValue.STATUS_SUCCESS) {
                gyro_data = 1;
                onRefresh();
            }
            else {
                gyro_data = 0;
            }
        };
        carSensors.addGyroscopeListener(CarSensors.UPDATE_RATE_NORMAL, getCarContext().getMainExecutor(), listener);
    }

    private String formatData(int data) {
        if (data == 0) {
            return "Not Present";
        }
        else if (data == 1) {
            return "Present";
        }
        return "Error";
    }
}
