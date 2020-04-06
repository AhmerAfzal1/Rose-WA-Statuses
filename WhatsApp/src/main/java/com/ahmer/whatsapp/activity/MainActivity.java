package com.ahmer.whatsapp.activity;

import android.content.Context;
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

import com.ahmer.afzal.utils.utilcode.FileUtils;
import com.ahmer.afzal.utils.utilcode.PathUtils;
import com.ahmer.afzal.utils.utilcode.ThreadUtils;
import com.ahmer.afzal.utils.utilcode.ThrowableUtils;
import com.ahmer.afzal.utils.utilcode.ToastUtils;
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
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.ahmer.whatsapp.ConstantsValues.EXT_GIF_LOWER_CASE;
import static com.ahmer.whatsapp.ConstantsValues.EXT_GIF_UPPER_CASE;
import static com.ahmer.whatsapp.ConstantsValues.EXT_JPG_LOWER_CASE;
import static com.ahmer.whatsapp.ConstantsValues.EXT_JPG_UPPER_CASE;
import static com.ahmer.whatsapp.ConstantsValues.EXT_MP4_LOWER_CASE;
import static com.ahmer.whatsapp.ConstantsValues.EXT_MP4_UPPER_CASE;
import static com.ahmer.whatsapp.ConstantsValues.TAG;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_INTERNAL_ERROR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_INVALID_REQUEST;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NETWORK_ERROR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL;

public class MainActivity extends AppCompatActivity {

