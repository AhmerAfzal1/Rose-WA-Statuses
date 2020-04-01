package com.ahmer.whatsapp;

import com.ahmer.afzal.utils.info.ApplicationUtils;

public class Application extends android.app.Application {

    private static Application instance;

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        ApplicationUtils.uninstallApp(ApplicationUtils.getAppPackageName());
    }
}