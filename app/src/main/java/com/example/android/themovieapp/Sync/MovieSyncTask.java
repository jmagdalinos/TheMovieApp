package com.example.android.themovieapp.Sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.example.android.themovieapp.Data.MovieContract.MovieEntry;
import com.example.android.themovieapp.Data.MovieContract.ReviewsEntry;
import com.example.android.themovieapp.Data.MovieContract.TrailersEntry;
import com.example.android.themovieapp.Data.PreferencesActivity;
import com.example.android.themovieapp.R;
import com.example.android.themovieapp.Utilities.NetworkUtilities;

/**
 * Task run every time the app has to sync the data with the server and update one of the tables
 */

public class MovieSyncTask {

    /** Gets the user preference, downloads the data and inserts it into the movies table */
    public static void syncMovies(Context context) {
        // Get the user's preference
        String orderByPreference = PreferencesActivity.getOrderByPreference(context);

        // If the preference is to show the favourites, DON'T sync since the favourites are
        // stored locally
        if (orderByPreference.equals(context.getResources().getString(R.string
                .order_by_favourite_value))) {
            return;
        }

        // Get the movies list
        ContentValues[] movieData = NetworkUtilities.getMovieData(orderByPreference);

        // Get the content uri for the movies table
        Uri moviesUri = MovieEntry.CONTENT_URI;

        // Update the movie table with the parsed values
        updateTable(context, moviesUri, movieData);
    }

    /** Downloads the trailers and inserts them into the trailers table */
    public static void syncTrailers(Context context, int movie_id) {
        // Get the trailers
        ContentValues[] trailerData = NetworkUtilities.getTrailerData(movie_id);

        // Get the content uri for the trailers table
        Uri trailersUri = TrailersEntry.CONTENT_URI;

        // Update the movie table with the parsed values
        updateTable(context, trailersUri, trailerData);
    }

    /** Downloads the reviews and inserts them into the trailers table */
    public static void syncReviews(Context context, int movie_id) {
        // Get the reviews
        ContentValues[] reviewData = NetworkUtilities.getReviewData(movie_id);

        // Get the content uri for the reviews table
        Uri reviewsUri = ReviewsEntry.CONTENT_URI;

        // Update the reviews table with the parsed values
        updateTable(context, reviewsUri, reviewData);
    }

    /** Deletes the previous data from a table and inserts the new one */
    public static void updateTable(Context context, Uri tableUri, ContentValues[] values) {
        // Get an instance of the content resolver to delete and insert data
        ContentResolver movieResolver = context.getContentResolver();

        if (values != null && values.length != 0) {
            // Delete all previous data
            movieResolver.delete(tableUri, null, null);

            // Insert new data
            movieResolver.bulkInsert(tableUri, values);
        } else {
            // if the values are empty, there was no connectivity.
            // To avoid  showing the user trailers and reviews from the wrong movie, we should clear
            // both the trailers and reviews tables
            if (tableUri == TrailersEntry.CONTENT_URI || tableUri == ReviewsEntry.CONTENT_URI) {
                // Delete all previous data
                movieResolver.delete(tableUri, null, null);
            }
        }
    }
}
