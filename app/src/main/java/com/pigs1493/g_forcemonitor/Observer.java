package com.pigs1493.g_forcemonitor;

import android.util.Log;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Observer implements DefaultLifecycleObserver {

    @Override
    public void onCreate(LifecycleOwner owner) {
        Log.i("Screen Status", "onCreate");

    }

    @Override
    public void onStart(LifecycleOwner owner) {
        Log.i("Screen Status", "onStart");
    }

    @Override
    public void onResume(LifecycleOwner owner) {
        Log.i("Screen Status", "onResume");
        try {
            Method temp = owner.getClass().getMethod("initSensors");
            temp.invoke(owner);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause(LifecycleOwner owner) {
        Log.i("Screen Status", "onPause");
        try {
            Method temp = owner.getClass().getMethod("stopSensors");
            temp.invoke(owner);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop(LifecycleOwner owner) {
        Log.i("Screen Status", "onStop");
    }

    @Override
    public void onDestroy(LifecycleOwner owner) {
        Log.i("Screen Status", "onDestroy");
    }
}
