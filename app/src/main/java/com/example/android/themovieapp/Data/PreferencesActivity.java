package com.example.android.themovieapp.Data;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.view.MenuItem;

import com.example.android.themovieapp.R;

/**
 * Activity with all preferences for the user's query
 */

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Fragment containing the preferences
    public static class MoviePreferencesFragment extends PreferenceFragmentCompat implements
            SharedPreferences.OnSharedPreferenceChangeListener {


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            // Add the xml file containing all the preferences
            addPreferencesFromResource(R.xml.preferences);

            // Get an instance of shared preferences and preference screen
            SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
            PreferenceScreen preferenceScreen = getPreferenceScreen();

            // Count all the present preferences
            int prefCount = preferenceScreen.getPreferenceCount();

            // Iterate through all preferences
            for (int i = 0; i < prefCount; i++) {
                // Get the current preference and its value
                Preference currentPreference = preferenceScreen.getPreference(i);
                String value = sharedPreferences.getString(currentPreference.getKey(), "");
                // Set the summary
                setPreferenceSummary(currentPreference, value);
            }
        }

        /**
         * Method used to show the value of a preference. Can be updated for more types of
         * preferences like list and checkbox
         */
        private void setPreferenceSummary(Preference preference, String value) {
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int listIndex = listPreference.findIndexOfValue(value);
                listPreference.setSummary(listPreference.getEntries()[listIndex]);
            } else {
                // Set summary
                preference.setSummary(value);
            }
        }

        /**
         * Method called by the listener that sets the summary when a value is changed
         */
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference preference = findPreference(key);
            String value = sharedPreferences.getString(key, "");
            setPreferenceSummary(preference, key);
        }

        /**
         * Override onStart and onStop to register and uRegister the listener
         */
        @Override
        public void onStart() {
            super.onStart();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener
                    (this);
        }

        @Override
        public void onStop() {
            super.onStop();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }
    }
}
