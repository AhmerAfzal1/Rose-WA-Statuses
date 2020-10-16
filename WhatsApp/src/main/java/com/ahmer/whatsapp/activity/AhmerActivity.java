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

import androidx.annotation.NonNull;
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
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;

public class AhmerActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private ActivityAhmerBinding binding = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAhmerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
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
    public void onClick(@NonNull View v) {
        if (v.getId() == R.id.toolbar) {
            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        } else if (v.getId() == R.id.idAhmerGmail || v.getId() == R.id.idAhmerYahoo) {
            HelperUtils.appShareOrEmail(this, false, null, getString(R.string.app_name));
        } else if (v.getId() == R.id.ivFacebook || v.getId() == R.id.tvFacebook) {
            String idFB = getString(R.string.url_facebook) + getString(R.string.id_facebook_ahmer);
            if (NetworkUtils.isConnected()) {
                if (AppUtils.isAppInstalled(AppPackageConstants.PKG_FACEBOOK)) {
                    Intent intentFB = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/100025917301113"));
                    intentFB.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        intentFB.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    startActivity(intentFB);
                } else {
                    HelperUtils.openCustomTabs(v.getContext(), idFB);
                }
            } else {
                HelperUtils.showNoInternetSnack(AhmerActivity.this);
            }
        } else if (v.getId() == R.id.ivTwitter || v.getId() == R.id.tvTwitter) {
            if (NetworkUtils.isConnected()) {
                if (AppUtils.isAppInstalled(AppPackageConstants.PKG_TWITTER)) {
                    try {
                        Intent intentTwitter = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("twitter://user?screen_name=" + getString(R.string.id_twitter_ahmer)));
                        intentTwitter.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            intentTwitter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        startActivity(intentTwitter);
                    } catch (Exception e) {
                        Intent intentTwitter1 = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://twitter.com/#!/" + getString(R.string.id_twitter_ahmer)));
                        intentTwitter1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            intentTwitter1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        startActivity(intentTwitter1);
                        ThrowableUtils.getFullStackTrace(e);
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }
                } else {
                    HelperUtils.openCustomTabs(v.getContext(), getString(R.string.url_twitter));
                }
            } else {
                HelperUtils.showNoInternetSnack(AhmerActivity.this);
            }
        } else if (v.getId() == R.id.ivGithub || v.getId() == R.id.tvGithub) {
            if (NetworkUtils.isConnected()) {
                String url = getString(R.string.url_github) + getString(R.string.id_github_ahmer) + "/";
                HelperUtils.openCustomTabs(v.getContext(), url);
            } else {
                HelperUtils.showNoInternetSnack(AhmerActivity.this);
            }
        } else if (v.getId() == R.id.ivBlogspot || v.getId() == R.id.tvBlogspot) {
            if (NetworkUtils.isConnected()) {
                HelperUtils.openCustomTabs(v.getContext(), getString(R.string.url_blogspot_ahmer));
            } else {
                HelperUtils.showNoInternetSnack(AhmerActivity.this);
            }
        }
    }

    @Override
    public boolean onLongClick(@NonNull View v) {
        if (v.getId() == R.id.idAhmerQQ) {
            HelperUtils.clipBoardCopied(v.getContext(), binding.idAhmerQQ);
        } else if (v.getId() == R.id.idAhmerGmail) {
            HelperUtils.clipBoardCopied(v.getContext(), binding.idAhmerGmail);
        } else if (v.getId() == R.id.idAhmerYahoo) {
            HelperUtils.clipBoardCopied(v.getContext(), binding.idAhmerYahoo);
        } else if (v.getId() == R.id.tvFacebook) {
            HelperUtils.clipBoardCopied(v.getContext(), binding.tvFacebook);
        } else if (v.getId() == R.id.tvTwitter) {
            HelperUtils.clipBoardCopied(v.getContext(), binding.tvTwitter);
        } else if (v.getId() == R.id.tvGithub) {
            HelperUtils.clipBoardCopied(v.getContext(), binding.tvGithub);
        } else if (v.getId() == R.id.tvBlogspot) {
            HelperUtils.clipBoardCopied(v.getContext(), binding.tvBlogspot);
        } else if (v.getId() == R.id.imageViewAhmer) {
            String idFB = getString(R.string.url_facebook) + getString(R.string.id_facebook_ahmer);
            if (NetworkUtils.isConnected()) {
                if (AppUtils.isAppInstalled(AppPackageConstants.PKG_FACEBOOK)) {
                    Intent intentFB = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/100025917301113"));
                    intentFB.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        intentFB.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    startActivity(intentFB);
                } else {
                    HelperUtils.openCustomTabs(v.getContext(), idFB);
                }
            } else {
                HelperUtils.showNoInternetSnack(this);
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
