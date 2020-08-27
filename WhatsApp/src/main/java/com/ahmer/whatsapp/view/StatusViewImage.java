package com.ahmer.whatsapp.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmer.afzal.utils.utilcode.FileUtils;
import com.ahmer.afzal.utils.utilcode.PathUtils;
import com.ahmer.afzal.utils.utilcode.ThreadUtils;
import com.ahmer.afzal.utils.utilcode.ThrowableUtils;
import com.ahmer.afzal.utils.utilcode.ToastUtils;
import com.ahmer.whatsapp.MediaScanner;
import com.ahmer.whatsapp.R;
import com.ahmer.whatsapp.Utilities;
import com.ahmer.whatsapp.activity.FragmentImages;
import com.ahmer.whatsapp.databinding.ViewImageBinding;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.util.Objects;

import static com.ahmer.whatsapp.Constant.EXT_JPG_LOWER_CASE;
import static com.ahmer.whatsapp.Constant.TAG;

public class StatusViewImage extends AppCompatActivity {

    private boolean isFabOpened = false;
    private ViewImageBinding binding = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ViewImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseCrashlytics firebaseCrashlytics = FirebaseCrashlytics.getInstance();
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        firebaseCrashlytics.log("Start " + getClass().getSimpleName() + " Crashlytics logging...");
        String format = getIntent().getStringExtra("format");
        String path = getIntent().getStringExtra("path");
        String fileFrom = getIntent().getStringExtra("from");
        int position = getIntent().getIntExtra("pos", 0);
        if (Objects.requireNonNull(fileFrom).equals("MainActivity")) {
            binding.fabMain.setVisibility(View.GONE);
            binding.fabBgLayout.setVisibility(View.GONE);
        } else if (Objects.requireNonNull(fileFrom).equals("Fragment")) {
            binding.fabBgLayout.setVisibility(View.GONE);
            binding.fabMain.setVisibility(View.VISIBLE);
            binding.fabMain.setOnClickListener(v -> {
                if (!isFabOpened) {
                    showFAB();
                } else {
                    closeFAB();
                }
            });
        } else {
            Log.v(TAG, getClass().getSimpleName() + " -> There is no found file");
        }
        try {
            if (Objects.requireNonNull(format).equals(EXT_JPG_LOWER_CASE)) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                binding.imageView.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ThrowableUtils.getFullStackTrace(e);
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.v(TAG, getClass().getSimpleName() + " -> Exception: " + e.getMessage());
        }
        Log.v(TAG, getClass().getSimpleName() + " -> ImagesAdapter position: " + position);
        binding.fabDownloadFile.setOnClickListener(v -> {
            try {
                File destPathJPG = new File(PathUtils.getExternalStoragePath() + Utilities.saveToWithFileName(path) + EXT_JPG_LOWER_CASE);
                FileUtils.move(new File(Objects.requireNonNull(path)), destPathJPG);
                ThreadUtils.runOnUiThread(() -> {
                    ToastUtils.showLong(getResources().getString(R.string.status_saved) + "\n" + destPathJPG.getPath());
                    new MediaScanner(v.getContext(), destPathJPG);
                });
                FragmentImages.updateList(position);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                Log.v(TAG, getClass().getSimpleName() + " -> Exception: " + e);
                ThrowableUtils.getFullStackTrace(e);
                FirebaseCrashlytics.getInstance().recordException(e);
            }
        });
        binding.fabShare.setOnClickListener(v -> Utilities.shareFile(v.getContext(), FragmentImages.statusItemFile, position));
        binding.fabShareWhatsApp.setOnClickListener(v -> Utilities.shareToWhatsApp(v.getContext(), FragmentImages.statusItemFile, position));
    }

    private void showFAB() {
        isFabOpened = true;
        Objects.requireNonNull(binding.tvDownload).setVisibility(View.VISIBLE);
        binding.fabDownloadFile.setVisibility(View.VISIBLE);
        Objects.requireNonNull(binding.tvShare).setVisibility(View.VISIBLE);
        binding.fabShare.setVisibility(View.VISIBLE);
        Objects.requireNonNull(binding.tvShareWhatsApp).setVisibility(View.VISIBLE);
        binding.fabShareWhatsApp.setVisibility(View.VISIBLE);
        binding.fabBgLayout.setVisibility(View.VISIBLE);

        binding.fabMain.animate().rotationBy(180);
    }

    private void closeFAB() {
        isFabOpened = false;
        binding.fabBgLayout.setVisibility(View.GONE);
        binding.fabMain.animate().rotation(0);
        Objects.requireNonNull(binding.tvDownload).setVisibility(View.GONE);
        binding.fabDownloadFile.setVisibility(View.GONE);
        Objects.requireNonNull(binding.tvShare).setVisibility(View.GONE);
        binding.fabShare.setVisibility(View.GONE);
        Objects.requireNonNull(binding.tvShareWhatsApp).setVisibility(View.GONE);
        binding.fabShareWhatsApp.setVisibility(View.GONE);
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
