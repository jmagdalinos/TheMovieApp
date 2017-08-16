package com.example.android.themovieapp.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.themovieapp.Data.MovieContract.FavEntry;
import com.example.android.themovieapp.Data.MovieContract.MovieEntry;
import com.example.android.themovieapp.Data.MovieContract.ReviewsEntry;
import com.example.android.themovieapp.Data.MovieContract.TrailersEntry;

/**
 * Helper class that creates the database and its tables
 */

public class MovieDbHelper extends SQLiteOpenHelper {
    /** The name of the database */
    private static final String DATABASE_NAME = "moviedb.db";

    /** The version of the database */
    private static final int DATABASE_VERSION = 1;

    /** Constructor for the helper object */
    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /** Called when creating a database */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement that creates the movie table
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                MovieEntry.COLUMN_RATING + " INTEGER, " +
                MovieEntry.COLUMN_GENRE_IDS + " TEXT, " +
                MovieEntry.COLUMN_POSTER + " TEXT, " +
                "UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        // SQL statement that creates the favourites table
        final String SQL_CREATE_FAVS_TABLE = "CREATE TABLE " +
                FavEntry.TABLE_NAME + " (" +
                FavEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                FavEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                FavEntry.COLUMN_OVERVIEW + " TEXT, " +
                FavEntry.COLUMN_RATING + " INTEGER, " +
                FavEntry.COLUMN_GENRE_IDS + " TEXT, " +
                FavEntry.COLUMN_POSTER + " TEXT, " +
                "UNIQUE (" + FavEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        // SQL statement that creates the trailers table
        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " +
                TrailersEntry.TABLE_NAME + " (" +
                TrailersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TrailersEntry.COLUMN_NAME + " TEXT, " +
                TrailersEntry.COLUMN_SOURCE + " TEXT" + ");";

        // SQL statement that creates the reviews table
        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " +
                ReviewsEntry.TABLE_NAME + " (" +
                ReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ReviewsEntry.COLUMN_AUTHOR + " TEXT, " +
                ReviewsEntry.COLUMN_CONTENT + " TEXT, " +
                ReviewsEntry.COLUMN_URL + " TEXT" + ");";

        // Create the tables
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_FAVS_TABLE);
        db.execSQL(SQL_CREATE_TRAILERS_TABLE);
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);
    }

    /** Called when upgrading the version */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +
                    MovieEntry.TABLE_NAME + ", " +
                    FavEntry.TABLE_NAME + ", " +
                    TrailersEntry.TABLE_NAME + ", " +
                    ReviewsEntry.TABLE_NAME);
            onCreate(db);
        }
    }
}
