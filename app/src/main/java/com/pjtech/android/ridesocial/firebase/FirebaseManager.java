package com.pjtech.android.ridesocial.firebase;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by almond on 6/19/2017.
 */

public class FirebaseManager {
    Context context;
    DatabaseReference databaseRef = null;

    ValueEventListener roomRefHandle = null;
    DatabaseReference roomRef = null;

    ValueEventListener userRefHandle = null;
    DatabaseReference userRef = null;

    public interface OnUserResponseListener {
        void onUserResponse(User user);
    }

    public interface OnMessageListener {
        void onMessageResponse(Message message);
    }

    public FirebaseManager(Context context)
    {
        this.context = context;
        databaseRef = FirebaseDatabase.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference().child("User");
    }

    public void destroy()
    {
        resetRoomRef();
        resetUserRef();
    }

    public void resetUserRef() {
        if (userRefHandle != null)
            userRef.removeEventListener(userRefHandle);
        userRef = null;
    }

    public void resetRoomRef() {
        if (roomRefHandle != null)
            roomRef.removeEventListener(roomRefHandle);
        roomRef = null;
    }

    public static void loginInAnonymously(OnCompleteListener onCompleteListener)
    {
        FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(onCompleteListener);
    }

    public void observeUserData(final OnUserResponseListener userResponseListener)
    {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userResponseListener.onUserResponse(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                userResponseListener.onUserResponse(null);
            }
        });
    }

    public DatabaseReference getUserRef(String userID)
    {
        if (userRef != null)
        {
            return userRef.child(userID);
        }

        return null;
    }

    public void setUserInfo(String userID, String name)
    {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userID", userID);
        userMap.put("name", name);
        userMap.put("location", "");

        getUserRef(userID).updateChildren(userMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

            }
        });
    }

    public void updateUserLocation(String userID, String location)
    {
        getUserRef(userID).child("location").setValue(location);
    }

    /*Messaging*/

    public DatabaseReference getRoomRef(String roomID)
    {
        if (databaseRef != null)
        {
            return databaseRef.child("Rooms").child(roomID);
        }
        return null;
    }

    public void sendMessage(String roomID, String senderID, String senderName, String msg)
    {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("senderID", senderID);
        userMap.put("senderName", senderName);
        userMap.put("msg", msg);

        getRoomRef(roomID).push().updateChildren(userMap);
    }

    public void observeRoomMessages(String roomID, final OnMessageListener onMessageListener)
    {
        if (roomRef != null)
        {
            resetRoomRef();
        }
        roomRef = getRoomRef(roomID);
        roomRefHandle = roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Message message = dataSnapshot.getValue(Message.class);
                onMessageListener.onMessageResponse(message);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onMessageListener.onMessageResponse(null);
            }
        });
    }

}
