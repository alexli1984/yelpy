package com.alapplication.yelpy.ui;

import android.content.Context;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * RecyclerView Adapter for grouped items
 */
public abstract class IndexedRecyclerViewAdapter<T extends IndexListItem, V extends AbstractRecyclerViewAdapter.ViewHolder>
        extends AbstractRecyclerViewAdapter<T, V> {

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_SECTION = 1;
    protected LayoutInflater mLayoutInflater;
    // sorted array list to store list view data to display
    private HashMap<String, Integer> mapIndex;

    public IndexedRecyclerViewAdapter(Context context) {
        super(context);
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setupIndex(ArrayList<T> sortedListItem) {
        mapIndex = new LinkedHashMap<>();

        if (sortedListItem.size() != 0) {
            Iterator<T> iterator = sortedListItem.iterator();
            int curPos = 0;
            int oldPos = 0;
            while (iterator.hasNext()) {
                IndexListItem indexListItem = iterator.next();
                if (indexListItem.isSectionItem(oldPos))
                    mapIndex.put(indexListItem.getIndexFieldValue(), curPos);

                curPos++;
                oldPos++;
            }
        }

        setItemList(new ArrayList<>(sortedListItem));
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.size() == 0)
            return TYPE_ITEM;
        return mList.get(position).isSectionItem(position) ? TYPE_SECTION : TYPE_ITEM;
    }

    public void updateIndexItemList(ArrayList<T> newListItems) {
        setupIndex(newListItems);
        notifyDataSetChanged();
    }
}
