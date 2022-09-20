package com.pigs1493.g_forcemonitor;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.CarToast;
import androidx.car.app.Screen;
import androidx.car.app.ScreenManager;
import androidx.car.app.model.ItemList;
import androidx.car.app.model.ListTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.SectionedItemList;
import androidx.car.app.model.Template;

public class SettingsScreen extends Screen {

    private sensorData SensorData;
    private int refresh_delay;

    static SettingsScreen create(@NonNull CarContext carContext, int refresh_delay) {
        return new SettingsScreen(carContext, refresh_delay);
    }

    public SettingsScreen(CarContext carContext, int refresh_delay) {
        super(carContext);

        this.refresh_delay = refresh_delay;
        SensorData = new sensorData(carContext);
        getLifecycle().addObserver(new Observer());
    }


    @NonNull
    @Override
    public Template onGetTemplate() {
        Row row_i = new Row.Builder().setTitle("Increase Delay").setOnClickListener(this::increaseDelay).build();
        Row row_d = new Row.Builder().setTitle("Decrease Delay").setOnClickListener(this::decreaseDelay).build();

        Row row_m = new Row.Builder().setTitle("Car API Value").addText(String.valueOf(getCarContext().getCarAppApiLevel())).build();

        Row row_x = new Row.Builder().setTitle("Accelerometer").addText(formatData(SensorData.accel_present)).build();
        Row row_y = new Row.Builder().setTitle("Compass").addText(formatData(SensorData.compass_present)).build();
        Row row_z = new Row.Builder().setTitle("Gyro").addText(formatData(SensorData.gyro_present)).build();

        Row row_r = new Row.Builder().setTitle("Refresh").setOnClickListener(this::onRefresh).build();
        Row row_e = new Row.Builder().setTitle("Back").setOnClickListener(this::loadBack).build();

        SectionedItemList list_1 = SectionedItemList.create(new ItemList.Builder().setNoItemsMessage("Empty").addItem(row_i).addItem(row_d).build(), "Screen Refresh Delay");
        SectionedItemList list_2 = SectionedItemList.create(new ItemList.Builder().setNoItemsMessage("Empty").addItem(row_x).addItem(row_y).addItem(row_z).build(), "Car Sensors");
        SectionedItemList list_3 = SectionedItemList.create(new ItemList.Builder().setNoItemsMessage("Empty").addItem(row_m).build(), "Debug Data");
        SectionedItemList list_4 = SectionedItemList.create(new ItemList.Builder().setNoItemsMessage("Empty").addItem(row_r).addItem(row_e).build(), "Settings");
        return new ListTemplate.Builder().addSectionedList(list_1).addSectionedList(list_2).addSectionedList(list_3).addSectionedList(list_4).setTitle("Settings").build();
    }

    public void initSensors() {
        if (getCarContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        SensorData.initSensors();
    }

    public void stopSensors() {
        SensorData.stopSensors();
    }

    private void increaseDelay() {
        refresh_delay += 100;
        CarToast.makeText(getCarContext(), "Screen will now refresh every " + refresh_delay + "ms.", CarToast.LENGTH_SHORT).show();
    }

    private void decreaseDelay() {
        refresh_delay -= 100;
        if (refresh_delay < 100) {
            refresh_delay = 100;
        }
        CarToast.makeText(getCarContext(), "Screen will now refresh every " + refresh_delay + "ms.", CarToast.LENGTH_SHORT).show();
    }

    private void onRefresh() {
        this.invalidate();
    }

    private void loadBack() {
        ScreenManager screenManager = getCarContext().getCarService(ScreenManager.class);
        screenManager.push(gforceScreen.create(getCarContext(), refresh_delay));
        this.finish();
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
