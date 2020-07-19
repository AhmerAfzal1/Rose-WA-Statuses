package com.ahmer.whatsapp.activity;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDexApplication;

public class MyApp extends MultiDexApplication implements LifecycleObserver {

    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @SuppressWarnings("EmptyMethod")
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        // Keep empty
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        SplashActivity.imageStatuses.clear();
        SplashActivity.videoStatuses.clear();
        SplashActivity.bothStatuses.clear();
    }
}
