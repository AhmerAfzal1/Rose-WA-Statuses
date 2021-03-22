package com.ahmer.whatsapp.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmer.afzal.utils.constants.AppPackageConstants;
import com.ahmer.afzal.utils.utilcode.AppUtils;
import com.ahmer.whatsapp.Helper;
import com.ahmer.whatsapp.R;
import com.ahmer.whatsapp.StatusItem;
import com.ahmer.whatsapp.databinding.FragmentImagesBinding;
import com.ahmer.whatsapp.databinding.StatusItemFragBinding;
import com.ahmer.whatsapp.view.StatusViewImage;
import com.google.android.gms.ads.AdView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;

import static com.ahmer.whatsapp.Constant.TAG;

public class FragmentImages extends Fragment {

    public static ArrayList<StatusItem> statusItemFile = null;
    private static ImagesAdapter adapter = null;
    private static RecyclerView recyclerViewImages = null;
    private AdView adView = null;
    private FragmentImagesBinding binding = null;

    public FragmentImages() {
        // Required empty public constructor
    }

    public static void updateList(int position) {
        statusItemFile.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeRemoved(position, adapter.getItemCount());
        recyclerViewImages.scrollToPosition(position);
        SplashActivity.imageStatuses.remove(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_images, container, false);
        binding = FragmentImagesBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        statusItemFile = new ArrayList<>(SplashActivity.imageStatuses);
        recyclerViewImages = binding.rvImages;
        adView = binding.adView;
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        GridLayoutManager gridLayoutManager;
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 720) {
            gridLayoutManager = new GridLayoutManager(getContext(), 3);
            Log.v(TAG, getClass().getSimpleName() + " -> Screen width: " + config.smallestScreenWidthDp);
        } else {
            gridLayoutManager = new GridLayoutManager(getContext(), 2);
        }
        gridLayoutManager.isAutoMeasureEnabled();
        gridLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerViewImages.getRecycledViewPool().clear();
        recyclerViewImages.setHasFixedSize(true);
        recyclerViewImages.setNestedScrollingEnabled(false);
        recyclerViewImages.setLayoutManager(gridLayoutManager);
        adapter = new ImagesAdapter(statusItemFile);
        recyclerViewImages.setAdapter(adapter);
        RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (!(AppUtils.isAppInstalled(AppPackageConstants.PKG_WHATSAPP)
                        || AppUtils.isAppInstalled(AppPackageConstants.PKG_WHATSAPP_BUSINESS)
                        || AppUtils.isAppInstalled(AppPackageConstants.PKG_WHATSAPP_FM)
                        || AppUtils.isAppInstalled(AppPackageConstants.PKG_WHATSAPP_YO))) {
                    binding.layoutNoStatus.setVisibility(View.VISIBLE);
                    binding.tvNoStatus.setText(R.string.no_whatsapp_installed);
                } else {
                    if (FragmentImages.adapter.getItemCount() == 0) {
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
        Helper.loadAds(adView, binding.adViewLayout);
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
        statusItemFile.clear();
    }

    public static class ImagesAdapter extends RecyclerView.Adapter<ImageViewHolder> {

        private final ArrayList<StatusItem> statusItem;

        public ImagesAdapter(ArrayList<StatusItem> list) {
            statusItem = list;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            StatusItemFragBinding binding = StatusItemFragBinding.inflate(inflater, parent, false);
            return new ImageViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            holder.imageView.setImageBitmap(statusItem.get(position).getThumbnails());
            holder.imageView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), StatusViewImage.class);
                intent.putExtra("path", statusItem.get(position).getPath());
                intent.putExtra("format", statusItem.get(position).getFormat());
                intent.putExtra("from", "Fragment");
                intent.putExtra("pos", position);
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
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        final ImageView imageView;

        public ImageViewHolder(@NonNull StatusItemFragBinding binding) {
            super(binding.getRoot());
            imageView = binding.ivImagesView;
        }
    }
}
