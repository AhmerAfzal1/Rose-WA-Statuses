package com.ahmer.whatsapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ahmer.afzal.utils.info.ApplicationUtils;
import com.ahmer.afzal.utils.toastandsnackbar.ToastUtils;
import com.ahmer.whatsapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.ahmer.whatsapp.ConstantsValues.TAG;

public class SplashActivity extends AppCompatActivity {

    private static final int MULTIPLE_PERMISSIONS = 1;
    private final String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TextView app_version = findViewById(R.id.app_version);
        app_version.setText(String.format(Locale.getDefault(), "App version: %s(%d)",
                ApplicationUtils.getAppVersionName(), ApplicationUtils.getAppVersionCode()));
        goHome();
    }

    private synchronized void goHome() {
        if (!flag) {
            flag = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) +
                        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission has granted");
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Log.v(TAG, "Permission has not been granted");
                    if (checkPermissions()) {
                        handleAfterPermissions();
                    }
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
                handleAfterPermissions();
            } else {
                StringBuilder permissionsDenied = new StringBuilder();
                for (String per : permissionsList) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        permissionsDenied.append("\n").append(per);
                    }
                }
                ToastUtils.showLong("Please grant the permission to run app");
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void handleAfterPermissions() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = true;
    }
}
