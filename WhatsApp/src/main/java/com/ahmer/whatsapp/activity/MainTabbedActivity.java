package com.ahmer.whatsapp.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmer.whatsapp.R;
import com.ahmer.whatsapp.databinding.ActivityTabbedBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainTabbedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTabbedBinding binding = ActivityTabbedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(this);
        adapter.addFragment(new FragmentImages());
        adapter.addFragment(new FragmentVideos());
        binding.viewPager.setAdapter(adapter);
        new TabLayoutMediator(binding.tabs, binding.viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(getResources().getString(R.string.tab_text_1));
                    break;

                case 1:
                    tab.setText(getResources().getString(R.string.tab_text_2));
                    break;
            }
        }).attach();
        binding.toolbarAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuSettings:
                    Intent intentSetting = new Intent(getApplicationContext(), SettingsActivity.class);
                    intentSetting.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        intentSetting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    startActivity(intentSetting);
                    break;

                case R.id.menuInfo:
                    Intent intentAhmer = new Intent(getApplicationContext(), AhmerActivity.class);
                    intentAhmer.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        intentAhmer.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    startActivity(intentAhmer);
                    break;

                default:
                    break;
            }
            return false;
        });
    }
}