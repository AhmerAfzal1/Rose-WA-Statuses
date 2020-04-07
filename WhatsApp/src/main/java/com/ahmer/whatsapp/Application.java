package com.ahmer.whatsapp;

import com.ahmer.afzal.utils.utilcode.AppUtils;

public class Application extends android.app.Application {

    private static Application instance;

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //AppUtils.uninstallApp(AppUtils.getAppPackageName());
    }
}