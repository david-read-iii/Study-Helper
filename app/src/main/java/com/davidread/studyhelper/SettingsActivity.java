package com.davidread.studyhelper;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

/**
 * {@link SettingsActivity} represents a settings user interface.
 */
public class SettingsActivity extends AppCompatActivity {

    /**
     * Callback method invoked when this activity is initially created. It loads a new
     * {@link SettingsFragment} and enables the up button in the app bar.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@link SettingsFragment} represents a settings user interface.
     */
    public static class SettingsFragment extends PreferenceFragmentCompat {

        /**
         * Callback method invoked when this fragment is initially created. It inflates the
         * preferences layout and sets up a preference listener to change the app's day/night mode
         * when its preference is toggled.
         */
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            /* Setup preference change listener on "Dark theme" preference to change the app's
             * day/night mode immediately on change. */
            SwitchPreferenceCompat themePref = findPreference("dark_theme");
            if (themePref != null) {
                themePref.setOnPreferenceChangeListener((preference, newValue) -> {

                    // Turn on or off night mode.
                    if ((Boolean) newValue) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }

                    return true;
                });
            }
        }
    }
}