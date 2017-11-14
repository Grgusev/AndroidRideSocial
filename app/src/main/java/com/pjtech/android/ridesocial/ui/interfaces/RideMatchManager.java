package com.pjtech.android.ridesocial.ui.interfaces;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.FirebaseDatabase;
import com.pjtech.android.ridesocial.GlobalData;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.model.MapStatusType;
import com.pjtech.android.ridesocial.ui.adapters.RidingListAdapter;
import com.pjtech.android.ridesocial.ui.fragments.HomeFragment;
import com.pjtech.android.ridesocial.utils.MapUtils;

/**
 * Created by android on 6/12/17.
 */

public class RideMatchManager {

    HomeFragment mFragment = null;
    View rootView;
    ListView mRideMatchList = null;
    Button mContinueRide    = null;
    Button mFindAnotherRide = null;

    RidingListAdapter mAdapter = null;

    public RideMatchManager(View rootView, HomeFragment fragment)
    {
        this.rootView = rootView;
        mRideMatchList  = (ListView)rootView.findViewById(R.id.ride_match_list);
        mContinueRide   = (Button)rootView.findViewById(R.id.btn_continue_ride);
        mFindAnotherRide   = (Button)rootView.findViewById(R.id.btn_find_another_ride);
        mFragment = fragment;

        mAdapter = new RidingListAdapter(mFragment.getActivity());
        mRideMatchList.setAdapter(mAdapter);

        mContinueRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.mMessageManager.createMessageRoom();
                mFragment.setupUI(MapStatusType.MESSAGE_WINDOW);
            }
        });

        mFindAnotherRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapUtils.clearRouterInfos();
                mFragment.findAnotherRide();
            }
        });
    }

    public void updateRouterData()
    {
        mAdapter.notifyDataSetChanged();
    }
}
