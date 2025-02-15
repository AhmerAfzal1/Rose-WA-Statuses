package com.ahmer.whatsapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.ahmer.afzal.utils.async.AsyncTask;
import com.ahmer.afzal.utils.constants.PermissionConstants;
import com.ahmer.afzal.utils.utilcode.PathUtils;
import com.ahmer.afzal.utils.utilcode.PermissionUtils;
import com.ahmer.afzal.utils.utilcode.SPUtils;
import com.ahmer.afzal.utils.utilcode.ScreenUtils;
import com.ahmer.afzal.utils.utilcode.ThrowableUtils;
import com.ahmer.afzal.utils.utilcode.UtilsTransActivity;
import com.ahmer.whatsapp.Constant;
import com.ahmer.whatsapp.StatusItem;
import com.ahmer.whatsapp.Thumbnails;
import com.ahmer.whatsapp.databinding.ActivitySplashBinding;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.ahmer.whatsapp.Constant.EXT_JPG_LOWER_CASE;
import static com.ahmer.whatsapp.Constant.EXT_JPG_UPPER_CASE;
import static com.ahmer.whatsapp.Constant.EXT_MP4_LOWER_CASE;
import static com.ahmer.whatsapp.Constant.EXT_MP4_UPPER_CASE;
import static com.ahmer.whatsapp.Constant.TAG;
import static com.ahmer.whatsapp.Constant.WHATSAPP_BUSINESS_LOCATION;
import static com.ahmer.whatsapp.Constant.WHATSAPP_FM_LOCATION;
import static com.ahmer.whatsapp.Constant.WHATSAPP_LOCATION;
import static com.ahmer.whatsapp.Constant.WHATSAPP_YO_LOCATION;

public class SplashActivity extends AppCompatActivity {

    public static final ArrayList<StatusItem> videoStatuses = new ArrayList<>();
    public static final ArrayList<StatusItem> imageStatuses = new ArrayList<>();
    public static final ArrayList<StatusItem> bothStatuses = new ArrayList<>();
    public static final File dirBusinessWhatsApp = new File(PathUtils.getExternalStoragePath() + WHATSAPP_BUSINESS_LOCATION);
    public static final File dirFMWhatsApp = new File(PathUtils.getExternalStoragePath() + WHATSAPP_FM_LOCATION);
    public static final File dirWhatsApp = new File(PathUtils.getExternalStoragePath() + WHATSAPP_LOCATION);
    public static final File dirYoWhatsApp = new File(PathUtils.getExternalStoragePath() + WHATSAPP_YO_LOCATION);

    public static void getData() {

        /*//File dirAhmer = new File(PathUtils.getExternalDownloadsPath());
        File dirAhmer = new File(PathUtils.getExternalStoragePath() + "/Ahmer");
        if (dirAhmer.exists()) {
            getStatuses(dirAhmer.listFiles());
        }*/

        if (dirWhatsApp.exists()) {
            getStatuses(dirWhatsApp.listFiles());
        }
        if (dirBusinessWhatsApp.exists()) {
            getStatuses(dirBusinessWhatsApp.listFiles());
        }
        if (dirFMWhatsApp.exists()) {
            getStatuses(dirFMWhatsApp.listFiles());
        }
        if (dirYoWhatsApp.exists()) {
            getStatuses(dirYoWhatsApp.listFiles());
        }
    }

    private static void getStatuses(File[] filesList) {
        if (filesList != null) {
            for (File file : filesList) {
                getVideoStatuses(file);
                getImageStatuses(file);
            }
        }
    }

    private static void getVideoStatuses(@NonNull File file) {
        String filePath = file.getAbsolutePath();
        if (filePath.endsWith(EXT_MP4_LOWER_CASE) || filePath.endsWith(EXT_MP4_UPPER_CASE)) {
            StatusItem item = new StatusItem();
            item.setPath(filePath);
            item.setName(file.getName());
            item.setSize(file.length());
            item.setFormat(EXT_MP4_LOWER_CASE);
            Bitmap video = Thumbnails.videoThumbnails(file);
            item.setThumbnails(video);
            videoStatuses.add(item);
            bothStatuses.add(item);
        }
    }

