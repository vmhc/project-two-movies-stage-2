package com.nanodegrees.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.nanodegrees.ui.details.DetailsActivity;
import com.nanodegrees.R;
import com.nanodegrees.models.Movie;
import com.nanodegrees.utils.Constants;
import java.util.List;

public class HeaderMovieAdapter extends RecyclerView.Adapter<HeaderMovieAdapter.HeaderMovieHolder> {

    private final Context ctx;
    private List<Movie> listMovie;

    public HeaderMovieAdapter(Context ctx, List<Movie> listMovie) {
        this.ctx = ctx;
        this.listMovie = listMovie;
    }

    @NonNull
    @Override
    public HeaderMovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HeaderMovieHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_movie, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderMovieHolder holder, int position) {
        holder.bindViewHolder(listMovie.get(position), ctx);
    }

    @Override
    public int getItemCount() {
        return listMovie != null ? listMovie.size() : 0;
    }

    public void setListMovie(List<Movie> listMovie) {
        this.listMovie = listMovie;
        notifyDataSetChanged();
    }

    class HeaderMovieHolder extends RecyclerView.ViewHolder {

        private final ImageView iv_movie;

        HeaderMovieHolder(@NonNull View itemView) {
            super(itemView);
            iv_movie = itemView.findViewById(R.id.iv_movie);
        }

        void bindViewHolder(final Movie movie, final Context ctx) {
            Glide.with(ctx).load(Constants.URI_BASE_IMAGE + movie.getPoster_path()).placeholder(R.drawable.img_placeholder).error(R.drawable.img_placeholder).into(iv_movie);
            iv_movie.invalidate();

            iv_movie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ctx.startActivity(DetailsActivity.newIntent(ctx, movie.getId()));
                }
            });
        }
    }
}
