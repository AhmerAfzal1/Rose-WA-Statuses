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

import static com.ahmer.whatsapp.ConstantsValues.IMAGE_HEIGHT;
import static com.ahmer.whatsapp.ConstantsValues.IMAGE_WIDTH;
import static com.ahmer.whatsapp.ConstantsValues.TAG;

public final class Thumbnails {

    private static final String LOG_THUMBNAILS = "ThumbnailUtils.createVideoThumbnail works";
    private static final String LOG_BITMAP = "BitmapFactory.decodeFile works";

    public static Bitmap videoThumbnails(String path) {
        Bitmap bitmap = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                CancellationSignal signal = new CancellationSignal();
                bitmap = ThumbnailUtils.createVideoThumbnail(new File(path), new Size(IMAGE_WIDTH, IMAGE_HEIGHT), signal);
                Log.v(TAG, Thumbnails.class.getSimpleName() + " -> " + LOG_THUMBNAILS);
                signal.throwIfCanceled();
            } else {
                bitmap = BitmapFactory.decodeFile(path);
                Log.v(TAG, Thumbnails.class.getSimpleName() + " -> " + LOG_BITMAP);
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


    public static Bitmap imageThumbnails(String path) {
        Bitmap bitmap = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                CancellationSignal signal = new CancellationSignal();
                bitmap = ThumbnailUtils.createImageThumbnail(new File(path), new Size(IMAGE_WIDTH, IMAGE_HEIGHT), signal);
                Log.v(TAG, Thumbnails.class.getSimpleName() + " -> " + LOG_THUMBNAILS);
                signal.throwIfCanceled();
            } else {
                bitmap = BitmapFactory.decodeFile(path);
                Log.v(TAG, Thumbnails.class.getSimpleName() + " -> " + LOG_BITMAP);
            }
        } catch (OperationCanceledException o) {
            o.printStackTrace();
            ThrowableUtils.getFullStackTrace(o);
            Log.v(TAG, Thumbnails.class.getSimpleName() + " -> OperationCanceledException during generating images thumbnails: " + o.getMessage());
            FirebaseCrashlytics.getInstance().recordException(o);
        } catch (Exception e) {
            e.printStackTrace();
            ThrowableUtils.getFullStackTrace(e);
            Log.v(TAG, Thumbnails.class.getSimpleName() + " -> Error during generating images thumbnails: " + e.getMessage());
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        return bitmap;
    }
}
