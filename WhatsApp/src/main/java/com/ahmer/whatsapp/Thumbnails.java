package com.ahmer.whatsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;
import android.util.Log;
import android.util.Size;

import com.ahmer.afzal.utils.utilcode.ThrowableUtils;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.net.URLConnection;

import static com.ahmer.whatsapp.Constant.EXT_GIF_LOWER_CASE;
import static com.ahmer.whatsapp.Constant.EXT_GIF_UPPER_CASE;
import static com.ahmer.whatsapp.Constant.EXT_JPG_LOWER_CASE;
import static com.ahmer.whatsapp.Constant.EXT_JPG_UPPER_CASE;
import static com.ahmer.whatsapp.Constant.EXT_MP4_LOWER_CASE;
import static com.ahmer.whatsapp.Constant.EXT_MP4_UPPER_CASE;
import static com.ahmer.whatsapp.Constant.IMAGE_HEIGHT;
import static com.ahmer.whatsapp.Constant.IMAGE_WIDTH;
import static com.ahmer.whatsapp.Constant.TAG;

public final class Thumbnails {

    public static Bitmap getThumbnails(File file) {
        Bitmap bitmap = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                CancellationSignal signal = new CancellationSignal();
                if (file.getName().endsWith(EXT_MP4_LOWER_CASE) || file.getName().endsWith(EXT_MP4_UPPER_CASE)) {
                    bitmap = ThumbnailUtils.createVideoThumbnail(file, new Size(IMAGE_WIDTH, IMAGE_HEIGHT), signal);
                    Log.v(TAG, Thumbnails.class.getSimpleName() + " -> MP4 files" + file.getName());
                }
                if (file.getName().endsWith(EXT_JPG_LOWER_CASE) || file.getName().endsWith(EXT_JPG_UPPER_CASE)) {
                    bitmap = ThumbnailUtils.createImageThumbnail(file, new Size(IMAGE_WIDTH, IMAGE_HEIGHT), signal);
                    Log.v(TAG, Thumbnails.class.getSimpleName() + " -> JPG files" + file.getName());
                }
                if (file.getName().endsWith(EXT_GIF_LOWER_CASE) || file.getName().endsWith(EXT_GIF_UPPER_CASE)) {
                    bitmap = ThumbnailUtils.createImageThumbnail(file, new Size(IMAGE_WIDTH, IMAGE_HEIGHT), signal);
                    Log.v(TAG, Thumbnails.class.getSimpleName() + " -> GIF files" + file.getName());
                }
                signal.throwIfCanceled();
            } else {
                if (file.getName().endsWith(EXT_MP4_LOWER_CASE) || file.getName().endsWith(EXT_MP4_UPPER_CASE) ||
                        file.getName().endsWith(EXT_JPG_LOWER_CASE) || file.getName().endsWith(EXT_JPG_UPPER_CASE) ||
                        file.getName().endsWith(EXT_GIF_LOWER_CASE) || file.getName().endsWith(EXT_GIF_UPPER_CASE)) {
                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    Log.v(TAG, Thumbnails.class.getSimpleName() + " -> BitmapFactory.decodeFile works");

                }
            }
        } catch (OperationCanceledException o) {
            o.printStackTrace();
            ThrowableUtils.getFullStackTrace(o);
            Log.v(TAG, Thumbnails.class.getSimpleName() + " -> OperationCanceledException during generating videos thumbnails: " + o.getMessage());
            FirebaseCrashlytics.getInstance().recordException(o);
        } catch (Exception e) {
            e.printStackTrace();
            ThrowableUtils.getFullStackTrace(e);
            Log.v(TAG, Thumbnails.class.getSimpleName() + " -> Error during generating videos thumbnails: " + e.getMessage());
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        return bitmap;
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }
}
