package com.ahmer.whatsapp.activity;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.ahmer.afzal.utils.utilcode.Utils;

public class MyApp extends MultiDexApplication implements LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public static void onAppBackgrounded() {
        SplashActivity.imageStatuses.clear();
        SplashActivity.videoStatuses.clear();
        SplashActivity.bothStatuses.clear();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(MyApp.this);
        Utils.init(MyApp.this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        // Keep empty
    }
}
