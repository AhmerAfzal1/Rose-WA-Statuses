package com.ahmer.whatsapp.view;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmer.afzal.utils.utilcode.FileUtils;
import com.ahmer.afzal.utils.utilcode.PathUtils;
import com.ahmer.afzal.utils.utilcode.ThreadUtils;
import com.ahmer.afzal.utils.utilcode.ThrowableUtils;
import com.ahmer.afzal.utils.utilcode.ToastUtils;
import com.ahmer.whatsapp.MediaScanner;
import com.ahmer.whatsapp.R;
import com.ahmer.whatsapp.Utilities;
import com.ahmer.whatsapp.activity.FragmentVideos;
import com.ahmer.whatsapp.databinding.ViewVideoBinding;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.util.Objects;

import static com.ahmer.whatsapp.Constant.EXT_MP4_LOWER_CASE;
import static com.ahmer.whatsapp.Constant.TAG;

public class StatusViewVideo extends AppCompatActivity {

    private boolean isFabOpened = false;
    private ViewVideoBinding binding;
    private MediaController mediaController = null;
    private VideoView view = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ViewVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseCrashlytics firebaseCrashlytics = FirebaseCrashlytics.getInstance();
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        firebaseCrashlytics.log("Start " + getClass().getSimpleName() + " Crashlytics logging...");
        view = binding.videoView;
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
            if (Objects.requireNonNull(format).equals(EXT_MP4_LOWER_CASE)) {
                Uri uri = Uri.fromFile(new File(Objects.requireNonNull(path)));
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(StatusViewVideo.this, uri);
                String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
                boolean isVideo = "yes".equals(hasVideo);
                if (isVideo) {
                    mediaController = new MediaController(StatusViewVideo.this);
                    mediaController.setAnchorView(view);
                    view.setMediaController(mediaController);
                    view.setVideoURI(uri);
                } else {
                    ToastUtils.showLong("Video file is corrupted.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ThrowableUtils.getFullStackTrace(e);
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.v(TAG, getClass().getSimpleName() + " -> Exception: " + e.getMessage());
        }
        if (view.isPlaying()) {
            mediaController.hide();
        }
        view.setOnCompletionListener(mp -> {
            view.stopPlayback();
            StatusViewVideo.this.finish();
        });
        view.setOnTouchListener((v, event) -> {
            v.performClick();
            mediaController.show();
            return v.onTouchEvent(event);
        });
        binding.fabDownloadFile.setOnClickListener(v -> {
            try {
                File destPathMP4 = new File(PathUtils.getExternalStoragePath() + Utilities.saveToWithFileName(path) + EXT_MP4_LOWER_CASE);
                FileUtils.move(new File(Objects.requireNonNull(path)), destPathMP4);
                ThreadUtils.runOnUiThread(() -> {
                    ToastUtils.showLong(getString(R.string.status_saved) + "\n" + destPathMP4.getPath());
                    new MediaScanner(v.getContext(), destPathMP4);
                });
                FragmentVideos.updateList(position);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                Log.v(TAG, getClass().getSimpleName() + " -> Exception: " + e.getMessage());
                ThrowableUtils.getFullStackTrace(e);
                FirebaseCrashlytics.getInstance().recordException(e);
            }
        });
        binding.fabShare.setOnClickListener(v -> Utilities.shareFile(v.getContext(), FragmentVideos.statusItemFile, position));
        binding.fabShareWhatsApp.setOnClickListener(v -> Utilities.shareToWhatsApp(v.getContext(), FragmentVideos.statusItemFile, position));
    }

    private void showFAB() {
        view.pause();
        isFabOpened = true;
        Objects.requireNonNull(binding.tvDownload).setVisibility(View.VISIBLE);
        binding.fabDownloadFile.setVisibility(View.VISIBLE);
        Objects.requireNonNull(binding.tvShare).setVisibility(View.VISIBLE);
        binding.fabShare.setVisibility(View.VISIBLE);
        Objects.requireNonNull(binding.tvShareWhatsApp).setVisibility(View.VISIBLE);
        binding.fabShareWhatsApp.setVisibility(View.VISIBLE);

        binding.fabMain.animate().rotationBy(180);
    }

    private void closeFAB() {
        view.start();
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

    @Override
    protected void onResume() {
        view.start();
        super.onResume();
    }

    @Override
    protected void onPause() {
        view.stopPlayback();
        super.onPause();
    }
}
