package com.ahmer.whatsapp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmer.afzal.utils.constants.AppPackageConstants;
import com.ahmer.afzal.utils.utilcode.AppUtils;
import com.ahmer.afzal.utils.utilcode.FileUtils;
import com.ahmer.afzal.utils.utilcode.PathUtils;
import com.ahmer.afzal.utils.utilcode.ThreadUtils;
import com.ahmer.afzal.utils.utilcode.ToastUtils;
import com.ahmer.whatsapp.Constant;
import com.ahmer.whatsapp.DialogAbout;
import com.ahmer.whatsapp.MediaScanner;
import com.ahmer.whatsapp.R;
import com.ahmer.whatsapp.StatusItem;
import com.ahmer.whatsapp.Utilities;
import com.ahmer.whatsapp.view.StatusViewImage;
import com.ahmer.whatsapp.view.StatusViewVideo;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;

import static com.ahmer.whatsapp.Constant.EXT_JPG_LOWER_CASE;
import static com.ahmer.whatsapp.Constant.EXT_JPG_UPPER_CASE;
import static com.ahmer.whatsapp.Constant.EXT_MP4_LOWER_CASE;
import static com.ahmer.whatsapp.Constant.EXT_MP4_UPPER_CASE;
import static com.ahmer.whatsapp.Constant.TAG;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_INTERNAL_ERROR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_INVALID_REQUEST;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NETWORK_ERROR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL;

public class MainActivity extends AppCompatActivity {

