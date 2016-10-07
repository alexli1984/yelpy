package com.alapplication.yelpy.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alapplication.yelpy.R;
import com.alapplication.yelpy.api.model.Review;
import com.alapplication.yelpy.util.Utils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.Date;

/**
 * Adapter for business review list
 */
public class ReviewListAdapter extends AbstractRecyclerViewAdapter<Review, ReviewListAdapter.ViewHolder> {

    public ReviewListAdapter(Context context) {
        super(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.review_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Review review = getItemAt(position);
        if (!TextUtils.isEmpty(review.user.image_url)) {
            Picasso.with(mContext).load(review.user.image_url).placeholder(R.drawable.yelp_android).fit().centerCrop().into(holder.thumbnail);
        } else {
            holder.thumbnail.setImageResource(R.drawable.yelp_android);
        }
        holder.summary.setText(review.text);
        holder.username.setText(review.user.name);
        holder.rating.setRating(review.rating);
        try {
            Date date = Utils.dateFormat.parse(review.time_created);
            holder.time.setText(Utils.getTimeDifference(mContext, date));
        } catch (ParseException e) {
        }
    }

    public class ViewHolder extends AbstractRecyclerViewAdapter.ViewHolder {
        private ImageView thumbnail;
        private TextView summary, time, username;
        private RatingBar rating;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            summary = (TextView) itemView.findViewById(R.id.summary);
            time = (TextView) itemView.findViewById(R.id.time);
            username = (TextView) itemView.findViewById(R.id.user_name);
            rating = (RatingBar) itemView.findViewById(R.id.rating);
        }
    }
}
