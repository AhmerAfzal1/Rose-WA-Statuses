package com.ahmer.whatsapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import com.ahmer.afzal.utils.Utilities;
import com.ahmer.afzal.utils.constants.AppPackageConstants;
import com.ahmer.afzal.utils.utilcode.AppUtils;
import com.ahmer.afzal.utils.utilcode.NetworkUtils;
import com.ahmer.afzal.utils.utilcode.PathUtils;
import com.ahmer.whatsapp.Constant;
import com.ahmer.whatsapp.EmailIntent;
import com.ahmer.whatsapp.R;
import com.ahmer.whatsapp.Thumbnails;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.ahmer.whatsapp.Constant.TAG;

public class AhmerActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private FirebaseAnalytics firebaseAnalytics;
    private TextView emailGmail;
    private TextView emailYahoo;
    private TextView qq;
    private TextView tvBlogSpot;
    private TextView tvFacebook;
    private TextView tvGithub;
    private TextView tvTwitter;

    public static File dirAhmer() {
        File dir = new File(PathUtils.getExternalAppDataPath(), Constant.FOLDER_AHMER);
        if (!dir.exists()) {
            boolean mkdir = dir.mkdir();
            if (!mkdir) {
                Log.v(TAG, Thumbnails.class.getSimpleName() + "-> New folder for " + dir + " is not created.");
            }
        }
        return dir;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ahmer);
        FirebaseCrashlytics firebaseCrashlytics = FirebaseCrashlytics.getInstance();
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        firebaseCrashlytics.log("Start " + getClass().getSimpleName() + " Crashlytics logging...");
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getClass().getSimpleName());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Ahmer Activity Opened");
        firebaseAnalytics.logEvent("Ahmer_Activity_Open", bundle);
        ImageView backIV = findViewById(R.id.ivBack);
        backIV.setOnClickListener(this);
        TextView aboutDeveloper = findViewById(R.id.tvTitle);
        aboutDeveloper.setText(getString(R.string.about_dev));
        emailYahoo = findViewById(R.id.idAhmerYahoo);
        emailYahoo.setOnClickListener(this);
        emailYahoo.setOnLongClickListener(this);
        emailGmail = findViewById(R.id.idAhmerGmail);
        emailGmail.setOnClickListener(this);
        emailGmail.setOnLongClickListener(this);
        qq = findViewById(R.id.idAhmerQQ);
        qq.setOnLongClickListener(this);
        ImageView ivFacebook = findViewById(R.id.ivFacebook);
        ivFacebook.setOnClickListener(this);
        tvFacebook = findViewById(R.id.tvFacebook);
        tvFacebook.setOnClickListener(this);
        tvFacebook.setOnLongClickListener(this);
        ImageView ivTwitter = findViewById(R.id.ivTwitter);
        ivTwitter.setOnClickListener(this);
        tvTwitter = findViewById(R.id.tvTwitter);
        tvTwitter.setOnClickListener(this);
        tvTwitter.setOnLongClickListener(this);
        ImageView ivGithub = findViewById(R.id.ivGithub);
        ivGithub.setOnClickListener(this);
        tvGithub = findViewById(R.id.tvGithub);
        tvGithub.setOnClickListener(this);
        tvGithub.setOnLongClickListener(this);
        ImageView ivBlogSpot = findViewById(R.id.ivBlogspot);
        ivBlogSpot.setOnClickListener(this);
        tvBlogSpot = findViewById(R.id.tvBlogspot);
        tvBlogSpot.setOnClickListener(this);
        tvBlogSpot.setOnLongClickListener(this);
        File preExistedPic = new File(dirAhmer() + "/" + Constant.FILE_NAME_AHMER + ".png");
        ContentLoadingProgressBar progressBar = findViewById(R.id.progressCircleImageView);
        ImageView loadPic = findViewById(R.id.imageViewAhmer);
        if (!preExistedPic.exists()) {
            DownloadImageTask download = new DownloadImageTask(loadPic, progressBar);
            download.execute(getString(R.string.ahmer_facebook_graph_link));
        } else {
            progressBar.setVisibility(View.GONE);
            Bitmap imageThumbnail = BitmapFactory.decodeFile(preExistedPic.getAbsolutePath());
            loadPic.setImageBitmap(imageThumbnail);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                finish();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                break;

            case R.id.idAhmerGmail:
            case R.id.idAhmerYahoo:
                Intent intentGmail = new Intent(v.getContext(), EmailIntent.class);
                intentGmail.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    intentGmail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(intentGmail);
                break;

            case R.id.ivFacebook:
            case R.id.tvFacebook:
                String idFB = getString(R.string.ahmer_facebook_url) + getString(R.string.ahmer_facebook_id);
                if (NetworkUtils.isConnected()) {
                    if (AppUtils.isAppInstalled(AppPackageConstants.PKG_FACEBOOK)) {
                        Intent intentFB = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/100025917301113"));
                        intentFB.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            intentFB.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        startActivity(intentFB);
                        Bundle facebook = new Bundle();
                        facebook.putString(FirebaseAnalytics.Param.ITEM_ID, "Ahmer Facebook");
                        facebook.putString(FirebaseAnalytics.Param.ITEM_NAME, "Ahmer Facebook ID Opened");
                        firebaseAnalytics.logEvent("Ahmer_FB_Open", facebook);
                    } else {
                        Utilities.openCustomTabs(v.getContext(), idFB);
                    }
                } else {
                    Utilities.showNoInternetSnack(AhmerActivity.this);
                }
                break;

            case R.id.ivTwitter:
            case R.id.tvTwitter:
                if (NetworkUtils.isConnected()) {
                    if (AppUtils.isAppInstalled(AppPackageConstants.PKG_TWITTER)) {
                        try {
                            Intent intentTwitter = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + getString(R.string.ahmer_twitter_id)));
                            intentTwitter.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                intentTwitter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            }
                            startActivity(intentTwitter);
                            Bundle twitter = new Bundle();
                            twitter.putString(FirebaseAnalytics.Param.ITEM_ID, "Ahmer Twitter");
                            twitter.putString(FirebaseAnalytics.Param.ITEM_NAME, "Ahmer Twitter ID Opened");
                            firebaseAnalytics.logEvent("Ahmer_Twitter_Open", twitter);
                        } catch (Exception e) {
                            Intent intentTwitter1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/" + getString(R.string.ahmer_twitter_id)));
                            intentTwitter1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                intentTwitter1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            }
                            startActivity(intentTwitter1);
                            FirebaseCrashlytics.getInstance().recordException(e);
                        }
                    } else {
                        Utilities.openCustomTabs(v.getContext(), getResources().getString(R.string.ahmer_twitter_url));
                    }
                } else {
                    Utilities.showNoInternetSnack(AhmerActivity.this);
                }
                break;

            case R.id.ivGithub:
            case R.id.tvGithub:
                if (NetworkUtils.isConnected()) {
                    String url = getResources().getString(R.string.ahmer_github_url) + getString(R.string.ahmer_github_id) + "/";
                    Utilities.openCustomTabs(v.getContext(), url);
                    Bundle blogSpot = new Bundle();
                    blogSpot.putString(FirebaseAnalytics.Param.ITEM_ID, "Ahmer Github");
                    blogSpot.putString(FirebaseAnalytics.Param.ITEM_NAME, "Ahmer Github Web Link Opened");
                    firebaseAnalytics.logEvent("Ahmer_Github_Open", blogSpot);
                } else {
                    Utilities.showNoInternetSnack(AhmerActivity.this);
                }
                break;

            case R.id.ivBlogspot:
            case R.id.tvBlogspot:
                if (NetworkUtils.isConnected()) {
                    Utilities.openCustomTabs(v.getContext(), getResources().getString(R.string.ahmer_blogspot_url));
                    Bundle blogSpot = new Bundle();
                    blogSpot.putString(FirebaseAnalytics.Param.ITEM_ID, "Ahmer BlogSpot");
                    blogSpot.putString(FirebaseAnalytics.Param.ITEM_NAME, "Ahmer BlogSpot Web Link Opened");
                    firebaseAnalytics.logEvent("Ahmer_BlogSpot_Open", blogSpot);
                } else {
                    Utilities.showNoInternetSnack(AhmerActivity.this);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.idAhmerQQ:
                Utilities.clipBoardCopied(v.getContext(), qq);
                break;

            case R.id.idAhmerGmail:
                Utilities.clipBoardCopied(v.getContext(), emailGmail);
                break;

            case R.id.idAhmerYahoo:
                Utilities.clipBoardCopied(v.getContext(), emailYahoo);

            case R.id.tvFacebook:
                Utilities.clipBoardCopied(v.getContext(), tvFacebook);
                break;

            case R.id.tvTwitter:
                Utilities.clipBoardCopied(v.getContext(), tvTwitter);
                break;

            case R.id.tvGithub:
                Utilities.clipBoardCopied(v.getContext(), tvGithub);
                break;

            case R.id.tvBlogspot:
                Utilities.clipBoardCopied(v.getContext(), tvBlogSpot);
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAnalytics.setCurrentScreen(AhmerActivity.this, "CurrentScreen: " + getClass().getSimpleName(), null);
    }

    private static class DownloadImageTask extends AsyncTask<String, Integer, Bitmap> {

        private final WeakReference<ContentLoadingProgressBar> progressBar;
        private final WeakReference<ImageView> imageView;

        public DownloadImageTask(ImageView imageView, ContentLoadingProgressBar progressBar) {
            this.imageView = new WeakReference<>(imageView);
            this.progressBar = new WeakReference<>(progressBar);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.get().setVisibility(View.VISIBLE);
        }

        protected Bitmap doInBackground(String... urls) {
            final OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(urls[0])
                    .build();

            Response response = null;
            Bitmap bitmap = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (Objects.requireNonNull(response).isSuccessful()) {
                try {
                    bitmap = BitmapFactory.decodeStream(Objects.requireNonNull(response.body()).byteStream());
                } catch (Exception e) {
                    Log.v(Constant.TAG, "Error" + e.getMessage());
                    e.printStackTrace();
                }

            }
            return bitmap;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.get().setProgress(values[0]);
        }

        protected void onPostExecute(Bitmap result) {
            imageView.get().setImageBitmap(result);
            Thumbnails.saveImage(dirAhmer(), result, Constant.FILE_NAME_AHMER);
            progressBar.get().setVisibility(View.GONE);
        }
    }
}