    private final ArrayList<StatusItem> contentList = new ArrayList<>(SplashActivity.imageStatuses);
    private AdView adView;
    private FirebaseAnalytics firebaseAnalytics;
    private RecyclerView recyclerView = null;
    private RelativeLayout noStatusLayout = null;
    private StatusVideoAdapter adapter = null;
    private TextView noStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView toolbar = findViewById(R.id.ivBack);
        toolbar.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        });
        contentList.addAll(SplashActivity.videoStatuses);
        Collections.sort(contentList, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        TextView title = findViewById(R.id.tvTitle);
        title.setText(R.string.app_name);
        ImageView info = findViewById(R.id.ivInfo);
        info.setOnClickListener(v -> new DialogAbout(this));
        ImageView settings = findViewById(R.id.ivSettings);
        settings.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SettingsActivity.class)));
        noStatus = findViewById(R.id.tvNoStatus);
        noStatusLayout = findViewById(R.id.layoutNoStatus);
        adView = findViewById(R.id.adView);
        recyclerView = findViewById(R.id.rvStatusList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        gridLayoutManager.isAutoMeasureEnabled();
        gridLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.getRecycledViewPool().clear();
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new StatusVideoAdapter();
        recyclerView.setAdapter(adapter);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        FirebaseCrashlytics firebaseCrashlytics = FirebaseCrashlytics.getInstance();
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        firebaseCrashlytics.log("Start " + getClass().getSimpleName() + " Crashlytics logging...");
        MobileAds.initialize(getApplicationContext(), initializationStatus -> {
            //Keep empty
        });
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.v(Constant.TAG, getResources().getString(R.string.adLoaded));
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                switch (errorCode) {
                    case ERROR_CODE_INTERNAL_ERROR: {
                        Log.v(Constant.TAG, getResources().getString(R.string.adFailedToLoad_ERROR_CODE_INTERNAL_ERROR));
                        firebaseCrashlytics.log(getResources().getString(R.string.adFailedToLoad_ERROR_CODE_INTERNAL_ERROR));
                    }
                    break;
                    case ERROR_CODE_INVALID_REQUEST: {
                        Log.v(Constant.TAG, getResources().getString(R.string.adFailedToLoad_ERROR_CODE_INVALID_REQUEST));
                        firebaseCrashlytics.log(getResources().getString(R.string.adFailedToLoad_ERROR_CODE_INVALID_REQUEST));
                    }
                    break;
                    case ERROR_CODE_NETWORK_ERROR: {
                        Log.v(Constant.TAG, getResources().getString(R.string.adFailedToLoad_ERROR_CODE_NETWORK_ERROR));
                        firebaseCrashlytics.log(getResources().getString(R.string.adFailedToLoad_ERROR_CODE_NETWORK_ERROR));
                    }
                    break;
                    case ERROR_CODE_NO_FILL: {
                        Log.v(Constant.TAG, getResources().getString(R.string.adFailedToLoad_ERROR_CODE_NO_FILL));
                        firebaseCrashlytics.log(getResources().getString(R.string.adFailedToLoad_ERROR_CODE_NO_FILL));
                    }
                    break;
                    default: {
                        Log.v(Constant.TAG, getResources().getString(R.string.adFailedToLoad) + errorCode);
                        firebaseCrashlytics.log(getResources().getString(R.string.adFailedToLoad) + errorCode);
                    }
                    break;
                }
            }

            @Override
            public void onAdOpened() {
                Log.v(Constant.TAG, getResources().getString(R.string.adOpened));
            }

            @Override
            public void onAdLeftApplication() {
                Log.v(Constant.TAG, getResources().getString(R.string.adLeftApplication));
            }

            @Override
            public void onAdClosed() {
                Log.v(Constant.TAG, getResources().getString(R.string.adClosed));
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (!(AppUtils.isAppInstalled(AppPackageConstants.PKG_WHATSAPP) || AppUtils.isAppInstalled(AppPackageConstants.PKG_BUSINESS_WHATSAPP)
                        || AppUtils.isAppInstalled(AppPackageConstants.PKG_FM_WhatsApp) || AppUtils.isAppInstalled(AppPackageConstants.PKG_Yo_WhatsApp))) {
                    Log.v(TAG, MainActivity.class.getSimpleName() + "-> No kind of WhatsApp installed");
                    noStatusLayout.setVisibility(View.VISIBLE);
                    noStatus.setText(R.string.no_whatsapp_installed);
                } else {
                    if (adapter.getItemCount() == 0) {
                        noStatusLayout.setVisibility(View.VISIBLE);
                        noStatus.setText(R.string.no_having_status);
                    } else {
                        noStatusLayout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                onChanged();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                onChanged();
            }
        };
        adapter.registerAdapterDataObserver(observer);
        observer.onChanged();
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
        if (contentList != null) {
            contentList.clear();
        }
    }

    static class MoveFiles extends AsyncTask<File, Integer, Boolean> {

        private final File destination;
        private final WeakReference<ProgressBar> progressBar;
        private final WeakReference<Context> context;

        private MoveFiles(Context context, File destination, ProgressBar progressBar) {
            this.context = new WeakReference<>(context);
            this.progressBar = new WeakReference<>(progressBar);
            this.destination = destination;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.get().setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(File... files) {
            ThreadUtils.runOnUiThread(() -> {
                File source = files[0];
                FileUtils.move(source, destination.getAbsoluteFile());
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.get().setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressBar.get().setVisibility(View.GONE);
            ThreadUtils.runOnUiThread(() -> {
                ToastUtils.showLong(context.get().getString(R.string.status_saved) + "\n" + destination.getPath());
                new MediaScanner(context.get(), destination);
            });
        }
    }

    public class StatusVideoAdapter extends RecyclerView.Adapter<StatusVideoAdapter.ViewHolder> {

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.ivThumbnails.setImageBitmap(contentList.get(position).getThumbnails());
            holder.relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.relativeLayout.setAlpha(0);
            holder.progressBar.setVisibility(View.GONE);
            holder.showSize.setText(Utilities.getFileSize(contentList.get(position).getSize()));
            File source = new File(contentList.get(position).getPath());
            String directoryAndFileName = "/Rose Statuses/Status_" + FileUtils.getFileNameNoExtension(source.getAbsolutePath());

            if (contentList.get(position).getFormat().endsWith(EXT_MP4_LOWER_CASE) ||
                    contentList.get(position).getFormat().endsWith(EXT_MP4_UPPER_CASE)) {
                holder.btnPlay.setVisibility(View.VISIBLE);
                holder.relativeLayout.setClickable(false);
                String mp4 = "MP4";
                holder.showType.setText(mp4);
            }

            if (contentList.get(position).getFormat().endsWith(EXT_JPG_LOWER_CASE) ||
                    contentList.get(position).getFormat().endsWith(EXT_JPG_UPPER_CASE)) {
                holder.btnPlay.setVisibility(View.GONE);
                holder.relativeLayout.setClickable(true);
                String jpg = "JPG";
                holder.showType.setText(jpg);
            }

            holder.btnDelete.setOnClickListener(v -> {
                File file = new File(contentList.get(position).getPath());
                FileUtils.delete(file);
                contentList.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeRemoved(position, getItemCount());
            });

            holder.relativeLayout.setOnClickListener(v -> {
                if (contentList.get(position).getFormat().endsWith(EXT_MP4_LOWER_CASE) ||
                        contentList.get(position).getFormat().endsWith(EXT_MP4_UPPER_CASE)) {
                    Bundle bundleMP4 = new Bundle();
                    bundleMP4.putString(FirebaseAnalytics.Param.ITEM_ID, "MP4");
                    bundleMP4.putString(FirebaseAnalytics.Param.ITEM_NAME, "MP4 Video Viewed");
                    firebaseAnalytics.logEvent("MP4_Open", bundleMP4);
                    Intent intentVideo = new Intent(v.getContext(), StatusViewVideo.class);
                    intentVideo.putExtra("format", contentList.get(position).getFormat());
                    intentVideo.putExtra("path", contentList.get(position).getPath());
                    intentVideo.putExtra("from", "MainActivity");
                    v.getContext().startActivity(intentVideo);
                }

                if (contentList.get(position).getFormat().endsWith(EXT_JPG_LOWER_CASE) ||
                        contentList.get(position).getFormat().endsWith(EXT_JPG_UPPER_CASE)) {
                    Bundle bundleJPG = new Bundle();
                    bundleJPG.putString(FirebaseAnalytics.Param.ITEM_ID, "JPG");
                    bundleJPG.putString(FirebaseAnalytics.Param.ITEM_NAME, "JPG Image Viewed");
                    firebaseAnalytics.logEvent("JPG_Open", bundleJPG);
                    Intent intentJPG = new Intent(v.getContext(), StatusViewImage.class);
                    intentJPG.putExtra("format", contentList.get(position).getFormat());
                    intentJPG.putExtra("path", contentList.get(position).getPath());
                    intentJPG.putExtra("from", "MainActivity");
                    v.getContext().startActivity(intentJPG);
                }
            });

            holder.btnShare.setOnClickListener(v -> {
                Bundle bundleShare = new Bundle();
                bundleShare.putString(FirebaseAnalytics.Param.ITEM_ID, "Share");
                bundleShare.putString(FirebaseAnalytics.Param.ITEM_NAME, "User Shared Something");
                firebaseAnalytics.logEvent("Share_Open", bundleShare);
                Utilities.shareFile(v.getContext(), contentList, position);
            });

            holder.btnShareWhatsApp.setOnClickListener(v -> {
                Bundle bundleWhatsApp = new Bundle();
                bundleWhatsApp.putString(FirebaseAnalytics.Param.ITEM_ID, "ShareWhatsApp");
                bundleWhatsApp.putString(FirebaseAnalytics.Param.ITEM_NAME, "User Shared Something on WhatsApp");
                firebaseAnalytics.logEvent("WhatsApp_Share_Open", bundleWhatsApp);
                Utilities.shareToWhatsApp(v.getContext(), contentList, position);
            });

            holder.btnDownload.setOnClickListener(v -> {
                File statusDirectory = new File(PathUtils.getExternalStoragePath(), v.getContext().getResources().getString(R.string.app_name));

                if (!statusDirectory.exists()) {
                    if (statusDirectory.mkdirs()) {
                        Log.v(TAG, getClass().getSimpleName() + "-> The directory has been created: " + statusDirectory);
                    } else {
                        Log.v(TAG, getClass().getSimpleName() + "-> Could not create the directory for some unknown reason");
                    }
                } else {
                    Log.v(TAG, getClass().getSimpleName() + "-> This directory has already been created");
                }

                if (source.getAbsolutePath().endsWith(EXT_MP4_LOWER_CASE) || source.getAbsolutePath().endsWith(EXT_MP4_UPPER_CASE)) {
                    File destPathMP4 = new File(PathUtils.getExternalStoragePath() + directoryAndFileName + EXT_MP4_LOWER_CASE);
                    Bundle bundleDownloadMP4 = new Bundle();
                    bundleDownloadMP4.putString(FirebaseAnalytics.Param.ITEM_ID, "DownloadMP4");
                    bundleDownloadMP4.putString(FirebaseAnalytics.Param.ITEM_NAME, "User Download MP4 Status");
                    firebaseAnalytics.logEvent("Download_MP4_Open", bundleDownloadMP4);
                    new MoveFiles(v.getContext(), destPathMP4, holder.progressBar).execute(source);
                    contentList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeRemoved(position, getItemCount());
                    recyclerView.scrollToPosition(position);
                } else {
                    Log.v(TAG, getClass().getSimpleName() + "-> MP4: No data was discovered and saved");
                }

                if (source.getAbsolutePath().endsWith(EXT_JPG_LOWER_CASE) || source.getAbsolutePath().endsWith(EXT_JPG_UPPER_CASE)) {
                    File destPathJPG = new File(PathUtils.getExternalStoragePath() + directoryAndFileName + EXT_JPG_LOWER_CASE);
                    Bundle bundleDownloadJPG = new Bundle();
                    bundleDownloadJPG.putString(FirebaseAnalytics.Param.ITEM_ID, "DownloadJPG");
                    bundleDownloadJPG.putString(FirebaseAnalytics.Param.ITEM_NAME, "User Download JPG Status");
                    firebaseAnalytics.logEvent("Download_JPG_Open", bundleDownloadJPG);
                    new MoveFiles(v.getContext(), destPathJPG, holder.progressBar).execute(source);
                    contentList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeRemoved(position, getItemCount());
                    recyclerView.scrollToPosition(position);
                } else {
                    Log.v(TAG, getClass().getSimpleName() + "-> JPG: No data was discovered and saved");
                }
            });
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_item, parent, false);
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

        private class ViewHolder extends RecyclerView.ViewHolder {

            final FloatingActionButton btnPlay;
            final ImageView btnDelete;
            final ImageView btnDownload;
            final ImageView btnShare;
            final ImageView btnShareWhatsApp;
            final ImageView ivThumbnails;
            final ProgressBar progressBar;
            final RelativeLayout relativeLayout;
            final TextView showSize;
            final TextView showType;

            private ViewHolder(View v) {
                super(v);
                btnDelete = v.findViewById(R.id.ivDelete);
                btnDownload = v.findViewById(R.id.ivDownload);
                btnPlay = v.findViewById(R.id.buttonPlay);
                btnShare = v.findViewById(R.id.ivShare);
                btnShareWhatsApp = v.findViewById(R.id.ivWhatsapp);
                ivThumbnails = v.findViewById(R.id.ivImage);
                progressBar = v.findViewById(R.id.progressBar);
                relativeLayout = v.findViewById(R.id.layoutStatus);
                showSize = v.findViewById(R.id.tvSize);
                showType = v.findViewById(R.id.tvType);
            }
        }
    }
}