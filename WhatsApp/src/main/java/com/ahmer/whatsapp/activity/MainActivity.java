package com.ahmer.whatsapp.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmer.afzal.utils.HelperUtils;
import com.ahmer.afzal.utils.constants.AppPackageConstants;
import com.ahmer.afzal.utils.utilcode.AppUtils;
import com.ahmer.afzal.utils.utilcode.FileUtils;
import com.ahmer.afzal.utils.utilcode.PathUtils;
import com.ahmer.afzal.utils.utilcode.ThreadUtils;
import com.ahmer.afzal.utils.utilcode.ToastUtils;
import com.ahmer.whatsapp.Helper;
import com.ahmer.whatsapp.MediaScanner;
import com.ahmer.whatsapp.R;
import com.ahmer.whatsapp.StatusItem;
import com.ahmer.whatsapp.databinding.ActivityMainBinding;
import com.ahmer.whatsapp.databinding.StatusItemActivityBinding;
import com.ahmer.whatsapp.view.StatusViewImage;
import com.ahmer.whatsapp.view.StatusViewVideo;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import static com.ahmer.whatsapp.Constant.EXT_JPG_LOWER_CASE;
import static com.ahmer.whatsapp.Constant.EXT_JPG_UPPER_CASE;
import static com.ahmer.whatsapp.Constant.EXT_MP4_LOWER_CASE;
import static com.ahmer.whatsapp.Constant.EXT_MP4_UPPER_CASE;
import static com.ahmer.whatsapp.Constant.TAG;

public class MainActivity extends AppCompatActivity {

