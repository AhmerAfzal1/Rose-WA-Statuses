package com.ahmer.whatsapp.activity;

import android.content.Context;
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
import com.ahmer.afzal.utils.utilcode.PathUtils;
import com.ahmer.afzal.utils.utilcode.ThrowableUtils;
import com.ahmer.whatsapp.R;
import com.ahmer.whatsapp.StatusItem;
import com.ahmer.whatsapp.Thumbnails;
import com.ahmer.whatsapp.view.StatusViewImage;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.util.ArrayList;

import static com.ahmer.whatsapp.Constant.EXT_JPG_LOWER_CASE;
import static com.ahmer.whatsapp.Constant.EXT_JPG_UPPER_CASE;
import static com.ahmer.whatsapp.Constant.TAG;

public class FragmentImages extends Fragment {

    private static final ArrayList<StatusItem> statusItemFile = new ArrayList<>();
    private RecyclerView recyclerViewImages = null;
    private ImagesAdapter adapter = null;
    private TextView noStatus = null;
    private RelativeLayout noStatusLayout = null;
    private final RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
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

    public FragmentImages() {
        // Required empty public constructor
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
        recyclerViewImages = view.findViewById(R.id.rvImages);
        noStatus = view.findViewById(R.id.tvNoStatus);
        noStatusLayout = view.findViewById(R.id.layoutNoStatus);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.isAutoMeasureEnabled();
        gridLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerViewImages.getRecycledViewPool().clear();
        recyclerViewImages.setHasFixedSize(true);
        recyclerViewImages.setNestedScrollingEnabled(false);
        recyclerViewImages.setLayoutManager(gridLayoutManager);
        adapter = new ImagesAdapter(getContext());
        try {
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
            ThrowableUtils.getFullStackTrace(e);
            Log.v(TAG, getClass().getSimpleName() + "-> Error during loading data: " + e.getMessage());
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    private void loadData() {

        File moviesFolder = new File(PathUtils.getExternalStoragePath() + "/AhmerFolder");
        //File moviesFolder = new File(PathUtils.getExternalStoragePath() + "/FMWhatsApp");
        Log.v(TAG, getClass().getSimpleName() + moviesFolder.getAbsolutePath());
        if (moviesFolder.exists()) {
            getStatuses(moviesFolder.listFiles());
        }
       /*
        if (dirWhatsApp.exists()) {
            getStatuses(dirWhatsApp.listFiles());
        }
        if (dirBusinessWhatsApp.exists()) {
            getStatuses(dirBusinessWhatsApp.listFiles());
        }
        if (dirFMWhatsApp.exists()) {
            getStatuses(dirFMWhatsApp.listFiles());
        }
        if (dirYoWhatsApp.exists()) {
            getStatuses(dirYoWhatsApp.listFiles());
        }*/
        recyclerViewImages.setAdapter(adapter);
        adapter.registerAdapterDataObserver(observer);
        observer.onChanged();
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
        if (filePath.endsWith(EXT_JPG_LOWER_CASE) || filePath.endsWith(EXT_JPG_UPPER_CASE)) {
            StatusItem item = new StatusItem();
            if (file.getName().endsWith(EXT_JPG_LOWER_CASE) || file.getName().endsWith(EXT_JPG_UPPER_CASE)) {
                item.setPath(file.getAbsolutePath());
                item.setSize(file.length());
                item.setFormat(EXT_JPG_LOWER_CASE);
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

    public static class ImagesAdapter extends RecyclerView.Adapter<ImageViewHolder> {

        private Context context;

        public ImagesAdapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_fragment_image, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            holder.imageView.setImageBitmap(statusItemFile.get(position).getThumbnails());
            holder.imageView.setOnClickListener(v -> {
                Intent intent = new Intent(context, StatusViewImage.class);
                intent.putExtra("path", statusItemFile.get(position).getPath());
                intent.putExtra("format", statusItemFile.get(position).getFormat());
                intent.putExtra("from", "Fragment");
                context.startActivity(intent);
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
            return statusItemFile.size();
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
