package com.ahmer.whatsapp;

import android.graphics.Bitmap;

public class StatusItem {

    private Bitmap mThumbnails = null;
    private long mSize = 0L;
    private String mFormat = null;
    private String mName = null;
    private String mPath = null;

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

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
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