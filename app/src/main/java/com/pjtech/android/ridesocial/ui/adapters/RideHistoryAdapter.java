package com.pjtech.android.ridesocial.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.pjtech.android.ridesocial.R;

/**
 * Created by android on 6/13/17.
 */

public class RideHistoryAdapter extends BaseAdapter {
    Context mContext;

    public RideHistoryAdapter(Context context)
    {
        mContext = context;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_ride_history, parent, false);
        }

        RecyclerView mList  = (RecyclerView)convertView.findViewById(R.id.mRideHistoryItem);

        return convertView;
    }
}
