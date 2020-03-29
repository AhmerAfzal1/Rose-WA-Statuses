package com.ahmer.whatsapp.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import com.ahmer.whatsapp.R;
import com.ahmer.whatsapp.Thumbnails;
import com.ahmer.whatsapp.WAStatusItem;
import com.ahmer.whatsapp.WAVideoStatusView;
import com.ahmer.whatsapp.permission.DownloadPermissionHandler;
import com.ahmer.whatsapp.permission.PermissionRequestCodes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import static com.ahmer.whatsapp.ConstantsValues.FM_WHATSAPP_STATUSES_LOCATION;
import static com.ahmer.whatsapp.ConstantsValues.GIF;
import static com.ahmer.whatsapp.ConstantsValues.JPG;
import static com.ahmer.whatsapp.ConstantsValues.MP4;
import static com.ahmer.whatsapp.ConstantsValues.TAG;
import static com.ahmer.whatsapp.ConstantsValues.WHATSAPP_STATUSES_LOCATION;
import static com.ahmer.whatsapp.ConstantsValues.YO_WHATSAPP_STATUSES_LOCATION;

public class MainActivity extends AppCompatActivity {

    private ActivityCompat.OnRequestPermissionsResultCallback onRequestPermissionsResultCallback;
    private RecyclerView rvVideo;
    private ArrayList<WAStatusItem> videoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView toolbar = findViewById(R.id.ivBack);
        toolbar.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        });
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
                    try {
                        getVideo();
                    } catch (IOException e) {
                        Log.v(TAG, Objects.requireNonNull(e.getMessage()));
                        e.printStackTrace();
                    }
                }
            }.checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, PermissionRequestCodes.DOWNLOADS);
        } else {
            try {
                getVideo();
            } catch (IOException e) {
                Log.v(TAG, Objects.requireNonNull(e.getMessage()));
                e.printStackTrace();
            }
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

    public void getVideo() throws IOException {

        File moviesFolder = new File(PathUtils.getExternalMoviesPath());
        Log.v(TAG, moviesFolder.getAbsolutePath());
        File[] movies;
        movies = moviesFolder.listFiles();
        if (moviesFolder.exists()) {
            findViewById(R.id.tvNoStatus).setVisibility(View.GONE);
        } else {
            findViewById(R.id.tvNoStatus).setVisibility(View.VISIBLE);
        }
        if (movies != null) {
            for (File wa : movies) {
                Log.v(TAG, "File Name: " + wa.getName());
                if (wa.getName().endsWith(MP4)) {
                    getMP4(wa);
                } else if (wa.getName().endsWith(JPG)) {
                    getJPG(wa);
                } else if (wa.getName().endsWith(GIF)) {
                    getGIF(wa);
                }
            }
        }

        File dirWhatsApp = new File(PathUtils.getExternalStoragePath() + WHATSAPP_STATUSES_LOCATION);
        File dirFMWhatsApp = new File(PathUtils.getExternalStoragePath() + FM_WHATSAPP_STATUSES_LOCATION);
        File dirYoWhatsApp = new File(PathUtils.getExternalStoragePath() + YO_WHATSAPP_STATUSES_LOCATION);
        File[] filesWA, filesFMWA, fileYoWA;
        filesWA = dirWhatsApp.listFiles();
        filesFMWA = dirFMWhatsApp.listFiles();
        fileYoWA = dirYoWhatsApp.listFiles();
        if (dirWhatsApp.exists() || dirFMWhatsApp.exists() || dirYoWhatsApp.exists()) {
            findViewById(R.id.tvNoStatus).setVisibility(View.GONE);
        } else {
            findViewById(R.id.tvNoStatus).setVisibility(View.VISIBLE);
        }
        if (filesWA != null) {
            for (File wa : filesWA) {
                if (wa.getName().endsWith(MP4)) {
                    getMP4(wa);
                } else if (wa.getName().endsWith(JPG)) {
                    getJPG(wa);
                } else if (wa.getName().endsWith(GIF)) {
                    getGIF(wa);
                }
            }
            if (filesFMWA != null) {
                for (File fmwa : filesFMWA) {
                    if (fmwa.getName().endsWith(MP4)) {
                        getMP4(fmwa);
                    } else if (fmwa.getName().endsWith(JPG)) {
                        getJPG(fmwa);
                    } else if (fmwa.getName().endsWith(GIF)) {
                        getGIF(fmwa);
                    }
                }
            }
            if (fileYoWA != null) {
                for (File yowa : fileYoWA) {
                    if (yowa.getName().endsWith(MP4)) {
                        getMP4(yowa);
                    } else if (yowa.getName().endsWith(JPG)) {
                        getJPG(yowa);
                    } else if (yowa.getName().endsWith(GIF)) {
                        getGIF(yowa);
                    }
                }
            }
        }
        StatusVideoAdapter statusVideoAdapter = new StatusVideoAdapter();
        rvVideo.setAdapter(statusVideoAdapter);
    }

    private void getMP4(File file) throws IOException {
        WAStatusItem obj = new WAStatusItem();
        obj.setSelect(false);
        obj.setPath(file.getAbsolutePath());
        Bitmap thumb = Thumbnails.videoThumbnails(file.getAbsolutePath());
        obj.setThumbnails(thumb);
        obj.setFormat(MP4);
        videoList.add(obj);
    }

    private void getJPG(File file) throws IOException {
        WAStatusItem obj_model = new WAStatusItem();
        obj_model.setSelect(false);
        obj_model.setPath(file.getAbsolutePath());
        Bitmap pic = Thumbnails.imageThumbnails(file.getAbsolutePath());
        obj_model.setThumbnails(pic);
        obj_model.setFormat(JPG);
        videoList.add(obj_model);
    }

    private void getGIF(File file) throws IOException {
        WAStatusItem obj = new WAStatusItem();
        obj.setSelect(false);
        obj.setPath(file.getAbsolutePath());
        Bitmap thumb = Thumbnails.videoThumbnails(file.getAbsolutePath());
        obj.setThumbnails(thumb);
        obj.setFormat(GIF);
        videoList.add(obj);
    }

    public class StatusVideoAdapter extends RecyclerView.Adapter<StatusVideoAdapter.ViewHolder> {

        @Override
        public void onBindViewHolder(final StatusVideoAdapter.ViewHolder holder, final int position) {
            holder.iv_image.setImageBitmap(videoList.get(position).getThumbnails());
            holder.rl_select.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.rl_select.setAlpha(0);
            holder.rl_select.setOnClickListener(view -> {
                Intent intent_gallery = new Intent(MainActivity.this, WAVideoStatusView.class);
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
                    ToastUtils.showLong("Status Saved Successfully at location:" + destPathMP4);
                } else {
                    Log.d(TAG, "onClick: no data saved");
                }
                if (sourcePath.endsWith(JPG)) {
                    IOUtils.move(source, destJPG);
                    ToastUtils.showLong("Status Saved Successfully at location:" + destPathJPG);
                } else {
                    Log.d(TAG, "onClick: no data saved");
                }
                if (sourcePath.endsWith(GIF)) {
                    IOUtils.move(source, destGIF);
                    ToastUtils.showLong("Status Saved Successfully at location:" + destPathGIF);
                } else {
                    Log.d(TAG, "onClick: no data saved");
                }
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

        private String getRandomNumberString() {
            // It will generate 6 digit random Number.
            // from 0 to 999999
            Random rnd = new Random();
            int number = rnd.nextInt(999999);
            // this will convert any number sequence into 6 character.
            return String.format(Locale.getDefault(), "%06d", number);
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            ImageView iv_image;
            RelativeLayout rl_select;
            ImageView wsp;
            ImageView share;
            ImageView download;
            FloatingActionButton play_btn;

            private ViewHolder(View v) {
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
