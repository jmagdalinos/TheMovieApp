package com.example.android.themovieapp.Sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.example.android.themovieapp.Data.MovieContract.MovieEntry;
import com.example.android.themovieapp.Data.PreferencesActivity;
import com.example.android.themovieapp.R;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Helper class with methods for:
 *      - Starting the scheduler
 *      - Checking if there is data in the content provider in order to avoid showing
 *          an empty recycler view
 *      - Syncing the data with the server
 */

public class MovieSyncUtilities {

    /** Tag to be used for logging messages */
    private static final String TAG = MovieSyncUtilities.class.getSimpleName();

    /** Tags used for the intents */
    public static final String TAG_SYNC_TYPE = "sync_type";
    public static final String TAG_MOVIE_ID = "movie_id";

    /** Types of sync used to distinguish between movie, trailer or reviews */
    public static final String SYNC_TYPE_MOVIES = "movies";
    public static final String SYNC_TYPE_TRAILERS = "trailers";
    public static final String SYNC_TYPE_REVIEWS = "reviews";

    /** Interval at which to sync the movie data */
    private static final int SYNC_INTERVAL = (int) TimeUnit.HOURS.toSeconds(3);
    private static final int SYNC_FLEX_TIME = SYNC_INTERVAL / 3;

    /** Tag used to identify the job */
    private static final String SYNC_MOVIE_TAG = "movie_sync";

    /** Schedules the sync and performs it if this is the first run */
    synchronized public static void initializeSync(final Context context) {
        // If the user has selected favourites, no need to sync
        if (PreferencesActivity.getPreferenceForTitle(context).equals(context.getResources()
                .getString(R.string.order_by_favourite_label))) {
            return;
        }

        // Check the user's preferences regarding auto sync
        if (PreferencesActivity.getAutoSyncPreference(context)) {
            // Cancel previous jobs
            cancelFirebaseJobDispatcher(context);
            // Start job scheduler
            scheduleFirebaseJobDispatcher(context);
        }

        // Check for data in the content provider so it can be showw in the recycler view.
        // Do this in a background thread to avoid UI lag
        Thread checkTableThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Uri for every row in the movies table
                Uri uri = MovieEntry.CONTENT_URI;

                // Query the table only for movie_ids to make sure it's not empty
                String[] projection = new String[] {MovieEntry.COLUMN_MOVIE_ID};

                Cursor cursor = context.getContentResolver().query(uri,
                        projection,
                        null,
                        null,
                        null);

                // If the cursor is null, start sync
                if (cursor == null || cursor.getCount() == 0) {
                    startMovieSync(context);
                }

                // Close the cursor to avoid memory leaks
                cursor.close();
            }
        });

        // Start the thread
        checkTableThread.start();
    }

    /** Schedule the sync to run every 3 hours */
    public static void scheduleFirebaseJobDispatcher(Context context) {
        // Initiate the dispatcher
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        // Set job parameters
        Job syncMovieJob = dispatcher.newJobBuilder()
                .setService(MovieFirebaseJobService.class)
                .setTag(SYNC_MOVIE_TAG)
                .setConstraints(Constraint.ON_UNMETERED_NETWORK)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(SYNC_INTERVAL, SYNC_INTERVAL + SYNC_FLEX_TIME))
                .setReplaceCurrent(true)
                .build();

        // Schedule the job
        dispatcher.schedule(syncMovieJob);
    }

    /** Cancel the job */
    public static void cancelFirebaseJobDispatcher(Context context) {
        // Initiate the dispatcher
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        // Cancel all jobs
        dispatcher.cancelAll();
    }

    /** Starts the intent service to sync the movies */
    public static void startMovieSync(Context context) {
        Intent movieSyncService = new Intent(context, MovieSyncService.class);
        movieSyncService.putExtra(TAG_SYNC_TYPE, SYNC_TYPE_MOVIES);
        context.startService(movieSyncService);
    }

    /** Starts the intent service to sync the trailers */
    public static void startTrailerSync(Context context, int movie_id) {
        Intent trailerSyncService = new Intent(context, MovieSyncService.class);
        trailerSyncService.putExtra(TAG_SYNC_TYPE, SYNC_TYPE_TRAILERS);
        trailerSyncService.putExtra(TAG_MOVIE_ID, movie_id);
        context.startService(trailerSyncService);
    }

    /** Starts the intent service to sync the reviews */
    public static void startReviewSync(Context context, int movie_id) {
        Intent reviewSyncService = new Intent(context, MovieSyncService.class);
        reviewSyncService.putExtra(TAG_SYNC_TYPE, SYNC_TYPE_REVIEWS);
        reviewSyncService.putExtra(TAG_MOVIE_ID, movie_id);
        context.startService(reviewSyncService);
    }
}
