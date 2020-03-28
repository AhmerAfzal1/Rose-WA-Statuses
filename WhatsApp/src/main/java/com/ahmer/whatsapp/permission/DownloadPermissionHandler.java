package com.ahmer.whatsapp.permission;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.ahmer.afzal.utils.toastandsnackbar.ToastUtils;


public abstract class DownloadPermissionHandler extends PermissionsManager implements PreferenceManager.OnActivityResultListener {

    private Activity activity;

    protected DownloadPermissionHandler(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public abstract void onPermissionGranted();

    @Override
    public void showRequestPermissionRationale() {
        showPermissionSummaryDialog((dialog, which) -> requestPermissions());
    }

    @Override
    public void requestDisallowedAction() {
        SharedPreferences prefs = activity.getSharedPreferences("settings", 0);
        boolean requestDisallowed = prefs.getBoolean("requestDisallowed", false);
        if (requestDisallowed) {
            showPermissionSummaryDialog((dialog, which) -> new AlertDialog.Builder(activity)
                    .setMessage("Go to Settings?")
                    .setPositiveButton("Yes", (dialog1, which1) ->
                            activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", activity.getPackageName(), null)), 1337))
                    .setNegativeButton("No", (dialog12, which12) ->
                            ToastUtils.showLong("Can't download; Necessary PERMISSIONS denied. Try again")));
        } else {
            prefs.edit().putBoolean("requestDisallowed", true).apply();
            onPermissionsDenied();
        }
    }

    @Override
    public void onPermissionsGranted() {
        onPermissionGranted();
    }

    @Override
    public void onPermissionsDenied() {
        ToastUtils.showLong("Can't download; Necessary PERMISSIONS denied. Try again");
    }

    private void showPermissionSummaryDialog(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setPositiveButton("OK", okListener)
                .setMessage("This feature requires WRITE_EXTERNAL_STORAGE " +
                        "permission to save downloaded videos into the Download " +
                        "folder. Make sure to grant this permission. Otherwise, " +
                        "downloading videos is not possible.")
                .create()
                .show();
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1337) {
            PermissionsManager downloadsPermMgr = new PermissionsManager(activity) {
                @Override
                public void showRequestPermissionRationale() {

                }

                @Override
                public void requestDisallowedAction() {
                    onPermissionsDenied();
                }

                @Override
                public void onPermissionsGranted() {
                    onPermissionGranted();
                }

                @Override
                public void onPermissionsDenied() {
                    ToastUtils.showLong("Can't download; Necessary PERMISSIONS denied. Try again");
                }
            };
            downloadsPermMgr.checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, PermissionRequestCodes.DOWNLOADS);
        }
        return true;
    }
}
