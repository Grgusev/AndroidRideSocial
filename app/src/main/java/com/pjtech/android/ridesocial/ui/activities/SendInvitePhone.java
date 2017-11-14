package com.pjtech.android.ridesocial.ui.activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.pjtech.android.ridesocial.GlobalData;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.model.PhoneContact;
import com.pjtech.android.ridesocial.model.RequestType;
import com.pjtech.android.ridesocial.ui.adapters.ContentAdapter;

import java.util.ArrayList;

public class SendInvitePhone extends BaseActivity {

    ListView mContactListView = null;
    EditText mSearchText     = null;
    Button mSendButton = null;

    ContentAdapter mContactApdater;

    ArrayList<PhoneContact> mSendList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_invite_phone);

        this.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBack();
            }
        });

        if (GlobalData.phoneContacts == null)
        {
            GlobalData.phoneContacts = getContactsList();
        }

        mContactListView = (ListView) this.findViewById(R.id.contactView);
        mSearchText = (EditText)this.findViewById(R.id.search);
        mSendButton = (Button)this.findViewById(R.id.invite);

        mContactApdater = new ContentAdapter(this);
        mContactApdater.setOnItemClickListener(new ContentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, boolean checked, int position, PhoneContact contact) {
                if (checked == false)
                {
                    mSendList.remove(contact);
                }
                else
                {
                    mSendList.add(contact);
                }
            }
        });

        mContactListView.setAdapter(mContactApdater);

        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mContactApdater.setFilterText(s.toString());
                mContactApdater.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSendList.size() >= 1)
                {
                    setPermissionSMSMessage();
                }
            }
        });
    }

    public boolean isSMSPermissionGranted(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, RequestType.MY_PERMISSIONS_REQUEST_SEND_SMS);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    protected void setPermissionSMSMessage() {
        dlg_progress.show();
        if (isSMSPermissionGranted())
        {
            sendSMSMessage();
        }
    }

    public void sendSMSMessage()
    {
        SmsManager smsManager = SmsManager.getDefault();

        for (int i = 0; i < mSendList.size(); i++)
        {
            if (mSendList.get(i).phoneNumber.size() > 0)
            {
                smsManager.sendTextMessage(mSendList.get(i).phoneNumber.get(0), null, GlobalData.APPLINK, null, null);
            }
        }
        dlg_progress.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle("SMS Confirm");
        builder.setMessage(this.getString(R.string.sms_send));
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == RequestType.MY_PERMISSIONS_REQUEST_SEND_SMS)
        {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSMSMessage();
            } else {
                dlg_progress.dismiss();

                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
                builder.setTitle("SMS Confirm");
                builder.setMessage(this.getString(R.string.sms_failed));
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        }
    }

    private ArrayList<PhoneContact> getContactsList()
    {
        ArrayList<PhoneContact> result = new ArrayList<>();

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                PhoneContact section  = new PhoneContact();
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                section.id = id;
                section.name = name;

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        section.phoneNumber.add(phoneNo);
                    }
                    pCur.close();
                }
                result.add(section);
            }
        }
        return result;
    }
}
