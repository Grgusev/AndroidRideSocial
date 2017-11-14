package com.pjtech.android.ridesocial.ui.interfaces;

import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.pjtech.android.ridesocial.GlobalData;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.RideSocialApp;
import com.pjtech.android.ridesocial.firebase.FirebaseManager;
import com.pjtech.android.ridesocial.firebase.Message;
import com.pjtech.android.ridesocial.model.MapStatusType;
import com.pjtech.android.ridesocial.ui.adapters.RidingListAdapter;
import com.pjtech.android.ridesocial.ui.fragments.HomeFragment;
import com.pjtech.android.ridesocial.utils.MapUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by android on 6/13/17.
 */

public class MessageManager {

    HomeFragment mFragment;
    View rootView;
    Spinner mPreMessage;
    EditText mCustomMessage;
    ListView mChatList;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    private FirebaseListAdapter<Message> adapter;

    public MessageManager(View rootView, HomeFragment fragment)
    {
        this.rootView = rootView;
        mFragment = fragment;

        mPreMessage = (Spinner)rootView.findViewById(R.id.preMessage);
        mCustomMessage = (EditText)rootView.findViewById(R.id.customMesg);
        mChatList = (ListView) rootView.findViewById(R.id.chatList);

        String[] preMessage = mFragment.getContext().getResources().getStringArray(R.array.preMessages);
        List<String> list = Arrays.asList(preMessage);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mFragment.getContext(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPreMessage.setAdapter(dataAdapter);

        rootView.findViewById(R.id.btn_reach_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.mHostingManager.addRadioButtons();
                mFragment.setupUI(MapStatusType.HOSTING_SELECTION);
            }
        });

        rootView.findViewById(R.id.btn_cancel_find_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapUtils.clearRouterInfos();
                mFragment.findAnotherRide();
            }
        });

        rootView.findViewById(R.id.btn_send_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mCustomMessage.getText().toString();
                GlobalData.mFirebaseManager.sendMessage(GlobalData.rideInfo.firebaseID, GlobalData.userInfo.userId, GlobalData.userInfo.userName, message);
            }
        });

        rootView.findViewById(R.id.btn_send_premessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = (String) mPreMessage.getSelectedItem();
                GlobalData.mFirebaseManager.sendMessage(GlobalData.rideInfo.firebaseID, GlobalData.userInfo.userId, GlobalData.userInfo.userName, message);
            }
        });
    }

    public void createMessageRoom()
    {
        if (adapter != null)
        {
            adapter.cleanup();
        }

        GlobalData.mFirebaseManager.observeRoomMessages(GlobalData.rideInfo.firebaseID, new FirebaseManager.OnMessageListener() {
            @Override
            public void onMessageResponse(Message message) {
            }
        });

        adapter = new FirebaseListAdapter<Message>(mFragment.getActivity(), Message.class,
                R.layout.message, GlobalData.mFirebaseManager.getRoomRef(GlobalData.rideInfo.firebaseID)) {
            @Override
            protected void populateView(View v, Message model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
//                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.msg);

                if (model.senderID.equals(GlobalData.userInfo.userId))
                {
                    messageUser.setText(RideSocialApp.mContext.getString(R.string.me));
                }
                else
                {
                    messageUser.setText(model.senderName);
                }

                // Format the date before showing it
//                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
//                        model.getMessageTime()));
            }
        };

        mChatList.setAdapter(adapter);
    }
}
