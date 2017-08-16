package com.example.android.themovieapp.Utilities;

import android.content.ContentValues;
import android.net.Uri;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Helper class containing all the methods required to make the HTTP request and retrieve the
 * JSON response
 */

public class NetworkUtilities {

    /** Tag to be used for logging messages */
    private final static String TAG = NetworkUtilities.class.getSimpleName();

    /** API Key used the movie db */
    private static final String API_KEY = "YOUR KEY HERE!!!";

    /** Base URL for the movie db */
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie";

    /** Query Parameter for inserting the API key */
    private static final String QUERY_API_KEY = "api_key";

    /** UrlTypes to enable the usage of buildUrl() for all requests */
    private static final int TYPE_MOVIE = 100;
    private static final int TYPE_TRAILER = 101;
    private static final int TYPE_REVIEW = 102;


    /** Returns a ContentValues object with the result of the HTTP request */
    public static ContentValues[] getMovieData(String sortPreference) {
        try {
            // Get the URL
            URL queryUrl = buildUrl(sortPreference, TYPE_MOVIE);
            // Make the HTTP request and return the JSON Response
            String jsonResponse = null;
            try {
                jsonResponse = makeHTTPRequest(queryUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Call the method to parse the JSON response
            return JSONUtilities.parseMovies(jsonResponse);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Returns a ContentValues array with the result of the HTTP request */
    public static ContentValues[] getTrailerData(int id) {
        try {
            // Get the URL
            URL queryUrl = buildUrl(String.valueOf(id), TYPE_TRAILER);
            // Make the HTTP request and return the JSON Response
            String jsonResponse = null;
            try {
                jsonResponse = makeHTTPRequest(queryUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Call the method to parse the JSON response
            return JSONUtilities.parseTrailers(jsonResponse);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Returns a ContentValues array with the result of the HTTP request */
    public static ContentValues[] getReviewData(int id) {
        try {
            // Get the URL
            URL queryUrl = buildUrl(String.valueOf(id), TYPE_REVIEW);
            // Make the HTTP request and return the JSON Response
            String jsonResponse = null;
            try {
                jsonResponse = makeHTTPRequest(queryUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Call the method to parse the JSON response
            return JSONUtilities.parseReviews(jsonResponse);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Returns the URL to be used to make the HTTP request */
    private static URL buildUrl(String parameter, int urlType) {
        Uri uri = null;

        // Create a new uri using the base url
        switch (urlType) {
            case TYPE_MOVIE:
                // Use the user's preference
                uri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(parameter)
                        .appendQueryParameter(QUERY_API_KEY, API_KEY)
                        .build();
                break;
            case TYPE_TRAILER:
                // Use the movie_id
                uri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(parameter)
                        .appendPath("trailers")
                        .appendQueryParameter(QUERY_API_KEY, API_KEY)
                        .build();
                break;
            case TYPE_REVIEW:
                // Use the movie_id
                uri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(parameter)
                        .appendPath("reviews")
                        .appendQueryParameter(QUERY_API_KEY, API_KEY)
                        .build();
                break;
        }

        // Create the url
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /** Makes the HTTP request and returns a JSON response string */
    private static String makeHTTPRequest(URL queryUrl) throws IOException {
        // Create the url connection
        HttpURLConnection urlConnection = (HttpURLConnection) queryUrl.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setConnectTimeout(10000);
        urlConnection.setReadTimeout(15000);

        // Read the input stream and then disconnect
        try {
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
