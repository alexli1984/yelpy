package com.alapplication.yelpy.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alapplication.yelpy.R;
import com.alapplication.yelpy.api.model.Business;
import com.alapplication.yelpy.api.model.SearchParam;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for business list
 */
public class BusinessListAdapter extends IndexedRecyclerViewAdapter<IndexListItem<Business>, BusinessListAdapter.ViewHolder> {

    private int mGroupBy = SearchParam.GROUP_BY_NONE;

    public BusinessListAdapter(Context context, int groupBy) {
        super(context);
        mGroupBy = groupBy;
    }

    public void initIndex(List<Business> businesses) {
        ArrayList<IndexListItem<Business>> indexItemList = new ArrayList<>();
        String prevSection = "";
        int curSectionPos = 0;
        for (Business curItem : businesses) {
            if (mGroupBy == SearchParam.GROUP_BY_NONE) {
                indexItemList.add(new IndexListItem<>(null, curItem, -1));
            } else {
                String curSection = curItem.getIndexString(mGroupBy);
                if (!prevSection.equals(curSection)) {
                    // if not equal to pre_section, we add a header item
                    IndexListItem<Business> header = new IndexListItem<>(curItem, curSection);
                    // update the curSectionPos as the new header position
                    curSectionPos = indexItemList.size();
                    // the header's section position is itself's position
                    header.setSectionPos(curSectionPos);
                    indexItemList.add(header);
                    prevSection = curSection;
                }
                indexItemList.add(new IndexListItem<>(curSection, curItem, curSectionPos));
            }
        }
        updateIndexItemList(indexItemList);
    }

    public void setBusinessList(List<Business> businessList, int groupBy) {
        mGroupBy = groupBy;
        initIndex(businessList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_ITEM:
            default:
                view = mLayoutInflater.inflate(R.layout.business_list_content, parent, false);
                break;
            case TYPE_SECTION:
                view = mLayoutInflater.inflate(R.layout.list_section_item, parent, false);
                view.setEnabled(false);
                break;
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case TYPE_ITEM:
                bindItemView(holder, position);
                break;
            case TYPE_SECTION:
                holder.categoryTitle.setText(getItemAt(position).getIndexFieldValue());
                break;
        }
    }


    private void bindItemView(ViewHolder holder, int position) {
        Business business = getItemAt(position).getContent();
        if (!TextUtils.isEmpty(business.image_url)) {
            Picasso.with(mContext).load(business.image_url).placeholder(R.drawable.ic_business_placeholder).fit().centerCrop().into(holder.thumbnail);
        } else {
            holder.thumbnail.setImageResource(R.drawable.ic_business_placeholder);
        }
        holder.title.setText(business.name);
        holder.rating.setRating(business.rating);
        holder.review.setText(mContext.getResources().getQuantityString(R.plurals.reviews, business.review_count, business.review_count));
        holder.price.setText(business.price);
        holder.address.setText(business.location.toString());
        holder.categories.setText(business.getCategorySummary());
        holder.address.setText(business.location == null ? "" : business.location.getShortForm());
    }

    public class ViewHolder extends AbstractRecyclerViewAdapter.ViewHolder {
        private ImageView thumbnail;
        private TextView title, review, price, address, categories, categoryTitle;
        private RatingBar rating;

        public ViewHolder(View itemView) {
            super(itemView);

            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            title = (TextView) itemView.findViewById(R.id.title);
            review = (TextView) itemView.findViewById(R.id.reviews);
            address = (TextView) itemView.findViewById(R.id.address);
            categories = (TextView) itemView.findViewById(R.id.categories);
            price = (TextView) itemView.findViewById(R.id.price);
            rating = (RatingBar) itemView.findViewById(R.id.rating);
            categoryTitle = (TextView) itemView.findViewById(R.id.category_title);
        }
    }
}
