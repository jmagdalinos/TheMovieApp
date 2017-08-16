package com.example.android.themovieapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.themovieapp.Data.MovieContract;
import com.example.android.themovieapp.Utilities.MovieUtilities;
import com.squareup.picasso.Picasso;

/**
 * Custom adapter that feeds the movie posters to a recycler view
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {

    /** Cursor holding the list of movies retrieved from the movie db */
    private Cursor mCursor;

    /** Context used throughout the adapter */
    private final Context mContext;

    /** ImageView that will show the movie poster */
    private ImageView mPosterImageView;

    /** Instance of MovieAdapterClickHandler used to when the user clicks on the poster in the
     * recycler view */
    private final MovieAdapterClickHandler mClickHandler;

    /** Constructor for the adapter */
    public MovieListAdapter(Context context, MovieAdapterClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    /** Interface to enable click functionality to the recycler view */
    public interface MovieAdapterClickHandler {
        void onPosterClick(int movie_id);
    }

    /** OnCreateViewHolder creates the custom viewholder */
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Get the context from the viewGroup view
        Context context = viewGroup.getContext();
        // Inflate the view using the movie_list_item as a layout
        int listItemResourceId = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(listItemResourceId, viewGroup, false);

        // Create an instance of the viewholder
        MovieViewHolder holder = new MovieViewHolder(view);
        return holder;
    }

    /** Sets the image thumbnail using picasso */
    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        // Move the cursor to the current position
        mCursor.moveToPosition(position);
        // Build the complete poster path
        String completePosterPath = MovieUtilities.buildPosterPath(mCursor.getString(mCursor
                .getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER)), MovieUtilities.FILE_SIZE_FULL);
        // Check if there is a poster
        if (completePosterPath.equals("no image")) {
            mPosterImageView.setImageResource(R.drawable.no_poster);
        } else {
            Picasso.with(mContext).load(completePosterPath).into (mPosterImageView);
        }
    }

    /** Returns the number of items in the adapter */
    @Override
    public int getItemCount() {
        // Check if the cursor is empty
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    /** The following method is overwritten to avoid duplication of views */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /** The following method is overwritten to avoid duplication of views */
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /** Swaps the cursor with the one created in the catalog activity by the loader */
    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    /** Custom viewholder which implements OnClickListener to enable clicking on the viewholder */
    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /**
         * Constructor for the viewholder
         * @param view the view using the movie_list_item a layout
         */
        public MovieViewHolder(View view) {
            super(view);
            // Find the image view that will hold the movie poster
            mPosterImageView = (ImageView) view.findViewById(R.id.im_movie_poster);
            // Set the OnClickListener to the view
            view.setOnClickListener(this);
        }

        /** Passes the current movie_id to the instance of MovieAdapterClickHandler */
        @Override
        public void onClick(View v) {
            // Move the cursor to the current position
            mCursor.moveToPosition(getAdapterPosition());

            // Pass the current movie id to the onPosterClick method
            mClickHandler.onPosterClick(mCursor.getInt(mCursor.getColumnIndex(MovieContract
                    .MovieEntry.COLUMN_MOVIE_ID)));
        }
    }
}
