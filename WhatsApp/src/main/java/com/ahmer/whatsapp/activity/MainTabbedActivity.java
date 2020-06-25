package com.ahmer.whatsapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.ahmer.whatsapp.R;
import com.google.android.material.tabs.TabLayout;

public class MainTabbedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(MainTabbedActivity.this,
                getSupportFragmentManager(), 0);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        ImageView settings = findViewById(R.id.ivSettings);
        settings.setOnClickListener(v-> startActivity(new Intent(MainTabbedActivity.this, SettingsActivity.class)));
    }
}