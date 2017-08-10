package com.example.android.themovieapp;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Custom adapter that feeds the movie posters to a recycler view
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {

    /** ImageView that will show the movie poster */
    private ImageView mPosterImageView;

    /** ArrayList holding the list of movies retrieved from the movie db */
    private ArrayList<Movie> mMovies;

    /** Instance of MovieAdapterClickHandler used to when the user clicks on the poster in the
     * recycler view */
    private final MovieAdapterClickHandler mClickHandler;

    /** The path for the image poster */
    private static Uri mImageUri;

    /** The current movie */
    private static Movie mCurrentMovie;

    /**
     * Constructor for the adapter
     */
    public MovieListAdapter(ArrayList<Movie> movies, MovieAdapterClickHandler clickHandler) {
        mClickHandler = clickHandler;
        mMovies = movies;
    }

    /**
     * Interface to enable click functionality to the recycler view
     */
    public interface MovieAdapterClickHandler {
        public void onPosterClick(Movie movie);
    }

    /**
     * OnCreateViewHolder creates the custom viewholder
     * @param viewGroup
     * @param viewType
     * @return
     */
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Get the context from the viewGroup view
        Context context = viewGroup.getContext();
        // Inflate the view using the movie_list_item as a layout
        int listItemResourceId = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachToRoot = false;
        View view = inflater.inflate(listItemResourceId, viewGroup, attachToRoot);

        // Create an instance of the viewholder
        MovieViewHolder holder = new MovieViewHolder(view, context);
        return holder;
    }

    /**
     * Sets the image thumbnail using picasso
     */
    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        // Check if there is a poster
        if (mMovies.get(position).getmPosterPathSmall().equals("no image")) {
            mPosterImageView.setImageResource(R.drawable.no_poster);
        } else {
            Picasso.with(holder.mContext).load(mMovies.get(position).getmPosterPathLarge()).into
                    (mPosterImageView);
        }
    }

    /**
     * Returns the number of items in the adapter
     */
    @Override
    public int getItemCount() {
        // Check if the array list is empty
        if (mMovies == null) return 0;
        return mMovies.size();
    }

    /**
     * The following two methods are overwritten to avoid duplication of views
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /**
     * Custom viewholder which implements OnClickListener to enable clicking on the viewholder
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /** Context for the viewholder */
        Context mContext;

        /**
         * Constructor for the viewholder
         * @param view the view using the movie_list_item a layout
         */
        public MovieViewHolder(View view, Context context) {
            super(view);
            // Get the context
            mContext = context;
            // Find the image view that will hold the movie poster
            mPosterImageView = (ImageView) view.findViewById(R.id.im_movie_poster);
            // Set the OnClickListener to the view
            view.setOnClickListener(this);
        }

        /**
         * Method that passes the current movie to the instance of MovieAdapterClickHandler when the
         * user has clicked on a poster
         */
        @Override
        public void onClick(View v) {
            // Get the current movie
            mCurrentMovie = mMovies.get(getAdapterPosition());

            // Pass the current movie object to the onPosterClick method
            mClickHandler.onPosterClick(mCurrentMovie);
        }
    }

    /**
     * Helper method to set the data to the adapter in case we have already created one in order
     * to avoid creating a new one
     */
    public void setMovieData(ArrayList<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }
}