    private final ArrayList<StatusItem> contentList = new ArrayList<>();
    private ActivityMainBinding binding = null;
    private AdView adView = null;
    private RecyclerView recyclerView = null;
    private StatusVideoAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menuSettings) {
                Intent intentSettings = new Intent(getApplicationContext(), SettingsActivity.class);
                intentSettings.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    intentSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(intentSettings);
            } else if (item.getItemId() == R.id.menuInfo) {
                Intent intentAbout = new Intent(getApplicationContext(), AhmerActivity.class);
                intentAbout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    intentAbout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(intentAbout);
            }
            return false;
        });
        adView = binding.adView;
        recyclerView = binding.rvStatusList;
        contentList.addAll(SplashActivity.bothStatuses);
        Collections.sort(contentList, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        loadData();
    }

    private void loadData() {
        Helper.loadAds(adView, binding.adViewLayout);
        GridLayoutManager gridLayoutManager;
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 720) {
            gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
            Log.v(TAG, getClass().getSimpleName() + " -> Screen width: " + config.smallestScreenWidthDp);
        } else {
            gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        }
        gridLayoutManager.isAutoMeasureEnabled();
        gridLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.getRecycledViewPool().clear();
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new StatusVideoAdapter();
        recyclerView.setAdapter(adapter);
        RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (!(AppUtils.isAppInstalled(AppPackageConstants.PKG_WHATSAPP) || AppUtils.isAppInstalled(AppPackageConstants.PKG_WHATSAPP_BUSINESS)
                        || AppUtils.isAppInstalled(AppPackageConstants.PKG_WHATSAPP_FM) || AppUtils.isAppInstalled(AppPackageConstants.PKG_WHATSAPP_YO))) {
                    binding.layoutNoStatus.setVisibility(View.VISIBLE);
                    binding.tvNoStatus.setText(R.string.no_whatsapp_installed);
                } else {
                    if (adapter.getItemCount() == 0) {
                        binding.layoutNoStatus.setVisibility(View.VISIBLE);
                        binding.tvNoStatus.setText(R.string.no_having_status);
                    } else {
                        binding.layoutNoStatus.setVisibility(View.GONE);
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
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (adView != null) {
            adView.pause();
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

    public class StatusVideoAdapter extends RecyclerView.Adapter<StatusVideoAdapter.ViewHolder> {

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.ivThumbnails.setImageBitmap(contentList.get(position).getThumbnails());
            holder.relativeLayout.setBackgroundColor(Color.WHITE);
            holder.relativeLayout.setAlpha(0);
            holder.showSize.setText(HelperUtils.getFileSize(contentList.get(position).getSize()));
            File source = new File(contentList.get(position).getPath());

            if (contentList.get(position).getFormat().endsWith(EXT_MP4_LOWER_CASE) ||
                    contentList.get(position).getFormat().endsWith(EXT_MP4_UPPER_CASE)) {
                holder.btnPlay.setVisibility(View.VISIBLE);
                holder.relativeLayout.setClickable(false);
                final String mp4 = "MP4";
                holder.showType.setText(mp4);
            }

            if (contentList.get(position).getFormat().endsWith(EXT_JPG_LOWER_CASE) ||
                    contentList.get(position).getFormat().endsWith(EXT_JPG_UPPER_CASE)) {
                holder.btnPlay.setVisibility(View.GONE);
                holder.relativeLayout.setClickable(true);
                final String jpg = "JPG";
                holder.showType.setText(jpg);
            }

            holder.relativeLayout.setOnClickListener(v -> {
                if (contentList.get(position).getFormat().endsWith(EXT_MP4_LOWER_CASE) ||
                        contentList.get(position).getFormat().endsWith(EXT_MP4_UPPER_CASE)) {
                    Intent intentVideo = new Intent(v.getContext(), StatusViewVideo.class);
                    intentVideo.putExtra("format", contentList.get(position).getFormat());
                    intentVideo.putExtra("path", contentList.get(position).getPath());
                    intentVideo.putExtra("from", "MainActivity");
                    v.getContext().startActivity(intentVideo);
                }

                if (contentList.get(position).getFormat().endsWith(EXT_JPG_LOWER_CASE) ||
                        contentList.get(position).getFormat().endsWith(EXT_JPG_UPPER_CASE)) {
                    Intent intentJPG = new Intent(v.getContext(), StatusViewImage.class);
                    intentJPG.putExtra("format", contentList.get(position).getFormat());
                    intentJPG.putExtra("path", contentList.get(position).getPath());
                    intentJPG.putExtra("from", "MainActivity");
                    v.getContext().startActivity(intentJPG);
                }
            });

            holder.btnShare.setOnClickListener(v -> Helper.shareFile(v.getContext(), contentList, position));
            holder.btnShareWhatsApp.setOnClickListener(v -> Helper.shareToWhatsApp(v.getContext(), contentList, position));
            holder.btnDownload.setOnClickListener(v -> {
                if (source.getAbsolutePath().endsWith(EXT_MP4_LOWER_CASE) || source.getAbsolutePath().endsWith(EXT_MP4_UPPER_CASE)) {
                    File destPathMP4 = new File(PathUtils.getExternalStoragePath() +
                            Helper.saveToWithFileName(source.getAbsolutePath()) + EXT_MP4_LOWER_CASE);
                    FileUtils.move(source, destPathMP4);
                    ThreadUtils.runOnUiThread(() -> {
                        ToastUtils.showLong(getString(R.string.status_saved) + "\n" + destPathMP4.getPath());
                        new MediaScanner(v.getContext(), destPathMP4);
                    });
                    contentList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeRemoved(position, getItemCount());
                    recyclerView.scrollToPosition(position);
                    SplashActivity.bothStatuses.remove(position);
                }

                if (source.getAbsolutePath().endsWith(EXT_JPG_LOWER_CASE) || source.getAbsolutePath().endsWith(EXT_JPG_UPPER_CASE)) {
                    File destPathJPG = new File(PathUtils.getExternalStoragePath() +
                            Helper.saveToWithFileName(source.getAbsolutePath()) + EXT_JPG_LOWER_CASE);
                    FileUtils.move(source, destPathJPG);
                    ThreadUtils.runOnUiThread(() -> {
                        ToastUtils.showLong(getString(R.string.status_saved) + "\n" + destPathJPG.getPath());
                        new MediaScanner(v.getContext(), destPathJPG);
                    });
                    contentList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeRemoved(position, getItemCount());
                    recyclerView.scrollToPosition(position);
                    SplashActivity.bothStatuses.remove(position);
                }
            });
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            StatusItemActivityBinding binding = StatusItemActivityBinding.inflate(inflater, parent, false);
            return new ViewHolder(binding);
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
            final ImageView btnDownload;
            final ImageView btnShare;
            final ImageView btnShareWhatsApp;
            final ImageView ivThumbnails;
            final RelativeLayout relativeLayout;
            final TextView showSize;
            final TextView showType;

            private ViewHolder(@NonNull StatusItemActivityBinding binding) {
                super(binding.getRoot());
                btnDownload = binding.ivDownload;
                btnPlay = binding.buttonPlay;
                btnShare = binding.ivShare;
                btnShareWhatsApp = binding.ivWhatsApp;
                ivThumbnails = binding.ivImage;
                relativeLayout = binding.layoutStatus;
                showSize = binding.tvSize;
                showType = binding.tvType;
            }
        }
    }
}