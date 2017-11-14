package com.pjtech.android.ridesocial.model;

import java.util.ArrayList;

/**
 * Created by android on 6/18/17.
 */

public class PhoneContact {

    public String id;
    public String name;
    public ArrayList<String> phoneNumber;

    public PhoneContact()
    {
        id = "";
        name = "";
        phoneNumber = new ArrayList<>();
    }
}
