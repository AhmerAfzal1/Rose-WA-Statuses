package com.ahmer.whatsapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmer.afzal.utils.constants.AppPackageConstants;
import com.ahmer.afzal.utils.utilcode.AppUtils;
import com.ahmer.whatsapp.Constant;
import com.ahmer.whatsapp.R;
import com.ahmer.whatsapp.StatusItem;
import com.ahmer.whatsapp.view.StatusViewImage;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;

import static com.google.android.gms.ads.AdRequest.ERROR_CODE_INTERNAL_ERROR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_INVALID_REQUEST;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NETWORK_ERROR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL;

public class FragmentImages extends Fragment {

    public static ArrayList<StatusItem> statusItemFile = null;
    private static ImagesAdapter adapter = null;
    private static RecyclerView recyclerViewImages = null;
    private AdView adView = null;
    private FirebaseAnalytics firebaseAnalytics = null;
    private FirebaseCrashlytics firebaseCrashlytics = null;
    private LinearLayout adViewLayout = null;
    private RelativeLayout noStatusLayout = null;
    private TextView noStatus = null;

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
        return inflater.inflate(R.layout.fragment_images, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        statusItemFile = new ArrayList<>(SplashActivity.imageStatuses);
        recyclerViewImages = view.findViewById(R.id.rvImages);
        noStatus = view.findViewById(R.id.tvNoStatus);
        noStatusLayout = view.findViewById(R.id.layoutNoStatus);
        adView = view.findViewById(R.id.adView);
        adViewLayout = view.findViewById(R.id.adViewLayout);
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext());
        firebaseCrashlytics = FirebaseCrashlytics.getInstance();
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        firebaseCrashlytics.log("Start " + getClass().getSimpleName() + " Crashlytics logging...");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
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
                if (!(AppUtils.isAppInstalled(AppPackageConstants.PKG_WHATSAPP) || AppUtils.isAppInstalled(AppPackageConstants.PKG_BUSINESS_WHATSAPP)
                        || AppUtils.isAppInstalled(AppPackageConstants.PKG_FM_WhatsApp) || AppUtils.isAppInstalled(AppPackageConstants.PKG_Yo_WhatsApp))) {
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
        loadAds();
    }

    private void loadAds() {
        MobileAds.initialize(getContext(), initializationStatus -> {
            //Keep empty
        });
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                adViewLayout.setVisibility(View.VISIBLE);
                Log.v(Constant.TAG, getResources().getString(R.string.adLoaded));
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                adViewLayout.setVisibility(View.GONE);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseAnalytics.setCurrentScreen(requireActivity(), "CurrentScreen: " + getClass().getSimpleName(), null);
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (statusItemFile != null) {
            statusItemFile.clear();
        }
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
            this.statusItem = list;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_item_frag, parent, false);
            return new ImageViewHolder(view);
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

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivImagesView);
        }
    }
}
