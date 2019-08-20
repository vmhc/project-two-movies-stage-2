package com.nanodegrees.ui.details;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.nanodegrees.data.MoviesRepository;
import com.nanodegrees.data.network.NetworkCallStatus;
import com.nanodegrees.models.MovieDetails;
import com.nanodegrees.models.review.Review;
import com.nanodegrees.models.videos.TrailerItem;
import com.nanodegrees.utils.InjectorUtils;

import java.util.List;


public class DetailsViewModel extends AndroidViewModel {

    private MoviesRepository repository;
    private MutableLiveData<NetworkCallStatus> networkCallStatusMutable = new MutableLiveData<>(NetworkCallStatus.NONE);
    private MutableLiveData<NetworkCallStatus> trailersCall = new MutableLiveData<>(NetworkCallStatus.NONE);
    private MutableLiveData<NetworkCallStatus> reviewsCall = new MutableLiveData<>(NetworkCallStatus.NONE);

    private LiveData<MovieDetails> movie;

    private LiveData<List<TrailerItem>> trailers;

    private LiveData<List<Review>> reviews;

    LiveData<NetworkCallStatus> networkCallStatusLiveData = Transformations.map(networkCallStatusMutable, status -> status);

    public DetailsViewModel(@NonNull Application application) {
        super(application);

        repository = InjectorUtils.provideRepository(application);

    }

    LiveData<Boolean> isFavourite(int movieId) {

        return repository.isFavourite(movieId);
    }

    LiveData<MovieDetails> getDetailsObservable(int movieId) {
        if (movie == null) {
            networkCallStatusMutable.setValue(NetworkCallStatus.PROCESS);

            movie = Transformations.map(repository.retrieveDetails(movieId), details -> {

                networkCallStatusMutable.setValue(details == null ? NetworkCallStatus.FAILED : NetworkCallStatus.NONE);
                return details;
            });

        }
        return movie;
    }

    LiveData<List<TrailerItem>> getTrailersListObservable(int movieId) {
        if (trailers == null) {

            trailersCall.setValue(NetworkCallStatus.PROCESS);

            trailers = Transformations.map(repository.retrieveTrailersList(movieId), details -> {

                trailersCall.setValue(details == null ? NetworkCallStatus.FAILED : NetworkCallStatus.NONE);
                return details;
            });

        }

        return trailers;
    }

    LiveData<List<Review>> getReviewListObservable(int movieId) {
        if (reviews == null) {

            reviewsCall.setValue(NetworkCallStatus.PROCESS);

            reviews = Transformations.map(repository.retrieveReviewsList(movieId), details -> {

                reviewsCall.setValue(details == null ? NetworkCallStatus.FAILED : NetworkCallStatus.NONE);
                return details;
            });

        }

        return reviews;
    }

    void removeFromFavourites(int movieId) {

        repository.removeFavourite(movieId);
    }

    void addToFavourites(MovieDetails details) {

        repository.saveFavourite(details);
    }
}
