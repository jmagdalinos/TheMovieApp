package com.example.android.themovieapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.themovieapp.Data.PreferencesActivity;
import com.example.android.themovieapp.Utilities.NetworkUtilities;

import java.util.ArrayList;
import java.util.Arrays;

public class CatalogActivity extends AppCompatActivity implements MovieListAdapter.MovieAdapterClickHandler{

    /** Tag to be used for logging messages */
    private final static String TAG = CatalogActivity.class.getSimpleName();

    /** Key used when passing the current movie to the Detail Activity */
    private final static String KEY_CURRENT_MOVIE = "current movie";

    /** Key for the Movies array list in saveInstanceState */
    private final static String STATE_MOVIES = "movies";

    /** Key for the recycler view layout list in saveInstanceState */
    private final static String STATE_LAYOUT_MANAGER = "layout manager";

    /** Parcelable to be used when saving the recycler view state */
    private Parcelable mLayoutManagerSavedState = null;

    /** Recycler view that will show a grid of all movie posters */
    RecyclerView mMovieRecyclerView;

    /** Instance of the movie adapter */
    MovieListAdapter mMovieListAdapter;

    /** ArrayList holding the data retrieved from the movie db */
    ArrayList<Movie> mMovies;

    /** Text view displaying error message */
    TextView mErrorTextView;

    /** Progress bar displaing loading progress */
    ProgressBar mLoadingProgress;

    /** Swipe refresh for the recycler view */
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Find the error text view and the loading progress bar within the layout
        mErrorTextView = (TextView) findViewById(R.id.tv_errors);
        mLoadingProgress = (ProgressBar) findViewById(R.id.pb_loading_progress);

