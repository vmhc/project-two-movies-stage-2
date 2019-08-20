package com.nanodegrees.ui.main;


import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nanodegrees.R;
import com.nanodegrees.adapter.HeaderMovieAdapter;
import com.nanodegrees.models.Movie;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private HeaderMovieAdapter adapter;
    private RecyclerView rvMovies;
    private ProgressBar progressBar;
    private MainViewModel viewModel;
    private List<Movie> favourites;
    private SelectedList selectedList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvMovies = findViewById(R.id.rv_movies);
        progressBar = findViewById(R.id.pb_loading);
        FloatingActionButton fab_popular = findViewById(R.id.fab_popular);
        FloatingActionButton fab_top_rated = findViewById(R.id.fab_top_rated);
        FloatingActionButton fab_up_coming = findViewById(R.id.fab_up_coming);

        fab_popular.setOnClickListener(this);
        fab_top_rated.setOnClickListener(this);
        fab_up_coming.setOnClickListener(this);

        int columns = 2;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            columns = 4;
        }

        GridLayoutManager layoutManager = new GridLayoutManager(this, columns);

        adapter = new HeaderMovieAdapter(this, null);
        rvMovies.setLayoutManager(layoutManager);
        rvMovies.setHasFixedSize(false);
        rvMovies.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        viewModel.getSelectedList().observe(this, selectedList -> {
            MainActivity.this.selectedList = selectedList;
            switch (selectedList) {

                case POPULAR:
                    Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.most_popular));
                    break;
                case TOP_RATED:
                    Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.top_rated));
                    break;
                case UP_COMING:
                    Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.upcoming));
                    break;
                case FAVOURITES:
                    Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.favourites));
                    break;
            }
        });

        viewModel.networkCallStatusLiveData.observe(this, status -> {
            switch (status) {
                case NONE:
                    showList();
                    break;
                case PROCESS:
                    showLoader();
                    break;
                case FAILED:
                    // TODO show error msj
                    showList();
                    break;

            }
        });

        viewModel.moviesList.observe(this, movies -> {

            if (selectedList != SelectedList.FAVOURITES) {

                if (movies == null) {

                    Toast.makeText(this, "Error retrieving data", Toast.LENGTH_SHORT).show();
                }
                adapter.setListMovie(movies);
            }
        });

        viewModel.favourites.observe(this, favourites -> {

            MainActivity.this.favourites = favourites;
            if (selectedList == SelectedList.FAVOURITES) {
                adapter.setListMovie(favourites);
            }
        });

        if (savedInstanceState == null) {

            viewModel.retrievePopular();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_popular:
                viewModel.retrievePopular();
                break;
            case R.id.fab_top_rated:
                viewModel.retrieveTopRated();
                break;
            case R.id.fab_up_coming:
                viewModel.retrieveUpcoming();
                break;
        }
    }

    private void showLoader() {

        progressBar.setVisibility(View.VISIBLE);
        rvMovies.setVisibility(View.GONE);
    }

    private void showList() {

        progressBar.setVisibility(View.GONE);
        rvMovies.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_favourites:
                // check for empty
                adapter.setListMovie(favourites);
                viewModel.setSelectedList(SelectedList.FAVOURITES);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
