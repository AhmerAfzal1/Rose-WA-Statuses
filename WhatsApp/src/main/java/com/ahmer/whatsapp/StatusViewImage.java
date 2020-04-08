package com.ahmer.whatsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmer.afzal.utils.utilcode.ThrowableUtils;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Objects;

import static com.ahmer.whatsapp.Constant.EXT_JPG_LOWER_CASE;
import static com.ahmer.whatsapp.Constant.TAG;

public class StatusViewImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_image);
        FirebaseCrashlytics firebaseCrashlytics = FirebaseCrashlytics.getInstance();
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        firebaseCrashlytics.log("Start " + getClass().getSimpleName() + " Crashlytics logging...");
        ImageView imageView = findViewById(R.id.imageView);
        String format = getIntent().getStringExtra("format");
        String path = getIntent().getStringExtra("path");
        try {
            if (Objects.requireNonNull(format).equals(EXT_JPG_LOWER_CASE)) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                Log.v(Constant.TAG, "Path is: " + bitmap);
                imageView.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ThrowableUtils.getFullStackTrace(e);
            Log.v(TAG, getClass().getSimpleName() + " -> " + e.getMessage());
        }
    }
}
