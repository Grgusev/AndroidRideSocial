package com.pjtech.android.ridesocial.utils;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.pjtech.android.ridesocial.GlobalData;
import com.pjtech.android.ridesocial.RideSocialApp;
import com.pjtech.android.ridesocial.model.RequestType;
import com.pjtech.android.ridesocial.model.RideInfo;
import com.pjtech.android.ridesocial.model.UserRideInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by android on 6/17/17.
 */

public class RideInfoUtils {

    public static int parseRouterInfo(JSONObject object)
    {
        int code = 1;
        String type = "";
        try {
            code = object.getInt("code");
            type = object.getString("type");

            if (code == 1 && type.equals("create_router"))
            {

                if (object.getString("router_id").equals("null")) return -1;

                JSONArray rideInfos = object.getJSONArray("ride_infos");
                String requestID    = object.getString("request_id");

                if (rideInfos.length() <= 1)
                {
                    if (!requestID.equals(GlobalData.requestId))
                    {
                        return -6;
                    }
                }

                GlobalData.rideInfo = new RideInfo();

                GlobalData.rideInfo.routerID = object.getString("router_id");
                GlobalData.rideInfo.firebaseID = object.getString("firebase_room_id");
                GlobalData.rideInfo.meetUp = new LatLng(object.getJSONObject("start").getDouble("lat"), object.getJSONObject("start").getDouble("lon"));
                GlobalData.rideInfo.dropOff = new LatLng(object.getJSONObject("end").getDouble("lat"), object.getJSONObject("end").getDouble("lon"));

                GlobalData.rideInfo.rideInfos.clear();

                if (rideInfos != null)
                {
                    for (int i = 0; i < rideInfos.length(); i ++)
                    {
                        UserRideInfo info = new UserRideInfo();
                        info.userId = rideInfos.getJSONObject(i).getString("user_id");

                        info.requestId = rideInfos.getJSONObject(i).getString("request_id");
                        info.username = rideInfos.getJSONObject(i).getString("name");
                        info.cellNum = rideInfos.getJSONObject(i).getString("cell_number");
                        info.orgPosition = new LatLng(rideInfos.getJSONObject(i).getJSONObject("start").getDouble("lat"),
                                rideInfos.getJSONObject(i).getJSONObject("start").getDouble("lon"));
                        info.destPosition = new LatLng(rideInfos.getJSONObject(i).getJSONObject("end").getDouble("lat"),
                                rideInfos.getJSONObject(i).getJSONObject("end").getDouble("lon"));
                        info.rating = rideInfos.getJSONObject(i).getDouble("rate");
                        info.facebookId = rideInfos.getJSONObject(i).getString("facebook_url");

                        if (info.userId .equals(GlobalData.userInfo.userId))
                        {
                            GlobalData.rideInfo.rideInfos.add(0, info);
                        }
                        else
                        {
                            GlobalData.rideInfo.rideInfos.add(info);
                        }
                    }
                }

                if (GlobalData.rideInfo.rideInfos.size() <= 1) {
                    return -1;
                }

                return code;
            }
            else if (code == 3 && type.equals("host_selected"))
            {
                if (object.getString("router_id").equals("null")) return -1;

                boolean same_id = object.getBoolean("same");

                if (same_id == true)
                {
                    GlobalData.hostingID = object.getString("host_id");
                    if (GlobalData.hostingID.equals(GlobalData.userInfo.userId))
                    {
                        return code;
                    }
                    else
                    {
                        return -2;
                    }
                }

                return -3;
            }
            else if (code == 4 && type.equals("payment_request"))
            {
                if (object.getString("router_id").equals("null")) return -1;

                String payment_request_id = object.getString("payment_request_id");
                String router_id = object.getString("router_id");
                String sender_id = object.getString("sender_id");
                String receiver_id = object.getString("receiver_id");
                long amount = object.getLong("requested_amount");

                Bundle bundle = new Bundle();
                bundle.putString("payment_request_id", payment_request_id);
                bundle.putString("router_id", router_id);
                bundle.putString("sender_id", sender_id);
                bundle.putString("receiver_id", receiver_id);
                bundle.putLong("requested_amount", amount);
                bundle.putLong("total_fare", object.getLong("total_amount"));

                Intent intent = new Intent();
                intent.setAction(RequestType.PUSH_RECEIVE_PAYMENT_REQUEST_RECEIVE);
                intent.putExtras(bundle);
                RideSocialApp.mContext.sendBroadcast(intent);

                return code;
            }
            else if (code == 5 && type.equals("payment_paid"))
            {
                if (object.getString("router_id").equals("null")) return -1;

                String payment_request_id = object.getString("payment_request_id");
                String router_id = object.getString("router_id");
                String sender_id = object.getString("sender_id");
                long requested_amount = object.getLong("requested_amount");
                long paid_amount = object.getLong("paid_amount");

                Bundle bundle = new Bundle();
                bundle.putString("payment_request_id", payment_request_id);
                bundle.putString("router_id", router_id);
                bundle.putString("sender_id", sender_id);
                bundle.putLong("requested_amount", requested_amount);
                bundle.putLong("paid_amount", paid_amount);
                bundle.putLong("total_fare", object.getLong("total_amount"));

                Intent intent = new Intent();
                intent.setAction(RequestType.PUSH_RECEIVE_PAYMENT_RECEIVE);
                intent.putExtras(bundle);
                RideSocialApp.mContext.sendBroadcast(intent);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
