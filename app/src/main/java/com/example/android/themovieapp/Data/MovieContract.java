package com.example.android.themovieapp.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract class containing the following tables:
 *      movies: contains a cached database for use offline
 *      favourites: contains the movies the user has chosen as favourites
 *      trailers: contains a temporary list of trailers for a single movie
 *      reviews: contains a temporary list of reviews for a single movie
 */

public class MovieContract {

    /** Content authority for the content provider */
    public static final String CONTENT_AUTHORITY = "com.example.android.themovieapp";

    /** The base for all uris used to contact the content provider */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /** Possible paths that can be appended to BASE_CONTENT_URI to form valid URLs */
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_FAVS = "favourites";
    public static final String PATH_TRAILERS = "trailers";
    public static final String PATH_REVIEWS = "reviews";

    /** Inner class that defines the table contents of the movies table */
    public static final class MovieEntry implements BaseColumns {
        /** The base CONTENT_URI used to query the favourites table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES).build();

        /** The MIME type of the {@link #CONTENT_URI} for a list of movies */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/";

        /** The MIME type of the {@link #CONTENT_URI} for a single movie */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/";


        /** Name of the favourites table */
        public static final String TABLE_NAME = "movies";

        /** Column containing movie name as TEXT */
        public static final String COLUMN_TITLE = "title";

        /** Column containing movie overview as TEXT */
        public static final String COLUMN_OVERVIEW = "overview";

        /** Column containing movie poster path as TEXT */
        public static final String COLUMN_POSTER = "poster";

        /** Column containing movie release date as TEXT */
        public static final String COLUMN_RELEASE_DATE = "date";

        /** Column containing movie rating as INTEGER */
        public static final String COLUMN_RATING = "rating";

        /**
         * Column containing movie genre ids as TEXT
         * Original values come in a form of an array which will be concatenated in a single,
         * comma-separated string and saved in the database
         */
        public static final String COLUMN_GENRE_IDS = "genre_ids";

        /**
         * Column containing movie ids stored as INTEGER
         * This is different from the row id issued by BaseColumns.
         * It is the id for each movie as stored in the movie db servers.
         */
        public static final String COLUMN_MOVIE_ID = "movie_id";

    }

    /** Inner class that defines the table contents of the favourites table */
    public static final class FavEntry implements BaseColumns {
        /** The base CONTENT_URI used to query the favourites table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVS)
                .build();

        /** The MIME type of the {@link #CONTENT_URI} for a list of favourites */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/";

        /** The MIME type of the {@link #CONTENT_URI} for a single favourite */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/";

        /** Name of the favourites table */
        public static final String TABLE_NAME = "favs";

        /** Column containing movie name as TEXT */
        public static final String COLUMN_TITLE = "title";

        /** Column containing movie overview as TEXT */
        public static final String COLUMN_OVERVIEW = "overview";

        /** Column containing movie poster path as TEXT */
        public static final String COLUMN_POSTER = "poster";

        /** Column containing movie release date as TEXT */
        public static final String COLUMN_RELEASE_DATE = "date";

        /** Column containing movie rating as INTEGER */
        public static final String COLUMN_RATING = "rating";

        /**
         * Column containing movie genre ids as TEXT
         * Original values come in a form of an array which will be concatenated in a single,
         * comma-separated string and saved in the database
         */
        public static final String COLUMN_GENRE_IDS = "genre_ids";

        /**
         * Column containing movie ids stored as INTEGER
         * This is different from the row id issued by BaseColumns.
         * It is the id for each movie as stored in the movie db servers.
         */
        public static final String COLUMN_MOVIE_ID = "movie_id";
    }

    /** Inner class that defines the table contents of the trailers table */
    public static final class TrailersEntry implements BaseColumns {
        /** The base CONTENT_URI used to query the trailers table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TRAILERS)
                .build();

        /** The MIME type of the {@link #CONTENT_URI} for a list of trailers */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/";

        /** The MIME type of the {@link #CONTENT_URI} for a single trailer */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/";

        /** Name of the trailers table */
        public static final String TABLE_NAME = "trailers";

        /** Column containing trailer name as TEXT */
        public static final String COLUMN_NAME = "name";

        /** Column containing trailer source as TEXT */
        public static final String COLUMN_SOURCE = "source";
    }

    /** Inner class that defines the table contents of the reviews table */
    public static final class ReviewsEntry implements BaseColumns {
        /** The base CONTENT_URI used to query the trailers table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_REVIEWS)
                .build();

        /** The MIME type of the {@link #CONTENT_URI} for a list of trailers */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/";

        /** The MIME type of the {@link #CONTENT_URI} for a single trailer */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/";

        /** Name of the trailers table */
        public static final String TABLE_NAME = "reviews";

        /** Column containing review author as TEXT */
        public static final String COLUMN_AUTHOR = "author";

        /** Column containing review content as TEXT */
        public static final String COLUMN_CONTENT = "content";

        /** Column containing review url as TEXT */
        public static final String COLUMN_URL = "url";
    }
}
