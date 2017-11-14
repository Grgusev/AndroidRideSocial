package com.pjtech.android.ridesocial.ui.adapters;

import android.content.Context;
import android.support.constraint.solver.Goal;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.pjtech.android.ridesocial.GlobalData;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.model.UserInfo;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by android on 6/13/17.
 */

public class RidingListAdapter extends BaseAdapter {

    Context mContext;

    public RidingListAdapter(Context context)
    {
        mContext = context;
    }

    @Override
    public int getCount() {
        if (GlobalData.rideInfo == null || GlobalData.rideInfo.rideInfos.size() <= 1) return 0;
        return GlobalData.rideInfo.rideInfos.size() - 1;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_match_riding, parent, false);
        }

        TextView name = (TextView)convertView.findViewById(R.id.name);
        name.setText(GlobalData.rideInfo.rideInfos.get(position + 1).username);

        RatingBar ratingBar = (RatingBar)convertView.findViewById(R.id.ratingBar);
        ratingBar.setRating((float)GlobalData.rideInfo.rideInfos.get(position + 1).rating);

        String fbId = GlobalData.rideInfo.rideInfos.get(position + 1).facebookId;

        convertView.findViewById(R.id.facebook_desc).setVisibility(View.GONE);

        if (GlobalData.userInfo.userId.equals(GlobalData.rideInfo.rideInfos.get(position + 1).userId))
        {
            convertView.setVisibility(View.GONE);
            return convertView;
        }

        if (GlobalData.userInfo.facebookUrl == null || fbId == null || "null".equals(GlobalData.userInfo.facebookUrl) || "null".equals(fbId))
        {

        }
        else if (!"".equals(GlobalData.userInfo.facebookUrl) && !"".equals(fbId))
        {
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/" + AccessToken.getCurrentAccessToken().getUserId() + "/friends/" + fbId,
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                                    /* handle the result */
                            android.util.Log.e("ddd", response.toString());
                        }
                    }
            ).executeAsync();
        }

        return convertView;
    }
}
