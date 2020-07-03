package com.ahmer.whatsapp.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.ahmer.afzal.utils.utilcode.AppUtils;
import com.ahmer.afzal.utils.utilcode.CleanUtils;
import com.ahmer.afzal.utils.utilcode.Utils;
import com.ahmer.whatsapp.Constant;
import com.ahmer.whatsapp.R;
import com.ahmer.whatsapp.Utilities;

import java.io.File;
import java.util.Locale;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager().beginTransaction().replace(R.id.settings, new SettingsFragment()).commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ImageView back = findViewById(R.id.ivBack);
        back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        });
        TextView title = findViewById(R.id.tvTitle);
        title.setText(R.string.title_settings);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private Preference buttonCaches;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings_preferences, rootKey);
            SharedPreferences prefLauncher = this.requireActivity().getSharedPreferences(Constant.PREFERENCE_LAUNCHER, Context.MODE_PRIVATE);
            SwitchPreferenceCompat buttonLauncher = findPreference(getString(R.string.button_change_view));
            Objects.requireNonNull(buttonLauncher).setChecked(prefLauncher.getBoolean(Constant.PREFERENCE_LAUNCHER_KEY, false));
            buttonLauncher.setOnPreferenceChangeListener((preference, newValue) -> {
                SharedPreferences.Editor editorSplash = prefLauncher.edit();
                editorSplash.putBoolean(Constant.PREFERENCE_LAUNCHER_KEY, (Boolean) newValue);
                editorSplash.apply();
                AppUtils.relaunchApp();
                return true;
            });
            SharedPreferences prefDarkMode = requireActivity().getSharedPreferences(Constant.PREFERENCE_DARK_MODE, Context.MODE_PRIVATE);
            SwitchPreferenceCompat darkMode = findPreference(getString(R.string.button_dark_mode));
            Objects.requireNonNull(darkMode).setChecked(prefDarkMode.getBoolean(Constant.PREFERENCE_DARK_MODE_KEY, false));
            darkMode.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isChecked = (Boolean) newValue;
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                SharedPreferences.Editor editorDarkMode = prefDarkMode.edit();
                editorDarkMode.putBoolean(Constant.PREFERENCE_DARK_MODE_KEY, isChecked);
                editorDarkMode.apply();
                //AppUtils.relaunchApp();
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
                    "App Version: %s(%d)", AppUtils.getAppVersionName(), AppUtils.getAppVersionCode()));
            initializeCache();
        }

        private void initializeCache() {
            long size = 0;
            size += getDirSize(Utils.getApp().getCacheDir());
            size += getDirSize(Objects.requireNonNull(Utils.getApp().getExternalCacheDir()));
            buttonCaches.setSummary("Caches Size " + Utilities.getFileSize(size));
        }

        private long getDirSize(File dir) {
            long size = 0;
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (file != null && file.isDirectory()) {
                    size += getDirSize(file);
                } else if (file != null && file.isFile()) {
                    size += file.length();
                }
            }
            return size;
        }
    }
}