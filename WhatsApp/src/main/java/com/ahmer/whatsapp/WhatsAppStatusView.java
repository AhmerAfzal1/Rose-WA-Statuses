package com.ahmer.whatsapp;

import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmer.whatsapp.activity.MainActivity;

import java.util.Objects;

public class WhatsAppStatusView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whatsapp_video_view);
        VideoView videoView = findViewById(R.id.vv_video);
        String format = getIntent().getStringExtra("format");
        String path = getIntent().getStringExtra("path");
        if (Objects.requireNonNull(format).equals(MainActivity.MP4)) {
            videoView.setVideoPath(path);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
            videoView.start();
        }
    }
}
