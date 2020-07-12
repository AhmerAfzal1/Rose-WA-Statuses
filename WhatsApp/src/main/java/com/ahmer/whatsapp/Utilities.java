package com.ahmer.whatsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.ahmer.afzal.utils.utilcode.FileUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Utilities {

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

    public static String getFileSize(long size) {
        if (size <= 0) return "0 Bytes";
        final String[] units = new String[]{"Bytes", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String saveToWithFileName(String path) {
        return Constant.SAVE_TO_WITH_FILE_NAME + FileUtils.getFileNameNoExtension(path);
    }
}
