package com.ahmer.whatsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import static com.ahmer.whatsapp.ConstantsValues.JPG;

public class WAImageStatusView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whatsapp_image_view);
        ImageView imageView = findViewById(R.id.image);
        String format = getIntent().getStringExtra("format");
        String path = getIntent().getStringExtra("path");
        if (Objects.requireNonNull(format).equals(JPG)) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            Log.v(ConstantsValues.TAG, "Path is: " + bitmap);
            imageView.setImageBitmap(bitmap);
        }
    }
}
