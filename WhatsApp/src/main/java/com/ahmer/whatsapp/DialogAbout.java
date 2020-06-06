package com.ahmer.whatsapp;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahmer.afzal.utils.utilcode.AppUtils;
import com.ahmer.afzal.utils.utilcode.ThrowableUtils;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Locale;
import java.util.Objects;

public class DialogAbout {

    public DialogAbout(Context context) {
        super();
        final Dialog dialog = new Dialog(context);
        try {
            Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.about_ahmer);
            dialog.getWindow().setLayout(-1, -2);
            dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            TextView app_version = dialog.findViewById(R.id.tvVersion);
            app_version.setText(String.format(Locale.getDefault(), "App version: %s(%d)",
                    AppUtils.getAppVersionName(), AppUtils.getAppVersionCode()));
            dialog.findViewById(R.id.tvOk).setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            ThrowableUtils.getFullStackTrace(e);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
}
