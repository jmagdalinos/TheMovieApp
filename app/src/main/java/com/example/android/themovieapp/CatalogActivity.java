package com.example.android.themovieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.themovieapp.Data.MovieContract.FavEntry;
import com.example.android.themovieapp.Data.MovieContract.MovieEntry;
import com.example.android.themovieapp.Data.PreferencesActivity;
import com.example.android.themovieapp.Sync.MovieSyncService;
import com.example.android.themovieapp.Sync.MovieSyncUtilities;
import com.example.android.themovieapp.databinding.ActivityCatalogBinding;

import static com.example.android.themovieapp.Sync.MovieSyncUtilities.SYNC_TYPE_MOVIES;
import static com.example.android.themovieapp.Sync.MovieSyncUtilities.TAG_SYNC_TYPE;

public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        MovieListAdapter.MovieAdapterClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener {

    /** Tag to be used for logging messages */
    private static final String TAG = CatalogActivity.class.getSimpleName();

    /** Ids of the cursor loader */
    private static final int MOVIE_LOADER_ID = 52;

    /** Key used when passing the current movie to the Detail Activity */
    private static final String KEY_CURRENT_MOVIE_ID = "current_movie_id";

    /** Key for the recycler view layout list in saveInstanceState */
    private static final String STATE_LAYOUT_MANAGER = "layout_manager";

    /** Parcelable to be used when saving the recycler view state */
    private Parcelable mLayoutManagerSavedState = null;

    /** Value of preference when user selects favourites */
    private static String favouritesPreference;


    /**
     * Stores the user's preference regarding sorting
     * This is used both to set the activity's title and to query the correct table
     */
    private static String titlePreference;

    /** Data binding object */
    ActivityCatalogBinding mBinding;

    /** Instance of the movie adapter */
    MovieListAdapter mMovieListAdapter;

    /** Action bar used to set the activity title */
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_catalog);

        // Get the value of the favourites preference
        favouritesPreference = getResources().getString(R.string.order_by_favourite_label);

        // Show home icon
        actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setIcon(R.mipmap.ic_launcher);

            // Get the title of the action bar based on the user preferences
            titlePreference = PreferencesActivity.getPreferenceForTitle(this);
            actionBar.setTitle(titlePreference + " Movies");
        }

        // Change the color scheme of the swipe refresh layout and set its listener
        mBinding.swipeRefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color
                .colorAccent) );
        mBinding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRecyclerView();
            }
        });

        // Create a Grid layout manager and assign it to the recycler view
        // The layout's columns are calculated with the calculateSpanCount method
        GridLayoutManager layoutManager = new GridLayoutManager(CatalogActivity.this, calculateSpanCount());
        mBinding.rvMovieList.setLayoutManager(layoutManager);
        // To improve performance, we make it so the child layout size in the RecyclerView does
        // not change
        mBinding.rvMovieList.setHasFixedSize(true);

        // Check if this is the the first run of the activity or if it has been restored.
        if (savedInstanceState != null) {
            // Hide progress bar
            mBinding.pbLoadingProgress.setVisibility(View.INVISIBLE);

            // Get the recycler view state
            mLayoutManagerSavedState = savedInstanceState.getParcelable(STATE_LAYOUT_MANAGER);

            // This is a restored activity, therefore restore the recycler view
            mBinding.rvMovieList.getLayoutManager().onRestoreInstanceState(mLayoutManagerSavedState);
        }
        // Create a new instance of a MovieListAdapter
        mMovieListAdapter = new MovieListAdapter(this, this);

        // Set the adapter on the recycler view
        mBinding.rvMovieList.setAdapter(mMovieListAdapter);

        // Hide error messages and show progress bar before loading data
        hideErrorMessage();

        // Initialize the loader for the movies
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);

        // If the user wants to see the favourites table, don't sync data
        if (!(titlePreference.equals(favouritesPreference))) {
            // Start the scheduler and sync data
            MovieSyncUtilities.initializeSync(this);
        }

        // Register the shared preferences change listener
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the recycler view states
        saveRecyclerViewState();
        outState.putParcelable(STATE_LAYOUT_MANAGER, mLayoutManagerSavedState);
        super.onSaveInstanceState(outState);
    }

    /** Saves the current state of the recycler view */
    private void saveRecyclerViewState() {
        // Save the state of the recycler view
        mLayoutManagerSavedState = mBinding.rvMovieList.getLayoutManager().onSaveInstanceState();
    }

    /** Overwritten to update when in favourites and returning from detail activity */
    @Override
    protected void onResume() {
        super.onResume();
        if (titlePreference.equals(favouritesPreference)) {
            refreshRecyclerView();
        }
    }

    /** Overwritten to unregister the shared preferences listener */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Register the shared preferences change listener
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Responds to user click by creating an intent that starts the detail activity.
     * Il also passes the current movie_id to the detail activity
     */
    @Override
    public void onPosterClick(int movie_id) {
        // Save the recycler view state
        saveRecyclerViewState();

        // Sync the trailers and the reviews using the intent service
        MovieSyncUtilities.startTrailerSync(this, movie_id);
        MovieSyncUtilities.startReviewSync(this, movie_id);

        // Create new intent and pass the current movie object as an extra
        Intent detailIntent = new Intent(CatalogActivity.this, DetailActivity.class);
        detailIntent.putExtra(KEY_CURRENT_MOVIE_ID, movie_id);
        startActivity(detailIntent);
    }

    /** Method that shows the error text view and hides the progress bar */
    private void showErrorMessage(String errorMessage) {
        if ((errorMessage == null)) {
            // There is no error message, so hide everything
            mBinding.pbLoadingProgress.setVisibility(View.INVISIBLE);
            mBinding.tvErrors.setVisibility(View.INVISIBLE);
            mBinding.swipeRefresh.setRefreshing(false);
        } else {
            mBinding.pbLoadingProgress.setVisibility(View.INVISIBLE);
            mBinding.tvErrors.setText(errorMessage);
            mBinding.tvErrors.setVisibility(View.VISIBLE);
            mBinding.swipeRefresh.setRefreshing(false);
        }
    }

    /** Method that hides the error text view and shows the progress bar */
    private void hideErrorMessage() {
        if (mBinding.swipeRefresh.isRefreshing()) {
            // Swipe refresh progress bar is visible, therefore, hide the loading bar
            mBinding.pbLoadingProgress.setVisibility(View.INVISIBLE);
        } else {
            mBinding.pbLoadingProgress.setVisibility(View.VISIBLE);
        }
        mBinding.tvErrors.setVisibility(View.INVISIBLE);
        mBinding.rvMovieList.setVisibility(View.VISIBLE);
    }

    /** Returns 2 columns if the screen orientation is portrait or 3 if it is landscape */
    private int calculateSpanCount() {
        // Check screen orientation and return values
        if (getResources().getConfiguration().orientation == Configuration
                .ORIENTATION_PORTRAIT) {
            return 2;
        } else {
            return 3;
        }
    }

    /** Method used to refresh the recycler view */
    private void refreshRecyclerView() {
        // Set the title on the action bar
        actionBar.setTitle(titlePreference + " Movies");

        // Clear the adapter
        mMovieListAdapter.swapCursor(null);
        mBinding.rvMovieList.setAdapter(mMovieListAdapter);

        // Restart the service
        Intent movieSyncService = new Intent(this, MovieSyncService.class);
        movieSyncService.putExtra(TAG_SYNC_TYPE, SYNC_TYPE_MOVIES);
        startService(movieSyncService);

        // Reload the data
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }

    /** Refreshes the recycler view when a shared preference has */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Get the title from the preferences
        titlePreference = PreferencesActivity.getPreferenceForTitle(this);

        if (titlePreference.equals(favouritesPreference)) {
            // Stop the dispatcher. Favourites are stored locally
            MovieSyncUtilities.cancelFirebaseJobDispatcher(this);
        } else {
            // Start the scheduler and sync data
            MovieSyncUtilities.initializeSync(this);
        }
        refreshRecyclerView();
    }

    /** Creates the loader for the activity */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        Uri queryUri;
        String[] projection;

        if (titlePreference.equals(favouritesPreference)) {
            // Build uri and projection for favourites table

            // Build the uri for the loader
            queryUri = FavEntry.CONTENT_URI;
            // Return only the movie_id and the poster
            projection = new String[] {
                    FavEntry.COLUMN_MOVIE_ID,
                    FavEntry.COLUMN_POSTER};
        } else {
            // Build uri and projection for movies table

            // Build the uri for the loader
            queryUri = MovieEntry.CONTENT_URI;
            // Return only the movie_id and the poster
            projection = new String[] {
                    MovieEntry.COLUMN_MOVIE_ID,
                    MovieEntry.COLUMN_POSTER};
        }

        switch (loaderId) {
            case MOVIE_LOADER_ID:
                // Return a cursor loader
                return new CursorLoader(this,
                        queryUri,
                        projection,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader" + loaderId + " not implemented");
        }
    }

    /** Called when the loader has finished */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case MOVIE_LOADER_ID:
                // Pass the cursor to the adapter
                mMovieListAdapter.swapCursor(data);

                // Inform SwipeRefreshLayout that loading is complete so it can hide its progress bar
                mBinding.swipeRefresh.setRefreshing(false);

                if (data != null && data.getCount() != 0) {
                    // Hide he progress bar
                    showErrorMessage(null);
                } else {
                    // Show "an error has occurred" error message
                    showErrorMessage(null);
                    // The cursor is empty. Clear the adapter
                    mMovieListAdapter.swapCursor(null);
                }
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // The cursor is now empty. Clear the adapter
        mMovieListAdapter.swapCursor(null);
    }

    /** Creates the menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the appropriate menu
        getMenuInflater().inflate(R.menu.catalog_menu, menu);
        return true;
    }

    /** Specifies what the menu buttons do when clicked */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_preferences:
                Intent preferencesIntent = new Intent(CatalogActivity.this, PreferencesActivity
                        .class);
                startActivity(preferencesIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
