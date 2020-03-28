package com.ahmer.whatsapp.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmer.afzal.utils.IOUtils;
import com.ahmer.afzal.utils.info.PathUtils;
import com.ahmer.afzal.utils.toastandsnackbar.ToastUtils;
import com.ahmer.whatsapp.CompletedVideos;
import com.ahmer.whatsapp.R;
import com.ahmer.whatsapp.WhatsAppStatusItem;
import com.ahmer.whatsapp.WhatsAppStatusView;
import com.ahmer.whatsapp.permission.DownloadPermissionHandler;
import com.ahmer.whatsapp.permission.PermissionRequestCodes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    public static final String MP4 = ".mp4";
    public static final String GIF = ".gif";
    public static final String JPG = ".jpg";
    private static final String WHATSAPP_STATUSES_LOCATION = "/WhatsApp/Media/.Statuses";
    private static final String FMWHATSAPP_STATUSES_LOCATION = "/FMWhatsApp/Media/.Statuses";
    private static final String YOWHATSAPP_STATUSES_LOCATION = "/YoWhatsApp/Media/.Statuses";
    private ActivityCompat.OnRequestPermissionsResultCallback onRequestPermissionsResultCallback;
    private RecyclerView rvVideo;
    private ArrayList<WhatsAppStatusItem> videoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MaterialTextView title = findViewById(R.id.tvTitle);
        title.setText(R.string.app_name);
        rvVideo = findViewById(R.id.rvWhatsappStatusList);
        rvVideo.setLayoutManager(new GridLayoutManager(this, 1));
        checkPermission();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            new DownloadPermissionHandler(this) {
                @Override
                public void onPermissionGranted() {
                    getVideo();
                }
            }.checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, PermissionRequestCodes.DOWNLOADS);
        } else {
            getVideo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onRequestPermissionsResultCallback.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void setOnRequestPermissionsResultListener(ActivityCompat.OnRequestPermissionsResultCallback onRequestPermissionsResultCallback) {
        this.onRequestPermissionsResultCallback = onRequestPermissionsResultCallback;
    }


    public void getVideo() {
        File dirWhatsApp = new File(PathUtils.getExternalStoragePath() + WHATSAPP_STATUSES_LOCATION);
        File dirFMWhatsApp = new File(PathUtils.getExternalStoragePath() + FMWHATSAPP_STATUSES_LOCATION);
        File dirYoWhatsApp = new File(PathUtils.getExternalStoragePath() + YOWHATSAPP_STATUSES_LOCATION);
        File[] filesWA, filesFMWA, fileYoWA;
        filesWA = dirWhatsApp.listFiles();
        filesFMWA = dirFMWhatsApp.listFiles();
        fileYoWA = dirYoWhatsApp.listFiles();
        if (filesWA != null) {
            findViewById(R.id.tvNoStatus).setVisibility(View.GONE);
            for (File file : filesWA) {
                if (file.getName().endsWith(MP4)) {
                    WhatsAppStatusItem obj_model = new WhatsAppStatusItem();
                    obj_model.setSelect(false);
                    obj_model.setPath(file.getAbsolutePath());
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                    obj_model.setThumbnails(thumb);
                    obj_model.setFormat(MP4);
                    videoList.add(obj_model);
                } else if (file.getName().endsWith(JPG)) {
                    WhatsAppStatusItem obj_model = new WhatsAppStatusItem();
                    obj_model.setSelect(false);
                    obj_model.setPath(file.getAbsolutePath());
                    Bitmap pic = ThumbnailUtils.createImageThumbnail(file.getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
                    obj_model.setThumbnails(pic);
                    obj_model.setFormat(JPG);
                    videoList.add(obj_model);
                } else if (file.getName().endsWith(GIF)) {
                    WhatsAppStatusItem obj_model = new WhatsAppStatusItem();
                    obj_model.setSelect(false);
                    obj_model.setPath(file.getAbsolutePath());
                    Bitmap pic = ThumbnailUtils.createImageThumbnail(file.getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
                    obj_model.setThumbnails(pic);
                    obj_model.setFormat(GIF);
                    videoList.add(obj_model);
                }
                if (filesFMWA != null) {
                    for (File file1 : filesFMWA) {
                        if (file1.getName().endsWith(JPG) || file1.getName().endsWith(GIF) || file1.getName().endsWith(MP4)) {
                            WhatsAppStatusItem obj_model = new WhatsAppStatusItem();
                            obj_model.setSelect(false);
                            obj_model.setPath(file1.getAbsolutePath());
                            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(file1.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                            obj_model.setThumbnails(thumb);
                            obj_model.setFormat(MP4);
                            videoList.add(obj_model);
                        }
                    }
                }
                if (fileYoWA != null) {
                    for (File file2 : fileYoWA) {
                        if (file2.getName().endsWith(JPG) || file2.getName().endsWith(GIF) || file2.getName().endsWith(MP4)) {
                            WhatsAppStatusItem obj_model = new WhatsAppStatusItem();
                            obj_model.setSelect(false);
                            obj_model.setPath(file2.getAbsolutePath());
                            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(file2.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                            obj_model.setThumbnails(thumb);
                            obj_model.setFormat(MP4);
                            videoList.add(obj_model);
                        }
                    }
                }
            }
        } else {
            findViewById(R.id.tvNoStatus).setVisibility(View.VISIBLE);
        }
        StatusVideoAdapter statusVideoAdapter = new StatusVideoAdapter();
        rvVideo.setAdapter(statusVideoAdapter);
    }

    public class StatusVideoAdapter extends RecyclerView.Adapter<StatusVideoAdapter.ViewHolder> {

        @Override
        public void onBindViewHolder(final StatusVideoAdapter.ViewHolder holder, final int position) {
            holder.iv_image.setImageBitmap(videoList.get(position).getThumbnails());
            holder.rl_select.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.rl_select.setAlpha(0);
            holder.rl_select.setOnClickListener(view -> {
                Intent intent_gallery = new Intent(MainActivity.this, WhatsAppStatusView.class);
                intent_gallery.putExtra("format", videoList.get(position).getFormat());
                intent_gallery.putExtra("path", videoList.get(position).getPath());
                MainActivity.this.startActivity(intent_gallery);
            });

            holder.share.setOnClickListener(v -> {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(videoList.get(position).getPath()));
                sendIntent.setType("file/*");
                MainActivity.this.startActivity(Intent.createChooser(sendIntent, "Send Status via:"));
            });

            holder.wsp.setOnClickListener(v -> {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setPackage("com.whatsapp");
                sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(videoList.get(position).getPath()));
                sendIntent.setType("file/*");
                MainActivity.this.startActivity(Intent.createChooser(sendIntent, "Send Status via:"));
            });

            holder.download.setOnClickListener(v -> {
                String sourcePath = videoList.get(position).getPath();
                File source = new File(sourcePath);
                File directory = Environment.getExternalStoragePublicDirectory(Objects.requireNonNull(MainActivity.this).getString(R.string.app_name));
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(directory);
                mediaScanIntent.setData(contentUri);
                MainActivity.this.sendBroadcast(mediaScanIntent);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                String fileName = "_" + getRandomNumberString();
                String folder = "Rose WA Statuses";
                String destPathMP4 = Environment.getExternalStoragePublicDirectory(folder) + "/WhatsAppStatus" + fileName + MP4;
                String destPathJPG = Environment.getExternalStoragePublicDirectory(folder) + "/WhatsAppStatus" + fileName + JPG;
                String destPathGIF = Environment.getExternalStoragePublicDirectory(folder) + "/WhatsAppStatus" + fileName + GIF;
                File destMP4 = new File(destPathMP4);
                File destJPG = new File(destPathJPG);
                File destGIF = new File(destPathGIF);
                if (sourcePath.endsWith(MP4)) {
                    IOUtils.move(source, destMP4);
                } else {
                    Log.d(TAG, "onClick: no data saved");
                }
                if (sourcePath.endsWith(JPG)) {
                    IOUtils.move(source, destJPG);
                } else {
                    Log.d(TAG, "onClick: no data saved");
                }
                if (sourcePath.endsWith(GIF)) {
                    IOUtils.move(source, destGIF);
                } else {
                    Log.d(TAG, "onClick: no data saved");
                }
                CompletedVideos completedVideos = CompletedVideos.load(MainActivity.this);
                completedVideos.addVideo(MainActivity.this, "WhatsAppStatus" + fileName + MP4);
                ToastUtils.showLong("Status Saved Successfully at location:" + destPathMP4);
            });
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.whatsapp_video_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return videoList.size();
        }

        public String getRandomNumberString() {
            // It will generate 6 digit random Number.
            // from 0 to 999999
            Random rnd = new Random();
            int number = rnd.nextInt(999999);
            // this will convert any number sequence into 6 character.
            return String.format(Locale.getDefault(), "%06d", number);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView iv_image;
            RelativeLayout rl_select;
            ImageView wsp;
            ImageView share;
            ImageView download;
            FloatingActionButton play_btn;

            public ViewHolder(View v) {
                super(v);
                iv_image = v.findViewById(R.id.iv_image);
                rl_select = v.findViewById(R.id.rl_select);
                wsp = v.findViewById(R.id.whatsapp);
                share = v.findViewById(R.id.share);
                download = v.findViewById(R.id.download);
                play_btn = v.findViewById(R.id.play_btn);
            }
        }
    }
}
