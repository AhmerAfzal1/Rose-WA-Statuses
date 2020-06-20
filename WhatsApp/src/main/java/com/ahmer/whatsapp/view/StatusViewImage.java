package com.ahmer.whatsapp.view;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmer.afzal.utils.imageview.ZoomImageView;
import com.ahmer.afzal.utils.utilcode.ThrowableUtils;
import com.ahmer.whatsapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Objects;

import static com.ahmer.whatsapp.Constant.EXT_JPG_LOWER_CASE;
import static com.ahmer.whatsapp.Constant.TAG;

public class StatusViewImage extends AppCompatActivity {

    private FloatingActionButton fabMain;
    private FloatingActionButton fileDownload;
    private FloatingActionButton shareWhatsApp;
    private FloatingActionButton share;
    private View bgLayout;
    private LinearLayout fileDownloadLayout;
    private LinearLayout shareWhatsAppLayout;
    private LinearLayout shareLayout;
    private boolean isFabOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_image);
        FirebaseCrashlytics firebaseCrashlytics = FirebaseCrashlytics.getInstance();
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        firebaseCrashlytics.log("Start " + getClass().getSimpleName() + " Crashlytics logging...");
        bgLayout = findViewById(R.id.fabBgLayout);
        fileDownloadLayout = findViewById(R.id.fabLayoutDownloadFile);
        shareWhatsAppLayout = findViewById(R.id.fabLayoutShareWhatsApp);
        shareLayout = findViewById(R.id.fabLayoutShare);
        fabMain = findViewById(R.id.fabMain);
        fileDownload = findViewById(R.id.fabDownloadFile);
        shareWhatsApp = findViewById(R.id.fabShareWhatsApp);
        share = findViewById(R.id.fabShare);
        ZoomImageView imageView = findViewById(R.id.imageView);
        String format = getIntent().getStringExtra("format");
        String path = getIntent().getStringExtra("path");
        String fileFrom = getIntent().getStringExtra("from");
        try {
            if (Objects.requireNonNull(format).equals(EXT_JPG_LOWER_CASE)) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                if (Objects.requireNonNull(fileFrom).equals("MainActivity")) {
                    fabMain.setVisibility(View.GONE);
                    bgLayout.setVisibility(View.GONE);
                    Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                    imageView.setImageBitmap(bitmap);
                } else if (Objects.requireNonNull(fileFrom).equals("Fragment")) {
                    Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                    //Log.v(Constant.TAG, "Path is: " + bitmap);
                    imageView.setImageBitmap(bitmap);
                    bgLayout.setVisibility(View.GONE);
                    fabMain.setVisibility(View.VISIBLE);
                    fabMain.setOnClickListener(v -> {
                        if (!isFabOpened) {
                            showFAB();
                        } else {
                            closeFAB();
                        }
                    });
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

    private void showFAB() {
        isFabOpened = true;
        fileDownloadLayout.setVisibility(View.VISIBLE);
        shareWhatsAppLayout.setVisibility(View.VISIBLE);
        shareLayout.setVisibility(View.VISIBLE);
        bgLayout.setVisibility(View.VISIBLE);

        fabMain.animate().rotationBy(180);

        fileDownloadLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        shareWhatsAppLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
        shareLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_145));
    }

    private void closeFAB() {
        isFabOpened = false;
        bgLayout.setVisibility(View.GONE);
        fabMain.animate().rotation(0);
        fileDownloadLayout.animate().translationY(0);
        shareWhatsAppLayout.animate().translationY(0);
        shareLayout.animate().translationY(0);
        shareLayout.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFabOpened) {
                    fileDownloadLayout.setVisibility(View.GONE);
                    shareWhatsAppLayout.setVisibility(View.GONE);
                    shareLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isFabOpened) {
            closeFAB();
        } else {
            super.onBackPressed();
        }
    }
}
