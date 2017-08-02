package com.example.android.themovieapp.Data;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.example.android.themovieapp.R;

/**
 * Activity with all preferences for the user's query
 */

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
    }

    // Fragment containing the preferences
    public static class MoviePreferencesFragment extends PreferenceFragment implements Preference
            .OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Set the preference file
            addPreferencesFromResource(R.xml.preferences);

            // Select each preference, set the on change listener and set the default value
            Preference orderBy = findPreference(getString(R.string.order_by_key));
            bindPreferenceToValue(orderBy);
        }

        private void bindPreferenceToValue(Preference preference) {
            // Set OnPreferenceChangeListener
            preference.setOnPreferenceChangeListener(this);
            // Get an instance of SharedPreferences
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                    (preference.getContext());
            // Retrieve default value
            String preferenceString = sharedPreferences.getString(preference.getKey(), getString
                    (R.string.order_by_default));
            // Show value
            onPreferenceChange(preference, preferenceString);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            // Store new value
            String stringValue = newValue.toString();
            // If this is a list preference, use values instead of keys to update summary
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            }
            // Set new value as summary
            preference.setSummary(stringValue);
            return true;
        }
    }
}
