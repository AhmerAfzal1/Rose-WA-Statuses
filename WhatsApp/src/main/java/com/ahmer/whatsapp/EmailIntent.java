package com.ahmer.whatsapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;

import com.ahmer.afzal.utils.utilcode.AppUtils;
import com.ahmer.afzal.utils.utilcode.DeviceUtils;
import com.ahmer.afzal.utils.utilcode.ToastUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.List;

public class EmailIntent extends Activity {

    private FirebaseAnalytics firebaseAnalytics;
    private String className = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics firebaseCrashlytics = FirebaseCrashlytics.getInstance();
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        firebaseCrashlytics.log("Start " + getClass().getSimpleName() + " Crashlytics logging...");
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "EmailAppEvent");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, getClass().getSimpleName());
        firebaseAnalytics.logEvent("Email_App_Open", bundle);
        try {
            PackageInfo version_number = getPackageManager().getPackageInfo(getPackageName(), 0);
            Log.v(Constant.TAG, "App Version number is: " + version_number);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        String sb = "\n\n\n" + getResources().getString(R.string.auto_generate_msg) + "\n\nApp Version = " + AppUtils.getAppVersionName() + "\nManufacturer = "
                + DeviceUtils.getModel() + "\nBrand = " + DeviceUtils.getManufacturer() + "\nProduct = " + DeviceUtils.getProduct() + "\nArchitecture = "
                + DeviceUtils.getArchitectureOS() + "\nHardware = " + DeviceUtils.getHardware() + "\nID = " + DeviceUtils.getId() + "\nOS Version = "
                + DeviceUtils.getSDKVersionName() + "\nOS API Level = " + DeviceUtils.getSDKVersionCode();
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        final PackageManager pm = this.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        for (final ResolveInfo info : matches) {
            if (info.activityInfo.packageName.equals("com.google.android.gm")) {
                className = info.activityInfo.name;
                if (className != null && !className.isEmpty()) {
                    break;
                }
            }
        }
        assert className != null;
        emailIntent.setClassName("com.google.android.gm", className);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.email_ahmer_yahoo)});
        emailIntent.putExtra(Intent.EXTRA_CC, new String[]{getResources().getString(R.string.email_ahmer_gmail)});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.send_email_to_author));
        emailIntent.putExtra(Intent.EXTRA_TEXT, sb);
        try {
            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.send_email_to_author)));
            }
            return;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        ToastUtils.showLong(getResources().getString(R.string.no_email_client));
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAnalytics.setCurrentScreen(this, "CurrentScreen: " + getClass().getSimpleName(), null);
    }
}