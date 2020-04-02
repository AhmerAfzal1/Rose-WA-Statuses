package com.ahmer.whatsapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ahmer.afzal.utils.Utilities;
import com.ahmer.afzal.utils.info.ApplicationUtils;
import com.ahmer.afzal.utils.toastandsnackbar.ToastUtils;
import com.ahmer.whatsapp.R;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.ahmer.whatsapp.ConstantsValues.TAG;

public class SplashActivity extends AppCompatActivity {

    private static final int MULTIPLE_PERMISSIONS = 1;
    private static final String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseCrashlytics firebaseCrashlytics = FirebaseCrashlytics.getInstance();
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        firebaseCrashlytics.log("Start " + getClass().getSimpleName() + " Crashlytics logging...");
        TextView app_version = findViewById(R.id.app_version);
        app_version.setText(String.format(Locale.getDefault(), "App version: %s(%d)",
                ApplicationUtils.getAppVersionName(), ApplicationUtils.getAppVersionCode()));
        goHome();
    }

    private void goHome() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) +
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, getClass().getSimpleName() + " -> Permission has been granted");
                new RunProgram(this).execute();
            } else {
                Log.v(TAG, getClass().getSimpleName() + " -> Permission has not been granted");
                if (checkPermissions()) {
                    Log.v(TAG, getClass().getSimpleName() + " -> Again checked and now permission has been granted");
                    new RunProgram(this).execute();
                } else {
                    Log.v(TAG, getClass().getSimpleName() + " -> Again checked and but permission has not been granted");
                }
            }
        }
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getApplication(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissionsList, @NonNull int[] grantResults) {
        if (requestCode == MULTIPLE_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new RunProgram(this).execute();
            } else {
                StringBuilder permissionsDenied = new StringBuilder();
                for (String per : permissionsList) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        permissionsDenied.append("\n").append(per);
                    }
                }
                ToastUtils.showLong("Please allow the permission for app works properly");
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static class RunProgram extends AsyncTask<Void, Void, Void> {

        private final WeakReference<Context> weakContext;

        private RunProgram(final Context context) {
            weakContext = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Utilities.runOnUI(()->{
                try {
                    MainActivity activity = new MainActivity();
                    activity.getVideo();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.v(TAG, getClass().getSimpleName() + " -> Error during loading data: " + e.getMessage());
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            });

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
                weakContext.get().startActivity(new Intent(weakContext.get(), MainActivity.class));
                ((SplashActivity) weakContext.get()).finish();

        }
    }
}
