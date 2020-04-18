package com.ahmer.whatsapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmer.afzal.utils.constants.PermissionConstants;
import com.ahmer.afzal.utils.utilcode.AppUtils;
import com.ahmer.afzal.utils.utilcode.PermissionUtils;
import com.ahmer.afzal.utils.utilcode.ScreenUtils;
import com.ahmer.afzal.utils.utilcode.ThreadUtils;
import com.ahmer.afzal.utils.utilcode.ThrowableUtils;
import com.ahmer.afzal.utils.utilcode.UtilsTransActivity;
import com.ahmer.whatsapp.R;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

import static com.ahmer.whatsapp.Constant.TAG;

public class SplashActivity extends AppCompatActivity {

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
            public void onGranted(List<String> permissionsGranted) {
                Log.v(TAG, getClass().getSimpleName() + "-> Permission has been granted");
                new RunProgram(SplashActivity.this).execute();
            }

            @Override
            public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
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
            ThreadUtils.runOnUiThread(() -> {
                try {
                    MainActivity activity = new MainActivity();
                    activity.getData();
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
