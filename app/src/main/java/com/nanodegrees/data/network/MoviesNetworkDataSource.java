/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nanodegrees.data.network;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nanodegrees.data.AppExecutors;
import com.nanodegrees.models.Movie;
import com.nanodegrees.models.MovieDetails;
import com.nanodegrees.models.MovieRequest;
import com.nanodegrees.models.review.Review;
import com.nanodegrees.models.review.ReviewResponse;
import com.nanodegrees.models.videos.MoviesResponse;
import com.nanodegrees.models.videos.TrailerItem;
import com.nanodegrees.utils.ClientServices;
import com.nanodegrees.utils.Constants;
import com.nanodegrees.utils.IServices;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Provides an API for doing all operations with the server data
 */
public class MoviesNetworkDataSource {

    private static final String LOG_TAG = MoviesNetworkDataSource.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static MoviesNetworkDataSource sInstance;
    private final Context mContext;
    private IServices iServices = ClientServices.getServices();

    // LiveData storing the latest downloaded movie/movies
    private final MutableLiveData<List<Movie>> mDownloadedMovies;
    private MutableLiveData<MovieDetails> mMovieDetails;

    private MutableLiveData<List<TrailerItem>> mTrailersList;
    private MutableLiveData<List<Review>> mReviewsList;

    private final AppExecutors mExecutors;

    final String KEY_SEARCH_CRITERIA = "search_criteria";
    final String KEY_MOVIE_ID = "movie_id";
    private final int POPULAR_MOVIES = 1;
    private final int TOP_RATED_MOVIES = 2;
    private final int UPCOMING_MOVIES = 3;


    private MoviesNetworkDataSource(Context context, AppExecutors executors) {
        mContext = context;
        mExecutors = executors;
        mDownloadedMovies = new MutableLiveData<>();
    }

    /**
     * Get the singleton for this class
     */
    public static MoviesNetworkDataSource getInstance(Context context, AppExecutors executors) {
        Log.d(LOG_TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MoviesNetworkDataSource(context.getApplicationContext(), executors);
                Log.d(LOG_TAG, "Made new network data source");
            }
        }
        return sInstance;
    }

    public LiveData<List<Movie>> getDownloadedMovies() {
        return mDownloadedMovies;
    }

    public LiveData<MovieDetails> getMovieDetails(int movieId) {

        mMovieDetails = new MutableLiveData<>();
        startMovieDetailsIntentService(movieId);
        return mMovieDetails;
    }

    public LiveData<List<TrailerItem>> getTrailersList(int movieId) {

        mTrailersList = new MutableLiveData<>();

        fetchTrailers(movieId);
        return mTrailersList;
    }

    public LiveData<List<Review>> retrieveReviewsList(int movieId) {

        mReviewsList = new MutableLiveData<>();

        fetchReviews(movieId);
        return mReviewsList;
    }

    public void fetchPopularMovies() {

        startMovieListIntentService(POPULAR_MOVIES);
    }

    public void fetchTopRatedMovies() {

        startMovieListIntentService(TOP_RATED_MOVIES);
    }

    public void fetchUpComingMovies() {

        startMovieListIntentService(UPCOMING_MOVIES);
    }

    void startMovieListIntentService(int searchCriteria) {

        Intent intentToFetch = new Intent(mContext, RetrieveMovieListIntentService.class);

        intentToFetch.putExtra(KEY_SEARCH_CRITERIA, searchCriteria);
        mContext.startService(intentToFetch);
        Log.d(LOG_TAG, "Service created");
    }

    void startMovieDetailsIntentService(int movieId) {

        Intent intentToFetch = new Intent(mContext, RetrieveMovieDetailsIntentService.class);

        intentToFetch.putExtra(KEY_MOVIE_ID, movieId);
        mContext.startService(intentToFetch);
        Log.d(LOG_TAG, "Service created");
    }

    /**
     * Gets movie list by search criteria.
     */
    void fetchMovies(Integer searchCriteria) {
        Log.d(LOG_TAG, "Fetch started");
        if (searchCriteria != null) {

            mExecutors.networkIO().execute(() -> {

                AtomicReference<List<Movie>> data = new AtomicReference<>();

                try {

                    switch (searchCriteria) {
                        case POPULAR_MOVIES: {
                            Call<MovieRequest> requestCall = iServices.getPopular(Constants.API_KEY_MOVIE, Constants.LENGUAJE, 1);

                            Response<MovieRequest> response = requestCall.clone().execute();

                            data.set(response.body().getResults());
                        }
                        break;
                        case TOP_RATED_MOVIES: {
                            Call<MovieRequest> requestCall = iServices.getTopRated(Constants.API_KEY_MOVIE, Constants.LENGUAJE, 1);

                            Response<MovieRequest> response = requestCall.clone().execute();

                            data.set(response.body().getResults());
                        }
                        break;
                        case UPCOMING_MOVIES: {
                            Call<MovieRequest> requestCall = iServices.getUpComing(Constants.API_KEY_MOVIE, Constants.LENGUAJE, 1);

                            Response<MovieRequest> response = requestCall.clone().execute();

                            data.set(response.body().getResults());
                        }
                        break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                mDownloadedMovies.postValue(data.get());

            });

        } else {
            mDownloadedMovies.postValue(null);
        }

    }

    void fetchMovieDetails(Integer movieId) {
        Log.d(LOG_TAG, "Fetch started");
        if (movieId != null) {

            mExecutors.networkIO().execute(() -> {

                AtomicReference<MovieDetails> data = new AtomicReference<>();

                try {

                    Call<MovieDetails> requestCall = iServices.getDetails(movieId, Constants.API_KEY_MOVIE, Constants.LENGUAJE, 1);

                    Response<MovieDetails> response = requestCall.clone().execute();

                    data.set(response.body());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                mMovieDetails.postValue(data.get());

            });

        } else {
            mMovieDetails.postValue(null);
        }
    }

    private void fetchTrailers(Integer movieId) {

        Log.d(LOG_TAG, "Fetch started");
        if (movieId != null) {

            mExecutors.networkIO().execute(() -> {

                AtomicReference<List<TrailerItem>> data = new AtomicReference<>();

                try {

                    Call<MoviesResponse> requestCall = iServices.getVideos(movieId, Constants.API_KEY_MOVIE, Constants.LENGUAJE, 1);

                    Response<MoviesResponse> response = requestCall.clone().execute();

                    data.set(response.body().getResults());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                mTrailersList.postValue(data.get());

            });

        } else {
            mTrailersList.postValue(null);
        }
    }

    private void fetchReviews(Integer movieId) {

        Log.d(LOG_TAG, "Fetch started");
        if (movieId != null) {

            mExecutors.networkIO().execute(() -> {

                AtomicReference<List<Review>> data = new AtomicReference<>();

                try {

                    Call<ReviewResponse> requestCall = iServices.getReviews(movieId, Constants.API_KEY_MOVIE, Constants.LENGUAJE, 1);

                    Response<ReviewResponse> response = requestCall.clone().execute();

                    data.set(response.body().getResults());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                mReviewsList.postValue(data.get());

            });

        } else {
            mReviewsList.postValue(null);
        }
    }

}