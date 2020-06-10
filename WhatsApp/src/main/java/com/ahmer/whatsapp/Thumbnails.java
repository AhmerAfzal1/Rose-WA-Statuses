package com.ahmer.whatsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;

import com.ahmer.afzal.utils.utilcode.ThrowableUtils;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.io.FileNotFoundException;

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
                Log.v(TAG, Thumbnails.class.getSimpleName() + "-> API 29 MP4 Thumbs: " + file.getName());
                signal.throwIfCanceled();
            } else {
                bitmap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                Log.v(TAG, Thumbnails.class.getSimpleName() + "-> API 28 MP4 Thumbs: " + file.getName());
            }
        } catch (OperationCanceledException o) {
            o.printStackTrace();
            ThrowableUtils.getFullStackTrace(o);
            Log.v(TAG, Thumbnails.class.getSimpleName() + "-> OperationCanceledException during generating video thumbnails: " + o.getMessage());
            FirebaseCrashlytics.getInstance().recordException(o);
        } catch (FileNotFoundException f) {
            f.printStackTrace();
            ThrowableUtils.getFullStackTrace(f);
            Log.v(TAG, Thumbnails.class.getSimpleName() + "-> File not found: " + f.getMessage());
            FirebaseCrashlytics.getInstance().recordException(f);
        } catch (Exception e) {
            e.printStackTrace();
            ThrowableUtils.getFullStackTrace(e);
            Log.v(TAG, Thumbnails.class.getSimpleName() + "-> Error during generating video thumbnails: " + e.getMessage());
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
                Log.v(TAG, Thumbnails.class.getSimpleName() + "-> API 29 JPG Thumbs: " + file.getName());
                signal.throwIfCanceled();
            } else {
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                bitmap = getBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT);
                Log.v(TAG, Thumbnails.class.getSimpleName() + "-> API 28 JPG Thumbs: " + file.getName());
            }
        } catch (OperationCanceledException o) {
            o.printStackTrace();
            ThrowableUtils.getFullStackTrace(o);
            Log.v(TAG, Thumbnails.class.getSimpleName() + "-> OperationCanceledException during generating image thumbnails: " + o.getMessage());
            FirebaseCrashlytics.getInstance().recordException(o);
        } catch (FileNotFoundException f) {
            f.printStackTrace();
            ThrowableUtils.getFullStackTrace(f);
            Log.v(TAG, Thumbnails.class.getSimpleName() + "-> File not found: " + f.getMessage());
            FirebaseCrashlytics.getInstance().recordException(f);
        } catch (Exception e) {
            e.printStackTrace();
            ThrowableUtils.getFullStackTrace(e);
            Log.v(TAG, Thumbnails.class.getSimpleName() + "-> Error during generating image thumbnails: " + e.getMessage());
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        return bitmap;
    }

    public static Bitmap getBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        // Resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);

        //Rotate is needed here because somehow the bitmap factory is getting my image rotated
        matrix.postRotate(-90);

        // Recreate the new bitmap
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }
}