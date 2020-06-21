package com.ahmer.whatsapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import static com.ahmer.whatsapp.Constant.BUSINESS_WHATSAPP_STATUSES_LOCATION;
import static com.ahmer.whatsapp.Constant.EXT_JPG_LOWER_CASE;
import static com.ahmer.whatsapp.Constant.EXT_JPG_UPPER_CASE;
import static com.ahmer.whatsapp.Constant.EXT_MP4_LOWER_CASE;
import static com.ahmer.whatsapp.Constant.EXT_MP4_UPPER_CASE;
import static com.ahmer.whatsapp.Constant.FM_WHATSAPP_STATUSES_LOCATION;
import static com.ahmer.whatsapp.Constant.TAG;
import static com.ahmer.whatsapp.Constant.WHATSAPP_STATUSES_LOCATION;
import static com.ahmer.whatsapp.Constant.YO_WHATSAPP_STATUSES_LOCATION;

public class SplashActivity extends AppCompatActivity {

    public static final ArrayList<StatusItem> allStatuses = new ArrayList<>();
    public static final File dirBusinessWhatsApp = new File(PathUtils.getExternalStoragePath() + BUSINESS_WHATSAPP_STATUSES_LOCATION);
    public static final File dirFMWhatsApp = new File(PathUtils.getExternalStoragePath() + FM_WHATSAPP_STATUSES_LOCATION);
    public static final File dirWhatsApp = new File(PathUtils.getExternalStoragePath() + WHATSAPP_STATUSES_LOCATION);
    public static final File dirYoWhatsApp = new File(PathUtils.getExternalStoragePath() + YO_WHATSAPP_STATUSES_LOCATION);

    public static void getData() {
        /*
        File moviesFolder = new File(PathUtils.getExternalStoragePath() + "/AhmerFolder");
        //File moviesFolder = new File(PathUtils.getExternalStoragePath() + "/FMWhatsApp");
        Log.v(TAG, SplashActivity.class.getSimpleName() + moviesFolder.getAbsolutePath());
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
                getStatusesContent(file);
            }
        }
    }

    private static void getStatusesContent(File file) {
        String filePath = file.getAbsolutePath();
        String fileName = FileUtils.getFileNameNoExtension(file.getName());
        File preExistedThumbnails = new File(Thumbnails.thumbnailDir() + "/" + fileName + ".png");
        if (filePath.endsWith(EXT_MP4_LOWER_CASE) || filePath.endsWith(EXT_MP4_UPPER_CASE) ||
                filePath.endsWith(EXT_JPG_LOWER_CASE) || filePath.endsWith(EXT_JPG_UPPER_CASE)) {
            StatusItem item = new StatusItem();
            if (file.getName().endsWith(EXT_MP4_LOWER_CASE) || file.getName().endsWith(EXT_MP4_UPPER_CASE)) {
                item.setPath(file.getAbsolutePath());
                item.setSize(file.length());
                item.setFormat(EXT_MP4_LOWER_CASE);
                if (!preExistedThumbnails.exists()) {
                    Log.v(TAG, SplashActivity.class.getSimpleName() + "-> First time generate thumbnails for videos");
                    Bitmap video = Thumbnails.videoThumbnails(file);
                    item.setThumbnails(video);
                    Thumbnails.saveImage(video, FileUtils.getFileNameNoExtension(file.getName()));
                } else {
                    Log.v(TAG, SplashActivity.class.getSimpleName() + "-> Load pre-existed thumbnails for videos");
                    Bitmap videoThumbnail = BitmapFactory.decodeFile(preExistedThumbnails.getAbsolutePath());
                    item.setThumbnails(videoThumbnail);
                }
            }
            if (file.getName().endsWith(EXT_JPG_LOWER_CASE) || file.getName().endsWith(EXT_JPG_UPPER_CASE)) {
                item.setPath(file.getAbsolutePath());
                item.setSize(file.length());
                item.setFormat(EXT_JPG_LOWER_CASE);
                if (!preExistedThumbnails.exists()) {
                    Log.v(TAG, SplashActivity.class.getSimpleName() + "-> First time generate thumbnails for images");
                    Bitmap jpg = Thumbnails.imageThumbnails(file);
                    item.setThumbnails(jpg);
                    Thumbnails.saveImage(jpg, FileUtils.getFileNameNoExtension(file.getName()));
                } else {
                    Log.v(TAG, SplashActivity.class.getSimpleName() + "-> Load pre-existed thumbnails for images");
                    Bitmap imageThumbnail = BitmapFactory.decodeFile(preExistedThumbnails.getAbsolutePath());
                    item.setThumbnails(imageThumbnail);
                }
            }
            allStatuses.add(item);
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
        app_version.setText(String.format(Locale.getDefault(), "App version: %s(%d)",
                AppUtils.getAppVersionName(), AppUtils.getAppVersionCode()));
        Thumbnails.thumbnailDir();
        checkPermissions();
    }

    private void checkPermissions() {
        PermissionUtils.permission(PermissionConstants.STORAGE).rationale(new PermissionUtils.OnRationaleListener() {
            @Override
            public void rationale(UtilsTransActivity activity, ShouldRequest shouldRequest) {
                shouldRequest.again(true);
                Log.v(TAG, getClass().getSimpleName() + "-> Again permission checking");
            }
        }).callback(new PermissionUtils.FullCallback() {
            @Override
            public void onGranted(@NonNull List<String> permissionsGranted) {
                Log.v(TAG, getClass().getSimpleName() + "-> Permission has been granted");
                new RunProgram(SplashActivity.this).execute();
            }

            @Override
            public void onDenied(@NonNull List<String> permissionsDeniedForever, @NonNull List<String> permissionsDenied) {
                Log.v(TAG, getClass().getSimpleName() + "-> Permission has not been granted");
                if (!permissionsDenied.isEmpty() || !permissionsDeniedForever.isEmpty()) {
                    finish();
                }
            }
        }).theme(new PermissionUtils.ThemeCallback() {
            @Override
            public void onActivityCreate(Activity activity) {
                Log.v(TAG, getClass().getSimpleName() + "-> Permission ThemeCallback runs");
                //Must add this for run app properly
                ScreenUtils.setFullScreen(activity);
            }
        }).request();
    }

    static class RunProgram extends AsyncTask<Void, Void, Void> {

        private final WeakReference<Context> weakContext;

        private RunProgram(final Context context) {
            weakContext = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ThreadUtils.runOnUiThread(() -> {
                try {
                    getData();
                } catch (Exception e) {
                    e.printStackTrace();
                    ThrowableUtils.getFullStackTrace(e);
                    Log.v(TAG, getClass().getSimpleName() + "-> Error during loading data: " + e.getMessage());
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            SharedPreferences pref = weakContext.get().getSharedPreferences(Constant.PREFERENCE_LAUNCHER, Context.MODE_PRIVATE);
            if (!pref.getBoolean(Constant.PREFERENCE_TRANSPARENT, false)) {
                weakContext.get().startActivity(new Intent(weakContext.get(), MainActivity.class));
            } else {
                weakContext.get().startActivity(new Intent(weakContext.get(), MainTabbedActivity.class));
            }
            ((SplashActivity) weakContext.get()).finish();
        }
    }
}