        // Initialize swipe refresh layout and set listener
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeColors(getColor(R.color.colorAccent));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Reload data
                mMovies = null;
                mMovieListAdapter.setMovieData(null);
                loadMovieData();
            }
        });

        // Find the recycler view within the layout
        mMovieRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_list);

        // Create a Grid layout manager and assign it to the recycler view
        // The layout's columns are calculated with the calculateSpanCount method
        GridLayoutManager layoutManager = new GridLayoutManager(CatalogActivity.this, calculateSpanCount());
        mMovieRecyclerView.setLayoutManager(layoutManager);
        // To improve performance, we make it so the child layout size in the RecyclerView does
        // not change
        mMovieRecyclerView.setHasFixedSize(true);

        // Check if this is the the first run of the activity or if it has been restored
        if (savedInstanceState == null || !savedInstanceState.containsKey(STATE_MOVIES)) {
            // This is the first run of the activity therefore, fetch the data from the internet
            // Check for internet connection and the fetch data from the internet
            loadMovieData();
        } else {
            // This is a restored activity therefore, get the array list and the recycler view
            // state from the bundle
            mMovies = savedInstanceState.getParcelableArrayList(STATE_MOVIES);
            // Hide progress bar
            mLoadingProgress.setVisibility(View.INVISIBLE);
            // Get the recycler view state
            mLayoutManagerSavedState = savedInstanceState.getParcelable(STATE_LAYOUT_MANAGER);
        }

        // Create a new instance of a MovieListAdapter passing the movie data as a parameter
        mMovieListAdapter = new MovieListAdapter(mMovies, this);

        // Set the adapter on the recycler view
        mMovieRecyclerView.setAdapter(mMovieListAdapter);
        if (mLayoutManagerSavedState != null) {
            // This is a restored activity, therefore restore the recycler view
            mMovieRecyclerView.getLayoutManager().onRestoreInstanceState(mLayoutManagerSavedState);
            mLayoutManagerSavedState = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the list of movies
        outState.putParcelableArrayList(STATE_MOVIES, mMovies);
        saveRecyclerViewState();
        outState.putParcelable(STATE_LAYOUT_MANAGER, mLayoutManagerSavedState);
        super.onSaveInstanceState(outState);
    }

    /**
     * Saves the current state of the recycler view
     */
    private void saveRecyclerViewState() {
        // Save the state of the recycler view
        mLayoutManagerSavedState = mMovieRecyclerView.getLayoutManager().onSaveInstanceState();
    }

    /**
     * Method that responds to user click by creating an intent that starts the detail activity.
     * Il also passes the current movie object to the detail activity
     */
    @Override
    public void onPosterClick(Movie movie) {
        // Save the recycler view state
        saveRecyclerViewState();
        // Create new intent and pass the current movie object as an extra
        Intent detailIntent = new Intent(CatalogActivity.this, DetailActivity.class);
        detailIntent.putExtra(KEY_CURRENT_MOVIE, movie);
        startActivity(detailIntent);
    }

    /**
     * Method that checks for internet connection and then starts the AsyncTask to fetch the data
     * from the internet
     */
    private void loadMovieData() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // Run AsyncTask to get the movie data
            new FetchMovieDbData().execute();
        } else {
            // Show "no internet connection" error message
            showErrorMessage(getString(R.string.error_message_no_connection));
        }
    }

    /**
     * Method that shows the error text view and hides the progress bar
     */
    private void showErrorMessage(String errorMessage) {
        if ((errorMessage == null)) {
            // There is no error message, so hide everything
            mLoadingProgress.setVisibility(View.INVISIBLE);
            mErrorTextView.setVisibility(View.INVISIBLE);
            mSwipeRefreshLayout.setRefreshing(false);
        } else {
            mErrorTextView.setText(errorMessage);
            mLoadingProgress.setVisibility(View.INVISIBLE);
            mMovieRecyclerView.setVisibility(View.INVISIBLE);
            mErrorTextView.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * Method that hides the error text view and shows the progress bar
     */
    private void hideErrorMessage() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            // Swipe refresh progress bar is visible, therefore, hide the loading bar
            mLoadingProgress.setVisibility(View.INVISIBLE);
        } else {
            mLoadingProgress.setVisibility(View.VISIBLE);
        }
        mErrorTextView.setVisibility(View.INVISIBLE);
        mMovieRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Calculates the maximum number of columns that the screen can accommodate
     */
    private int calculateSpanCount() {
        // Calculate display width
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int displayWidth = size.x;
        // Get the list view padding and subtract it from the screen width
        int displayPadding = (int) getResources().getDimension(R.dimen.catalog_padding);
        int finalDisplayWidth = displayWidth - (2 * displayPadding);

        // Get poster image width and add its padding
        int posterPadding = (int) getResources().getDimension(R.dimen.list_item_padding_left_right);
        int posterWidth = (int) getResources().getDimension(R.dimen.list_image_width);
        int totalPosterWidth = posterWidth + (2 * posterPadding);

        // Calculate max number of spans
        int spanCount = (int) (finalDisplayWidth / totalPosterWidth);

        return spanCount;
    }

    /** AsyncTask class used to fetch data from the internet and return a list of movies */
    public class FetchMovieDbData extends AsyncTask<Void, Void, ArrayList<Movie>> {
        @Override
        protected void onPreExecute() {
            // Hide error messages and show progress bar before loading data
            hideErrorMessage();
        }

        @Override
        protected ArrayList<Movie> doInBackground(Void... params) {
            // Get the user's preference
            String orderByPreference = getOrderByPreference();
            // Get the movies list
            mMovies = NetworkUtilities.getMovieData(orderByPreference);            return mMovies;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            if (movies != null) {
                // Inform SwipeRefreshLayout that loading is complete so it can hide its progress bar
                mSwipeRefreshLayout.setRefreshing(false);
                // Hide he progress bar
                showErrorMessage(null);
                // Pass the data to the adapter
                mMovieListAdapter.setMovieData(movies);
            } else {
                // Inform SwipeRefreshLayout that loading is complete so it can hide its progress bar
                mSwipeRefreshLayout.setRefreshing(false);
                // Show "an error has occurred" error message
                showErrorMessage(getString(R.string.error_message_general));
            }
        }
    }

    private String getOrderByPreference() {
        // Get an instance of the shared preference and get the user's order by preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (CatalogActivity.this);
        String orderByPreference = sharedPreferences.getString(getString(R.string
                .order_by_key), getString(R.string
                .order_by_default));

        // Get the orderby preference labels and values
        String[] orderByPreferenceLabels = getResources().getStringArray(R.array
                .sort_by_labels);
        String[] orderByPreferenceValues = getResources().getStringArray(R.array
                .sort_by_values);

        // Get the position of the user's preference in the array
        int arrayIndex = Arrays.asList(orderByPreferenceLabels).indexOf(orderByPreference);
        // Get the value with the same index
        return orderByPreferenceValues[arrayIndex];
    }

    /**
     * Create the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the appropriate menu
        getMenuInflater().inflate(R.menu.catalog_menu, menu);
        return true;
    }

    /**
     * Specify what the menu buttons do when clicked
     */
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
