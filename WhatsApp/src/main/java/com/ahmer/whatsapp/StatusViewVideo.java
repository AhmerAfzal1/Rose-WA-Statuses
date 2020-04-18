package com.ahmer.whatsapp;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmer.afzal.utils.utilcode.ThrowableUtils;
import com.ahmer.afzal.utils.utilcode.ToastUtils;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.util.Objects;

import static com.ahmer.whatsapp.Constant.EXT_MP4_LOWER_CASE;
import static com.ahmer.whatsapp.Constant.TAG;

public class StatusViewVideo extends AppCompatActivity {

    private VideoView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_video);
        FirebaseCrashlytics firebaseCrashlytics = FirebaseCrashlytics.getInstance();
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        firebaseCrashlytics.log("Start " + getClass().getSimpleName() + " Crashlytics logging...");
        view = findViewById(R.id.videoView);
        String format = getIntent().getStringExtra("format");
        String path = getIntent().getStringExtra("path");
        Log.v(Constant.TAG, "Path is: " + path);
        try {
            if (Objects.requireNonNull(format).equals(EXT_MP4_LOWER_CASE)) {
                Uri uri = Uri.fromFile(new File(Objects.requireNonNull(path)));
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(getApplicationContext(), uri);
                String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
                boolean isVideo = "yes".equals(hasVideo);
                if (isVideo) {
                    MediaController mediaController = new MediaController(StatusViewVideo.this);
                    mediaController.setAnchorView(view);
                    view.setMediaController(mediaController);
                    view.setVideoURI(uri);
                } else {
                    ToastUtils.showLong("Video file is corrupt.");
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
