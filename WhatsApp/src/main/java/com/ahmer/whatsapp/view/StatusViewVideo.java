package com.ahmer.whatsapp.view;

import android.animation.Animator;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.util.Objects;

import static com.ahmer.whatsapp.Constant.EXT_MP4_LOWER_CASE;
import static com.ahmer.whatsapp.Constant.TAG;

public class StatusViewVideo extends AppCompatActivity {

    private boolean isFabOpened = false;
    private FloatingActionButton fabMain = null;
    private LinearLayout fileDownloadLayout = null;
    private LinearLayout shareLayout = null;
    private LinearLayout shareWhatsAppLayout = null;
    private VideoView view = null;
    private View bgLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_video);
        FirebaseCrashlytics firebaseCrashlytics = FirebaseCrashlytics.getInstance();
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        firebaseCrashlytics.log("Start " + getClass().getSimpleName() + " Crashlytics logging...");
        bgLayout = findViewById(R.id.fabBgLayout);
        fileDownloadLayout = findViewById(R.id.fabLayoutDownloadFile);
        shareWhatsAppLayout = findViewById(R.id.fabLayoutShareWhatsApp);
        shareLayout = findViewById(R.id.fabLayoutShare);
        fabMain = findViewById(R.id.fabMain);
        FloatingActionButton fileDownload = findViewById(R.id.fabDownloadFile);
        FloatingActionButton shareWhatsApp = findViewById(R.id.fabShareWhatsApp);
        FloatingActionButton share = findViewById(R.id.fabShare);
        view = findViewById(R.id.videoView);
        String format = getIntent().getStringExtra("format");
        String path = getIntent().getStringExtra("path");
        String fileFrom = getIntent().getStringExtra("from");
        int position = getIntent().getIntExtra("pos", 0);
        if (Objects.requireNonNull(fileFrom).equals("MainActivity")) {
            fabMain.setVisibility(View.GONE);
            bgLayout.setVisibility(View.GONE);
        } else if (Objects.requireNonNull(fileFrom).equals("Fragment")) {
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
        try {
            if (Objects.requireNonNull(format).equals(EXT_MP4_LOWER_CASE)) {
                Uri uri = Uri.fromFile(new File(Objects.requireNonNull(path)));
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(StatusViewVideo.this, uri);
                String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
                boolean isVideo = "yes".equals(hasVideo);
                if (isVideo) {
                    MediaController mediaController = new MediaController(StatusViewVideo.this);
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
            Log.v(TAG, getClass().getSimpleName() + "-> Exception: " + e.getMessage());
        }
        view.setOnCompletionListener(mp -> {
            view.stopPlayback();
            StatusViewVideo.this.finish();
        });
        fileDownload.setOnClickListener(v -> {
            try {
                String directoryAndFileName = "/Rose Statuses/Status_" + FileUtils.getFileNameNoExtension(path);
                File destPathMP4 = new File(PathUtils.getExternalStoragePath() + directoryAndFileName + EXT_MP4_LOWER_CASE);
                FileUtils.move(new File(Objects.requireNonNull(path)), destPathMP4);
                ThreadUtils.runOnUiThread(() -> {
                    ToastUtils.showLong(getString(R.string.status_saved) + "\n" + destPathMP4.getPath());
                    new MediaScanner(v.getContext(), destPathMP4);
                });
                FragmentVideos.updateList(position);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                Log.v(TAG, getClass().getSimpleName() + "-> Exception: " + e.getMessage());
                ThrowableUtils.getFullStackTrace(e);
                FirebaseCrashlytics.getInstance().recordException(e);
            }
        });
        share.setOnClickListener(v -> Utilities.shareFile(v.getContext(), FragmentVideos.statusItemFile, position));
        shareWhatsApp.setOnClickListener(v -> Utilities.shareToWhatsApp(v.getContext(), FragmentVideos.statusItemFile, position));
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
