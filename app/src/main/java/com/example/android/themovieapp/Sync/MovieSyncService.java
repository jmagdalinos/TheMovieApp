package com.example.android.themovieapp.Sync;

import android.app.IntentService;
import android.content.Intent;

/**
 * Service called to sync movie data
 */

public class MovieSyncService extends IntentService {
    /** Creates an IntentService.  Invoked by subclass's constructor */
    public MovieSyncService() {
        super("MovieSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Get the sync type to distinguish between movies, trailers and reviews
        String intentExtra = intent.getStringExtra(MovieSyncUtilities.TAG_SYNC_TYPE);

        int movie_id;

        switch (intentExtra) {
            case MovieSyncUtilities.SYNC_TYPE_MOVIES:
                // Sync the movies
                MovieSyncTask.syncMovies(this);
                break;
            case MovieSyncUtilities.SYNC_TYPE_TRAILERS:
                // Get the movie_id
                movie_id = intent.getIntExtra(MovieSyncUtilities.TAG_MOVIE_ID, -1);
                // Sync the trailers
                MovieSyncTask.syncTrailers(this, movie_id);
                break;
            case MovieSyncUtilities.SYNC_TYPE_REVIEWS:
                // Get the movie_id
                movie_id = intent.getIntExtra(MovieSyncUtilities.TAG_MOVIE_ID, -1);
                // Sync the reviews
                MovieSyncTask.syncReviews(this, movie_id);
                break;
            default:
                throw new RuntimeException("Sync type " + intentExtra + " not implemented");
        }
    }
}
