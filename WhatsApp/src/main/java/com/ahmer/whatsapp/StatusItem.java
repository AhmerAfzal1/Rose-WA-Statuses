package com.ahmer.whatsapp;

import android.graphics.Bitmap;

public class StatusItem {

    private Bitmap mThumbnails;
    private long mSize;
    private String mFormat;
    private String mPath;

    public String getFormat() {
        return mFormat;
    }

    public void setFormat(String format) {
        mFormat = format;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long size) {
        mSize = size;
    }

    public Bitmap getThumbnails() {
        return mThumbnails;
    }

    public void setThumbnails(Bitmap thumbnails) {
        mThumbnails = thumbnails;
    }
}