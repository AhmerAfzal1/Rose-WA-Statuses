package com.ahmer.whatsapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmer.afzal.utils.IOUtils;
import com.ahmer.afzal.utils.info.PathUtils;
import com.ahmer.afzal.utils.toastandsnackbar.ToastUtils;
import com.ahmer.whatsapp.ConstantsValues;
import com.ahmer.whatsapp.MediaScanner;
import com.ahmer.whatsapp.R;
import com.ahmer.whatsapp.Thumbnails;
import com.ahmer.whatsapp.WAImageStatusView;
import com.ahmer.whatsapp.WAStatusItem;
import com.ahmer.whatsapp.WAVideoStatusView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import static com.ahmer.whatsapp.ConstantsValues.FM_WHATSAPP_STATUSES_LOCATION;
import static com.ahmer.whatsapp.ConstantsValues.GIF;
import static com.ahmer.whatsapp.ConstantsValues.JPG;
import static com.ahmer.whatsapp.ConstantsValues.MP4;
import static com.ahmer.whatsapp.ConstantsValues.TAG;
import static com.ahmer.whatsapp.ConstantsValues.WHATSAPP_STATUSES_LOCATION;
import static com.ahmer.whatsapp.ConstantsValues.YO_WHATSAPP_STATUSES_LOCATION;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_INTERNAL_ERROR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_INVALID_REQUEST;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NETWORK_ERROR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvVideo;
    private ArrayList<WAStatusItem> contentList = new ArrayList<>();
    private AdView adView;
    private FirebaseAnalytics firebaseAnalytics;
    private ContentLoadingProgressBar progressBar;
    private TextView noStatus;

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
        noStatus = findViewById(R.id.tvNoStatus);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        rvVideo = findViewById(R.id.rvWhatsappStatusList);
        rvVideo.setLayoutManager(new GridLayoutManager(this, 1));
        adView = findViewById(R.id.adView);
        FirebaseCrashlytics firebaseCrashlytics = FirebaseCrashlytics.getInstance();
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        firebaseCrashlytics.log("Start " + MainActivity.class.getSimpleName() + " Crashlytics logging...");
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        MobileAds.initialize(this, getResources().getString(R.string.banner_ad_app_id));
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.v(ConstantsValues.TAG, getResources().getString(R.string.adLoaded));
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                switch (errorCode) {
                    case ERROR_CODE_INTERNAL_ERROR: {
                        Log.v(ConstantsValues.TAG, getResources().getString(R.string.adFailedToLoad_ERROR_CODE_INTERNAL_ERROR));
                        firebaseCrashlytics.log(getResources().getString(R.string.adFailedToLoad_ERROR_CODE_INTERNAL_ERROR));
                    }
                    break;
                    case ERROR_CODE_INVALID_REQUEST: {
                        Log.v(ConstantsValues.TAG, getResources().getString(R.string.adFailedToLoad_ERROR_CODE_INVALID_REQUEST));
                        firebaseCrashlytics.log(getResources().getString(R.string.adFailedToLoad_ERROR_CODE_INVALID_REQUEST));
                    }
                    break;
                    case ERROR_CODE_NETWORK_ERROR: {
                        Log.v(ConstantsValues.TAG, getResources().getString(R.string.adFailedToLoad_ERROR_CODE_NETWORK_ERROR));
                        firebaseCrashlytics.log(getResources().getString(R.string.adFailedToLoad_ERROR_CODE_NETWORK_ERROR));
                    }
                    break;
                    case ERROR_CODE_NO_FILL: {
                        Log.v(ConstantsValues.TAG, getResources().getString(R.string.adFailedToLoad_ERROR_CODE_NO_FILL));
                        firebaseCrashlytics.log(getResources().getString(R.string.adFailedToLoad_ERROR_CODE_NO_FILL));
                    }
                    break;
                    default: {
                        Log.v(ConstantsValues.TAG, getResources().getString(R.string.adFailedToLoad) + errorCode);
                        firebaseCrashlytics.log(getResources().getString(R.string.adFailedToLoad) + errorCode);
                    }
                    break;
                }
            }

            @Override
            public void onAdOpened() {
                Log.v(ConstantsValues.TAG, getResources().getString(R.string.adOpened));
            }

            @Override
            public void onAdLeftApplication() {
                Log.v(ConstantsValues.TAG, getResources().getString(R.string.adLeftApplication));
            }

            @Override
            public void onAdClosed() {
                Log.v(ConstantsValues.TAG, getResources().getString(R.string.adClosed));
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        try {
            getVideo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //new RunProgress().execute();
    }

    public void getVideo() throws IOException {

        File moviesFolder = new File(PathUtils.getExternalMoviesPath());
        Log.v(TAG, moviesFolder.getAbsolutePath());
        File[] movies;
        movies = moviesFolder.listFiles();
        if (moviesFolder.exists()) {
            noStatus.setText(R.string.no_having_status);
            noStatus.setVisibility(View.VISIBLE);
        } else {
            noStatus.setText(R.string.no_status);
            noStatus.setVisibility(View.VISIBLE);
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
            noStatus.setText(R.string.no_having_status);
            noStatus.setVisibility(View.VISIBLE);
        } else {
            noStatus.setText(R.string.no_status);
            noStatus.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
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
                progressBar.setVisibility(View.INVISIBLE);
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
                progressBar.setVisibility(View.INVISIBLE);
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
            progressBar.setVisibility(View.INVISIBLE);
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
        contentList.add(obj);
    }

    private void getJPG(File file) throws IOException {
        WAStatusItem obj_model = new WAStatusItem();
        obj_model.setSelect(false);
        obj_model.setPath(file.getAbsolutePath());
        Bitmap pic = Thumbnails.imageThumbnails(file.getAbsolutePath());
        obj_model.setThumbnails(pic);
        obj_model.setFormat(JPG);
        contentList.add(obj_model);
    }

    private void getGIF(File file) throws IOException {
        WAStatusItem obj = new WAStatusItem();
        obj.setSelect(false);
        obj.setPath(file.getAbsolutePath());
        Bitmap thumb = Thumbnails.videoThumbnails(file.getAbsolutePath());
        obj.setThumbnails(thumb);
        obj.setFormat(GIF);
        contentList.add(obj);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (adView != null) {
            adView.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseAnalytics.setCurrentScreen(this, "CurrentScreen: " + getClass().getSimpleName(), null);
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adView != null) {
            adView.destroy();
        }
    }

    public class StatusVideoAdapter extends RecyclerView.Adapter<StatusVideoAdapter.ViewHolder> {

        @Override
        public void onBindViewHolder(final StatusVideoAdapter.ViewHolder holder, final int position) {
            holder.iv_image.setImageBitmap(contentList.get(position).getThumbnails());
            holder.layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.layout.setAlpha(0);
            holder.layout.setOnClickListener(view -> {
                if (contentList.get(position).getFormat().endsWith(MP4)) {
                    Bundle bundleMP4 = new Bundle();
                    bundleMP4.putString(FirebaseAnalytics.Param.ITEM_ID, "MP4");
                    bundleMP4.putString(FirebaseAnalytics.Param.ITEM_NAME, "MP4 Video Viewed");
                    firebaseAnalytics.logEvent("MP4_Open", bundleMP4);
                    Intent intent_gallery = new Intent(MainActivity.this, WAVideoStatusView.class);
                    intent_gallery.putExtra("format", contentList.get(position).getFormat());
                    intent_gallery.putExtra("path", contentList.get(position).getPath());
                    MainActivity.this.startActivity(intent_gallery);
                }
                if (contentList.get(position).getFormat().endsWith(JPG)) {
                    Bundle bundleJPG = new Bundle();
                    bundleJPG.putString(FirebaseAnalytics.Param.ITEM_ID, "JPG");
                    bundleJPG.putString(FirebaseAnalytics.Param.ITEM_NAME, "JPG Image Viewed");
                    firebaseAnalytics.logEvent("JPG_Open", bundleJPG);
                    Intent intent_gallery = new Intent(MainActivity.this, WAImageStatusView.class);
                    intent_gallery.putExtra("format", contentList.get(position).getFormat());
                    intent_gallery.putExtra("path", contentList.get(position).getPath());
                    MainActivity.this.startActivity(intent_gallery);
                }
                if (contentList.get(position).getFormat().endsWith(GIF)) {
                    Bundle bundleGIF = new Bundle();
                    bundleGIF.putString(FirebaseAnalytics.Param.ITEM_ID, "GIF");
                    bundleGIF.putString(FirebaseAnalytics.Param.ITEM_NAME, "GIF Image Viewed");
                    firebaseAnalytics.logEvent("GIF_Open", bundleGIF);
                    Intent intent_gallery = new Intent(MainActivity.this, WAVideoStatusView.class);
                    intent_gallery.putExtra("format", contentList.get(position).getFormat());
                    intent_gallery.putExtra("path", contentList.get(position).getPath());
                    MainActivity.this.startActivity(intent_gallery);
                }
            });

            holder.share.setOnClickListener(v -> {
                Bundle bundleShare = new Bundle();
                bundleShare.putString(FirebaseAnalytics.Param.ITEM_ID, "Share");
                bundleShare.putString(FirebaseAnalytics.Param.ITEM_NAME, "User Shared Something");
                firebaseAnalytics.logEvent("Share_Open", bundleShare);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(contentList.get(position).getPath()));
                sendIntent.setType("file/*");
                MainActivity.this.startActivity(Intent.createChooser(sendIntent, "Send Status via:"));
            });

            holder.whatsAppShare.setOnClickListener(v -> {
                Bundle bundleWhatsApp = new Bundle();
                bundleWhatsApp.putString(FirebaseAnalytics.Param.ITEM_ID, "ShareWhatsApp");
                bundleWhatsApp.putString(FirebaseAnalytics.Param.ITEM_NAME, "User Shared Something on WhatsApp");
                firebaseAnalytics.logEvent("WhatsApp_Share_Open", bundleWhatsApp);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setPackage("com.whatsapp");
                sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(contentList.get(position).getPath()));
                sendIntent.setType("file/*");
                MainActivity.this.startActivity(Intent.createChooser(sendIntent, "Send Status via:"));
            });

            holder.download.setOnClickListener(v -> {

                String sourcePath = contentList.get(position).getPath();
                File source = new File(sourcePath);
                File statusDirectory = new File(PathUtils.getExternalStoragePath(), MainActivity.this.getString(R.string.app_name));
                if (!statusDirectory.exists()) {
                    if (statusDirectory.mkdirs()) {
                        Log.d(TAG, "The directory has been created: " + statusDirectory);
                    } else {
                        Log.d(TAG, "Could not create the directory for some unknown reason");
                    }
                } else {
                    Log.d(TAG, "This directory has already been created");
                }
                String directoryAndFileName = "/Rose WA Statuses/WhatsAppStatus_" + getRandomNumberString();
                String toastText = "Status have been successfully saved to: ";
                File destPathMP4 = new File(PathUtils.getExternalStoragePath() + directoryAndFileName + MP4);
                File destPathJPG = new File(PathUtils.getExternalStoragePath() + directoryAndFileName + JPG);
                File destPathGIF = new File(PathUtils.getExternalStoragePath() + directoryAndFileName + GIF);
                if (sourcePath.endsWith(MP4)) {
                    Bundle bundleDownloadMP4 = new Bundle();
                    bundleDownloadMP4.putString(FirebaseAnalytics.Param.ITEM_ID, "DownloadMP4");
                    bundleDownloadMP4.putString(FirebaseAnalytics.Param.ITEM_NAME, "User Download MP4 Status");
                    firebaseAnalytics.logEvent("Download_MP4_Open", bundleDownloadMP4);
                    IOUtils.move(source, destPathMP4.getAbsoluteFile());
                    ToastUtils.showLong(toastText + destPathMP4.getAbsolutePath());
                    MainActivity.this.recreate();
                    new MediaScanner(MainActivity.this, destPathMP4);
                } else {
                    Log.d(TAG, "onClick: no data saved");
                }
                if (sourcePath.endsWith(JPG)) {
                    Bundle bundleDownloadJPG = new Bundle();
                    bundleDownloadJPG.putString(FirebaseAnalytics.Param.ITEM_ID, "DownloadJPG");
                    bundleDownloadJPG.putString(FirebaseAnalytics.Param.ITEM_NAME, "User Download JPG Status");
                    firebaseAnalytics.logEvent("Download_JPG_Open", bundleDownloadJPG);
                    IOUtils.move(source, destPathJPG.getAbsoluteFile());
                    ToastUtils.showLong(toastText + destPathJPG.getAbsolutePath());
                    MainActivity.this.recreate();
                    new MediaScanner(MainActivity.this, destPathJPG);
                } else {
                    Log.d(TAG, "onClick: no data saved");
                }
                if (sourcePath.endsWith(GIF)) {
                    Bundle bundleDownloadGIF = new Bundle();
                    bundleDownloadGIF.putString(FirebaseAnalytics.Param.ITEM_ID, "DownloadGIF");
                    bundleDownloadGIF.putString(FirebaseAnalytics.Param.ITEM_NAME, "User Download GIF Status");
                    firebaseAnalytics.logEvent("Download_GIF_Open", bundleDownloadGIF);
                    IOUtils.move(source, destPathGIF.getAbsoluteFile());
                    ToastUtils.showLong(toastText + destPathGIF.getAbsolutePath());
                    MainActivity.this.recreate();
                    new MediaScanner(MainActivity.this, destPathGIF);
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
            return contentList.size();
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
            RelativeLayout layout;
            ImageView whatsAppShare;
            ImageView share;
            ImageView download;
            FloatingActionButton play_btn;

            private ViewHolder(View v) {
                super(v);
                iv_image = v.findViewById(R.id.iv_image);
                layout = v.findViewById(R.id.rl_select);
                whatsAppShare = v.findViewById(R.id.whatsapp);
                share = v.findViewById(R.id.share);
                download = v.findViewById(R.id.download);
                play_btn = v.findViewById(R.id.play_btn);
            }
        }
    }
}