    private static void getImageStatuses(@NonNull File file) {
        String filePath = file.getAbsolutePath();
        if (filePath.endsWith(EXT_JPG_LOWER_CASE) || filePath.endsWith(EXT_JPG_UPPER_CASE)) {
            StatusItem item = new StatusItem();
            item.setPath(filePath);
            item.setName(file.getName());
            item.setSize(file.length());
            item.setFormat(EXT_JPG_LOWER_CASE);
            Bitmap jpg = Thumbnails.imageThumbnails(file);
            item.setThumbnails(jpg);
            imageStatuses.add(item);
            bothStatuses.add(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        SPUtils themePref = SPUtils.getInstance(Constant.PREFERENCE_THEME);
        boolean isChecked = themePref.getBoolean(Constant.PREFERENCE_THEME_KEY);
        if (isChecked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        checkPermissions();
    }

    private void checkPermissions() {
        PermissionUtils.permission(PermissionConstants.STORAGE).rationale(new PermissionUtils.OnRationaleListener() {
            @Override
            public void rationale(@NonNull UtilsTransActivity activity, @NonNull ShouldRequest shouldRequest) {
                shouldRequest.again(true);
                Log.v(TAG, getClass().getSimpleName() + " -> Again permission checking");
            }
        }).callback(new PermissionUtils.FullCallback() {
            @Override
            public void onGranted(@NonNull List<String> permissionsGranted) {
                Log.v(TAG, getClass().getSimpleName() + " -> Permission has been granted");
                RunMainProgram runMainProgram = new RunMainProgram(SplashActivity.this);
                runMainProgram.execute(null);
            }

            @Override
            public void onDenied(@NonNull List<String> permissionsDeniedForever, @NonNull List<String> permissionsDenied) {
                Log.v(TAG, getClass().getSimpleName() + " -> Permission has not been granted");
                if (!permissionsDenied.isEmpty() || !permissionsDeniedForever.isEmpty()) {
                    finish();
                }
            }
        }).theme(new PermissionUtils.ThemeCallback() {
            @Override
            public void onActivityCreate(@NonNull Activity activity) {
                Log.v(TAG, getClass().getSimpleName() + " -> Permission ThemeCallback runs");
                //Must add this for run app properly
                ScreenUtils.setFullScreen(activity);
            }
        }).request();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (imageStatuses != null) {
            imageStatuses.clear();
        }
        if (videoStatuses != null) {
            videoStatuses.clear();
        }
        if (bothStatuses != null) {
            bothStatuses.clear();
        }
    }

    private static class RunMainProgram extends AsyncTask<Void, Void, Void> {

        private final Activity activity;

        RunMainProgram(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void aVoid) throws Exception {
            SplashActivity.getData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            SPUtils launcherPref = SPUtils.getInstance(Constant.PREFERENCE_LAUNCHER);
            if (!launcherPref.getBoolean(Constant.PREFERENCE_LAUNCHER_KEY)) {
                Intent intentMainActivity = new Intent(activity, MainActivity.class);
                intentMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                activity.startActivity(intentMainActivity);
            } else {
                Intent intentMainTabbed = new Intent(activity, MainTabbedActivity.class);
                intentMainTabbed.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    intentMainTabbed.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                activity.startActivity(intentMainTabbed);
            }
            activity.finish();
        }

        @Override
        protected void onBackgroundError(@NonNull Exception e) {
            e.printStackTrace();
            Log.v(TAG, getClass().getSimpleName() + " -> Exception: Error during loading data: " + e.getMessage());
            ThrowableUtils.getFullStackTrace(e);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
}
