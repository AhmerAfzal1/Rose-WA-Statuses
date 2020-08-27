package com.ahmer.whatsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.ahmer.afzal.utils.utilcode.FileUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class Utilities {

    public static void loadAds(Context context, AdView adView, LinearLayout layout) {
        MobileAds.initialize(context, initializationStatus -> {
            //Keep empty
        });
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                layout.setVisibility(View.VISIBLE);
                Log.v(Constant.TAG, context.getResources().getString(R.string.adLoaded));
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.v(Constant.TAG, context.getResources().getString(R.string.adFailedToLoad) + loadAdError.getCode());
                layout.setVisibility(View.GONE);
            }

            @Override
            public void onAdOpened() {
                Log.v(Constant.TAG, context.getResources().getString(R.string.adOpened));
            }

            @Override
            public void onAdLeftApplication() {
                Log.v(Constant.TAG, context.getResources().getString(R.string.adLeftApplication));
            }

            @Override
            public void onAdClosed() {
                Log.v(Constant.TAG, context.getResources().getString(R.string.adClosed));
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public static void shareToWhatsApp(Context context, ArrayList<StatusItem> contentList, int position) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage("com.whatsapp");
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(contentList.get(position).getPath()));
        sendIntent.setType("file/*");
        context.startActivity(Intent.createChooser(sendIntent, "Send Status via:"));
    }

    public static void shareFile(Context context, ArrayList<StatusItem> contentList, int position) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(contentList.get(position).getPath()));
        sendIntent.setType("file/*");
        context.startActivity(Intent.createChooser(sendIntent, "Send Status via:"));
    }

    public static String saveToWithFileName(String path) {
        return Constant.SAVE_TO_WITH_FILE_NAME + FileUtils.getFileNameNoExtension(path);
    }
}
