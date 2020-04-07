package com.ahmer.whatsapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmer.afzal.utils.utilcode.ThrowableUtils;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Objects;

import static com.ahmer.whatsapp.Constant.EXT_GIF_LOWER_CASE;
import static com.ahmer.whatsapp.Constant.TAG;

public class StatusViewGIF extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_gif);
        WebView webView = findViewById(R.id.webView);
        FirebaseCrashlytics firebaseCrashlytics = FirebaseCrashlytics.getInstance();
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        firebaseCrashlytics.log("Start " + getClass().getSimpleName() + " Crashlytics logging...");
        String format = getIntent().getStringExtra("format");
        String path = getIntent().getStringExtra("path");
        try {
            if (Objects.requireNonNull(format).equals(EXT_GIF_LOWER_CASE)) {
                webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                webView.getSettings().setLoadsImagesAutomatically(true);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl("file:///" + path);
                Log.v(TAG, getClass().getSimpleName() + " -> GIF path: " + "file:///" + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ThrowableUtils.getFullStackTrace(e);
            Log.v(TAG, getClass().getSimpleName() + " -> " + e.getMessage());
        }
    }
}
