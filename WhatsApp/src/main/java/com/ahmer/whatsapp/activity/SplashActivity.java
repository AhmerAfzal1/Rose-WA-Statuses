package com.ahmer.whatsapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.ahmer.afzal.utils.SharedPreferencesUtil;
import com.ahmer.afzal.utils.constants.PermissionConstants;
import com.ahmer.afzal.utils.utilcode.AppUtils;
import com.ahmer.afzal.utils.utilcode.FileUtils;
import com.ahmer.afzal.utils.utilcode.PathUtils;
import com.ahmer.afzal.utils.utilcode.PermissionUtils;
import com.ahmer.afzal.utils.utilcode.ScreenUtils;
import com.ahmer.afzal.utils.utilcode.ThreadUtils;
import com.ahmer.afzal.utils.utilcode.ThrowableUtils;
import com.ahmer.afzal.utils.utilcode.UtilsTransActivity;
import com.ahmer.whatsapp.Constant;
import com.ahmer.whatsapp.R;
import com.ahmer.whatsapp.StatusItem;
import com.ahmer.whatsapp.Thumbnails;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        /*
        File moviesFolder = new File(PathUtils.getExternalStoragePath() + "/AhmerFolder");
        if (moviesFolder.exists()) {
            getStatuses(moviesFolder.listFiles());
        }
       */
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

    private static void getVideoStatuses(File file) {
        String filePath = file.getAbsolutePath();
        String fileName = FileUtils.getFileNameNoExtension(file.getName());
        File preExistedThumbnails = new File(Thumbnails.thumbnailDir() + "/" + fileName + ".png");
        if (filePath.endsWith(EXT_MP4_LOWER_CASE) || filePath.endsWith(EXT_MP4_UPPER_CASE)) {
            StatusItem item = new StatusItem();
            item.setPath(file.getAbsolutePath());
            item.setName(file.getName());
            item.setSize(file.length());
            item.setFormat(EXT_MP4_LOWER_CASE);
            if (!preExistedThumbnails.exists()) {
                Log.v(TAG, SplashActivity.class.getSimpleName() + " -> First time generate thumbnails for videos");
                Bitmap video = Thumbnails.videoThumbnails(file);
                item.setThumbnails(video);
                Thumbnails.saveImage(video, FileUtils.getFileNameNoExtension(file.getName()));
            } else {
                Log.v(TAG, SplashActivity.class.getSimpleName() + " -> Load pre-existed thumbnails for videos");
                Bitmap videoThumbnail = BitmapFactory.decodeFile(preExistedThumbnails.getAbsolutePath());
                item.setThumbnails(videoThumbnail);
            }
            videoStatuses.add(item);
            bothStatuses.add(item);
        }
    }

    private static void getImageStatuses(File file) {
        String filePath = file.getAbsolutePath();
        String fileName = FileUtils.getFileNameNoExtension(file.getName());
        File preExistedThumbnails = new File(Thumbnails.thumbnailDir() + "/" + fileName + ".png");
        if (filePath.endsWith(EXT_JPG_LOWER_CASE) || filePath.endsWith(EXT_JPG_UPPER_CASE)) {
            StatusItem item = new StatusItem();
            item.setPath(file.getAbsolutePath());
            item.setName(file.getName());
            item.setSize(file.length());
            item.setFormat(EXT_JPG_LOWER_CASE);
            if (!preExistedThumbnails.exists()) {
                Log.v(TAG, SplashActivity.class.getSimpleName() + " -> First time generate thumbnails for images");
                Bitmap jpg = Thumbnails.imageThumbnails(file);
                item.setThumbnails(jpg);
                Thumbnails.saveImage(jpg, FileUtils.getFileNameNoExtension(file.getName()));
            } else {
                Log.v(TAG, SplashActivity.class.getSimpleName() + " -> Load pre-existed thumbnails for images");
                Bitmap imageThumbnail = BitmapFactory.decodeFile(preExistedThumbnails.getAbsolutePath());
                item.setThumbnails(imageThumbnail);
            }
            imageStatuses.add(item);
            bothStatuses.add(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseCrashlytics firebaseCrashlytics = FirebaseCrashlytics.getInstance();
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        firebaseCrashlytics.log("Start " + getClass().getSimpleName() + " Crashlytics logging...");
        TextView app_version = findViewById(R.id.app_version);
        app_version.setText(String.format(Locale.getDefault(), "App version: %s (%d)",
                AppUtils.getAppVersionName(), AppUtils.getAppVersionCode()));
        SharedPreferencesUtil themePref = new SharedPreferencesUtil(getApplicationContext(), Constant.PREFERENCE_THEME);
        boolean isChecked = themePref.loadBooleanSharedPreference(Constant.PREFERENCE_THEME_KEY);
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
            public void rationale(UtilsTransActivity activity, ShouldRequest shouldRequest) {
                shouldRequest.again(true);
                Log.v(TAG, getClass().getSimpleName() + " -> Again permission checking");
            }
        }).callback(new PermissionUtils.FullCallback() {
            @Override
            public void onGranted(@NonNull List<String> permissionsGranted) {
                Log.v(TAG, getClass().getSimpleName() + " -> Permission has been granted");
                new RunProgram(SplashActivity.this).execute();
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
            public void onActivityCreate(Activity activity) {
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

    static class RunProgram extends AsyncTask<Void, Void, Void> {

        private final WeakReference<SplashActivity> weakContext;

        private RunProgram(final SplashActivity context) {
            weakContext = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Thumbnails.thumbnailDir();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ThreadUtils.runOnUiThread(() -> {
                try {
                    getData();
                } catch (Exception e) {
                    e.printStackTrace();
                    ThrowableUtils.getFullStackTrace(e);
                    Log.v(TAG, getClass().getSimpleName() + " -> Exception: Error during loading data: " + e.getMessage());
                    ThrowableUtils.getFullStackTrace(e);
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            SharedPreferencesUtil launcherPref = new SharedPreferencesUtil(weakContext.get(), Constant.PREFERENCE_LAUNCHER);
            if (!launcherPref.loadBooleanSharedPreference(Constant.PREFERENCE_LAUNCHER_KEY)) {
                Intent intentMainActivity = new Intent(weakContext.get(), MainActivity.class);
                intentMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                weakContext.get().startActivity(intentMainActivity);
            } else {
                Intent intentMainTabbed = new Intent(weakContext.get(), MainTabbedActivity.class);
                intentMainTabbed.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    intentMainTabbed.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                weakContext.get().startActivity(intentMainTabbed);
            }
            weakContext.get().finish();
        }
    }
}
