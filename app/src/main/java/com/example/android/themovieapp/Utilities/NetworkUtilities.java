package com.example.android.themovieapp.Utilities;

import android.net.Uri;
import android.util.Log;

import com.example.android.themovieapp.Movie;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class containing all the methods required to make the HTTP request and retrieve the
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


    /**
     * Method returning an ArrayList<Movie> with the result of the HTTP request
     * @param sortPreference either "popularity.desc" or "vote_average.desc"
     */
    public static ArrayList<Movie> getMovieData(String sortPreference) {
        // Create a new array list
        ArrayList<Movie> dbMovies = null;
        // Get the URL
        URL queryUrl = buildUrl(sortPreference);
        Log.v(TAG, queryUrl.toString());
        // Make the HTTP request and return the JSON Response
        String jsonResponse = null;
        try {
            jsonResponse = makeHTTPRequest(queryUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Call the method to parse the JSON response
        try {
            dbMovies = JSONUtilities.parseJSONResponse(jsonResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dbMovies;
    }

    /**
     * Method returning the URL to be used to make the HTTP request
     * @param sortPreference either "popularity.desc" or "vote_average.desc"
     */
    private static URL buildUrl(String sortPreference) {
        Uri uri = null;

        // Create a new uri using the base url and the user preferences
        uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(sortPreference)
                .appendQueryParameter(QUERY_API_KEY, API_KEY)
                .build();

        // Create the url
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Method that makes the HTTP request and returns a JSON response string
     * @param queryUrl the url used to make the request
     */
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
