package com.ahmer.whatsapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ahmer.afzal.utils.HelperUtils;
import com.ahmer.afzal.utils.constants.AppPackageConstants;
import com.ahmer.afzal.utils.utilcode.AppUtils;
import com.ahmer.afzal.utils.utilcode.NetworkUtils;
import com.ahmer.afzal.utils.utilcode.PathUtils;
import com.ahmer.afzal.utils.utilcode.ThrowableUtils;
import com.ahmer.whatsapp.Constant;
import com.ahmer.whatsapp.R;
import com.ahmer.whatsapp.Thumbnails;
import com.ahmer.whatsapp.databinding.ActivityAhmerBinding;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.ahmer.whatsapp.Constant.TAG;

public class AhmerActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private ActivityAhmerBinding binding = null;
    private FirebaseAnalytics firebaseAnalytics = null;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAhmerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseCrashlytics firebaseCrashlytics = FirebaseCrashlytics.getInstance();
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        firebaseCrashlytics.log("Start " + getClass().getSimpleName() + " Crashlytics logging...");
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getClass().getSimpleName());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Ahmer Activity Opened");
        firebaseAnalytics.logEvent("Ahmer_Activity_Open", bundle);
        binding.idAhmerGmail.setOnClickListener(this);
        binding.idAhmerGmail.setOnLongClickListener(this);
        binding.idAhmerQQ.setOnLongClickListener(this);
        binding.idAhmerYahoo.setOnClickListener(this);
        binding.idAhmerYahoo.setOnLongClickListener(this);
        binding.ivBack.setOnClickListener(this);
        binding.ivBlogspot.setOnClickListener(this);
        binding.ivFacebook.setOnClickListener(this);
        binding.ivGithub.setOnClickListener(this);
        binding.ivTwitter.setOnClickListener(this);
        binding.tvBlogspot.setOnClickListener(this);
        binding.tvBlogspot.setOnLongClickListener(this);
        binding.tvFacebook.setOnClickListener(this);
        binding.tvFacebook.setOnLongClickListener(this);
        binding.tvGithub.setOnClickListener(this);
        binding.tvGithub.setOnLongClickListener(this);
        binding.tvTitle.setText(getResources().getString(R.string.about_dev));
        binding.tvTwitter.setOnClickListener(this);
        binding.tvTwitter.setOnLongClickListener(this);
        File preExistedPic = new File(dirAhmer() + "/" + Constant.FILE_NAME_AHMER + ".png");
        if (!preExistedPic.exists()) {
            if (NetworkUtils.isConnected()) {
                final Bitmap[] bitmap = {null};
                Observable.fromCallable(() -> {
                    Request request = new Request.Builder()
                            .url(getResources().getString(R.string.ahmer_facebook_graph_link))
                            .build();
                    OkHttpClient okHttpClient = new OkHttpClient();
                    try {
                        return okHttpClient.newCall(request).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                        ThrowableUtils.getFullStackTrace(e);
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }
                    return null;
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableObserver<Response>() {
                            @Override
                            public void onNext(@NonNull Response response) {
                                bitmap[0] = BitmapFactory.decodeStream(Objects.requireNonNull(response.body()).byteStream());
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                e.printStackTrace();
                                ThrowableUtils.getFullStackTrace(e);
                                FirebaseCrashlytics.getInstance().recordException(e);
                                Log.v(TAG, getClass().getSimpleName() + " -> Exception: " + e.getMessage());
                                binding.progressCircleImageView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onComplete() {
                                Thumbnails.saveImage(dirAhmer(), bitmap[0], Constant.FILE_NAME_AHMER);
                                binding.imageViewAhmer.setImageBitmap(bitmap[0]);
                                binding.progressCircleImageView.setVisibility(View.GONE);
                            }
                        });
            } else {
                HelperUtils.showNoInternetSnack(AhmerActivity.this);
                new Handler().postDelayed(() -> binding.progressCircleImageView.setVisibility(View.GONE), 5000);
            }
        } else {
            binding.progressCircleImageView.setVisibility(View.GONE);
            Bitmap imageThumbnail = BitmapFactory.decodeFile(preExistedPic.getAbsolutePath());
            binding.imageViewAhmer.setImageBitmap(imageThumbnail);
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
                HelperUtils.appShareOrEmail(this, false, null, getString(R.string.app_name));
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "EmailAppEvent");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, getClass().getSimpleName());
                firebaseAnalytics.logEvent("Email_App_Open", bundle);
                break;

            case R.id.ivFacebook:
            case R.id.tvFacebook:
                String idFB = getResources().getString(R.string.ahmer_facebook_url) + getResources().getString(R.string.ahmer_facebook_id);
                if (NetworkUtils.isConnected()) {
                    if (AppUtils.isAppInstalled(AppPackageConstants.PKG_FACEBOOK)) {
                        Intent intentFB = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/100025917301113"));
                        intentFB.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            intentFB.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        startActivity(intentFB);
                        Bundle facebook = new Bundle();
                        facebook.putString(FirebaseAnalytics.Param.ITEM_ID, "Ahmer Facebook");
                        facebook.putString(FirebaseAnalytics.Param.ITEM_NAME, "Ahmer Facebook ID Opened");
                        firebaseAnalytics.logEvent("Ahmer_FB_Open", facebook);
                    } else {
                        HelperUtils.openCustomTabs(v.getContext(), idFB);
                    }
                } else {
                    HelperUtils.showNoInternetSnack(AhmerActivity.this);
                }
                break;

            case R.id.ivTwitter:
            case R.id.tvTwitter:
                if (NetworkUtils.isConnected()) {
                    if (AppUtils.isAppInstalled(AppPackageConstants.PKG_TWITTER)) {
                        try {
                            Intent intentTwitter = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + getResources().getString(R.string.ahmer_twitter_id)));
                            intentTwitter.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                intentTwitter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            }
                            startActivity(intentTwitter);
                            Bundle twitter = new Bundle();
                            twitter.putString(FirebaseAnalytics.Param.ITEM_ID, "Ahmer Twitter");
                            twitter.putString(FirebaseAnalytics.Param.ITEM_NAME, "Ahmer Twitter ID Opened");
                            firebaseAnalytics.logEvent("Ahmer_Twitter_Open", twitter);
                        } catch (Exception e) {
                            Intent intentTwitter1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/" + getResources().getString(R.string.ahmer_twitter_id)));
                            intentTwitter1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                intentTwitter1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            }
                            startActivity(intentTwitter1);
                            ThrowableUtils.getFullStackTrace(e);
                            FirebaseCrashlytics.getInstance().recordException(e);
                        }
                    } else {
                        HelperUtils.openCustomTabs(v.getContext(), getResources().getString(R.string.ahmer_twitter_url));
                    }
                } else {
                    HelperUtils.showNoInternetSnack(AhmerActivity.this);
                }
                break;

            case R.id.ivGithub:
            case R.id.tvGithub:
                if (NetworkUtils.isConnected()) {
                    String url = getResources().getString(R.string.ahmer_github_url) + getResources().getString(R.string.ahmer_github_id) + "/";
                    HelperUtils.openCustomTabs(v.getContext(), url);
                    Bundle blogSpot = new Bundle();
                    blogSpot.putString(FirebaseAnalytics.Param.ITEM_ID, "Ahmer Github");
                    blogSpot.putString(FirebaseAnalytics.Param.ITEM_NAME, "Ahmer Github Web Link Opened");
                    firebaseAnalytics.logEvent("Ahmer_Github_Open", blogSpot);
                } else {
                    HelperUtils.showNoInternetSnack(AhmerActivity.this);
                }
                break;

            case R.id.ivBlogspot:
            case R.id.tvBlogspot:
                if (NetworkUtils.isConnected()) {
                    HelperUtils.openCustomTabs(v.getContext(), getResources().getString(R.string.ahmer_blogspot_url));
                    Bundle blogSpot = new Bundle();
                    blogSpot.putString(FirebaseAnalytics.Param.ITEM_ID, "Ahmer BlogSpot");
                    blogSpot.putString(FirebaseAnalytics.Param.ITEM_NAME, "Ahmer BlogSpot Web Link Opened");
                    firebaseAnalytics.logEvent("Ahmer_BlogSpot_Open", blogSpot);
                } else {
                    HelperUtils.showNoInternetSnack(AhmerActivity.this);
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
                HelperUtils.clipBoardCopied(v.getContext(), binding.idAhmerQQ);
                break;

            case R.id.idAhmerGmail:
                HelperUtils.clipBoardCopied(v.getContext(), binding.idAhmerGmail);
                break;

            case R.id.idAhmerYahoo:
                HelperUtils.clipBoardCopied(v.getContext(), binding.idAhmerYahoo);

            case R.id.tvFacebook:
                HelperUtils.clipBoardCopied(v.getContext(), binding.tvFacebook);
                break;

            case R.id.tvTwitter:
                HelperUtils.clipBoardCopied(v.getContext(), binding.tvTwitter);
                break;

            case R.id.tvGithub:
                HelperUtils.clipBoardCopied(v.getContext(), binding.tvGithub);
                break;

            case R.id.tvBlogspot:
                HelperUtils.clipBoardCopied(v.getContext(), binding.tvBlogspot);
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
