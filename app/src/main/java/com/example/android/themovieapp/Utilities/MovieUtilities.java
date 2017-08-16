package com.example.android.themovieapp.Utilities;

import android.content.Context;
import android.net.Uri;

import com.example.android.themovieapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Helper class with methods for:
 *      - Converting the date format
 *      - Matching the parsed genre_ids to genre names
 */

public class MovieUtilities {

    /** Base url used to build the complete poster image path */
    private static final String BASE_URL = "http://image.tmdb.org/t/p/";

    /** File size ued to build the complete poster image path for large images */
    public static final String FILE_SIZE_FULL = "w780";

    /** Method that uses the 3 parts of the movie poster path to build the complete path */
    public static String buildPosterPath(String path, String fileSize) {
        // Check if there is a poster
        if (!path.equals("null")) {
            // Create the final uri by adding the poster image path
            Uri finalPosterImageUri = Uri.parse(BASE_URL).buildUpon()
                    .appendEncodedPath(fileSize)
                    .appendEncodedPath(path)
                    .build();
            return finalPosterImageUri.toString();
        } else {
            return "no image";
        }
    }

    /** Integer array holding genre ids */
    private static int[] genreIds;

    /** String array holding genre names */
    private static String[] genreNames;

    /** Matches the genre id with the genre name and displays it on the text view */
    public static String matchGenres(Context context, String parsedGenres) {
        // Get array values
        genreIds = context.getResources().getIntArray(R.array.genre_id);
        genreNames = context.getResources().getStringArray(R.array.genre_name);

        // The genre ids are separated by commas. Split them
        String[] singleGenreIdString = parsedGenres.split(",");
        int arrayLength = singleGenreIdString.length;

        StringBuilder finalGenres = new StringBuilder();
        // Iterate though all parsed genre ids for the current movie
        for (String output: singleGenreIdString) {
            // Convert the string id to an int id
            int singeGenreId = Integer.parseInt(output);

            // Test index
            int genreIndex = -1;

            // Iterate though all saved genre ids to find a match
            for (int i = 0; i < genreIds.length; i++) {
                if (genreIds[i] == singeGenreId) {
                    // The value has been found. Save it and exit
                    genreIndex = i;
                    break;
                }
            }

            // Check if this is the last genre
            if (output.equals(singleGenreIdString[(arrayLength - 1)])) {
                // This is the last genre, so don't add a comma
                finalGenres.append(genreNames[genreIndex]);
            } else {
                finalGenres.append(genreNames[genreIndex] + ", ");
            }
        }
        return finalGenres.toString();
    }

    /** Helper method which converts the date from yyyy-MM-dd to dd/MM/yyyy */
    public static String convertDateFormat(String input) {
        // Create input and output patterns
        String oldFormat = "yyyy-MM-dd";
        String newFormat = "dd/MM/yyyy";
        // Create SimpleDateFormats using the above patterns
        SimpleDateFormat inputFormat = new SimpleDateFormat(oldFormat);
        SimpleDateFormat outputFormat = new SimpleDateFormat(newFormat);

        Date date;
        String outputString = null;
        try {
            date = inputFormat.parse(input);
            outputString = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputString;
    }
}
