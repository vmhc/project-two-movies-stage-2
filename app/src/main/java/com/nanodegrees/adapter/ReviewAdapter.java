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
import com.nanodegrees.models.review.Review;
import com.nanodegrees.models.videos.TrailerItem;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final Context ctx;
    private List<Review> list;

    public ReviewAdapter(Context ctx, List<Review> listTrailers) {
        this.ctx = ctx;
        this.list = listTrailers;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.bindViewHolder(list.get(position), ctx);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public void setReviews(List<Review> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void bindViewHolder(final Review item, final Context ctx) {
            TextView tvAuthor = itemView.findViewById(R.id.tv_author);
            TextView tvContent = itemView.findViewById(R.id.tv_content);

            tvAuthor.setText(item.getAuthor());
            tvContent.setText(item.getContent());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(item.getUrl()));
                    ctx.startActivity(i);
                }
            });
        }
    }
}
