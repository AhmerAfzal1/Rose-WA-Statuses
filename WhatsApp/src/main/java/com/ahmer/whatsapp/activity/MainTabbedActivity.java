package com.ahmer.whatsapp.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.ahmer.whatsapp.R;
import com.ahmer.whatsapp.databinding.ActivityTabbedBinding;
import com.google.android.material.tabs.TabLayout;

public class MainTabbedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTabbedBinding binding = ActivityTabbedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(MainTabbedActivity.this,
                getSupportFragmentManager(), 0);
        ViewPager viewPager = binding.getRoot().findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        ImageView settings = binding.ivSettings;
        settings.setOnClickListener(v -> {
            Intent intentSetting = new Intent(v.getContext(), SettingsActivity.class);
            intentSetting.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                intentSetting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            startActivity(intentSetting);
        });
        ImageView info = binding.ivInfo;
        info.setOnClickListener(v -> {
            Intent intentAhmer = new Intent(v.getContext(), AhmerActivity.class);
            intentAhmer.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                intentAhmer.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            startActivity(intentAhmer);
        });
    }
}