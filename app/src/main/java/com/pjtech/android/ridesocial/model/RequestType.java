package com.pjtech.android.ridesocial.model;

/**
 * Created by android on 6/8/17.
 */

public class RequestType {
    public static final String SELECTED_HOSTING_NOT_YOU = "push_receive_hosting_not_you";
    public static final String SELECTED_NOT_HOSTING = "push_receive_host_not_selected";
    public static final String PUSH_RECEIVE_HOSTING_SELECTED_YOU = "push_receive_host_selected_you";
    public static final String PUSH_RECEIVE_PAYMENT_REQUEST_RECEIVE = "push_receive_payment_request_receive";
    public static final String PUSH_RECEIVE_PAYMENT_RECEIVE = "push_receive_payment_paid";
    public static final String PUSH_RECEIVE_SUBMIT_ROUTER = "push_receive_submit_router";

    public static int REGISTER_PAYMENT_REQUEST = 1001;
    public static int PICK_IMAGE_REQUEST    = 1002;
    public static int TAKE_PHOTO_REQUEST    = 1003;
    public static int MY_PERMISSIONS_REQUEST_CAMERA    = 1004;
    public static int WAIT_GET_ROUTER_REQUEST    = 1005;
    public static String PUSH_RECEIVE_CREATE_ROUTER = "push_receive_create_router";
    public static String PUSH_RECEIVE_FAILED_ROUTER = "push_receive_failed_router";
    public static int MY_PERMISSIONS_REQUEST_SEND_SMS = 1006;
    public static int MY_PERMISSIONS_REQUEST_LOCATION = 1007;
}
