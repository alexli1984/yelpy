package com.alapplication.yelpy.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alapplication.yelpy.R;
import com.alapplication.yelpy.api.YelpApi;
import com.alapplication.yelpy.api.model.Business;
import com.alapplication.yelpy.api.model.LocationParam;
import com.alapplication.yelpy.api.model.SearchParam;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;

import java.util.Collections;

import de.greenrobot.event.EventBus;

/**
 * An activity representing a list of Businesses. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link BusinessDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class BusinessListActivity extends AppCompatActivity implements AbstractRecyclerViewAdapter.OnItemClickListener,
        NavigationView.OnNavigationItemSelectedListener, FilterDialog.FilterListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_PERMISSION = 1;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private BusinessListAdapter mAdapter;
    private SearchParam mSearchParam;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private com.alapplication.yelpy.ui.BusinessListActivityBinding mBinding;
    private RecyclerView mListView;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_business_list);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_business_list);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mGoogleApiClient.connect();

        setSupportActionBar(mBinding.toolbar);
        mBinding.toolbar.setTitle(getTitle());

        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterDialog dialog = FilterDialog.newInstance(mSearchParam);
                dialog.setFilterListener(BusinessListActivity.this);
                dialog.show(getFragmentManager(), "filter");
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mBinding.drawerLayout, mBinding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mBinding.drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mBinding.navView.setNavigationItemSelectedListener(this);

        SearchParam.Builder builder = new SearchParam.Builder(new LocationParam("Toronto"));
        mSearchParam = builder.build();

        mListView = (RecyclerView) findViewById(R.id.business_list);
        mAdapter = new BusinessListAdapter(this, mSearchParam.getGroupBy());
        mAdapter.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter);
        mListView.setLayoutManager(new LinearLayoutManager(this));

        if (findViewById(R.id.business_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Business business = mAdapter.getItemAt(position).getContent();
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putSerializable(BusinessDetailFragment.ARG_ITEM, business);
            BusinessDetailFragment fragment = new BusinessDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.business_detail_container, fragment)
                    .commit();
        } else {
            Context context = view.getContext();
            Intent intent = new Intent(this, BusinessDetailActivity.class);
            intent.putExtra(BusinessDetailFragment.ARG_ITEM, business);

            context.startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);

        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchItem.collapseActionView();
                mSearchParam = new SearchParam.Builder(mSearchParam).term(query).build();
                searchBusiness();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        mSearchView.setQuery("Restaurant", false);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initialSearch() {
        SearchParam.Builder builder = new SearchParam.Builder(mSearchParam);
        if (!TextUtils.isEmpty(mSearchView.getQuery())) {
            builder.term(mSearchView.getQuery().toString());
        }
        mSearchParam = builder.build();
        searchBusiness();
    }

    public void onEventMainThread(YelpApi.Search.Response response) {
        if (response.isSuccess()) {
            if (mSearchParam.getGroupBy() == SearchParam.GROUP_BY_CATEGORY) {
                Collections.sort(response.businesses, new Business.CategorySorter());
            } else if (mSearchParam.getGroupBy() == SearchParam.GROUP_BY_PRICE) {
                Collections.sort(response.businesses, new Business.PriceSorter());
            }
            mAdapter.setBusinessList(response.businesses, mSearchParam.getGroupBy());
            mBinding.loadingProgress.setVisibility(View.GONE);
            if (response.businesses == null || response.businesses.size() == 0) {
                mBinding.noResultText.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
            } else {
                mBinding.noResultText.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
            }
        } else {
            Toast.makeText(this, "Search Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchBusiness() {
        //location is required
        if (mSearchParam.getLocation() == null ||
                (TextUtils.isEmpty(mSearchParam.getLocation().description)
                        && mSearchParam.getLocation().latitude == 0
                        && mSearchParam.getLocation().longitude == 0)) {
            if (mLastLocation != null) {
                mSearchParam = new SearchParam.Builder(mSearchParam)
                        .location(mLastLocation.getLatitude(), mLastLocation.getLongitude()).build();
            } else {
                mSearchParam = new SearchParam.Builder(mSearchParam).location("Toronto").build();
            }
        }
        mBinding.loadingProgress.setVisibility(View.VISIBLE);
        mBinding.noResultText.setVisibility(View.GONE);
        mListView.setVisibility(View.GONE);
        new YelpApi.Search(mSearchParam).postRequestAsync();
    }

    @Override
    public void onFilterSet(SearchParam searchParam) {
        mSearchParam = searchParam;
        searchBusiness();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSION);
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mSearchParam.useCurrentLocation() && mLastLocation != null) {
            mSearchParam = new SearchParam.Builder(mSearchParam).
                    location(mLastLocation.getLatitude(), mLastLocation.getLongitude()).build();
        }

        initialSearch();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mSearchParam.useCurrentLocation() && mLastLocation != null) {
                mSearchParam = new SearchParam.Builder(mSearchParam).
                        location(mLastLocation.getLatitude(), mLastLocation.getLongitude()).build();
            }
            initialSearch();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        initialSearch();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        initialSearch();
    }
}
