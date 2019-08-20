package com.nanodegrees.utils;

import com.nanodegrees.models.MovieDetails;
import com.nanodegrees.models.MovieRequest;
import com.nanodegrees.models.review.ReviewResponse;
import com.nanodegrees.models.videos.MoviesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IServices {

    @GET(Constants.URI_POPULAR)
    Call<MovieRequest> getPopular(@Query("api_key") String key, @Query("language") String lenguaje, @Query("page") Integer page);

    @GET(Constants.URI_TOP_RATED)
    Call<MovieRequest> getTopRated(@Query("api_key") String key, @Query("language") String lenguaje, @Query("page") Integer page);

    @GET(Constants.URI_UPCOMING)
    Call<MovieRequest> getUpComing(@Query("api_key") String key, @Query("language") String lenguaje, @Query("page") Integer page);


    @GET(Constants.URI_MOVIE_DETAILS)
    Call<MovieDetails> getDetails(@Path("ID") Integer id, @Query("api_key") String key, @Query("language") String lenguaje, @Query("page") Integer page);

    @GET(Constants.URI_MOVIE_VIDEOS)
    Call<MoviesResponse> getVideos(@Path("ID") Integer id, @Query("api_key") String key, @Query("language") String lenguaje, @Query("page") Integer page);

    @GET(Constants.URI_MOVIE_REVIEWS)
    Call<ReviewResponse> getReviews(@Path("ID") Integer id, @Query("api_key") String key, @Query("language") String lenguaje, @Query("page") Integer page);

}
