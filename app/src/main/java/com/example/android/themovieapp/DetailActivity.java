package com.example.android.themovieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    /** Tag to be used for logging messages */
    private final static String TAG = DetailActivity.class.getSimpleName();

    /** Key used when passing the current movie to the activity */
    private final static String KEY_CURRENT_MOVIE = "current movie";

    /** Movie object holding the movie passed on with the activity */
    private static Movie mCurrentMovie;

    /** Movie original title */
    private String mTitle;

    /** Movie overview (plot synopsis) */
    private String mOverview;

    /** Movie poster path */
    private String mPosterPath;

    /** Movie release date */
    private String mReleaseDate;

    /** Movie Rating */
    private double mRating;

    /** Movie genres */
    private static ArrayList<Integer> mGenres;

    /** TextViews holding movie properties */
    TextView mTitleTextView, mOverViewTextView, mReleaseDateTextView, mGenreTextView;

    /** ImageView showing the movie poster */
    ImageView mPosterImageView;

    /** Rating bar for the movie rating */
    RatingBar mRatingBar;

    /** Integer array holding genre ids */
    private static int[] genreIds;

    /** String array holding genre names */
    private static String[] genreNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Get the intent used to create this activity
        Intent movieIntent = getIntent();
        // Get the parcelable object passed on with the activity
        mCurrentMovie = movieIntent.getParcelableExtra(KEY_CURRENT_MOVIE);

        // Get movie properties
        retrieveMovieProperties();

        // Get views
        prepareViews();

        // Get array values
        genreIds = getResources().getIntArray(R.array.genre_id);
        genreNames = getResources().getStringArray(R.array.genre_name);

        // Set text for movie title
        mTitleTextView.setText(mTitle);
        // Set text for overview
        mOverViewTextView.setText(mOverview);
        // Convert the date format and set it on the text view
        mReleaseDateTextView.setText(convertDateFormat(mReleaseDate));
        // Set rating in rating bar (divide by 2 to get 0 out of 5 rating)
        mRatingBar.setRating((float) (mRating / 2.0));
        // Match the genre id with the genre name and display it
        matchGenres();

        // Check if there is a poster and set image as necessary
        if (mPosterPath.equals("no image")) {
            mPosterImageView.setImageResource(R.drawable.no_poster);
        } else {
            Picasso.with(DetailActivity.this).load(mPosterPath).into(mPosterImageView);
        }
    }

    /**
     * Helper method that retrieves all movie properties and assigns them to the appropriate
     * variables
     */
    private void retrieveMovieProperties() {
        mTitle = mCurrentMovie.getmTitle();
        mOverview = mCurrentMovie.getmOverview();
        mPosterPath = mCurrentMovie.getmPosterPathLarge();
        mReleaseDate = mCurrentMovie.getmReleaseDate();
        mRating = mCurrentMovie.getmRating();
        mGenres = mCurrentMovie.getmGenreIds();
    }

    /**
     * Helper method that finds all views within the layout
     */
    private void prepareViews() {
        mPosterImageView = (ImageView) findViewById(R.id.iv_poster_image);
        mTitleTextView = (TextView) findViewById(R.id.tv_movie_title);
        mOverViewTextView = (TextView) findViewById(R.id.tv_overview);
        mReleaseDateTextView = (TextView) findViewById(R.id.tv_release_date);
        mGenreTextView = (TextView) findViewById(R.id.tv_genre);
        mRatingBar = (RatingBar) findViewById(R.id.rb_rating);
    }

    /**
     * Helper method which converts the date from yyyy-MM-dd to dd/MM/yyyy
     */
    private String convertDateFormat(String input) {
        // Create input and output patterns
        String oldFormat = "yyyy-MM-dd";
        String newFormat = "dd/MM/yyyy";
        // Create SimpleDateFormats using the above patterns
        SimpleDateFormat inputFormat = new SimpleDateFormat(oldFormat);
        SimpleDateFormat outputFormat = new SimpleDateFormat(newFormat);

        Date date = null;
        String outputString = null;
        try {
            date = inputFormat.parse(input);
            outputString = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputString;
    }

    /**
     * Matches the genre id with the genre name and displays it on the text view
     */
    private void matchGenres() {
        // Iterate though all genre ids for the current movie
        for (int i:mGenres) {
            // Test index
            int genreIndex = -1;
            for (int j = 0; j < genreIds.length; j ++) {
                if (genreIds[j] == i) {
                    // The value has been found. Save it and exit
                    genreIndex = j;
                    break;
                }
            }
            // Get the corresponding value in the array of genre names
            String genreName = genreNames[genreIndex];
            // Check if this is the last genre
            if (i == mGenres.get(mGenres.size() - 1)) {
                // This is the last genre, so don't add a comma
                mGenreTextView.append(genreName);
            } else {
                mGenreTextView.append(genreName + ", ");
            }
        }
    }
}