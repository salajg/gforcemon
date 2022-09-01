package com.pigs1493.gforcemonitor;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.CarToast;
import androidx.car.app.Screen;
import androidx.car.app.model.Action;
import androidx.car.app.model.Pane;
import androidx.car.app.model.PaneTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.Template;

import android.content.Intent;
import android.os.Handler;

public class GForceScreen extends Screen {

    //private SensorActivity sensors;

    private int first_run = 1;

    private double g_x = 0;
    private double g_y = 0;
    private double g_z = 0;

    private int refresh_delay = 1000;

    public GForceScreen(CarContext carContext) {
        super(carContext);
    }

    @NonNull
    @Override
    public Template onGetTemplate() {

        if (first_run == 1) {
            first_run = 0;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    onRefresh();
                    handler.postDelayed(this, refresh_delay);
                }
            }, refresh_delay);
        }

        Row row_x = new Row.Builder().setTitle("X Value").addText(String.valueOf(g_x)).build();
        Row row_y = new Row.Builder().setTitle("Y Value").addText(String.valueOf(g_y)).build();
        Row row_z = new Row.Builder().setTitle("Z Value").addText(String.valueOf(g_z)).build();
        Action action = new Action.Builder().setTitle("Refresh").setOnClickListener(this::onRefresh).build();

        Pane pane = new Pane.Builder().addRow(row_x).addRow(row_y).addRow(row_z).addAction(action).build();

        return new PaneTemplate.Builder(pane).setTitle("G-Force Monitor").build();
    }
    private void onRefresh() {
        g_x = (g_x+1)%100;
        g_y = (g_y+2)%100;
        g_z = (g_x+g_y)/2;
        //g_x = sensors.g_x;
        //g_y = sensors.g_y;
        //g_z = sensors.g_z;
        this.invalidate();
    }
}


