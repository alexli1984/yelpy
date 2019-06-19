package com.alapplication.yelpy.ui;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic Recycler View Adapter with a onItemClick callback
 *
 * @param <T> Type of list item
 * @param <V> Type of ViewHolder
 */
public abstract class AbstractRecyclerViewAdapter<T, V extends AbstractRecyclerViewAdapter.ViewHolder> extends RecyclerView.Adapter<V> {
    protected List<T> mList = new ArrayList<>();
    protected Context mContext;
    protected OnItemClickListener mListener;

    public AbstractRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    public void setItemList(List<T> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mListener = mItemClickListener;
    }


    public T getItemAt(int position) {
        return mList != null && position >= 0 && position < mList.size() ? mList.get(position) : null;
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ViewHolder(View itemView) {
            super(itemView);
            if (mListener != null) {
                itemView.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(v, getLayoutPosition());
            }
        }
    }
}
