package com.ahmer.whatsapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.widget.ContentLoadingProgressBar;

import com.ahmer.afzal.utils.async.AsyncTask;
import com.ahmer.afzal.utils.constants.TimeConstants;
import com.ahmer.afzal.utils.utilcode.FileUtils;
import com.ahmer.afzal.utils.utilcode.ImageUtils;
import com.ahmer.afzal.utils.utilcode.PathUtils;
import com.ahmer.afzal.utils.utilcode.SPUtils;
import com.ahmer.afzal.utils.utilcode.ThrowableUtils;
import com.ahmer.afzal.utils.utilcode.TimeUtils;
import com.ahmer.afzal.utils.utilcode.ToastUtils;
import com.ahmer.afzal.utils.utilcode.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.ahmer.whatsapp.Constant.TAG;

public class Helper {

    public static void loadAds(@NotNull AdView adView, LinearLayout layout) {
        MobileAds.initialize(Utils.getApp(), initializationStatus -> {
            //Keep empty
        });
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                layout.setVisibility(View.VISIBLE);
                Log.v(Constant.TAG, getClass().getSimpleName() + " -> "
                        + Utils.getApp().getString(R.string.adLoaded));
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.v(Constant.TAG, getClass().getSimpleName() + " -> " + Utils.getApp()
                        .getString(R.string.adFailedToLoad) + loadAdError.getCode());
                layout.setVisibility(View.GONE);
            }

            @Override
            public void onAdOpened() {
                Log.v(Constant.TAG, getClass().getSimpleName() + " -> "
                        + Utils.getApp().getString(R.string.adOpened));
            }

            @Override
            public void onAdClosed() {
                Log.v(Constant.TAG, getClass().getSimpleName() + " -> "
                        + Utils.getApp().getString(R.string.adClosed));
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public static void shareToWhatsApp(@NonNull Context context, @NonNull ArrayList<StatusItem> contentList, int position) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage("com.whatsapp");
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(contentList.get(position).getPath()));
        sendIntent.setType("file/*");
        context.startActivity(Intent.createChooser(sendIntent, "Send Status via:"));
    }

    public static void shareFile(@NonNull Context context, @NonNull ArrayList<StatusItem> contentList, int position) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(contentList.get(position).getPath()));
        sendIntent.setType("file/*");
        context.startActivity(Intent.createChooser(sendIntent, "Send Status via:"));
    }

    @NonNull
    public static String saveToWithFileName(String path) {
        return Constant.SAVE_TO_WITH_FILE_NAME + FileUtils.getFileNameNoExtension(path);
    }

    @NotNull
    public static File dirAhmer() {
        File dir = new File(PathUtils.getExternalAppDataPath(), Constant.FOLDER_AHMER);
        if (!dir.exists()) {
            if (dir.mkdir()) {
                Log.v(TAG, Thumbnails.class.getSimpleName() + " -> The directory has been created: " + dir);
            } else {
                Log.v(TAG, Thumbnails.class.getSimpleName() + " -> Could not create the directory for some unknown reason");
            }
        } else {
            Log.v(TAG, Thumbnails.class.getSimpleName() + " -> This directory has already been created");
        }
        return dir;
    }

    public static void setLastSync(String syncValue, String syncKey) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        String lastSyncDate = dateFormat.format(date);
        SPUtils saveSyncDate = SPUtils.getInstance(syncValue);
        saveSyncDate.put(syncKey, lastSyncDate);
    }

    public static long getLastSyncWithTimeSpanByNow(String syncValue, String syncKey) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        SPUtils getSyncDate = SPUtils.getInstance(syncValue);
        String lastSyncDateWas = getSyncDate.getString(syncKey);
        return TimeUtils.getTimeSpanByNow(lastSyncDateWas, dateFormat, TimeConstants.DAY);
    }

    public static class DownloadPic extends AsyncTask<String, Integer, Bitmap> {

        private final ImageView imageView;
        private final ContentLoadingProgressBar progressBar;
        private final String fileName;

        public DownloadPic(ImageView imageView, ContentLoadingProgressBar progressBar, String fileName) {
            this.imageView = imageView;
            this.progressBar = progressBar;
            this.fileName = fileName;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String s) throws Exception {
            Bitmap bitmap = null;
            String link = "";
            OkHttpClient clientProfile = new OkHttpClient();
            Request requestProfile = new Request.Builder()
                    .url(Constant.PROFILE_SERVER)
                    .get()
                    .build();
            Call callProfile = clientProfile.newCall(requestProfile);
            Response responseProfile = callProfile.execute();
            if (responseProfile.isSuccessful()) {
                String result = Objects.requireNonNull(responseProfile.body()).string();
                Log.v(Constant.TAG, getClass().getSimpleName() + " -> Result is: " + result);
                if (TextUtils.isEmpty(result)) {
                    Log.v(Constant.TAG, getClass().getSimpleName() + " -> Result is null");
                } else {
                    JSONObject object = new JSONObject(Objects.requireNonNull(result));
                    link = object.getString(s);
                    Log.v(Constant.TAG, getClass().getSimpleName() + " -> " + link);
                }
            } else {
                callProfile.cancel();
            }

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(link)
                    .get()
                    .build();
            Call call = client.newCall(request);
            Response response = call.execute();
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body != null) {
                    bitmap = BitmapFactory.decodeStream(body.byteStream());
                } else {
                    bitmap = BitmapFactory.decodeStream(null);
                    ToastUtils.showLong(Utils.getApp().getString(R.string.update_app_server_not_found));
                }
            } else {
                call.cancel();
            }
            return bitmap;
        }

        @Override
        protected void publishProgress(Integer integer) {
            super.publishProgress(integer);
            progressBar.setProgress(integer);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            File path = new File(Helper.dirAhmer() + "/" + fileName + "." + Bitmap.CompressFormat.PNG);
            ImageUtils.save(bitmap, path, Bitmap.CompressFormat.PNG, 100);
            imageView.setImageBitmap(bitmap);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected void onBackgroundError(@NonNull Exception e) {
            e.printStackTrace();
            ThrowableUtils.getFullStackTrace(e);
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.v(Constant.TAG, Helper.class.getSimpleName() + " -> Exception: " + e.getMessage());
            progressBar.setVisibility(View.GONE);
        }
    }
}
