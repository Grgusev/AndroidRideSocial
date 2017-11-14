package com.pjtech.android.ridesocial.ui.interfaces;

import android.view.View;
import android.widget.ListView;

import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.ui.adapters.RateAdapter;
import com.pjtech.android.ridesocial.ui.fragments.HomeFragment;

/**
 * Created by android on 6/13/17.
 */

public class RateRiderManager {
    HomeFragment mFragment;
    View rootView;

    ListView mRateRider;
    RateAdapter mAdapter;

    public RateRiderManager(View rootView, HomeFragment fragment)
    {
        this.rootView = rootView;
        mFragment = fragment;

        mRateRider = (ListView)rootView.findViewById(R.id.rate_riders);
        mAdapter = new RateAdapter(mFragment.getActivity());
        mRateRider.setAdapter(mAdapter);

        rootView.findViewById(R.id.btn_submit_riders).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
