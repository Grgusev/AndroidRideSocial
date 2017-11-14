package com.pjtech.android.ridesocial.ui.interfaces;

import android.view.View;

import com.pjtech.android.ridesocial.GlobalData;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.model.MapStatusType;
import com.pjtech.android.ridesocial.ui.fragments.HomeFragment;
import com.pjtech.android.ridesocial.utils.UberUtils;

/**
 * Created by android on 6/13/17.
 */

public class UberManager {
    HomeFragment mFragment;
    View rootView;

    public UberManager(View rootView, HomeFragment fragment)
    {
        this.rootView = rootView;
        mFragment = fragment;

        if (!UberUtils.installedPackage(UberUtils.uberPackageName))
        {
            rootView.findViewById(R.id.btn_call_uber).setVisibility(View.GONE);
        }
        else
        {
            rootView.findViewById(R.id.btn_call_uber).setVisibility(View.VISIBLE);
        }

        if (!UberUtils.installedPackage(UberUtils.lyftPackageName))
        {
            rootView.findViewById(R.id.btn_call_lyft).setVisibility(View.GONE);
        }
        else
        {
            rootView.findViewById(R.id.btn_call_lyft).setVisibility(View.VISIBLE);
        }

        rootView.findViewById(R.id.btn_call_uber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UberUtils.startUberCabServiceCall(mFragment.getActivity(), GlobalData.rideInfo.meetUp, GlobalData.rideInfo.dropOff,
                        GlobalData.userInfo.userName);
            }
        });

        rootView.findViewById(R.id.btn_call_lyft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UberUtils.startLyftCabServiceCall(mFragment.getActivity(), GlobalData.rideInfo.meetUp, GlobalData.rideInfo.dropOff,
                        GlobalData.userInfo.userName);
            }
        });

        rootView.findViewById(R.id.btn_next_uber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.setupUI(MapStatusType.SEND_FARE);
            }
        });
    }


    public void setUberButtonsVisibility()
    {
        if (!UberUtils.installedPackage(UberUtils.uberPackageName))
        {
            rootView.findViewById(R.id.btn_call_uber).setVisibility(View.GONE);
        }
        else
        {
            rootView.findViewById(R.id.btn_call_uber).setVisibility(View.VISIBLE);
        }

        if (!UberUtils.installedPackage(UberUtils.lyftPackageName))
        {
            rootView.findViewById(R.id.btn_call_lyft).setVisibility(View.GONE);
        }
        else
        {
            rootView.findViewById(R.id.btn_call_lyft).setVisibility(View.VISIBLE);
        }
    }
}
