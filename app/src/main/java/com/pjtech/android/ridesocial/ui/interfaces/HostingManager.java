package com.pjtech.android.ridesocial.ui.interfaces;

import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pjtech.android.ridesocial.GlobalData;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.RideSocialApp;
import com.pjtech.android.ridesocial.model.HostingResponse;
import com.pjtech.android.ridesocial.model.MapStatusType;
import com.pjtech.android.ridesocial.model.RouterResponse;
import com.pjtech.android.ridesocial.model.UserInfo;
import com.pjtech.android.ridesocial.model.UserRideInfo;
import com.pjtech.android.ridesocial.rest.ApiClient;
import com.pjtech.android.ridesocial.rest.ApiInterface;
import com.pjtech.android.ridesocial.ui.dialog.MyProgressDialog;
import com.pjtech.android.ridesocial.ui.fragments.HomeFragment;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by android on 6/13/17.
 */

public class HostingManager {
    HomeFragment mFragment;
    View rootView;
    RadioGroup mHostings;

    public void addRadioButtons()
    {
        if (mHostings == null) return;

        mHostings.removeAllViews();
        ArrayList<UserRideInfo> mUserlist = GlobalData.rideInfo.rideInfos;
        for (int i = 0; i < mUserlist.size(); i ++)
        {
            RadioButton rdbtn = new RadioButton(mFragment.getActivity());

            rdbtn.setId(i);

            if (GlobalData.userInfo.userId.equals(GlobalData.rideInfo.rideInfos.get(i).userId))
            {
                rdbtn.setText(RideSocialApp.mContext.getString(R.string.me));
            }
            else
            {
                rdbtn.setText(mUserlist.get(i).username);
            }

            int padding = (int)mFragment.getActivity().getResources().getDimension(R.dimen.margin_small);
            rdbtn.setPadding(padding, padding, padding, padding);
            rdbtn.setTextSize(mFragment.getActivity().getResources().getDimension(R.dimen.textsize_small));
            mHostings.addView(rdbtn);
        }
    }

    public HostingManager(View rootView, HomeFragment fragment)
    {
        this.rootView = rootView;
        mFragment = fragment;

        mHostings = (RadioGroup) rootView.findViewById(R.id.hosting_select);

        rootView.findViewById(R.id.btn_next_hosting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectId = mHostings.getCheckedRadioButtonId();

                final MyProgressDialog dlg_progress = new MyProgressDialog(mFragment.getActivity());

                dlg_progress.show();
                ApiInterface apiService =
                        ApiClient.getClient().create(ApiInterface.class);
                Call<HostingResponse> call = apiService.selectHosting(GlobalData.userInfo.userId, GlobalData.rideInfo.routerID,
                        GlobalData.rideInfo.rideInfos.get(selectId).userId, GlobalData.requestId);

                call.enqueue(new Callback<HostingResponse>() {
                    @Override
                    public void onResponse(Call<HostingResponse> call, Response<HostingResponse> response) {
                        dlg_progress.dismiss();

                        if (!response.isSuccessful()) return;

                        if (response.body().status.equals("0")) {
                            return;
                        }
                        else if (response.body().status.equals("1")) {
                            Toast.makeText(mFragment.getActivity(), mFragment.getActivity().getText(R.string.no_existing_name), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<HostingResponse> call, Throwable t) {
                        // Log error here since request failed
                        dlg_progress.dismiss();
                        Toast.makeText(mFragment.getActivity(), mFragment.getActivity().getText(R.string.connect_err), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    public void showHostingSelectedNotYOU() {
        TextView mStatus = (TextView)rootView.findViewById(R.id.host_select_status);
        mStatus.setText(RideSocialApp.mContext.getString(R.string.hosting_selected_not_you, GlobalData.getName(GlobalData.hostingID)));
        mStatus.setVisibility(View.VISIBLE);
    }

    public void showNotSelected() {
        TextView mStatus = (TextView)rootView.findViewById(R.id.host_select_status);
        mStatus.setText(RideSocialApp.mContext.getString(R.string.host_not_selected));
        mStatus.setVisibility(View.VISIBLE);
    }
}
