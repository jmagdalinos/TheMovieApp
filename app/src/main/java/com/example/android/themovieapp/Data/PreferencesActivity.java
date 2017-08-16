package com.example.android.themovieapp.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.view.MenuItem;

import com.example.android.themovieapp.R;
import com.example.android.themovieapp.Sync.MovieSyncUtilities;

import java.util.Arrays;

/**
 * Activity with all preferences for the user's queries
 */

public class PreferencesActivity extends AppCompatActivity {
    /** Tag to be used for logging messages */
    private static final String TAG = PreferenceActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    /** Implement back button functionality */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /** Fragment containing the preferences */
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
                // Check if the current preference is a check box
                if (!(currentPreference instanceof CheckBoxPreference)) {
                    String value = sharedPreferences.getString(currentPreference.getKey(), "");
                    // Set the summary
                    setPreferenceSummary(currentPreference, value);
                }
            }
        }

        /** Method used to show the value of a preference */
        private void setPreferenceSummary(Preference preference, String value) {
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int listIndex = listPreference.findIndexOfValue(value);
                if (listIndex >= 0) {
                    listPreference.setSummary(listPreference.getEntries()[listIndex]);
                }
            } else {
                // Set summary
                preference.setSummary(value);
            }
        }

        /** Method called by the listener that sets the summary when a value is changed */
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // Get the changed preference
            Preference preference = findPreference(key);
            if (preference != null) {
                // Check if the current preference is a check box
                if (!(preference instanceof CheckBoxPreference)) {
                    String value = sharedPreferences.getString(key, "");
                    setPreferenceSummary(preference, value);
                }
            }

            // Check if the preference is the auto sync data preference
            if (preference.getKey().equals(getContext().getResources().getString(R.string
                    .sync_periodically_key))) {
                if (getAutoSyncPreference(getContext())) {
                    // Schedule the sync job
                    MovieSyncUtilities.scheduleFirebaseJobDispatcher(getContext());
                } else {
                    // Cancel the sync job
                    MovieSyncUtilities.cancelFirebaseJobDispatcher(getContext());
                }
            }
        }

        /** Override onStart to register the listener */
        @Override
        public void onStart() {
            super.onStart();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener
                    (this);
        }

        /** Override  onStop to uRegister the listener */
        @Override
        public void onStop() {
            super.onStop();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    /** Retrieves the user sorting preference */
    public static String getOrderByPreference(Context context) {
        // Get an instance of the shared preference and get the user's order by preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (context);
        String orderByPreference = sharedPreferences.getString(context.getString(R.string
                .order_by_key), context.getString(R.string
                .order_by_default));
        Log.v(TAG, orderByPreference);

        // Get the order by preference labels and values
        String[] orderByPreferenceLabels = context.getResources().getStringArray(R.array
                .sort_by_labels);
        String[] orderByPreferenceValues = context.getResources().getStringArray(R.array
                .sort_by_values);

        // Get the position of the user's preference in the array
        int arrayIndex = Arrays.asList(orderByPreferenceLabels).indexOf(orderByPreference);
        // Get the value with the same index
        return orderByPreferenceValues[arrayIndex];
    }

    /** Retrieves the user preference for use as the CatalogActivity's title */
    public static String getPreferenceForTitle(Context context) {
        // Get an instance of the shared preference and get the user's order by preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (context);
        return sharedPreferences.getString(context.getString(R.string
                .order_by_key), context.getString(R.string
                .order_by_default));
    }

    /** Retrieves the user preference regarding auto data syncing */
    public static boolean getAutoSyncPreference(Context context) {
        // Get an instance of the shared preference and get the user's order by preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (context);
        return sharedPreferences.getBoolean(context.getString(R.string.sync_periodically_key),
                true);
    }
}
