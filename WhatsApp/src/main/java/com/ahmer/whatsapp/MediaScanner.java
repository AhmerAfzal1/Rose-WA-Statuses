package com.ahmer.whatsapp;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

import java.io.File;

public class MediaScanner implements MediaScannerConnectionClient {

    private MediaScannerConnection scannerConnection;
    private File file;

    public MediaScanner(Context context, File file) {
        this.file = file;
        scannerConnection = new MediaScannerConnection(context, this);
        scannerConnection.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        scannerConnection.scanFile(file.getAbsolutePath(), null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        scannerConnection.disconnect();
    }
}