package com.example.android.themovieapp.Utilities;

import android.util.Log;

import com.example.android.themovieapp.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Class containing all the methods necessary to parse the JSON response
 */

public class JSONUtilities {

    /** Tag to be used for logging messages */
    private final static String TAG = JSONUtilities.class.getSimpleName();

    /** Keys used to parse the JSON response for the movie list */
    final static String KEY_RESULTS = "results";
    final static String KEY_STATUS_CODE = "status_code";
    final static String KEY_STATUS_MESSAGE = "status_message";
    final static String KEY_TITLE = "original_title";
    final static String KEY_POSTER = "poster_path";
    final static String KEY_OVERVIEW = "overview";
    final static String KEY_RATING = "vote_average";
    final static String KEY_RELEASE_DATE = "release_date";
    final static String KEY_GENRE_IDS = "genre_ids";

    /**
     * Method which parses the JSON response and returns an array list of movies
     */
    public static ArrayList<Movie> parseJSONResponse(String jsonResponse) throws JSONException {

        /** ArrayList holding the parsed data */
        ArrayList<Movie> parsedMovies = null;

        // If the response is null (an error has occured) exit early
        if (jsonResponse == null) {
            return null;
        }

        // Create the base JSON; Object
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

        JSONArray resultsJSONArray = baseJSONObject.getJSONArray(KEY_RESULTS);

        parsedMovies = new ArrayList<Movie>();

        // Iterate through all movies in the JSON Array and store their properties
        for (int i = 0; i < resultsJSONArray.length(); i ++) {
            // Temporary variables that will hold the parsed results
            String title, poster, overview, release_date;
            double rating;
            ArrayList<Integer> genreIds = new ArrayList<Integer>();

            JSONObject currentJSONMovie = resultsJSONArray.getJSONObject(i);
            title = currentJSONMovie.getString(KEY_TITLE);
            poster = currentJSONMovie.getString(KEY_POSTER);
            overview = currentJSONMovie.getString(KEY_OVERVIEW);
            release_date = currentJSONMovie.getString(KEY_RELEASE_DATE);
            rating = currentJSONMovie.getInt(KEY_RATING);
            JSONArray genresJSONArray = currentJSONMovie.getJSONArray(KEY_GENRE_IDS);
            // Iterate through all genres ids and store them
            for (int j = 0; j < genresJSONArray.length(); j ++) {
                genreIds.add(genresJSONArray.getInt(j));
            }

            // Add a new movie object to the array list
            parsedMovies.add(new Movie(title, overview, poster, release_date, rating, genreIds));
        }
        return parsedMovies;
    }
}
