package com.example.android.themovieapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.themovieapp.Data.MovieContract.TrailersEntry;

/**
 * Custom adapter that feeds the trailers to a recycler view
 */

public class TrailerListAdapter  extends RecyclerView.Adapter<TrailerListAdapter.TrailerViewholder>{

    /** Cursor holding the list of trailers retrieved from the movie db */
    private Cursor mCursor;

    /** Context used throughout the adapter */
    private final Context mContext;

    /** TextView that will show the trailer name */
    private TextView mTrailerName;

    /** Instance of ReviewAdapterClickHandler used to when the user clicks on the view in the
     * * recycler view */
    private final TrailerAdapterClickHandler mClickHandler;

    /** Constructor for the adapter */
    public TrailerListAdapter(Context context, TrailerAdapterClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    /** Interface to enable click functionality to the recycler view */
    public interface TrailerAdapterClickHandler {
        void onTrailerClick(String trailer_source);
    }

    @Override
    public TrailerViewholder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Get the context from the viewGroup view
        Context context = viewGroup.getContext();
        // Inflate the view using the movie_list_item as a layout
        int listItemResourceId = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(listItemResourceId, viewGroup, false);

        // Create an instance of the viewholder
        TrailerViewholder holder = new TrailerViewholder(view);
        return holder;
    }

    /** Sets the trailer name */
    @Override
    public void onBindViewHolder(TrailerViewholder holder, int position) {
        // Move the cursor to the current position
        mCursor.moveToPosition(position);

        // Display the trailer name
        mTrailerName.setText(mCursor.getString(mCursor.getColumnIndex(TrailersEntry.COLUMN_NAME)));
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
    public class TrailerViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /**
         * Constructor for the viewholder
         */
        public TrailerViewholder(View view) {
            super(view);
            // Find the text view that will hold the trailer name
            mTrailerName = (TextView) view.findViewById(R.id.tv_trailer_name);
            // Set the OnClickListener to the view
            view.setOnClickListener(this);
        }

        /** Passes the current trailer source to the instance of ReviewAdapterClickHandler */
        @Override
        public void onClick(View v) {
            // Move the cursor to the current position
            mCursor.moveToPosition(getAdapterPosition());

            // Pass the current trailer source to the onPosterClick method
            mClickHandler.onTrailerClick(mCursor.getString(mCursor.getColumnIndex(TrailersEntry
                    .COLUMN_SOURCE)));
        }
    }
}
