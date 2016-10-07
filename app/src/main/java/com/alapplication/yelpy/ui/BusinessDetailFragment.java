package com.alapplication.yelpy.ui;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.alapplication.yelpy.R;
import com.alapplication.yelpy.api.YelpApi;
import com.alapplication.yelpy.api.model.Business;
import com.alapplication.yelpy.api.model.Review;
import com.alapplication.yelpy.ui.binding.BusinessDetailBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * A fragment representing a single Business detail screen.
 * This fragment is either contained in a {@link BusinessListActivity}
 * in two-pane mode (on tablets) or a {@link BusinessDetailActivity}
 * on handsets.
 */
public class BusinessDetailFragment extends Fragment implements OnMapReadyCallback, AbstractRecyclerViewAdapter.OnItemClickListener {
    private static final String TAG = BusinessDetailFragment.class.getSimpleName();

    public static final String ARG_ITEM = "arg_item";

    private Business mBusiness;
    private BusinessDetailBinding mBinding;
    private ReviewListAdapter mReviewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BusinessDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        if (getArguments().containsKey(ARG_ITEM)) {
            mBusiness = (Business) getArguments().getSerializable(ARG_ITEM);
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mBusiness.name);
            }
            ImageView appBarBg = (ImageView) activity.findViewById(R.id.detail_background);
            if (!TextUtils.isEmpty(mBusiness.image_url) && appBarBg != null) {
                Picasso.with(activity).load(mBusiness.image_url).into(appBarBg);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.business_detail, container, false);
        mBinding.mapView.onCreate(savedInstanceState);
        mBinding.mapView.getMapAsync(this);
        mReviewAdapter = new ReviewListAdapter(getActivity());
        mReviewAdapter.setOnItemClickListener(this);
        mBinding.reviewList.setAdapter(mReviewAdapter);
        mBinding.reviewList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.setBusiness(mBusiness);
        mBinding.setContext(this);
        return mBinding.getRoot();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mBusiness != null && mBusiness.coordinates != null) {
            LatLng ll = new LatLng(mBusiness.coordinates.latitude, mBusiness.coordinates.longitude);
            googleMap.addMarker(new MarkerOptions().position(ll));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 16), 1, null);
        }
    }

    private void refreshBusiness() {
        if (mBusiness.photos == null || mBusiness.photos.length == 0) {
            mBinding.photoContainer.setVisibility(View.GONE);
        } else {
            mBinding.photoContainer.setVisibility(View.VISIBLE);
            mBinding.photoScroller.removeAllViews();
            for (String url : mBusiness.photos) {
                if (!TextUtils.isEmpty(url)) {
                    mBinding.photoScroller.addView(generateImageView(url));
                }
            }
        }

        mBinding.setBusiness(mBusiness);

        String hoursToday = mBusiness.getHoursSummary(getActivity(), Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        mBinding.hours.setText(getString(R.string.hours_today, hoursToday));
    }

    private void refreshReviews(List<Review> reviews) {
        mReviewAdapter.setItemList(reviews);
    }

    public void getDirection() {
        launchIntent(new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + mBusiness.coordinates.latitude + "," + mBusiness.coordinates.longitude)));
    }

    public void callBusiness() {
        launchIntent(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mBusiness.phone)));
    }

    public void visitWebsite() {
        launchIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(mBusiness.url)));
    }

    private void launchIntent(Intent intent) {
        try {
            startActivity(intent);
        } catch (Throwable t) {
            Log.e(TAG, "No Activity to handle the intent");
        }
    }

    private ImageView generateImageView(String url) {
        ImageView img = (ImageView) LayoutInflater.from(getActivity()).inflate(R.layout.business_photo, mBinding.photoScroller, false);
        Picasso.with(getActivity()).load(url).fit().centerCrop().into(img);
        return img;
    }

    public void onEventMainThread(YelpApi.GetBusinessDetail.Response response) {
        if (response.isSuccess()) {
            mBusiness.hours = response.hours;
            mBusiness.photos = response.photos;
            refreshBusiness();
        } else {
            Toast.makeText(getActivity(), "Error Fetching Business", Toast.LENGTH_SHORT);
        }
    }

    public void onEventMainThread(YelpApi.GetReviews.Response response) {
        if (response.isSuccess()) {
            refreshReviews(response.reviews);
        } else {
            Toast.makeText(getActivity(), "Error Fetching Reviews", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mBinding.mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinding.mapView.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mBinding.mapView.onStart();
        new YelpApi.GetBusinessDetail(mBusiness.id).postRequestAsync();
        new YelpApi.GetReviews(mBusiness.id).postRequestAsync();
    }

    @Override
    public void onStop() {
        super.onStop();
        mBinding.mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mBinding.mapView.onLowMemory();
    }

    @Override
    public void onItemClick(View view, int position) {
        Review review = mReviewAdapter.getItemAt(position);
        if (!TextUtils.isEmpty(review.url)) {
            launchIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(review.url)));
        }
    }
}
