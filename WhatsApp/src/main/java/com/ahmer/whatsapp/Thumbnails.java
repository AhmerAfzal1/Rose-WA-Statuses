package com.ahmer.whatsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;
import android.util.Log;
import android.util.Size;

import java.io.File;
import java.io.IOException;

import static com.ahmer.whatsapp.ConstantsValues.IMAGE_HEIGHT;
import static com.ahmer.whatsapp.ConstantsValues.IMAGE_WIDTH;
import static com.ahmer.whatsapp.ConstantsValues.TAG;

public final class Thumbnails {

    public static Bitmap videoThumbnails(String path) throws IOException {
        Bitmap photo = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                CancellationSignal signal = new CancellationSignal();
                photo = ThumbnailUtils.createVideoThumbnail(new File(path), new Size(IMAGE_WIDTH, IMAGE_HEIGHT), signal);
                Log.v(TAG, "ThumbnailUtils.createVideoThumbnail works");
                signal.throwIfCanceled();
            } else {
                photo = BitmapFactory.decodeFile(path);
                Log.v(TAG, "BitmapFactory.decodeFile works");
            }
        } catch (OperationCanceledException c) {
            c.printStackTrace();
        }
        return photo;

        //If we want to delete the userPhoto that is not the thumbnail. uncomment below code
        /*File file =  new File(path);
        file.delete();*/
    }


    public static Bitmap imageThumbnails(String path) throws IOException {
        Bitmap photo = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                CancellationSignal signal = new CancellationSignal();
                photo = ThumbnailUtils.createImageThumbnail(new File(path), new Size(IMAGE_WIDTH, IMAGE_HEIGHT), signal);
                Log.v(TAG, "ThumbnailUtils.createImageThumbnail works");
                signal.throwIfCanceled();
            } else {
                photo = BitmapFactory.decodeFile(path);
                Log.v(TAG, "BitmapFactory.decodeFile works");
            }
        } catch (OperationCanceledException c) {
            c.printStackTrace();
        }
        return photo;

        //If we want to delete the userPhoto that is not the thumbnail. uncomment below code
        /*File file =  new File(path);
        file.delete();*/
    }
}
