package com.ahmer.whatsapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmer.afzal.utils.toastandsnackbar.ToastUtils;

import java.util.Objects;

import static com.ahmer.whatsapp.ConstantsValues.MP4;

public class WAVideoStatusView extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whatsapp_video_view);
        videoView = findViewById(R.id.vv_video);
        String format = getIntent().getStringExtra("format");
        String path = getIntent().getStringExtra("path");
        Log.v(ConstantsValues.TAG, "Path is: " + path);
        if (Objects.requireNonNull(format).equals(MP4)) {
            videoView.setVideoPath(path);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
        }
    }

    @Override
    protected void onResume() {
        videoView.start();
        super.onResume();
    }

    @Override
    protected void onPause() {
        videoView.stopPlayback();
        super.onPause();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        ToastUtils.showShort("Video finished");
    }
}
