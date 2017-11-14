package com.pjtech.android.ridesocial.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.ui.fragments.HomeFragment;

/**
 * Created by android on 6/13/17.
 */

public class RateAdapter extends BaseAdapter {

    Context mContext;

    public RateAdapter(Context context)
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_rate_users, parent, false);
        }

        TextView name = (TextView)convertView.findViewById(R.id.name);
//        name.setText(HomeFragment.mUserManager.mUsers.get(position).userName);

        RatingBar bar = (RatingBar)convertView.findViewById(R.id.ratingBar);

        return convertView;
    }
}
