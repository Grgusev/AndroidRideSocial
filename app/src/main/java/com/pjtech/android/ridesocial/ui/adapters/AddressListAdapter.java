package com.pjtech.android.ridesocial.ui.adapters;

import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.RideSocialApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by almond on 7/25/2017.
 */

public class AddressListAdapter extends BaseAdapter implements Filterable {

    Context mContext;
    List<String> mAddressList;

    private static final NoFilter NO_FILTER = new NoFilter();

    private static class NoFilter extends Filter {
        protected FilterResults performFiltering(CharSequence prefix) {
            return new FilterResults();
        }

        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Do nothing
        }
    }


    @Override
    public Filter getFilter() {
        return NO_FILTER;
    }

    public interface OnClickListener {
        void onClickListener(int position);
    }

    AddressListAdapter.OnClickListener mClickListener;

    public void setClickListener(AddressListAdapter.OnClickListener clickListener)
    {
        mClickListener = clickListener;
    }


    public AddressListAdapter(Context context)
    {
        mContext = context;
    }

    public void setAddressList(List<String> list)
    {
        mAddressList = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mAddressList == null)
            return 0;

        return mAddressList.size();
    }

    @Override
    public String getItem(int position) {
        return mAddressList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(RideSocialApp.mContext).inflate(R.layout.item_address, parent, false);
        }

        TextView address = (TextView)convertView.findViewById(R.id.address);
        address.setText(mAddressList.get(position));

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onClickListener(position);
            }
        });

        return convertView;
    }

    private String getFullAddres(final Address adrss)
    {
        String city = adrss.getLocality();
        String country = adrss.getCountryName();
        String addr = adrss.getAddressLine(0);

        return addr+", "+city+", "+country;
    }
}
