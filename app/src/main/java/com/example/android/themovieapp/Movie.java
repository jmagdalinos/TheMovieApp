package com.example.android.themovieapp;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Movie object that will hold all information for a single movie, as they are retrieved from the
 * movie db
 */

public class Movie implements Parcelable {

    /** Movie original title */
    private String mTitle;

    /** Movie overview (plot synopsis) */
    private String mOverview;

    /** Movie poster file path for use in thumbnails */
    private String mPosterPathSmall;

    /** Movie poster file path for use in large images */
    private String mPosterPathLarge;

    /** Movie release date (String format) */
    private String mReleaseDate;

    /** Movie rating */
    private double mRating;

    /** Movie genre ids */
    private ArrayList<Integer> mGenreIds;

    /** Base url used to build the complete poster image path */
    private static final String BASE_URL = "http://image.tmdb.org/t/p/";

    /** File size ued to build the complete poster image path for thumbnails */
    private static final String FILE_SIZE_THUMBNAIL = "w185";

    /** File size ued to build the complete poster image path for large images */
    private static final String FILE_SIZE_FULL = "w500";


    /**
     * Constructor for the Movie object
     * @param title the original title
     * @param overview the plot synopsis
     * @param posterPath the path to the poster image thumbnail
     * @param releaseDate the release date
     * @param rating the rating (double)
     */
    public Movie(String title, String overview, String posterPath, String releaseDate, double
            rating, ArrayList<Integer> genreIds) {

        mTitle = title;
        mOverview = overview;
        mPosterPathSmall = buildPosterPath(posterPath, FILE_SIZE_THUMBNAIL);
        mPosterPathLarge = buildPosterPath(posterPath, FILE_SIZE_FULL);
        mReleaseDate = releaseDate;
        mRating = rating;
        mGenreIds = genreIds;
    }

    /**
     * Constructor for the Movie object using a parcel
     */
    public Movie(Parcel parcel) {
        mTitle = parcel.readString();
        mOverview = parcel.readString();
        mPosterPathSmall = parcel.readString();
        mPosterPathLarge = parcel.readString();
        mReleaseDate = parcel.readString();
        mRating = parcel.readDouble();
        mGenreIds = parcel.readArrayList(ArrayList.class.getClassLoader());
    }

    /** Method allowing the retrieval of the movie name */
    public String getmTitle() {
        return mTitle;
    }

    /** Method allowing the retrieval of the movie overview */
    public String getmOverview() {
        return mOverview;
    }

    /** Method allowing the retrieval of the movie poster path for thumbnails*/
    public String getmPosterPathSmall() {
        return mPosterPathSmall;
    }

    /** Method allowing the retrieval of the movie poster path for large images*/
    public String getmPosterPathLarge() {
        return mPosterPathLarge;
    }

    /** Method allowing the retrieval of the movie release date */
    public String getmReleaseDate() {
        return mReleaseDate;
    }

    /** Method allowing the retrieval of the movie rating */
    public double getmRating() {
        return mRating;
    }

    public ArrayList<Integer> getmGenreIds() {return mGenreIds;}

    /** Method that uses the 3 parts of the movie poster path to build the complete path */
    private String buildPosterPath(String path, String fileSize) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Write all movie object properties to the parcel
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mOverview);
        dest.writeString(mPosterPathSmall);
        dest.writeString(mPosterPathLarge);
        dest.writeString(mReleaseDate);
        dest.writeDouble(mRating);
        dest.writeList(mGenreIds);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
