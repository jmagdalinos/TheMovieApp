package com.example.android.themovieapp.Utilities;

import android.content.ContentValues;
import android.util.Log;

import com.example.android.themovieapp.Data.MovieContract.MovieEntry;
import com.example.android.themovieapp.Data.MovieContract.ReviewsEntry;
import com.example.android.themovieapp.Data.MovieContract.TrailersEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Class containing all the methods necessary to parse the JSON response for:
 *      - movies
 *      - trailers
 *      - reviews
 */

public class JSONUtilities {

    /** Tag to be used for logging messages */
    private final static String TAG = JSONUtilities.class.getSimpleName();

    /** Keys used to parse the JSON response for the movie list */
    private static final String KEY_RESULTS = "results";
    private static final String KEY_STATUS_CODE = "status_code";
    private static final String KEY_STATUS_MESSAGE = "status_message";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "original_title";
    private static final String KEY_POSTER = "poster_path";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_RATING = "vote_average";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String KEY_GENRE_IDS = "genre_ids";

    /** Keys used to parse the JSON response for the movie reviews */
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_URL = "url";

    /** Keys used to parse the JSON response for the movie trailers */
    private static final String KEY_YOUTUBE = "youtube";
    private static final String KEY_NAME = "name";
    private static final String KEY_SOURCE = "source";


