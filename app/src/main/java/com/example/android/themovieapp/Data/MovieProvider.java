package com.example.android.themovieapp.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.android.themovieapp.Data.MovieContract.FavEntry;
import com.example.android.themovieapp.Data.MovieContract.MovieEntry;
import com.example.android.themovieapp.Data.MovieContract.ReviewsEntry;
import com.example.android.themovieapp.Data.MovieContract.TrailersEntry;

/**
 * Content provider used to access the database
 */

public class MovieProvider extends ContentProvider {

    /** Member variable for the MovieDbHelper initialized in onCreate */
    private static MovieDbHelper mMovieDbHelper;

    /** Constants used in the URI Matcher */
    private static final int CODE_MOVIES = 200;
    private static final int CODE_MOVIES_ITEM = 201;
    private static final int CODE_FAV = 300;
    private static final int CODE_FAV_ITEM = 301;
    private static final int CODE_TRAILERS = 400;
    private static final int CODE_TRAILERS_ITEM = 401;
    private static final int CODE_REVIEWS = 500;
    private static final int CODE_REVIEWS_ITEM = 501;

    /** The URI Matcher used by this content provider */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /** Creates the URI Matcher that will match each URI to the appropriate code values */
    private static UriMatcher buildUriMatcher() {
        // Create the URI matcher and get the content authority
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String contentAuthority = MovieContract.CONTENT_AUTHORITY;

        // Add all the possible acceptable URIs with the corresponding codes
        uriMatcher.addURI(contentAuthority, MovieContract.PATH_MOVIES, CODE_MOVIES);
        uriMatcher.addURI(contentAuthority, MovieContract.PATH_MOVIES + "/#", CODE_MOVIES_ITEM);
        uriMatcher.addURI(contentAuthority, MovieContract.PATH_FAVS, CODE_FAV);
        uriMatcher.addURI(contentAuthority, MovieContract.PATH_FAVS + "/#", CODE_FAV_ITEM);
        uriMatcher.addURI(contentAuthority, MovieContract.PATH_TRAILERS, CODE_TRAILERS);
        uriMatcher.addURI(contentAuthority, MovieContract.PATH_TRAILERS + "/#", CODE_TRAILERS_ITEM);
        uriMatcher.addURI(contentAuthority, MovieContract.PATH_REVIEWS, CODE_REVIEWS);
        uriMatcher.addURI(contentAuthority, MovieContract.PATH_REVIEWS + "/#", CODE_REVIEWS_ITEM);

        return uriMatcher;
    }

