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

import static com.ahmer.whatsapp.Constant.EXT_JPG_LOWER_CASE;
import static com.ahmer.whatsapp.Constant.EXT_MP4_LOWER_CASE;
import static com.ahmer.whatsapp.Constant.TAG;

public class StatusViewVideo extends AppCompatActivity {

    private boolean isFabOpened = false;
    private FloatingActionButton fabMain;
    private LinearLayout fileDownloadLayout;
    private LinearLayout shareLayout;
    private LinearLayout shareWhatsAppLayout;
    private VideoView view;
    private View bgLayout;

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
                retriever.setDataSource(getApplicationContext(), uri);
                String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
                boolean isVideo = "yes".equals(hasVideo);
                if (isVideo) {
                    MediaController mediaController = new MediaController(getApplicationContext());
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
            Log.v(TAG, getClass().getSimpleName() + "-> " + e.getMessage());
        }
        view.setOnCompletionListener(mp -> {
            view.stopPlayback();
            StatusViewVideo.this.finish();
        });
        FragmentVideos fragmentVideos = new FragmentVideos();
        FragmentVideos.VideosAdapter adapter = new FragmentVideos.VideosAdapter(fragmentVideos.statusItemFile,
                fragmentVideos.recyclerViewVideos, fragmentVideos.adapter);
        fileDownload.setOnClickListener(v -> {
            String directoryAndFileName = "/Rose Statuses/Status_" + FileUtils.getFileNameNoExtension(path);
            File destPathJPG = new File(PathUtils.getExternalStoragePath() + directoryAndFileName + EXT_JPG_LOWER_CASE);
            FileUtils.move(new File(Objects.requireNonNull(path)), destPathJPG);
            adapter.updateList();
            ThreadUtils.runOnUiThread(() -> {
                ToastUtils.showLong(getString(R.string.status_saved) + "\n" + destPathJPG.getPath());
                new MediaScanner(getApplicationContext(), destPathJPG);
            });
            finish();
        });
        share.setOnClickListener(v -> Utilities.shareFile(getApplicationContext(),
                fragmentVideos.statusItemFile, adapter.getPosition()));
        shareWhatsApp.setOnClickListener(v -> Utilities.shareToWhatsApp(getApplicationContext(),
                fragmentVideos.statusItemFile, adapter.getPosition()));
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
