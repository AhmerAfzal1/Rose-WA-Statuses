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

import static com.ahmer.whatsapp.Constant.IMAGE_HEIGHT;
import static com.ahmer.whatsapp.Constant.IMAGE_WIDTH;
import static com.ahmer.whatsapp.Constant.TAG;

public final class Thumbnails {

    public static Bitmap videoThumbnails(File file) {
        Bitmap bitmap = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                CancellationSignal signal = new CancellationSignal();
                bitmap = ThumbnailUtils.createVideoThumbnail(file, new Size(IMAGE_WIDTH, IMAGE_HEIGHT), signal);
                Log.v(TAG, Thumbnails.class.getSimpleName() + " -> MP4 files" + file.getName());
                signal.throwIfCanceled();
            } else {
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                Log.v(TAG, Thumbnails.class.getSimpleName() + " -> BitmapFactory.decodeFile works");
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

    public static Bitmap imageThumbnails(File file) {
        Bitmap bitmap = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                CancellationSignal signal = new CancellationSignal();
                bitmap = ThumbnailUtils.createImageThumbnail(file, new Size(IMAGE_WIDTH, IMAGE_HEIGHT), signal);
                Log.v(TAG, Thumbnails.class.getSimpleName() + " -> JPG files" + file.getName());
                signal.throwIfCanceled();
            } else {
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                Log.v(TAG, Thumbnails.class.getSimpleName() + " -> BitmapFactory.decodeFile works");
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
}
