package com.alapplication.yelpy.ui;

import android.app.DialogFragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.alapplication.yelpy.R;
import com.alapplication.yelpy.api.model.LocationParam;
import com.alapplication.yelpy.api.model.SearchParam;

import java.util.Arrays;
import java.util.List;

/**
 * Dialog fragment for search filter
 */
public class FilterDialog extends DialogFragment {
    public static final String EXTRA_SEARCH_PARAM = "extra_search_param";

    private SearchParam mSearchParam;
    private com.alapplication.yelpy.ui.FilterDialogBinding mBinding;
    private FilterListener mListener;

    public interface FilterListener {
        void onFilterSet(SearchParam searchParam);
    }

    public FilterDialog() {
    }

    public static FilterDialog newInstance(SearchParam searchParam) {
        FilterDialog dialog = new FilterDialog();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_SEARCH_PARAM, searchParam);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_filter, container, false);

        if (mSearchParam != null) {
            List<String> sortValues = Arrays.asList(getResources().getStringArray(R.array.sort_by_value));
            int index = sortValues.indexOf(mSearchParam.getSortBy());
            mBinding.sortBySpinner.setSelection(index >= 0 ? index : 0);
            mBinding.groupBySpinner.setSelection(mSearchParam.getGroupBy());
            mBinding.openNowSwitch.setChecked(mSearchParam.getOpenNow() != null ? mSearchParam.getOpenNow() : false);
            String attributes = mSearchParam.getAttributes();
            mBinding.offerDealSwitch.setChecked(attributes != null && attributes.contains(SearchParam.ATTR_DEALS));
            mBinding.hotNewSwitch.setChecked(attributes != null && attributes.contains(SearchParam.ATTR_HOT_NEW));
            mBinding.useCurrentCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mBinding.locationEdit.setEnabled(!isChecked);
                }
            });
            LocationParam location = mSearchParam.getLocation();
            if (location != null) {
                if (location.latitude != 0 || location.longitude != 0) {
                    mBinding.useCurrentCheckbox.setChecked(true);
                } else if (location.description != null) {
                    mBinding.locationEdit.setText(location.description);
                }
            }
        }

        mBinding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });

        mBinding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onFilterSet(saveSearchParam());
                }
                dismissAllowingStateLoss();
            }
        });
        return mBinding.getRoot();
    }

    private SearchParam saveSearchParam() {
        SearchParam.Builder builder = new SearchParam.Builder(mSearchParam);
        if (mBinding.useCurrentCheckbox.isChecked()) {
            builder.location(null);
        } else {
            builder.location(mBinding.locationEdit.getText().toString());
        }
        builder.openNow(mBinding.openNowSwitch.isChecked());

        List<String> sortValues = Arrays.asList(getResources().getStringArray(R.array.sort_by_value));
        builder.sortBy(sortValues.get(mBinding.sortBySpinner.getSelectedItemPosition()));

        builder.attributes(getAttributes());
        SearchParam param = builder.build();
        param.setUseCurrentLocation(mBinding.useCurrentCheckbox.isChecked());
        param.setGroupBy(mBinding.groupBySpinner.getSelectedItemPosition());
        return param;
    }

    private String getAttributes() {
        StringBuffer sb = new StringBuffer();
        if (mBinding.offerDealSwitch.isChecked()) {
            sb.append(SearchParam.ATTR_DEALS + ",");
        }
        if (mBinding.hotNewSwitch.isChecked()) {
            sb.append(SearchParam.ATTR_HOT_NEW + ",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args.containsKey(EXTRA_SEARCH_PARAM)) {
            mSearchParam = (SearchParam) args.getSerializable(EXTRA_SEARCH_PARAM);
        } else {
            mSearchParam = new SearchParam(new LocationParam("Toronto"));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void setFilterListener(FilterListener listener) {
        mListener = listener;
    }
}
