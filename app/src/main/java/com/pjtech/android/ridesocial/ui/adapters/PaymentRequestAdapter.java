package com.pjtech.android.ridesocial.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.pjtech.android.ridesocial.GlobalData;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.firebase.Message;
import com.pjtech.android.ridesocial.model.UserInfo;
import com.pjtech.android.ridesocial.ui.fragments.HomeFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by android on 6/13/17.
 */

public class PaymentRequestAdapter extends BaseAdapter {
    Context mContext;

    public PaymentRequestAdapter(Context context)
    {
        mContext = context;
    }

    public interface OnClickListener {
        void onClickListener(int position, double amount);
    }

    OnClickListener mClickListener;

    public void setClickListener(OnClickListener clickListener)
    {
        mClickListener = clickListener;
    }

    @Override
    public int getCount() {
        if (GlobalData.rideInfo == null ||GlobalData.rideInfo.rideInfos.size() <= 1) return 0;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_payment_request, parent, false);
        }

        TextView name = (TextView)convertView.findViewById(R.id.name);
        name.setText(GlobalData.rideInfo.rideInfos.get(position + 1).username);

        final EditText payment = (EditText)convertView.findViewById(R.id.fare);

        convertView.findViewById(R.id.btn_send_fare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null)
                {
                    long value = Long.valueOf(payment.getText().toString());
                    if (value > 0)
                    {
                        mClickListener.onClickListener(position + 1, value);
                    }
                }
            }
        });

        return convertView;
    }
}
