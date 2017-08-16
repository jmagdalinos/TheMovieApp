package com.example.android.themovieapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.themovieapp.Data.MovieContract.FavEntry;
import com.example.android.themovieapp.Data.MovieContract.MovieEntry;
import com.example.android.themovieapp.Data.MovieContract.ReviewsEntry;
import com.example.android.themovieapp.Data.MovieContract.TrailersEntry;
import com.example.android.themovieapp.Utilities.MovieUtilities;
import com.example.android.themovieapp.databinding.ActivityDetailBinding;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        TrailerListAdapter.TrailerAdapterClickHandler,
        ReviewListAdapter.ReviewAdapterClickHandler {

    /** Tag to be used for logging messages */
    private final static String TAG = DetailActivity.class.getSimpleName();

    /** Key used when passing the current movie to the activity */
    private final static String KEY_CURRENT_MOVIE_ID = "current_movie_id";

    /** Key used when saving the first trailer source in onSaveState */
    private static final String STATE_FIRST_TRAILER = "first_trailer_source";

    /** Id of the cursor loader */
    private static final int CURRENT_MOVIE_LOADER_ID = 50;
    private static final int TRAILER_LOADER_ID = 60;
    private static final int REVIEW_LOADER_ID = 70;
    private static final int FAVOURITE_CHECK_LOADER_ID = 90;

    /** Flag stating if a movie is in the favourites table */
    private boolean isFavourite = false;

    /** Movie id */
    private static int mId;

    /** Movie Attributes */
    private String mTitle, mOverview, mPosterPath, mReleaseDate, mGenres;

    /** Movie Rating */
    private double mRating;

    /** Data binding object */
    ActivityDetailBinding mBinding;

    /** Instance of the trailer and review list adapters */
    private TrailerListAdapter mTrailerListAdapter;
    private ReviewListAdapter mReviewListAdapter;

    /** Menu item with favourite icon */
    private static MenuItem favouriteIcon;

    /** Save a copy of the movies' content values so we can insert them into the favourites */
    private static ContentValues currentMovie;

    /** Save the source of the first trailer so it can be later shared */
    private String mFirstTrailerUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
        }

        // Get the intent used to create this activity
        Intent movieIntent = getIntent();
        // Get the parcelable object passed on with the activity
        mId = movieIntent.getIntExtra(KEY_CURRENT_MOVIE_ID, 0);

        // Initialize the loader
        getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(CURRENT_MOVIE_LOADER_ID, null, this);

        // Create linear layout managers and assign them to the recycler views
        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this);
        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this);
        mBinding.rvTrailers.setLayoutManager(trailerLayoutManager);
        mBinding.rvReviews.setLayoutManager(reviewLayoutManager);

        // If this is a restored activity, retrieve the value of the first trailer source
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_FIRST_TRAILER)) {
            mFirstTrailerUri = savedInstanceState.getString(STATE_FIRST_TRAILER);
        }

        // Set the divider for the recycler views
        mBinding.rvTrailers.addItemDecoration(new DividerItemDecoration(ContextCompat
                .getDrawable(this, R.drawable.line_divider)));
        mBinding.rvReviews.addItemDecoration(new DividerItemDecoration(ContextCompat
                .getDrawable(this, R.drawable.line_divider)));


        // Disable nested scrolling to show entire contents of recycle view
        mBinding.rvTrailers.setNestedScrollingEnabled(false);
        mBinding.rvReviews.setNestedScrollingEnabled(false);

        // To improve performance, we make it so the child layout size in the RecyclerView does
        // not change
        mBinding.rvTrailers.setHasFixedSize(true);
        mBinding.rvReviews.setHasFixedSize(true);

        // Create a new instance of a TrailerListAdapter and a ReviewListAdapter
        mTrailerListAdapter = new TrailerListAdapter(this, this);
        mReviewListAdapter = new ReviewListAdapter(this, this);

        // Set the adapters on the recycler views
        mBinding.rvTrailers.setAdapter(mTrailerListAdapter);
        mBinding.rvReviews.setAdapter(mReviewListAdapter);

    }

    /** Save the value of the first trailer source */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_FIRST_TRAILER, mFirstTrailerUri);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        // Check which loader was initiated
        switch (loaderId) {
            case CURRENT_MOVIE_LOADER_ID :
                // Build the query uri using the movie_id
                Uri movieQueryUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, mId);

                // Build the projection
                String[] movieProjection = {
                        MovieEntry.COLUMN_MOVIE_ID,
                        MovieEntry.COLUMN_TITLE,
                        MovieEntry.COLUMN_OVERVIEW,
                        MovieEntry.COLUMN_POSTER,
                        MovieEntry.COLUMN_RELEASE_DATE,
                        MovieEntry.COLUMN_RATING,
                        MovieEntry.COLUMN_GENRE_IDS};

                // Return the cursor loader
                return new CursorLoader(this,
                        movieQueryUri,
                        movieProjection,
                        null,
                        null,
                        null);
            case TRAILER_LOADER_ID:
                // Build the query uri using the movie_id
                Uri trailerQueryUri = TrailersEntry.CONTENT_URI;

                // Build the projection
                String[] trailerProjection = {
                        TrailersEntry.COLUMN_NAME,
                        TrailersEntry.COLUMN_SOURCE};

                // Return the cursor loader
                return new CursorLoader(this,
                        trailerQueryUri,
                        trailerProjection,
                        null,
                        null,
                        TrailersEntry.COLUMN_NAME + " ASC");
            case REVIEW_LOADER_ID:
                // Build the query uri using the movie_id
                Uri reviewQueriUri = ReviewsEntry.CONTENT_URI;

                // Build the projection
                String[] reviewProjection = {
                        ReviewsEntry.COLUMN_AUTHOR,
                        ReviewsEntry.COLUMN_CONTENT,
                        ReviewsEntry.COLUMN_URL};
                return new CursorLoader(this,
                        reviewQueriUri,
                        reviewProjection,
                        null,
                        null,
                        ReviewsEntry.COLUMN_AUTHOR + " ASC");
            case FAVOURITE_CHECK_LOADER_ID:
                // Build the query uri using the movie_id
                Uri favouriteCheckQueryUri = ContentUris.withAppendedId(FavEntry.CONTENT_URI, mId);

                // Build the projection
                String[] favouriteCheckProjection = {
                        FavEntry.COLUMN_MOVIE_ID};
                return new CursorLoader(this,
                    favouriteCheckQueryUri,
                    favouriteCheckProjection,
                    null,
                    null,
                    null);
            default:
                throw new RuntimeException("Loader" + loaderId + " not implemented");
        }
    }

    /** Called when the loader has finished */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case CURRENT_MOVIE_LOADER_ID:
                // Movie data was synced
                readMovieCursor(data);
                break;
            case TRAILER_LOADER_ID:
                // Trailer data was synced
                // Pass the cursor to the adapter
                mTrailerListAdapter.swapCursor(data);

                if (data != null && data.getCount() != 0) {
                    // Move to the first row
                    data.moveToFirst();
                    // Retrieve and save the first source so it can be shared later
                    mFirstTrailerUri = data.getString(data.getColumnIndex(TrailersEntry
                            .COLUMN_SOURCE));
                } else {
                    mFirstTrailerUri = null;
                }

                break;
            case REVIEW_LOADER_ID:
                // Reviews data was synced
                // Pass the cursor to the adapter
                mReviewListAdapter.swapCursor(data);
                break;
            case FAVOURITE_CHECK_LOADER_ID:
                // The favourites tables was queried to check if current movie is a favourite
                if (data != null && data.getCount() != 0) {
                    // Set the flag to true
                    isFavourite = true;
                    // Change icon
                    changeFavouriteIcon();
                }
                break;
            default:
                throw new RuntimeException("Loader" + loader.getId() + " not implemented");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTrailerListAdapter.swapCursor(null);
        mReviewListAdapter.swapCursor(null);
    }

    /** Reads movie data from cursor and populates the layout */
    private void readMovieCursor(Cursor cursor) {
        if (cursor != null && cursor.getCount() != 0) {
            // Move to the first (and only) row of the cursor
            cursor.moveToFirst();

            // Get the movie attributes
            mTitle = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_TITLE));
            mOverview = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW));
            mReleaseDate = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE));
            mRating = cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_RATING));
            mGenres = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_GENRE_IDS));

            // Build the complete poster path
            mPosterPath = MovieUtilities.buildPosterPath(cursor.getString(cursor
                    .getColumnIndex(MovieEntry.COLUMN_POSTER)), MovieUtilities.FILE_SIZE_FULL);

            // Display the movie attributes
            mBinding.tvMovieTitle.setText(mTitle);
            mBinding.tvOverview.setText(mOverview);
            mBinding.tvReleaseDate.setText(MovieUtilities.convertDateFormat(mReleaseDate));

            // // Match the genre id with the genre name and display it
            mBinding.tvGenre.setText(MovieUtilities.matchGenres(this, mGenres));

            // Display rating using the rating bar (divide by 2 to get 0 out of 5 rating)
            mBinding.rbRating.setRating((float) (mRating / 2.0));

            // Check if there is a poster and set image as necessary
            if (mPosterPath.equals("no image")) {
                mBinding.ivPosterImage.setImageResource(R.drawable.no_poster);
            } else {
                Picasso.with(DetailActivity.this).load(mPosterPath).into(mBinding.ivPosterImage);
            }

            // Save the current movie into a content values object so we can insert it into the
            // favourites table if needed
            currentMovie = new ContentValues();
            currentMovie.put(FavEntry.COLUMN_MOVIE_ID, mId);
            currentMovie.put(FavEntry.COLUMN_TITLE, mTitle);
            currentMovie.put(FavEntry.COLUMN_OVERVIEW, mOverview);
            currentMovie.put(FavEntry.COLUMN_RELEASE_DATE, mReleaseDate);
            currentMovie.put(FavEntry.COLUMN_RATING, mRating);
            currentMovie.put(FavEntry.COLUMN_GENRE_IDS, mGenres);
            currentMovie.put(FavEntry.COLUMN_POSTER, cursor.getString(cursor
                    .getColumnIndex(MovieEntry.COLUMN_POSTER)));
        }
    }

    /** Responds to user click by creating an intent that displays the trailer */
    @Override
    public void onTrailerClick(String trailer_source) {
        // Play the video on youtube
        startActivity(new Intent(Intent.ACTION_VIEW, createYoutubeUri(trailer_source)));
    }

    /** Responds to user click by creating an intent that displays the review */
    @Override
    public void onReviewClick(String review_url) {
        // Create the youtube url
        // Create the review url
        final Uri reviewUri = Uri.parse(review_url).buildUpon().build();
        startActivity(new Intent(Intent.ACTION_VIEW, reviewUri));
    }

    /** Changes the favourite icon depending on the isFavourite flag */
    private void changeFavouriteIcon() {
        if (isFavourite) {
            favouriteIcon.setIcon(R.drawable.ic_favorite_white_24dp);
        } else {
            favouriteIcon.setIcon(R.drawable.ic_favorite_border_white_24dp);
        }
    }

    /** Creates the full youtube uri for the trailer */
    private Uri createYoutubeUri(String trailer_source) {
        // Create the youtube url
        return Uri.parse("https://www.youtube.com/watch").buildUpon()
                .appendQueryParameter("v", trailer_source)
                .build();
    }

    /** Creates the menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the appropriate menu
        getMenuInflater().inflate(R.menu.detail_menu, menu);

        // Get the favourites icon
        favouriteIcon = menu.getItem(0);

        changeFavouriteIcon();
        // Check if the current movie is a favourite
        getSupportLoaderManager().initLoader(FAVOURITE_CHECK_LOADER_ID, null, this);
        return true;
    }

    /** Implement menu button functionality */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_mark_favourite:
                // Marks/unmarks a movie as a favourite
                if (!isFavourite) {
                    // This is not a favourite movie
                    if (currentMovie != null && currentMovie.size() != 0) {
                        // Get the content resolver and insert the movie to the favourites table
                        getContentResolver().insert(FavEntry.CONTENT_URI, currentMovie);
                        // Set the flag to true
                        isFavourite = true;
                        // Change the icon
                        changeFavouriteIcon();
                    }
                } else {
                    // This is not a favourite movie
                    // Get the content resolver and delete the movie from the favourites table
                    getContentResolver().delete(ContentUris.withAppendedId(FavEntry.CONTENT_URI, mId),
                            null,
                            null);
                    // Set the flag to false
                    isFavourite = false;
                    // Change the icon
                    changeFavouriteIcon();
                }

                break;
            case R.id.action_share:
                // Share the first trailer by creating a ShareCompatIntent
                if (mFirstTrailerUri != null) {
                    Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                            .setType("text/*")
                            .setText(createYoutubeUri(mFirstTrailerUri).toString())
                            .getIntent();
                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                    startActivity(shareIntent);
                }
                break;
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Delete all rows from trailers and reviews
        getContentResolver().delete(TrailersEntry.CONTENT_URI,null,null);
        getContentResolver().delete(ReviewsEntry.CONTENT_URI,null,null);

        super.onBackPressed();
    }
}