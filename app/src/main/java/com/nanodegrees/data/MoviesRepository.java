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

package com.nanodegrees.data;


import android.util.Log;



import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nanodegrees.data.database.MoviesDAO;
import com.nanodegrees.data.network.MoviesNetworkDataSource;
import com.nanodegrees.models.Movie;
import com.nanodegrees.models.MovieDetails;
import com.nanodegrees.models.review.Review;
import com.nanodegrees.models.videos.TrailerItem;

import java.util.List;

public class MoviesRepository {

    private static final String LOG_TAG = MoviesRepository.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static MoviesRepository sInstance;
    private final MoviesDAO mMoviesDao;
    private final MoviesNetworkDataSource mWeatherNetworkDataSource;
    private final AppExecutors mExecutors;

    // mutable list
    private final MutableLiveData<List<Movie>> mutableMoviesList = new MutableLiveData<>();

    private MoviesRepository(MoviesDAO moviesDao,
                             MoviesNetworkDataSource weatherNetworkDataSource,
                             AppExecutors executors) {
        mMoviesDao = moviesDao;
        mWeatherNetworkDataSource = weatherNetworkDataSource;
        mExecutors = executors;

        // observing remote movie list
        mWeatherNetworkDataSource.getDownloadedMovies().observeForever(mutableMoviesList::postValue);

    }

    public synchronized static MoviesRepository getInstance(
            MoviesDAO moviesDao, MoviesNetworkDataSource weatherNetworkDataSource,
            AppExecutors executors) {
        Log.d(LOG_TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MoviesRepository(moviesDao, weatherNetworkDataSource,
                        executors);
                Log.d(LOG_TAG, "Made new repository");
            }
        }
        return sInstance;
    }


    //region Main movies list.

    public LiveData<List<Movie>> getFavouriteMovies() {

        return mMoviesDao.getAllFavourites();
    }

    public LiveData<List<Movie>> getMoviesList() {

        return mutableMoviesList;
    }


    public void retrievePopularMovies() {

        mWeatherNetworkDataSource.fetchPopularMovies();
    }

    public void retrieveTopRatedMovies() {

        mWeatherNetworkDataSource.fetchTopRatedMovies();
    }

    public void retrieveUpcomingMovies() {

        mWeatherNetworkDataSource.fetchUpComingMovies();
    }

    //endregion

    //region Details

    public LiveData<MovieDetails> retrieveDetails(int movieId) {

        // observe network call
        return mWeatherNetworkDataSource.getMovieDetails(movieId);
    }

    //endregion

    //region trailers
    public LiveData<List<TrailerItem>> retrieveTrailersList(int movieId) {

        return mWeatherNetworkDataSource.getTrailersList(movieId);
    }
    //endregion

    //region reviews
    public LiveData<List<Review>> retrieveReviewsList(int movieId) {

        return mWeatherNetworkDataSource.retrieveReviewsList(movieId);
    }
    //endregion



    //region Favourites

    public void removeFavourite(int movieId) {

        mExecutors.diskIO().execute(() -> {

            mMoviesDao.deleteFavourite(movieId);
        });
    }

    public LiveData<Boolean> isFavourite(int movieId) {

        return mMoviesDao.isFavourite(movieId);
    }

    public void saveFavourite(MovieDetails details) {

        mExecutors.diskIO().execute(() -> {

            mMoviesDao.insertFavourite(details);
        });
    }

    //endregion

    //region Movies

    // TODO expose here movies livedata & api call

    //endregion

    //region reviews

    // TODO expose here movies livedata & api call

    //endregion


}