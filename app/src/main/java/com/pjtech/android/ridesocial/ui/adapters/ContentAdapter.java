package com.pjtech.android.ridesocial.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.pjtech.android.ridesocial.GlobalData;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.model.PhoneContact;

import java.util.ArrayList;

/**
 * Created by android on 6/18/17.
 */

public class ContentAdapter extends BaseAdapter implements Filterable {

    private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private Context context;
    private ArrayList<PhoneContact> mFilterObject;
    private String filterText;

    private OnItemClickListener mItemClickListener;

    private ItemFilter mItemFilter = new ItemFilter();

    public interface OnItemClickListener {
        void onItemClick(View view, boolean checked, int position, PhoneContact contact);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mItemClickListener = listener;
    }

    public ContentAdapter(Context context) {
        this.context = context;
        mFilterObject = GlobalData.phoneContacts;
    }

    @Override
    public int getCount() {
        return mFilterObject.size();
    }

    @Override
    public Object getItem(int position) {
        return mFilterObject.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setFilterText (String filter) {
        filterText = filter;
        mItemFilter.filter(filterText);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.intem_contact, parent, false);
        }

        TextView name = (TextView)convertView.findViewById(R.id.name);
        CheckBox checked = (CheckBox)convertView.findViewById(R.id.checkbox);

        name.setText(mFilterObject.get(position).name);

        checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mItemClickListener.onItemClick(buttonView, isChecked, position, mFilterObject.get(position));
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checked = (CheckBox)v.findViewById(R.id.checkbox);
                if (checked.isChecked()) checked.setChecked(false);
                else checked.setChecked(true);

                mItemClickListener.onItemClick(v, checked.isChecked(), position, mFilterObject.get(position));
            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return mItemFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String query = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();
            final ArrayList<PhoneContact> result_list = new ArrayList<>(GlobalData.phoneContacts.size());

            for (int i = 0; i < GlobalData.phoneContacts.size(); i++) {
                String str_title = GlobalData.phoneContacts.get(i).name;
                if (str_title.toLowerCase().contains(query)) {
                    result_list.add(GlobalData.phoneContacts.get(i));
                }
            }

            results.values = result_list;
            results.count = result_list.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilterObject = (ArrayList<PhoneContact>) results.values;
            notifyDataSetChanged();
        }
    }
}
