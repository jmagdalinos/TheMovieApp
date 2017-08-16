package com.example.android.themovieapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.themovieapp.Data.MovieContract.ReviewsEntry;

/**
 * Custom adapter that feeds the reviews to a recycler view
 */

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewViewholder>{

    /** Cursor holding the list of reviews retrieved from the movie db */
    private Cursor mCursor;

    /** Context used throughout the adapter */
    private final Context mContext;

    /** TextView that will show the review's author */
    private TextView mReviewAuthor;

    /** TextView that will show the review's content */
    private TextView mReviewContent;

    /** Instance of ReviewAdapterClickHandler used to when the user clicks on the view in the
     * * recycler view */
    private final ReviewAdapterClickHandler mClickHandler;

    /** Constructor for the adapter */
    public ReviewListAdapter(Context context, ReviewAdapterClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    /** Interface to enable click functionality to the recycler view */
    public interface ReviewAdapterClickHandler {
        void onReviewClick(String review_url);
    }

    @Override
    public ReviewViewholder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Get the context from the viewGroup view
        Context context = viewGroup.getContext();
        // Inflate the view using the movie_list_item as a layout
        int listItemResourceId = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(listItemResourceId, viewGroup, false);

        // Create an instance of the viewholder
        ReviewViewholder holder = new ReviewViewholder(view);
        return holder;
    }

    /** Sets the trailer name */
    @Override
    public void onBindViewHolder(ReviewViewholder holder, int position) {
        // Move the cursor to the current position
        mCursor.moveToPosition(position);

        // Display the review's author
        mReviewAuthor.setText(mCursor.getString(mCursor.getColumnIndex(ReviewsEntry
                .COLUMN_AUTHOR)));
        // Display the review's content
        mReviewContent.setText(mCursor.getString(mCursor.getColumnIndex(ReviewsEntry
                .COLUMN_CONTENT)));
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

    /** Swaps the cursor with the one created in the detail activity by the loader */
    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    /** Custom viewholder which implements OnClickListener to enable clicking on the viewholder */
    public class ReviewViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /**
         * Constructor for the viewholder
         */
        public ReviewViewholder(View view) {
            super(view);
            // Find the text view that will hold the review's author
            mReviewAuthor = (TextView) view.findViewById(R.id.tv_author);
            // Find the text view that will hold the review's content
            mReviewContent = (TextView) view.findViewById(R.id.tv_content);
            // Set the OnClickListener to the view
            view.setOnClickListener(this);
        }

        /** Passes the current review url to the instance of ReviewAdapterClickHandler */
        @Override
        public void onClick(View v) {
            // Move the cursor to the current position
            mCursor.moveToPosition(getAdapterPosition());

            // Pass the current review url to the onPosterClick method
            mClickHandler.onReviewClick(mCursor.getString(mCursor.getColumnIndex(ReviewsEntry
                    .COLUMN_URL)));
        }
    }
}
