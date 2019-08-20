package com.nanodegrees.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.nanodegrees.data.network.NetworkCallStatus;
import com.nanodegrees.data.MoviesRepository;
import com.nanodegrees.models.Movie;
import com.nanodegrees.utils.InjectorUtils;

import java.lang.annotation.RetentionPolicy;
import java.util.List;


public class MainViewModel extends AndroidViewModel {

    private MoviesRepository repository;

    private MutableLiveData<NetworkCallStatus> networkCallStatusMutable = new MutableLiveData<>(NetworkCallStatus.NONE);
    private MutableLiveData<SelectedList> selectedList = new MutableLiveData<>(SelectedList.POPULAR);

    LiveData<List<Movie>> moviesList;
    LiveData<List<Movie>> favourites;

    LiveData<NetworkCallStatus> networkCallStatusLiveData = Transformations.map(networkCallStatusMutable, status -> status);


    public MainViewModel(@NonNull Application application) {
        super(application);

        repository = InjectorUtils.provideRepository(application);

        moviesList = Transformations.map(repository.getMoviesList(), list -> {

            networkCallStatusMutable.setValue(list == null ? NetworkCallStatus.FAILED : NetworkCallStatus.NONE);
            return list;
        });

        favourites = repository.getFavouriteMovies();

    }

    void retrievePopular() {

        selectedList.postValue(SelectedList.POPULAR);
        networkCallStatusMutable.setValue(NetworkCallStatus.PROCESS);
        repository.retrievePopularMovies();
    }


    void retrieveTopRated() {

        selectedList.postValue(SelectedList.TOP_RATED);
        networkCallStatusMutable.setValue(NetworkCallStatus.PROCESS);
        repository.retrieveTopRatedMovies();
    }


    void retrieveUpcoming() {

        selectedList.postValue(SelectedList.UP_COMING);
        networkCallStatusMutable.setValue(NetworkCallStatus.PROCESS);
        repository.retrieveUpcomingMovies();
    }

    LiveData<SelectedList> getSelectedList() {

        return selectedList;
    }

    void setSelectedList(SelectedList favourites) {

        selectedList.postValue(favourites);
    }
}
