package com.ahmer.whatsapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ahmer.afzal.utils.HelperUtils;
import com.ahmer.afzal.utils.constants.AppPackageConstants;
import com.ahmer.afzal.utils.utilcode.AppUtils;
import com.ahmer.afzal.utils.utilcode.NetworkUtils;
import com.ahmer.afzal.utils.utilcode.ThrowableUtils;
import com.ahmer.whatsapp.Constant;
import com.ahmer.whatsapp.Helper;
import com.ahmer.whatsapp.R;
import com.ahmer.whatsapp.databinding.ActivityAhmerBinding;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;

public class AhmerActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private ActivityAhmerBinding binding = null;
    private FirebaseAnalytics firebaseAnalytics = null;

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
        binding.toolbar.setOnClickListener(this);
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
        binding.tvTwitter.setOnClickListener(this);
        binding.tvTwitter.setOnLongClickListener(this);
        File preExistedPic = new File(Helper.dirAhmer() + "/" + Constant.FILE_NAME_AHMER + ".png");
        if (!preExistedPic.exists()) {
            downloadPic();
        } else {
            binding.progressCircleImageView.setVisibility(View.GONE);
            Bitmap imageThumbnail = BitmapFactory.decodeFile(preExistedPic.getAbsolutePath());
            binding.imageViewAhmer.setImageBitmap(imageThumbnail);
            long lastSyncWas = Helper.getLastSyncWithTimeSpanByNow(Constant.PREFERENCE_SYNC_AHMER,
                    Constant.PREFERENCE_SYNC_KEY_AHMER);
            // getTimeSpanByNow return minus value
            if (lastSyncWas < 0) {
                if (NetworkUtils.isConnected()) {
                    boolean isOldPicDeleted = preExistedPic.delete();
                    if (isOldPicDeleted) {
                        downloadPic();
                    } else {
                        Log.v(Constant.TAG, getClass().getSimpleName() + " -> Could not deleted for some unknown reason");
                    }
                } else {
                    Log.v(Constant.TAG, getClass().getSimpleName() + " ->  There's no working Internet connection");
                }
            } else {
                Log.v(Constant.TAG, getClass().getSimpleName() + " -> Value is zero");
            }
        }
        binding.imageViewAhmer.setOnClickListener(v -> downloadPic());
        binding.imageViewAhmer.setOnLongClickListener(this);
    }

    private void downloadPic() {
        binding.progressCircleImageView.setVisibility(View.VISIBLE);
        if (NetworkUtils.isConnected()) {
            Helper.setLastSync(Constant.PREFERENCE_SYNC_AHMER, Constant.PREFERENCE_SYNC_KEY_AHMER);
            new Helper.DownloadPic(binding.imageViewAhmer, binding.progressCircleImageView,
                    Constant.FILE_NAME_AHMER)
                    .execute(getString(R.string.graph_link_facebook_ahmer));
        } else {
            HelperUtils.showNoInternetSnack(this);
            new Handler(Looper.getMainLooper()).postDelayed(() ->
                    binding.progressCircleImageView.setVisibility(View.GONE), 5000);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar:
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
                String idFB = getResources().getString(R.string.url_facebook) + getResources().getString(R.string.id_facebook_ahmer);
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
                            Intent intentTwitter = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + getResources().getString(R.string.id_twitter_ahmer)));
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
                            Intent intentTwitter1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/" + getResources().getString(R.string.id_twitter_ahmer)));
                            intentTwitter1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                intentTwitter1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            }
                            startActivity(intentTwitter1);
                            ThrowableUtils.getFullStackTrace(e);
                            FirebaseCrashlytics.getInstance().recordException(e);
                        }
                    } else {
                        HelperUtils.openCustomTabs(v.getContext(), getResources().getString(R.string.url_twitter));
                    }
                } else {
                    HelperUtils.showNoInternetSnack(AhmerActivity.this);
                }
                break;

            case R.id.ivGithub:
            case R.id.tvGithub:
                if (NetworkUtils.isConnected()) {
                    String url = getResources().getString(R.string.url_github) + getResources().getString(R.string.id_github_ahmer) + "/";
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
                    HelperUtils.openCustomTabs(v.getContext(), getResources().getString(R.string.url_blogspot_ahmer));
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

            case R.id.imageViewAhmer:
                String idFB = getString(R.string.url_facebook) + getString(R.string.id_facebook_ahmer);
                if (NetworkUtils.isConnected()) {
                    if (AppUtils.isAppInstalled(AppPackageConstants.PKG_FACEBOOK)) {
                        Intent intentFB = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/100025917301113"));
                        intentFB.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            intentFB.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                    HelperUtils.showNoInternetSnack(this);
                }
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
