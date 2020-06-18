package com.ahmer.whatsapp.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmer.afzal.utils.fab.FloatingActionsMenu;
import com.ahmer.afzal.utils.imageview.ZoomImageView;
import com.ahmer.afzal.utils.utilcode.ThrowableUtils;
import com.ahmer.whatsapp.R;
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
        ZoomImageView imageView = findViewById(R.id.imageView);
        FloatingActionsMenu imageFAB = findViewById(R.id.fabImageMain);
        String format = getIntent().getStringExtra("format");
        String path = getIntent().getStringExtra("path");
        String fileFrom = getIntent().getStringExtra("from");
        try {
            if (Objects.requireNonNull(format).equals(EXT_JPG_LOWER_CASE)) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                if (Objects.requireNonNull(fileFrom).equals("MainActivity")) {
                    imageFAB.setVisibility(View.GONE);
                    Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                    imageView.setImageBitmap(bitmap);
                } else if (Objects.requireNonNull(fileFrom).equals("Fragment")) {
                    Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                    //Log.v(Constant.TAG, "Path is: " + bitmap);
                    imageView.setImageBitmap(bitmap);
                } else {
                    Log.v(TAG, getClass().getSimpleName() + "-> There is no found file");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ThrowableUtils.getFullStackTrace(e);
            Log.v(TAG, getClass().getSimpleName() + "-> " + e.getMessage());
        }
    }
}