    private AdView adView;
    private ArrayList<WAStatusItem> contentList = new ArrayList<>();
    private ContentLoadingProgressBar progressBar;
    private FirebaseAnalytics firebaseAnalytics;
    private RecyclerView recyclerView;
    private StatusVideoAdapter adapter;
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
        adapter = new StatusVideoAdapter();
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.rvWhatsappStatusList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adView = findViewById(R.id.adView);
        FirebaseCrashlytics firebaseCrashlytics = FirebaseCrashlytics.getInstance();
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        firebaseCrashlytics.log("Start " + getClass().getSimpleName() + " Crashlytics logging...");
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
            getData();
        } catch (Exception e) {
            e.printStackTrace();
            ThrowableUtils.getFullStackTrace(e);
            Log.v(TAG, getClass().getSimpleName() + " -> Error during loading data: " + e.getMessage());
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    public void getData() {

        //File moviesFolder = new File(PathUtils.getExternalStoragePath() + "/AhmerFolder");
        File moviesFolder = new File(PathUtils.getExternalMoviesPath());
        Log.v(TAG, getClass().getSimpleName() + moviesFolder.getAbsolutePath());
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
                Log.v(TAG, getClass().getSimpleName() + "File Name: " + wa.getName());
                if (wa.getName().endsWith(EXT_MP4_LOWER_CASE) || wa.getName().endsWith(EXT_MP4_UPPER_CASE)) {
                    getMP4(wa);
                } else if (wa.getName().endsWith(EXT_JPG_LOWER_CASE) || wa.getName().endsWith(EXT_JPG_UPPER_CASE)) {
                    getJPG(wa);
                } else if (wa.getName().endsWith(EXT_GIF_LOWER_CASE) || wa.getName().endsWith(EXT_GIF_UPPER_CASE)) {
                    getGIF(wa);
                }
            }
        }
        File dirWhatsApp = new File(PathUtils.getExternalStoragePath() + "/WhatsApp");
        File dirFMWhatsApp = new File(PathUtils.getExternalStoragePath() + "/FMWhatsApp");
        File dirYoWhatsApp = new File(PathUtils.getExternalStoragePath() + "/YoWhatsApp");
       /*
        File dirWhatsApp = new File(PathUtils.getExternalStoragePath() + WHATSAPP_STATUSES_LOCATION);
        File dirFMWhatsApp = new File(PathUtils.getExternalStoragePath() + FM_WHATSAPP_STATUSES_LOCATION);
        File dirYoWhatsApp = new File(PathUtils.getExternalStoragePath() + YO_WHATSAPP_STATUSES_LOCATION);
*/
        File[] filesWA = dirWhatsApp.listFiles();
        File[] filesFMWA = dirFMWhatsApp.listFiles();
        File[] fileYoWA = dirYoWhatsApp.listFiles();

        if (dirWhatsApp.exists()) {
            Log.v(TAG, getClass().getSimpleName() + " -> WhatsApp installed");
            if (filesWA != null) {
                noStatus.setVisibility(View.INVISIBLE);
                for (File wa : filesWA) {
                    if (wa.getName().endsWith(EXT_MP4_LOWER_CASE) || wa.getName().toLowerCase().endsWith(EXT_MP4_UPPER_CASE)) {
                        getMP4(wa);
                    } else if (wa.getName().endsWith(EXT_JPG_LOWER_CASE) || wa.getName().toLowerCase().endsWith(EXT_JPG_UPPER_CASE)) {
                        getJPG(wa);
                    } else if (wa.getName().endsWith(EXT_GIF_LOWER_CASE) || wa.getName().toLowerCase().endsWith(EXT_GIF_UPPER_CASE)) {
                        getGIF(wa);
                    }
                }
            } else {
                noStatus.setText(R.string.no_having_status);
                noStatus.setVisibility(View.VISIBLE);
                Log.v(TAG, "WA : " + noStatus);
            }
        }

        if (dirFMWhatsApp.exists()) {
            Log.v(TAG, getClass().getSimpleName() + " -> FMWhatsApp installed");
            if (filesFMWA != null) {
                noStatus.setVisibility(View.INVISIBLE);
                for (File fmWA : filesFMWA) {
                    if (fmWA.getName().endsWith(EXT_MP4_LOWER_CASE) || fmWA.getName().toLowerCase().endsWith(EXT_MP4_UPPER_CASE)) {
                        getMP4(fmWA);
                    } else if (fmWA.getName().endsWith(EXT_JPG_LOWER_CASE) || fmWA.getName().toLowerCase().endsWith(EXT_JPG_UPPER_CASE)) {
                        getJPG(fmWA);
                    } else if (fmWA.getName().endsWith(EXT_GIF_LOWER_CASE) || fmWA.getName().toLowerCase().endsWith(EXT_GIF_UPPER_CASE)) {
                        getGIF(fmWA);
                    }
                }
            } else {
                noStatus.setText(R.string.no_having_status);
                noStatus.setVisibility(View.VISIBLE);
                Log.v(TAG, "FM : " + noStatus);
            }
        }
        if (dirYoWhatsApp.exists()) {
            Log.v(TAG, getClass().getSimpleName() + " -> YoWhatsApp installed");
            if (fileYoWA != null) {
                noStatus.setVisibility(View.INVISIBLE);
                for (File yoWA : fileYoWA) {
                    if (yoWA.getName().endsWith(EXT_MP4_LOWER_CASE) || yoWA.getName().toLowerCase().endsWith(EXT_MP4_UPPER_CASE)) {
                        getMP4(yoWA);
                    } else if (yoWA.getName().endsWith(EXT_JPG_LOWER_CASE) || yoWA.getName().toLowerCase().endsWith(EXT_JPG_UPPER_CASE)) {
                        getJPG(yoWA);
                    } else if (yoWA.getName().endsWith(EXT_GIF_LOWER_CASE) || yoWA.getName().toLowerCase().endsWith(EXT_GIF_UPPER_CASE)) {
                        getGIF(yoWA);
                    }
                }
            } else {
                noStatus.setText(R.string.no_having_status);
                noStatus.setVisibility(View.VISIBLE);
                Log.v(TAG, "Yo : " + noStatus);
            }
        }
        if (!dirWhatsApp.exists() && !dirFMWhatsApp.exists() && !dirYoWhatsApp.exists()) {
            Log.v(TAG, getClass().getSimpleName() + " -> No kind of WhatsApp installed");
            noStatus.setText(R.string.no_status);
            noStatus.setVisibility(View.VISIBLE);
        }
        recyclerView.setAdapter(adapter);
    }

    private void getMP4(File file) {
        WAStatusItem getMP4 = new WAStatusItem();
        getMP4.setSelect(false);
        getMP4.setPath(file.getAbsolutePath());
        Bitmap thumb = Thumbnails.videoThumbnails(file.getAbsolutePath());
        getMP4.setThumbnails(thumb);
        getMP4.setFormat(EXT_MP4_LOWER_CASE);
        contentList.add(getMP4);
    }

    private void getJPG(File file) {
        WAStatusItem getJPG = new WAStatusItem();
        getJPG.setSelect(false);
        getJPG.setPath(file.getAbsolutePath());
        Bitmap pic = Thumbnails.imageThumbnails(file.getAbsolutePath());
        getJPG.setThumbnails(pic);
        getJPG.setFormat(EXT_JPG_LOWER_CASE);
        contentList.add(getJPG);
    }

    private void getGIF(File file) {
        WAStatusItem getGIF = new WAStatusItem();
        getGIF.setSelect(false);
        getGIF.setPath(file.getAbsolutePath());
        Bitmap thumb = Thumbnails.imageThumbnails(file.getAbsolutePath());
        getGIF.setThumbnails(thumb);
        getGIF.setFormat(EXT_GIF_LOWER_CASE);
        contentList.add(getGIF);
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

    public static class MoveFiles extends AsyncTask<File, Integer, Boolean> {

        private WeakReference<Context> context;
        private WeakReference<ContentLoadingProgressBar> progressBar;
        private File destination;

        private MoveFiles(Context context, File destination, ContentLoadingProgressBar progressBar) {
            this.context = new WeakReference<>(context);
            this.progressBar = new WeakReference<>(progressBar);
            this.destination = destination;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.get().setVisibility(View.VISIBLE);
            Log.v(TAG, "onPreExecute");
        }

        @Override
        protected Boolean doInBackground(File... files) {
            ThreadUtils.runOnUiThread(() -> {
                File source = files[0];
                FileUtils.move(source, destination.getAbsoluteFile());
                Log.v(TAG, "doInBackground");
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.get().setProgress(values[0]);
            Log.v(TAG, "onProgressUpdate");
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressBar.get().setVisibility(View.GONE);
            Log.v(TAG, "progressBar: " + progressBar + " View: " + View.GONE);
            ToastUtils.showLong("Status have been successfully saved to: " + destination.getAbsolutePath());
            ThreadUtils.runOnUiThread(() -> {
                new MediaScanner(context.get(), destination);
                Log.v(TAG, "onPostExecute");
            });
        }
    }

    public class StatusVideoAdapter extends RecyclerView.Adapter<StatusVideoAdapter.ViewHolder> {

        @Override
        public void onBindViewHolder(final StatusVideoAdapter.ViewHolder holder, final int position) {
            holder.iv_image.setImageBitmap(contentList.get(position).getThumbnails());
            holder.layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.layout.setAlpha(0);
            holder.layout.setOnClickListener(view -> {
                if (contentList.get(position).getFormat().endsWith(EXT_MP4_LOWER_CASE) || contentList.get(position).getFormat().toLowerCase().endsWith(EXT_MP4_UPPER_CASE)) {
                    Bundle bundleMP4 = new Bundle();
                    bundleMP4.putString(FirebaseAnalytics.Param.ITEM_ID, "MP4");
                    bundleMP4.putString(FirebaseAnalytics.Param.ITEM_NAME, "MP4 Video Viewed");
                    firebaseAnalytics.logEvent("MP4_Open", bundleMP4);
                    Intent intentVideo = new Intent(MainActivity.this, WAVideoStatusView.class);
                    intentVideo.putExtra("format", contentList.get(position).getFormat());
                    intentVideo.putExtra("path", contentList.get(position).getPath());
                    MainActivity.this.startActivity(intentVideo);
                }
                if (contentList.get(position).getFormat().endsWith(EXT_JPG_LOWER_CASE) || contentList.get(position).getFormat().toLowerCase().endsWith(EXT_JPG_UPPER_CASE)) {
                    Bundle bundleJPG = new Bundle();
                    bundleJPG.putString(FirebaseAnalytics.Param.ITEM_ID, "JPG");
                    bundleJPG.putString(FirebaseAnalytics.Param.ITEM_NAME, "JPG Image Viewed");
                    firebaseAnalytics.logEvent("JPG_Open", bundleJPG);
                    Intent intentJPG = new Intent(MainActivity.this, WAImageStatusView.class);
                    intentJPG.putExtra("format", contentList.get(position).getFormat());
                    intentJPG.putExtra("path", contentList.get(position).getPath());
                    MainActivity.this.startActivity(intentJPG);
                }
                if (contentList.get(position).getFormat().endsWith(EXT_GIF_LOWER_CASE) || contentList.get(position).getFormat().toLowerCase().endsWith(EXT_GIF_UPPER_CASE)) {
                    Bundle bundleGIF = new Bundle();
                    bundleGIF.putString(FirebaseAnalytics.Param.ITEM_ID, "GIF");
                    bundleGIF.putString(FirebaseAnalytics.Param.ITEM_NAME, "GIF Image Viewed");
                    firebaseAnalytics.logEvent("GIF_Open", bundleGIF);
                    Intent intentGIF = new Intent(MainActivity.this, WAImageStatusView.class);
                    intentGIF.putExtra("format", contentList.get(position).getFormat());
                    intentGIF.putExtra("path", contentList.get(position).getPath());
                    MainActivity.this.startActivity(intentGIF);
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
                File source = new File(contentList.get(position).getPath());
                String directoryAndFileName = "/Rose WA Statuses/Status_" + FileUtils.getFileNameNoExtension(source.getAbsolutePath());
                File statusDirectory = new File(PathUtils.getExternalStoragePath(), MainActivity.this.getString(R.string.app_name));
                if (!statusDirectory.exists()) {
                    if (statusDirectory.mkdirs()) {
                        Log.v(TAG, getClass().getSimpleName() + " -> The directory has been created: " + statusDirectory);
                    } else {
                        Log.v(TAG, getClass().getSimpleName() + " -> Could not create the directory for some unknown reason");
                    }
                } else {
                    Log.v(TAG, getClass().getSimpleName() + " -> This directory has already been created");
                }
                if (source.getAbsolutePath().endsWith(EXT_MP4_LOWER_CASE) || source.getAbsolutePath().endsWith(EXT_MP4_UPPER_CASE)) {
                    File destPathMP4 = new File(PathUtils.getExternalStoragePath() + directoryAndFileName + EXT_MP4_LOWER_CASE);
                    Bundle bundleDownloadMP4 = new Bundle();
                    bundleDownloadMP4.putString(FirebaseAnalytics.Param.ITEM_ID, "DownloadMP4");
                    bundleDownloadMP4.putString(FirebaseAnalytics.Param.ITEM_NAME, "User Download MP4 Status");
                    firebaseAnalytics.logEvent("Download_MP4_Open", bundleDownloadMP4);
                    new MoveFiles(MainActivity.this, destPathMP4, progressBar).execute(source);
                    updateList(position);
                } else {
                    Log.v(TAG, getClass().getSimpleName() + " -> onClick: no data saved");
                }
                if (source.getAbsolutePath().endsWith(EXT_JPG_LOWER_CASE) || source.getAbsolutePath().endsWith(EXT_JPG_UPPER_CASE)) {
                    File destPathJPG = new File(PathUtils.getExternalStoragePath() + directoryAndFileName + EXT_JPG_LOWER_CASE);
                    Bundle bundleDownloadJPG = new Bundle();
                    bundleDownloadJPG.putString(FirebaseAnalytics.Param.ITEM_ID, "DownloadJPG");
                    bundleDownloadJPG.putString(FirebaseAnalytics.Param.ITEM_NAME, "User Download JPG Status");
                    firebaseAnalytics.logEvent("Download_JPG_Open", bundleDownloadJPG);
                    new MoveFiles(MainActivity.this, destPathJPG, progressBar).execute(source);
                    updateList(position);
                } else {
                    Log.v(TAG, getClass().getSimpleName() + " -> onClick: no data saved");
                }
                if (source.getAbsolutePath().endsWith(EXT_GIF_LOWER_CASE) || source.getAbsolutePath().endsWith(EXT_GIF_UPPER_CASE)) {
                    File destPathGIF = new File(PathUtils.getExternalStoragePath() + directoryAndFileName + EXT_GIF_LOWER_CASE);
                    Bundle bundleDownloadGIF = new Bundle();
                    bundleDownloadGIF.putString(FirebaseAnalytics.Param.ITEM_ID, "DownloadGIF");
                    bundleDownloadGIF.putString(FirebaseAnalytics.Param.ITEM_NAME, "User Download GIF Status");
                    firebaseAnalytics.logEvent("Download_GIF_Open", bundleDownloadGIF);
                    new MoveFiles(MainActivity.this, destPathGIF, progressBar).execute(source);
                    updateList(position);
                } else {
                    Log.v(TAG, getClass().getSimpleName() + " -> onClick: no data saved");
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

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        private void updateList(final int position) {
            contentList.remove(position);
            adapter.notifyItemChanged(position);
            adapter.notifyItemRangeChanged(position, getItemCount());
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