    /** Initialize the mMovieDbHelper */
    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    /** Returns a cursor with the queried rows */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        // Use sUriMatcher to use the correct actions
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES:
                // Returns a cursor with that contains every row of data of the movies table
                cursor = mMovieDbHelper.getReadableDatabase().query(MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_MOVIES_ITEM:
                // Get the movie_id from the URI
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                // Returns a cursor with that contains a single row of data of the movies table
                // The identifier for the row is the MOVIE_ID
                cursor = mMovieDbHelper.getReadableDatabase().query(MovieEntry.TABLE_NAME,
                        projection,
                        MovieEntry.COLUMN_MOVIE_ID + "=?",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_FAV:
                // Returns a cursor with that contains every row of data of the favourites table
                cursor = mMovieDbHelper.getReadableDatabase().query(FavEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_FAV_ITEM:
                // Get the movie_id from the URI
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                // Returns a cursor with that contains a single row of data of the favourites table
                // The identifier for the row is the MOVIE_ID
                cursor = mMovieDbHelper.getReadableDatabase().query(FavEntry.TABLE_NAME,
                        projection,
                        FavEntry.COLUMN_MOVIE_ID + "=?",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_TRAILERS:
                // Returns a cursor with that contains every row of data of the trailers table
                cursor = mMovieDbHelper.getReadableDatabase().query(TrailersEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_TRAILERS_ITEM:
                // Get the movie_id from the URI
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                // Returns a cursor with that contains a single row of data of the trailers table
                // The identifier for the row is the MOVIE_ID
                cursor = mMovieDbHelper.getReadableDatabase().query(TrailersEntry.TABLE_NAME,
                        projection,
                        TrailersEntry._ID + "=?",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_REVIEWS:
                // Returns a cursor with that contains every row of data of the reviews table
                cursor = mMovieDbHelper.getReadableDatabase().query(ReviewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_REVIEWS_ITEM:
                // Get the movie_id from the URI
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                // Returns a cursor with that contains a single row of data of the reviews table
                // The identifier for the row is the MOVIE_ID
                cursor = mMovieDbHelper.getReadableDatabase().query(ReviewsEntry.TABLE_NAME,
                        projection,
                        ReviewsEntry._ID + "=?",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Register to watch a content URI for changes.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /** Returns the MIME type */
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES:
                return MovieEntry.CONTENT_LIST_TYPE;
            case CODE_MOVIES_ITEM:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case CODE_FAV:
                return FavEntry.CONTENT_LIST_TYPE;
            case CODE_FAV_ITEM:
                return FavEntry.CONTENT_ITEM_TYPE;
            case CODE_TRAILERS:
                return TrailersEntry.CONTENT_LIST_TYPE;
            case CODE_TRAILERS_ITEM:
                return TrailersEntry.CONTENT_ITEM_TYPE;
            case CODE_REVIEWS:
                return ReviewsEntry.CONTENT_LIST_TYPE;
            case CODE_REVIEWS_ITEM:
                return ReviewsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + sUriMatcher.match(uri));
        }
    }

    /** Inserts a single row and returns the uri of the inserted row */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long id;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES:
                // Insert a row into movies table
                id = mMovieDbHelper.getWritableDatabase().insert(MovieEntry.TABLE_NAME,
                        null,
                        values);
                break;
            case CODE_FAV:
                // Insert a row into favourites table
                id = mMovieDbHelper.getWritableDatabase().insert(FavEntry.TABLE_NAME,
                        null,
                        values);
                break;
            case CODE_TRAILERS:
                // Insert a row into trailers table
                id = mMovieDbHelper.getWritableDatabase().insert(TrailersEntry.TABLE_NAME,
                        null,
                        values);
                break;
            case CODE_REVIEWS:
                // Insert a row into reviews table
                id = mMovieDbHelper.getWritableDatabase().insert(ReviewsEntry.TABLE_NAME,
                        null,
                        values);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (id > 0) {
            // The operation was successful. Notify all listeners
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return a new URI with the id of the inserted row
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        // Get an instance of a writable database
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int rowsInserted;
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES:
                // Begin transaction with the database
                db.beginTransaction();
                rowsInserted = 0;
                try {
                    // Iterate through all content values and insert them one by one
                    for (ContentValues currentValue: values) {
                        long movie_id = db.insert(MovieEntry.TABLE_NAME,
                                null,
                                currentValue);
                        // If the operation was successful, increment the value of rowsInserted
                        if (movie_id != -1) rowsInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    // End the transaction
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    // The operation was successful. Notify all listeners
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;
            case CODE_FAV:
                // Begin transaction with the database
                db.beginTransaction();
                rowsInserted = 0;

                try {
                    // Iterate through all content values and insert them one by one
                    for (ContentValues currentValue: values) {
                        long movie_id = db.insert(FavEntry.TABLE_NAME,
                                null,
                                currentValue);
                        // If the operation was successful, increment the value of rowsInserted
                        if (movie_id != -1) rowsInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    // End the transaction
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    // The operation was successful. Notify all listeners
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;
            case CODE_TRAILERS:
                // Begin transaction with the database
                db.beginTransaction();
                rowsInserted = 0;

                try {
                    // Iterate through all content values and insert them one by one
                    for (ContentValues currentValue: values) {
                        long movie_id = db.insert(TrailersEntry.TABLE_NAME,
                                null,
                                currentValue);
                        // If the operation was successful, increment the value of rowsInserted
                        if (movie_id != -1) rowsInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    // End the transaction
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    // The operation was successful. Notify all listeners
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;
            case CODE_REVIEWS:
                // Begin transaction with the database
                db.beginTransaction();
                rowsInserted = 0;

                try {
                    // Iterate through all content values and insert them one by one
                    for (ContentValues currentValue: values) {
                        long movie_id = db.insert(ReviewsEntry.TABLE_NAME,
                                null,
                                currentValue);
                        // If the operation was successful, increment the value of rowsInserted
                        if (movie_id != -1) rowsInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    // End the transaction
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    // The operation was successful. Notify all listeners
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);

        }
    }

    /** Deletes one or more rows and returns number of deleted rows */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES:
                // Delete the rows from the movies table
                rowsDeleted = mMovieDbHelper.getWritableDatabase().delete(MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_MOVIES_ITEM:
                // Get the movie_id from the URI
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // Delete the row from the movies table
                rowsDeleted = mMovieDbHelper.getWritableDatabase().delete(MovieEntry.TABLE_NAME,
                        MovieEntry.COLUMN_MOVIE_ID + "=?",
                        selectionArgs);
                break;
            case CODE_FAV:
                // Delete the rows from the favourites table
                rowsDeleted = mMovieDbHelper.getWritableDatabase().delete(FavEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_FAV_ITEM:
                // Get the movie_id from the URI
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // Delete the row from the favourites table
                rowsDeleted = mMovieDbHelper.getWritableDatabase().delete(FavEntry.TABLE_NAME,
                        FavEntry.COLUMN_MOVIE_ID + "=?",
                        selectionArgs);
                break;
            case CODE_TRAILERS:
                // Delete the rows from the trailers table
                rowsDeleted = mMovieDbHelper.getWritableDatabase().delete(TrailersEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_TRAILERS_ITEM:
                // Get the movie_id from the URI
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // Delete the row from the trailers table
                rowsDeleted = mMovieDbHelper.getWritableDatabase().delete(TrailersEntry.TABLE_NAME,
                        TrailersEntry._ID + "=?",
                        selectionArgs);
                break;
            case CODE_REVIEWS:
                // Delete the rows from the reviews table
                rowsDeleted = mMovieDbHelper.getWritableDatabase().delete(ReviewsEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_REVIEWS_ITEM:
                // Get the movie_id from the URI
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // Delete the row from the reviews table
                rowsDeleted = mMovieDbHelper.getWritableDatabase().delete(ReviewsEntry.TABLE_NAME,
                        ReviewsEntry._ID + "=?",
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted > 0) {
            // The operation was successful. Notify all listeners
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of deleted rows
        return rowsDeleted;
    }

    /** Updates one or more rows and returns number of updated rows */
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES:
                // Update the rows of the movies table
                rowsUpdated = mMovieDbHelper.getWritableDatabase().update(MovieEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CODE_MOVIES_ITEM:
                // Get the movie_id from the URI
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // Update a single row of the movies table
                rowsUpdated = mMovieDbHelper.getWritableDatabase().update(MovieEntry.TABLE_NAME,
                        values,
                        MovieEntry.COLUMN_MOVIE_ID + "=?",
                        selectionArgs);
                break;
            case CODE_FAV:
                // Update the rows of the favourites table
                rowsUpdated = mMovieDbHelper.getWritableDatabase().update(FavEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CODE_FAV_ITEM:
                // Get the movie_id from the URI
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // Update a single row of the favourites table
                rowsUpdated = mMovieDbHelper.getWritableDatabase().update(FavEntry.TABLE_NAME,
                        values,
                        FavEntry.COLUMN_MOVIE_ID + "=?",
                        selectionArgs);
                break;
            case CODE_TRAILERS:
                // Update the rows of the trailers table
                rowsUpdated = mMovieDbHelper.getWritableDatabase().update(TrailersEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CODE_TRAILERS_ITEM:
                // Get the movie_id from the URI
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // Update a single row of the trailers table
                rowsUpdated = mMovieDbHelper.getWritableDatabase().update(TrailersEntry.TABLE_NAME,
                        values,
                        TrailersEntry._ID + "=?",
                        selectionArgs);
                break;
            case CODE_REVIEWS:
                // Update the rows of the reviews table
                rowsUpdated = mMovieDbHelper.getWritableDatabase().update(ReviewsEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CODE_REVIEWS_ITEM:
                // Get the movie_id from the URI
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // Update a single row of the reviews table
                rowsUpdated = mMovieDbHelper.getWritableDatabase().update(ReviewsEntry.TABLE_NAME,
                        values,
                        ReviewsEntry._ID + "=?",
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated > 0) {
            // The operation was successful. Notify all listeners
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of deleted rows
        return rowsUpdated;
    }
}
