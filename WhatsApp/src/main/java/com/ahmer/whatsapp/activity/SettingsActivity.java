package com.ahmer.whatsapp.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.ahmer.afzal.utils.HelperUtils;
import com.ahmer.afzal.utils.utilcode.AppUtils;
import com.ahmer.afzal.utils.utilcode.CleanUtils;
import com.ahmer.afzal.utils.utilcode.SPUtils;
import com.ahmer.afzal.utils.utilcode.Utils;
import com.ahmer.whatsapp.Constant;
import com.ahmer.whatsapp.R;
import com.ahmer.whatsapp.databinding.ActivitySettingsBinding;

import java.io.File;
import java.util.Locale;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySettingsBinding binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportFragmentManager().beginTransaction().replace(R.id.settings, new SettingsFragment()).commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        });
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private Preference buttonCaches;

        private static long getDirSize(@NonNull File dir) {
            long size = 0L;
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (file != null && file.isDirectory()) {
                    size += getDirSize(file);
                } else if (file != null && file.isFile()) {
                    size += file.length();
                }
            }
            return size;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            setDivider(new ColorDrawable(Color.TRANSPARENT));
            setDividerHeight(0);
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings_pref, rootKey);
            SPUtils prefLauncher = SPUtils.getInstance(Constant.PREFERENCE_LAUNCHER);
            SwitchPreferenceCompat buttonLauncher = findPreference(getString(R.string.button_change_view));
            Objects.requireNonNull(buttonLauncher).setOnPreferenceChangeListener((preference, newValue) -> {
                prefLauncher.put(Constant.PREFERENCE_LAUNCHER_KEY, (Boolean) newValue);
                AppUtils.relaunchApp();
                return true;
            });
            SPUtils prefTheme = SPUtils.getInstance(Constant.PREFERENCE_THEME);
            SwitchPreferenceCompat buttonTheme = findPreference(getString(R.string.button_dark_mode));
            if (Objects.requireNonNull(buttonTheme).isChecked()) {
                buttonTheme.setTitle(R.string.title_light_mode);
                buttonTheme.setIcon(R.drawable.ic_settings_sun);
                buttonTheme.setSummary(R.string.summary_light_mode);
            } else {
                buttonTheme.setTitle(R.string.title_dark_mode);
                buttonTheme.setIcon(R.drawable.ic_settings_moon);
                buttonTheme.setSummary(R.string.summary_dark_mode);
            }
            buttonTheme.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isChecked = (Boolean) newValue;
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                prefTheme.put(Constant.PREFERENCE_THEME_KEY, isChecked);
                return true;
            });
            buttonCaches = findPreference(getString(R.string.button_clear_caches));
            Objects.requireNonNull(buttonCaches).setOnPreferenceClickListener(preference -> {
                CleanUtils.cleanExternalCache();
                CleanUtils.cleanInternalCache();
                CleanUtils.cleanInternalFiles();
                initializeCache();
                return true;
            });
            Preference versionApp = findPreference(getString(R.string.button_about));
            Objects.requireNonNull(versionApp).setSummary(String.format(Locale.getDefault(),
                    "App Version: %s (%d)", AppUtils.getAppVersionName(), AppUtils.getAppVersionCode()));
            initializeCache();
        }

        private void initializeCache() {
            long size = 0L;
            size += getDirSize(Utils.getApp().getCacheDir());
            size += getDirSize(Objects.requireNonNull(Utils.getApp().getExternalCacheDir()));
            buttonCaches.setSummary("Caches Size " + HelperUtils.getFileSize(size));
        }
    }
}