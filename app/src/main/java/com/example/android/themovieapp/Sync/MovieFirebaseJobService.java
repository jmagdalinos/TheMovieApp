package com.example.android.themovieapp.Sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Firebase job scheduler that schedules a single sync task
 */

public class MovieFirebaseJobService extends JobService {

    /** Async task which will sync the movie data in the background */
    private AsyncTask<Void, Void, Void> mFetchMovieTask;

    /** Offloads the sync job with an async task */
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        mFetchMovieTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // Get the application context
                Context context = getApplicationContext();

                // Sync the data and update the movies table
                MovieSyncTask.syncMovies(context);
                jobFinished(jobParameters, false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(jobParameters, false);
            }
        };
        return true;
    }

    /** Called when the scheduling engine has interrupted the execution of a running job */
    @Override
    public boolean onStopJob(JobParameters job) {
        if (mFetchMovieTask != null) mFetchMovieTask.cancel(true);
        return true;
    }
}
