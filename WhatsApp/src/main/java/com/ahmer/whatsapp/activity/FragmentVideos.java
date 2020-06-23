package com.ahmer.whatsapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmer.afzal.utils.constants.AppPackageConstants;
import com.ahmer.afzal.utils.utilcode.AppUtils;
import com.ahmer.afzal.utils.utilcode.FileUtils;
import com.ahmer.afzal.utils.utilcode.ThrowableUtils;
import com.ahmer.whatsapp.R;
import com.ahmer.whatsapp.StatusItem;
import com.ahmer.whatsapp.Thumbnails;
import com.ahmer.whatsapp.view.StatusViewVideo;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import static com.ahmer.whatsapp.Constant.EXT_MP4_LOWER_CASE;
import static com.ahmer.whatsapp.Constant.EXT_MP4_UPPER_CASE;
import static com.ahmer.whatsapp.Constant.TAG;

public class FragmentVideos extends Fragment {

    public final ArrayList<StatusItem> statusItemFile = new ArrayList<>();
    public RecyclerView recyclerViewVideos = null;
    public VideosAdapter adapter = null;
    private TextView noStatus = null;
    private RelativeLayout noStatusLayout = null;

    public FragmentVideos() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_videos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewVideos = view.findViewById(R.id.rvVideos);
        noStatus = view.findViewById(R.id.tvNoStatus);
        noStatusLayout = view.findViewById(R.id.layoutNoStatus);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.isAutoMeasureEnabled();
        gridLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerViewVideos.getRecycledViewPool().clear();
        recyclerViewVideos.setHasFixedSize(true);
        recyclerViewVideos.setNestedScrollingEnabled(false);
        recyclerViewVideos.setLayoutManager(gridLayoutManager);
        adapter = new VideosAdapter(statusItemFile, recyclerViewVideos, adapter);
        recyclerViewVideos.setAdapter(adapter);
        try {
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
            ThrowableUtils.getFullStackTrace(e);
            Log.v(TAG, getClass().getSimpleName() + "-> Error during loading data: " + e.getMessage());
            FirebaseCrashlytics.getInstance().recordException(e);
        }
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
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                onChanged();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                onChanged();
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                onChanged();
            }
        };
        adapter.registerAdapterDataObserver(observer);
        observer.onChanged();
    }

    private void loadData() {
        /*
        File moviesFolder = new File(PathUtils.getExternalStoragePath() + "/AhmerFolder");
        //File moviesFolder = new File(PathUtils.getExternalStoragePath() + "/FMWhatsApp");
        Log.v(TAG, getClass().getSimpleName() + moviesFolder.getAbsolutePath());
        if (moviesFolder.exists()) {
            getStatuses(moviesFolder.listFiles());
        }
       */
        if (SplashActivity.dirWhatsApp.exists()) {
            getStatuses(SplashActivity.dirWhatsApp.listFiles());
        }
        if (SplashActivity.dirBusinessWhatsApp.exists()) {
            getStatuses(SplashActivity.dirBusinessWhatsApp.listFiles());
        }
        if (SplashActivity.dirFMWhatsApp.exists()) {
            getStatuses(SplashActivity.dirFMWhatsApp.listFiles());
        }
        if (SplashActivity.dirYoWhatsApp.exists()) {
            getStatuses(SplashActivity.dirYoWhatsApp.listFiles());
        }

    }

    private void getStatuses(File[] filesList) {
        if (filesList != null) {
            for (File file : filesList) {
                getImagesStatus(file);
            }
        }
    }

    private void getImagesStatus(File file) {
        String filePath = file.getAbsolutePath();
        String fileName = FileUtils.getFileNameNoExtension(file.getName());
        File preExistedThumbnails = new File(Thumbnails.thumbnailDir() + "/" + fileName + ".png");
        if (filePath.endsWith(EXT_MP4_LOWER_CASE) || filePath.endsWith(EXT_MP4_UPPER_CASE)) {
            StatusItem item = new StatusItem();
            if (file.getName().endsWith(EXT_MP4_LOWER_CASE) || file.getName().endsWith(EXT_MP4_UPPER_CASE)) {
                item.setPath(file.getAbsolutePath());
                item.setSize(file.length());
                item.setFormat(EXT_MP4_LOWER_CASE);
                if (!preExistedThumbnails.exists()) {
                    Log.v(TAG, getClass().getSimpleName() + "-> First time generate thumbnails for images");
                    Bitmap jpg = Thumbnails.imageThumbnails(file);
                    item.setThumbnails(jpg);
                    Thumbnails.saveImage(jpg, FileUtils.getFileNameNoExtension(file.getName()));
                } else {
                    Log.v(TAG, getClass().getSimpleName() + "-> Load pre-existed thumbnails for images");
                    Bitmap imageThumbnail = BitmapFactory.decodeFile(preExistedThumbnails.getAbsolutePath());
                    item.setThumbnails(imageThumbnail);
                }
            }
            statusItemFile.add(item);
        }
    }

    @Override
    public void onDestroyView() {
        recyclerViewVideos.swapAdapter(null, true);
        statusItemFile.clear();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (recyclerViewVideos != null) {
            recyclerViewVideos.swapAdapter(null, true);
        }
        statusItemFile.clear();
        super.onDestroy();
    }

    public static class VideosAdapter extends RecyclerView.Adapter<VideoViewHolder> {

        final HashSet<VideoViewHolder> holders;
        private final ArrayList<StatusItem> statusItem;
        private final RecyclerView recyclerView;
        private final VideosAdapter adapter;

        public VideosAdapter(ArrayList<StatusItem> list, RecyclerView rv, VideosAdapter adapter) {
            this.holders = new HashSet<>();
            this.statusItem = list;
            this.recyclerView = rv;
            this.adapter = adapter;
        }

        @NonNull
        @Override
        public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_item_frag, parent, false);
            return new FragmentVideos.VideoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
            holder.imageView.setImageBitmap(statusItem.get(position).getThumbnails());
            holder.imageView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), StatusViewVideo.class);
                intent.putExtra("path", statusItem.get(position).getPath());
                intent.putExtra("format", statusItem.get(position).getFormat());
                intent.putExtra("from", "Fragment");
                v.getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getItemCount() {
            return statusItem.size();
        }

        public void updateList() {
            int position = 0;
            for (VideoViewHolder holder : holders) {
                position = holder.getBindingAdapterPosition();
            }
            statusItem.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeRemoved(position, getItemCount());
            recyclerView.scrollToPosition(position);
            adapter.notifyDataSetChanged();
        }
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {

        final ImageView imageView;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivImagesView);
        }
    }
}
