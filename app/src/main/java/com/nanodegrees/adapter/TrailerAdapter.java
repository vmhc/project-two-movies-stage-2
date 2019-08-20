package com.nanodegrees.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nanodegrees.R;
import com.nanodegrees.models.videos.TrailerItem;
import com.nanodegrees.utils.Constants;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private final Context ctx;
    private List<TrailerItem> listTrailer;

    public TrailerAdapter(Context ctx, List<TrailerItem> listTrailers) {
        this.ctx = ctx;
        this.listTrailer = listTrailers;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TrailerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        holder.bindViewHolder(listTrailer.get(position), ctx);
    }

    @Override
    public int getItemCount() {
        return listTrailer != null ? listTrailer.size() : 0;
    }

    public void setTrailers(List<TrailerItem> list) {
        this.listTrailer = list;
        notifyDataSetChanged();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder {

        TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void bindViewHolder(final TrailerItem trailer, final Context ctx) {

            TextView tvTitle = itemView.findViewById(R.id.tv_title);
            tvTitle.setText(trailer.getName());

            itemView.setOnClickListener(v -> {
                String pathTrailer = Constants.YOUTUBE_URL_BASE +
                        trailer.getKey();
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pathTrailer));
                ctx.startActivity(myIntent);
            });

        }
    }
}
