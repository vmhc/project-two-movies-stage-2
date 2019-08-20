package com.nanodegrees.ui.details;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nanodegrees.R;
import com.nanodegrees.adapter.ReviewAdapter;
import com.nanodegrees.adapter.TrailerAdapter;
import com.nanodegrees.models.MovieDetails;
import com.nanodegrees.utils.Constants;

import java.util.Objects;

public class DetailsActivity extends AppCompatActivity {

    private static final String KEY = "KEY";
    private ImageView ivMovie;
    private TextView tvOverviewDetail;
    private TextView tvRanking;
    private TextView runTime;
    private TextView tvDate;
    private ImageView ivIsFavourite;
    private TextView movieTitle;
    private LinearLayout ll_loader;
    private DetailsViewModel viewModel;
    private Boolean mIsFavourite;
    private MovieDetails mMovieDetails;

    private RecyclerView trailers;
    private TrailerAdapter trailerAdapter;

    private RecyclerView reviews;
    private ReviewAdapter reviewAdapter;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ll_loader = findViewById(R.id.ll_loader);
        ll_loader.setVisibility(View.VISIBLE);
        ivMovie = findViewById(R.id.iv_movie);
        tvOverviewDetail = findViewById(R.id.tv_overview_detail);
        tvRanking = findViewById(R.id.tv_ranking);
        tvDate = findViewById(R.id.tv_date);
        ivIsFavourite = findViewById(R.id.iv_favourite);
        movieTitle = findViewById(R.id.tv_title);
        runTime = findViewById(R.id.tv_duration);

        trailers = findViewById(R.id.rvTrailers);

        trailerAdapter = new TrailerAdapter(this, null);
        trailers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        trailers.setHasFixedSize(false);
        trailers.setAdapter(trailerAdapter);



        reviews = findViewById(R.id.rvReviews);
        reviewAdapter = new ReviewAdapter(this, null);
        reviews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        reviews.setHasFixedSize(false);
        reviews.setAdapter(reviewAdapter);

        int id = getIntent().getIntExtra(KEY, 0);

        if (id > 0) {
            viewModel = ViewModelProviders.of(this).get(DetailsViewModel.class);

            viewModel.networkCallStatusLiveData.observe(this, status -> {
                switch (status) {
                    case NONE:
                        ll_loader.setVisibility(View.GONE);
                        break;
                    case PROCESS:
                        ll_loader.setVisibility(View.VISIBLE);
                        // TODO hide data layout.
                        break;
                    case FAILED:
                        Toast.makeText(this, "Error retrieving data", Toast.LENGTH_LONG).show();
                        finish();
                        break;

                }
            });

            viewModel.getDetailsObservable(id).observe(this, details -> {

                if (details != null) {

                    DetailsActivity.this.mMovieDetails = details;
                    initDetailMovie(details);
                }
            });

            viewModel.isFavourite(id).observe(this, isFavourite -> {

                mIsFavourite = isFavourite;

                Drawable d = ContextCompat.getDrawable(
                        DetailsActivity.this,
                        isFavourite != null && isFavourite ? R.drawable.ic_favorite : R.drawable.ic_favorite_empty);

                ivIsFavourite.setImageDrawable(d);
            });

            observeTrailers(id);
            observeReviews(id);

        } else {
            finish();
        }

        ivIsFavourite.setOnClickListener(view -> {

            if (mIsFavourite != null && mIsFavourite) {
                viewModel.removeFromFavourites(id);
            } else {
                viewModel.addToFavourites(mMovieDetails);
            }

        });

    }

    private void observeTrailers(int id) {

        viewModel.getTrailersListObservable(id).observe(this, trailerItems -> {

            trailerAdapter.setTrailers(trailerItems);
        });
    }

    private void observeReviews(int id) {

        viewModel.getReviewListObservable(id).observe(this, trailerItems -> {

            reviewAdapter.setReviews(trailerItems);
        });
    }



    private void initDetailMovie(MovieDetails movie) {

        Glide.with(DetailsActivity.this).load(Constants.URI_BASE_IMAGE + movie.getBackdrop_path()).into(ivMovie);
        ivMovie.invalidate();
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.movie_details));
        movieTitle.setText(movie.getOriginal_title());

        tvOverviewDetail.setText(movie.getOverview());
        tvDate.setText(movie.getRelease_date().substring(0,4));

        tvRanking.setText(getString(R.string.ranking_placeholder, movie.getVote_average()));

        runTime.setText(getString(R.string.runtime_placeholder, movie.getRuntime()));

    }

    public static Intent newIntent(Context ctx, Integer id) {
        Intent intent = new Intent(ctx, DetailsActivity.class);
        intent.putExtra(KEY, id);
        return intent;
    }
}