    /** Parses the JSON response and returns a ContentValues array of movies */
    public static ContentValues[] parseMovies(String jsonResponse) throws JSONException {
        // If the response is null (an error has occurred) exit early
        if (jsonResponse == null) {
            return null;
        }

        // Create the base JSON Object
        JSONObject baseJSONObject = new JSONObject(jsonResponse);

        // Check if there is an error. If so, retrieve the error from the JSON response and log it
        if (baseJSONObject.has(KEY_STATUS_CODE)) {
            int statusCode = baseJSONObject.getInt(KEY_STATUS_CODE);
            switch (statusCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                default:
                    Log.v(TAG, baseJSONObject.getString(KEY_STATUS_MESSAGE));
                    return null;
            }
        }

        // Get the JSON array with the results
        JSONArray resultsJSONArray = baseJSONObject.getJSONArray(KEY_RESULTS);

        // Content values array holding the parsed data
        ContentValues[] parsedMovies = new ContentValues[resultsJSONArray.length()];

        // Iterate through all movies in the JSON Array and store their properties
        for (int i = 0; i < resultsJSONArray.length(); i ++) {
            // Temporary variables that will hold the parsed results
            String title, poster, overview, releaseDate;
            int id;
            double rating;

            // Get the movie at position i
            JSONObject currentJSONMovie = resultsJSONArray.getJSONObject(i);

            // Save the results in the temporary variables
            id = currentJSONMovie.getInt(KEY_ID);
            title = currentJSONMovie.getString(KEY_TITLE);
            poster = currentJSONMovie.getString(KEY_POSTER);
            overview = currentJSONMovie.getString(KEY_OVERVIEW);
            releaseDate = currentJSONMovie.getString(KEY_RELEASE_DATE);
            rating = currentJSONMovie.getInt(KEY_RATING);
            JSONArray genresJSONArray = currentJSONMovie.getJSONArray(KEY_GENRE_IDS);

            // Iterate through all genres ids and store them
            StringBuilder genreStringBuilder = new StringBuilder();
            for (int j = 0; j < genresJSONArray.length(); j ++) {
                genreStringBuilder.append(String.valueOf(genresJSONArray.getInt(j)) + ",");
            }

            // Create a new content values object to store the current movie
            ContentValues currentContentValue = new ContentValues();
            currentContentValue.put(MovieEntry.COLUMN_MOVIE_ID, id);
            currentContentValue.put(MovieEntry.COLUMN_TITLE, title);
            currentContentValue.put(MovieEntry.COLUMN_POSTER, poster);
            currentContentValue.put(MovieEntry.COLUMN_OVERVIEW, overview);
            currentContentValue.put(MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
            currentContentValue.put(MovieEntry.COLUMN_RATING, rating);
            currentContentValue.put(MovieEntry.COLUMN_GENRE_IDS, genreStringBuilder.toString());

            // Add the current content values object to the final content values object
            parsedMovies[i] = currentContentValue;
        }
        return parsedMovies;
    }

    /** Parses the JSON response and returns an ContentValues array of movie trailers */
    public static ContentValues[] parseTrailers(String jsonResponse) throws JSONException {
        // If the response is null (an error has occurred) exit early
        if (jsonResponse == null) {
            return null;
        }

        // Create the base JSON Object
        JSONObject baseJSONObject = new JSONObject(jsonResponse);

        // Check if there is an error. If so, retrieve the error from the JSON response and log it
        if (baseJSONObject.has(KEY_STATUS_CODE)) {
            int statusCode = baseJSONObject.getInt(KEY_STATUS_CODE);
            switch (statusCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                default:
                    Log.v(TAG, baseJSONObject.getString(KEY_STATUS_MESSAGE));
                    return null;
            }
        }
        // Get the JSON array with the results
        JSONArray youtubeJSONArray = baseJSONObject.getJSONArray(KEY_YOUTUBE);

        // Content values array holding the parsed data
        ContentValues[] parsedTrailers = new ContentValues[youtubeJSONArray.length()];

        // Iterate through all trailers in the JSON Array and store their properties
        for (int i = 0; i < youtubeJSONArray.length(); i ++) {
            // Temporary variables that will hold the parsed results
            String name, source;

            // Get the trailer at position i
            JSONObject currentJSONReview = youtubeJSONArray.getJSONObject(i);

            // Save the results in the temporary variables
            name = currentJSONReview.getString(KEY_NAME);
            source = currentJSONReview.getString(KEY_SOURCE);

            // Create a new content values object to store the current review
            ContentValues currentContentValue = new ContentValues();
            currentContentValue.put(TrailersEntry.COLUMN_NAME, name);
            currentContentValue.put(TrailersEntry.COLUMN_SOURCE, source);

            // Add the current content values object to the final content values object
            parsedTrailers[i] = currentContentValue;
        }
        return parsedTrailers;
    }

    /** Parses the JSON response and returns an ContentValues array of movie reviews */
    public static ContentValues[] parseReviews(String jsonResponse) throws JSONException {
        // If the response is null (an error has occurred) exit early
        if (jsonResponse == null) {
            return null;
        }

        // Create the base JSON Object
        JSONObject baseJSONObject = new JSONObject(jsonResponse);

        // Check if there is an error. If so, retrieve the error from the JSON response and log it
        if (baseJSONObject.has(KEY_STATUS_CODE)) {
            int statusCode = baseJSONObject.getInt(KEY_STATUS_CODE);
            switch (statusCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                default:
                    Log.v(TAG, baseJSONObject.getString(KEY_STATUS_MESSAGE));
                    return null;
            }
        }

        // Get the JSON array with the results
        JSONArray resultsJSONArray = baseJSONObject.getJSONArray(KEY_RESULTS);

        // Content values array holding the parsed data
        ContentValues[] parsedReviews = new ContentValues[resultsJSONArray.length()];

        // Iterate through all reviews in the JSON Array and store their properties
        for (int i = 0; i < resultsJSONArray.length(); i ++) {
            // Temporary variables that will hold the parsed results
            String author, content, url;

            // Get the review at position i
            JSONObject currentJSONReview = resultsJSONArray.getJSONObject(i);

            // Save the results in the temporary variables
            author = currentJSONReview.getString(KEY_AUTHOR);
            content = currentJSONReview.getString(KEY_CONTENT);
            url = currentJSONReview.getString(KEY_URL);

            // Create a new content values object to store the current review
            ContentValues currentContentValue = new ContentValues();
            currentContentValue.put(ReviewsEntry.COLUMN_AUTHOR, author);
            currentContentValue.put(ReviewsEntry.COLUMN_CONTENT, content);
            currentContentValue.put(ReviewsEntry.COLUMN_URL, url);

            // Add the current content values object to the final content values object
            parsedReviews[i] = currentContentValue;
        }
        return parsedReviews;
    }
}
