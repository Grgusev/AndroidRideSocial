package com.pjtech.android.ridesocial.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.maps.GeoApiContext;
import com.pjtech.android.ridesocial.GlobalData;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.RideSocialApp;
import com.pjtech.android.ridesocial.model.MapStatusType;
import com.pjtech.android.ridesocial.model.RequestType;
import com.pjtech.android.ridesocial.model.RouterResponse;
import com.pjtech.android.ridesocial.rest.ApiClient;
import com.pjtech.android.ridesocial.rest.ApiInterface;
import com.pjtech.android.ridesocial.ui.activities.HomeActivity;
import com.pjtech.android.ridesocial.ui.adapters.AddressListAdapter;
import com.pjtech.android.ridesocial.ui.dialog.MyProgressDialog;
import com.pjtech.android.ridesocial.ui.interfaces.HostingManager;
import com.pjtech.android.ridesocial.ui.interfaces.MessageManager;
import com.pjtech.android.ridesocial.ui.interfaces.PaymentReceiveManager;
import com.pjtech.android.ridesocial.ui.interfaces.PaymentRequestManager;
import com.pjtech.android.ridesocial.ui.interfaces.PaymentRequestRecManager;
import com.pjtech.android.ridesocial.ui.interfaces.RateRiderManager;
import com.pjtech.android.ridesocial.ui.interfaces.RideMatchManager;
import com.pjtech.android.ridesocial.ui.interfaces.UberManager;
import com.pjtech.android.ridesocial.utils.MapUtils;
import com.pjtech.android.ridesocial.utils.Tool;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements OnMapReadyCallback, LocationListener, GoogleApiClient.OnConnectionFailedListener {
    // TODO: Rename parameter arguments, choose names that match

    // TODO: Rename and change types of parameters

    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    private static final int MAX_ADDRESSES = 10;
    private static final double REGION_RADIUS = 50;
    private Activity mActivity;

    public RideMatchManager mRideMatchManager;
    public MessageManager mMessageManager;
    public HostingManager mHostingManager;
    public PaymentRequestManager mRequestManager;
    public PaymentRequestRecManager mRequestRecManager;
    public PaymentReceiveManager mReceiveManager;
    public UberManager mUberMapager;
    public RateRiderManager mRateManager;

    View rootView;
    //interface picker select view
    AutoCompleteTextView originPickerView = null;
    AutoCompleteTextView destPickerView = null;

    private String status = MapStatusType.MAP_PICK;
    private int visibleUI = R.id.selectPick;

    //location router
    private Marker origin = null;
    private Marker destination = null;
    private Marker currentMarker = null;

    public MyProgressDialog dlg_progress = null;

    private BroadcastReceiver mUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RequestType.PUSH_RECEIVE_CREATE_ROUTER)) {
                setupUI(MapStatusType.RIDE_MATCH);

                mRideMatchManager.updateRouterData();
                if (origin != null && destination != null) {
                    origin.setVisible(false);
                    destination.setVisible(false);
                }

                MapUtils.drawRouterInfos();
            } else if (intent.getAction().equals(RequestType.PUSH_RECEIVE_FAILED_ROUTER)) {
                if (status.equals(MapStatusType.SUBMIT_RIDES)) {
                    rootView.findViewById(R.id.no_router).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.submitProgress).setVisibility(View.GONE);
                }
            } else if (intent.getAction().equals(RequestType.SELECTED_NOT_HOSTING)) {
                mHostingManager.showNotSelected();
            } else if (intent.getAction().equals(RequestType.SELECTED_HOSTING_NOT_YOU)) {
                mHostingManager.showHostingSelectedNotYOU();
                setupUI(MapStatusType.WAIT_PAYMENT_REQUEST);
            } else if (intent.getAction().equals(RequestType.PUSH_RECEIVE_HOSTING_SELECTED_YOU)) {
                mUberMapager.setUberButtonsVisibility();
                setupUI(MapStatusType.CALL_CABSERVICE);
            } else if (intent.getAction().equals(RequestType.PUSH_RECEIVE_PAYMENT_REQUEST_RECEIVE)) {
                Bundle data = intent.getExtras();
                mRequestRecManager.setData(data);

                String sender_id = data.getString("sender_id");
                String sender_name = "";

                for (int i = 0; i < GlobalData.rideInfo.rideInfos.size(); i++) {
                    if (sender_id.equals(GlobalData.rideInfo.rideInfos.get(i).userId)) {
                        sender_name = GlobalData.rideInfo.rideInfos.get(i).username;
                        break;
                    }
                }

                HomeActivity.mInstance.setToolbarTitleText(RideSocialApp.mContext.getString(R.string.payment_requested_from,
                        sender_name));
                setupUI(MapStatusType.REQUEST_PAYMENT);
            } else if (intent.getAction().equals(RequestType.PUSH_RECEIVE_PAYMENT_RECEIVE)) {
                Bundle data = intent.getExtras();
                mReceiveManager.setData(data);

                String sender_id = data.getString("sender_id");
                String sender_name = "";

                for (int i = 0; i < GlobalData.rideInfo.rideInfos.size(); i++) {
                    if (sender_id.equals(GlobalData.rideInfo.rideInfos.get(i).userId)) {
                        sender_name = GlobalData.rideInfo.rideInfos.get(i).username;
                        break;
                    }
                }

                HomeActivity.mInstance.setToolbarTitleText(RideSocialApp.mContext.getString(R.string.payment_received_from,
                        sender_name));
                setupUI(MapStatusType.RECEIVE_PAYMENT);
            } else if (intent.getAction().equals(RequestType.PUSH_RECEIVE_SUBMIT_ROUTER)) {

            }
        }
    };

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        mActivity = getActivity();
        dlg_progress = new MyProgressDialog(this.getActivity());

        mGoogleApiClient = new GoogleApiClient
                .Builder(mActivity)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(RequestType.PUSH_RECEIVE_CREATE_ROUTER);
        filter.addAction(RequestType.PUSH_RECEIVE_FAILED_ROUTER);
        filter.addAction(RequestType.PUSH_RECEIVE_HOSTING_SELECTED_YOU);
        filter.addAction(RequestType.PUSH_RECEIVE_PAYMENT_RECEIVE);
        filter.addAction(RequestType.PUSH_RECEIVE_PAYMENT_REQUEST_RECEIVE);
        filter.addAction(RequestType.SELECTED_HOSTING_NOT_YOU);
        filter.addAction(RequestType.SELECTED_NOT_HOSTING);
        filter.addAction(RequestType.PUSH_RECEIVE_SUBMIT_ROUTER);

        mActivity.registerReceiver(mUIReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mUIReceiver != null && mActivity != null) {
            mActivity.unregisterReceiver(mUIReceiver);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        MapView mapView = (MapView) rootView.findViewById(R.id.map);

        mapView.onCreate(savedInstanceState);

        mapView.onResume(); // needed to get the map to display immediately

        MapUtils.context = new GeoApiContext().setApiKey(getString(R.string.mapAPIKey));

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(this);

        setupUI(MapStatusType.MAP_PICK);

        //init UI

        setupPickerSelectUI();
        setupSubmitUI();

        mRideMatchManager = new RideMatchManager(rootView, this);
        mMessageManager = new MessageManager(rootView, this);
        mHostingManager = new HostingManager(rootView, this);
        mRequestManager = new PaymentRequestManager(rootView, this);
        mRequestRecManager = new PaymentRequestRecManager(rootView, this);
        mReceiveManager = new PaymentReceiveManager(rootView, this);
        mUberMapager = new UberManager(rootView, this);
        mRateManager = new RateRiderManager(rootView, this);

        return rootView;
    }

    public boolean isLocationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (mActivity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    mActivity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, RequestType.MY_PERMISSIONS_REQUEST_LOCATION);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RequestType.MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                MapUtils.mMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapUtils.mMap = googleMap;
        if (isLocationPermissionGranted()) {
            MapUtils.mMap.setMyLocationEnabled(true);
        }
        MapUtils.mMap.getUiSettings().setMyLocationButtonEnabled(false);
        MapUtils.mMap.getUiSettings().setRotateGesturesEnabled(false);

        MapUtils.setRecentlyUsedMarker();

        MapUtils.mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (!status.equals(MapStatusType.MAP_PICK)) return;
                setPlace(cameraPosition.target);
            }
        });

        setPlace(MapUtils.mMap.getCameraPosition().target);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

//        if (isGPSEnabled)
//        {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
//        }
//        if (isNetworkEnabled) {
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
//        }

//        LatLng latLng = new LatLng(39.951444611345, 116.78631846205);
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
//        MapUtils.mMap.animateCamera(cameraUpdate);

        LatLng latLng = new LatLng(37.780969, -122.4082002);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        MapUtils.mMap.animateCamera(cameraUpdate);

    }

    private int mYear, mMonth, mDay, mHour, mMinute;
    private long currentTimeMills;

    String address = "";

    AddressListAdapter origAdapter = new AddressListAdapter(RideSocialApp.mContext);
    AddressListAdapter destAdapter = new AddressListAdapter(RideSocialApp.mContext);

    public LatLng getLatLng(String address) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            address = address.replaceAll(" ","%20");

            dlg_progress.show();

            HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            stringBuilder = new StringBuilder();

            response = client.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        } finally {
            dlg_progress.hide();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());

            double longitute = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            double latitude = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

            return new LatLng(latitude, longitute);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }


    private void setupPickerSelectUI() {
        originPickerView = (AutoCompleteTextView) rootView.findViewById(R.id.pickerOrig);
        destPickerView = (AutoCompleteTextView) rootView.findViewById(R.id.pickerDest);

        origAdapter.setClickListener(new AddressListAdapter.OnClickListener() {
            @Override
            public void onClickListener(int position) {
                String addr = origAdapter.getItem(position);
                originPickerView.setText(addr);

                LatLng newLatLng = getLatLng(addr);

                if (newLatLng != null)
                {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(newLatLng, 15);
                    MapUtils.mMap.animateCamera(cameraUpdate);

                    if (origin == null)
                    {
                        origin = MapUtils.addCustomMarker(newLatLng, "", R.drawable.start);
                    }
                    else
                    {
                        MapUtils.animateMarker(origin, newLatLng);
                    }
                }

            }
        });

        destAdapter.setClickListener(new AddressListAdapter.OnClickListener() {
            @Override
            public void onClickListener(int position) {
                String addr = destAdapter.getItem(position);
                destPickerView.setText(addr);

                LatLng newLatLng = getLatLng(addr);

                if (newLatLng != null)
                {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(newLatLng, 15);
                    MapUtils.mMap.animateCamera(cameraUpdate);

                    if (destination == null)
                    {
                        destination = MapUtils.addCustomMarker(newLatLng, "", R.drawable.destination);
                    }
                    else
                    {
                        MapUtils.animateMarker(destination, newLatLng);
                    }
                }
            }
        });

        originPickerView.setThreshold(1);
        originPickerView.setAdapter(origAdapter);

        originPickerView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                address = s.toString();
                mAddressListHandler.removeMessages(0);
                mAddressListHandler.sendEmptyMessageDelayed(0, 100);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        destPickerView.setThreshold(1);
        destPickerView.setAdapter(destAdapter);

        destPickerView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                address = s.toString();
                mAddressListHandler.removeMessages(1);
                mAddressListHandler.sendEmptyMessageDelayed(1, 100);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);

        currentTimeMills = calendar.getTimeInMillis();

        android.text.format.DateFormat dateFormat = new android.text.format.DateFormat();

        TextView date = (TextView)rootView.findViewById(R.id.datePicker);
        date.setText(dateFormat.format("yyyy-MM-dd hh:mm:ss a", calendar.getTime()));

        rootView.findViewById(R.id.origLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (origin == null)
                {
                    origin = MapUtils.addCustomMarker(currentMarker.getPosition(), "", R.drawable.start);
                }
                else
                {
                    MapUtils.animateMarker(origin, currentMarker.getPosition());
                }

                mAddressHandler.removeMessages(0);
                mAddressHandler.sendEmptyMessageDelayed(0, 200);
            }
        });

        rootView.findViewById(R.id.destLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (destination == null)
                {
                    destination = MapUtils.addCustomMarker(currentMarker.getPosition(), "", R.drawable.destination);
                }
                else
                {
                    MapUtils.animateMarker(destination, currentMarker.getPosition());
                }

                mAddressHandler.removeMessages(1);
                mAddressHandler.sendEmptyMessageDelayed(1, 200);
            }
        });

        rootView.findViewById(R.id.btn_find_others).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (origin != null && destination != null)
                {
                    findAnotherRide();
                }
            }
        });

        rootView.findViewById(R.id.datePicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dateDailog = new DatePickerDialog(HomeFragment.this.getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mYear = year;
                        mMonth = month;
                        mDay = dayOfMonth;

                        // Launch Time Picker Dialog
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {
                                        mHour = hourOfDay;
                                        mMinute = minute;

                                        Calendar calendar = Calendar.getInstance();
                                        calendar.set(mYear, mMonth, mDay, mHour, mMinute);
                                        currentTimeMills = calendar.getTimeInMillis();

                                        android.text.format.DateFormat dateFormat = new android.text.format.DateFormat();
                                        TextView date = (TextView)rootView.findViewById(R.id.datePicker);
                                        date.setText(dateFormat.format("yyyy-MM-dd hh:mm:ss a", calendar.getTime()));

                                    }
                                }, mHour, mMinute, false);
                        timePickerDialog.show();

                    }
                }, mYear, mMonth, mDay);
                dateDailog.show();
            }
        });
    }

    private void setupSubmitUI() {
        rootView.findViewById(R.id.btn_cancel_find_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiInterface apiService =
                        ApiClient.getClient().create(ApiInterface.class);
                Call<RouterResponse> call = apiService.cancelTravelPath(GlobalData.userInfo.userId, GlobalData.requestId);

                call.enqueue(new retrofit2.Callback<RouterResponse>() {
                    @Override
                    public void onResponse(Call<RouterResponse> call, Response<RouterResponse> response) {
                        if (!response.isSuccessful()) return;

                        if (response.body().status.equals("0")) {
                            //sign up
                            gotoPickScreens();
                            return;
                        }
                        else if (response.body().status.equals("1")) {
                            Toast.makeText(getContext(), getContext().getText(R.string.no_user), Toast.LENGTH_SHORT).show();
                            gotoPickScreens();
                        }
                    }

                    @Override
                    public void onFailure(Call<RouterResponse> call, Throwable t) {
                        // Log error here since request failed
                        Toast.makeText(getContext(), getContext().getText(R.string.connect_err), Toast.LENGTH_SHORT).show();
                        gotoPickScreens();
                    }
                });
            }
        });

        rootView.findViewById(R.id.btn_cancel_find_payment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiInterface apiService =
                        ApiClient.getClient().create(ApiInterface.class);
                Call<RouterResponse> call = apiService.cancelTravelPath(GlobalData.userInfo.userId, GlobalData.requestId);

                call.enqueue(new retrofit2.Callback<RouterResponse>() {
                    @Override
                    public void onResponse(Call<RouterResponse> call, Response<RouterResponse> response) {
                        if (!response.isSuccessful()) return;

                        if (response.body().status.equals("0")) {
                            //sign up
                            gotoPickScreens();
                            return;
                        }
                        else if (response.body().status.equals("1")) {
                            Toast.makeText(getContext(), getContext().getText(R.string.no_user), Toast.LENGTH_SHORT).show();
                            gotoPickScreens();
                        }
                    }

                    @Override
                    public void onFailure(Call<RouterResponse> call, Throwable t) {
                        // Log error here since request failed
                        Toast.makeText(getContext(), getContext().getText(R.string.connect_err), Toast.LENGTH_SHORT).show();
                        gotoPickScreens();
                    }
                });
            }
        });
    }

    public void findAnotherRide()
    {
        if (currentMarker != null) {
            currentMarker.remove();
            currentMarker = null;
        }
        origin.setVisible(true);
        destination.setVisible(true);

        setupUI(MapStatusType.SUBMIT_RIDES);

        rootView.findViewById(R.id.no_router).setVisibility(View.GONE);
        rootView.findViewById(R.id.submitProgress).setVisibility(View.VISIBLE);

        JSONObject originJSON = new JSONObject();
        JSONObject destJSON = new JSONObject();
        try {
            originJSON.put("lat", origin.getPosition().latitude);
            originJSON.put("lon", origin.getPosition().longitude);
            destJSON.put("lat", destination.getPosition().latitude);
            destJSON.put("lon", destination.getPosition().longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        long timestamp = 0;

        RadioGroup selectTime = (RadioGroup) rootView.findViewById(R.id.set_start_time);

        if (selectTime.getCheckedRadioButtonId() == R.id.now) timestamp = 0;
        else timestamp = currentTimeMills;

        Call<RouterResponse> call = apiService.submitTravelPath(GlobalData.userInfo.userId, GlobalData.userInfo.userName,
                originJSON.toString(), destJSON.toString(), timestamp);

        call.enqueue(new Callback<RouterResponse>() {
            @Override
            public void onResponse(Call<RouterResponse> call, Response<RouterResponse> response) {
                if (!response.isSuccessful()) return;

                if (response.body().status.equals("0")) {
                    //sign up
                    GlobalData.requestId = response.body().request_id;
                    waitRouter();
                    return;
                }
                else if (response.body().status.equals("1")) {
                    Toast.makeText(getContext(), getContext().getText(R.string.no_user), Toast.LENGTH_SHORT).show();
                    gotoPickScreens();
                }
            }

            @Override
            public void onFailure(Call<RouterResponse> call, Throwable t) {
                // Log error here since request failed
                Toast.makeText(getContext(), getContext().getText(R.string.connect_err), Toast.LENGTH_SHORT).show();
                gotoPickScreens();
            }
        });
    }

    public Handler mWaitingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            Call<RouterResponse> call = apiService.submitLastCalcRouter(GlobalData.userInfo.userId, GlobalData.requestId);

            call.enqueue(new retrofit2.Callback<RouterResponse>() {
                @Override
                public void onResponse(Call<RouterResponse> call, Response<RouterResponse> response) {
                    if (!response.isSuccessful()) {
                        gotoPickScreens();
                        return;
                    }

                    if (response.body().status.equals("0")) {
                        //sign up
                        GlobalData.requestId = response.body().request_id;
                        return;
                    }
                    else if (response.body().status.equals("1")) {
                        Toast.makeText(getContext(), getContext().getText(R.string.no_user), Toast.LENGTH_SHORT).show();
                        gotoPickScreens();
                    }
                }

                @Override
                public void onFailure(Call<RouterResponse> call, Throwable t) {
                    // Log error here since request failed
                    Toast.makeText(getContext(), getContext().getText(R.string.connect_err), Toast.LENGTH_SHORT).show();
                    gotoPickScreens();
                }
            });
        }
    };

    public void waitRouter()
    {
        mWaitingHandler.removeMessages(RequestType.WAIT_GET_ROUTER_REQUEST);
        mWaitingHandler.sendEmptyMessageDelayed(RequestType.WAIT_GET_ROUTER_REQUEST, 20000);
    }

    public void gotoPickScreens()
    {
        mWaitingHandler.removeMessages(RequestType.WAIT_GET_ROUTER_REQUEST);
        if (origin != null) origin.setVisible(true);
        if (destination != null) destination.setVisible(true);

        setupUI(MapStatusType.MAP_PICK);
        currentMarker = null;

        setPlace(MapUtils.mMap.getCameraPosition().target);
    }

    Handler mAddressHandler = new Handler(){
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (msg.what == 0)
                    {
                        originPickerView.setText(getAddressString(origin.getPosition()));
                    }
                    else
                    {
                        destPickerView.setText(getAddressString(destination.getPosition()));
                    }
                }
            });
        }
    };

    Handler mAddressListHandler = new Handler(){
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (msg.what == 0)
                    {
                        getAddressInfo(mActivity, address, origAdapter);
                    }
                    else
                    {
                        getAddressInfo(mActivity, address, destAdapter);
                    }
                }
            });
        }
    };


    private void getAddressInfo(Context context, String locationName, final AddressListAdapter locationList){
        double  lowerLeftLatitude = currentMarker.getPosition().latitude - REGION_RADIUS / 111000f;
        double  lowerLeftLongitude = currentMarker.getPosition().longitude - REGION_RADIUS / 111000f;
        double  upperRightLatitude = currentMarker.getPosition().latitude + REGION_RADIUS / 111000f;
        double  upperRightLongitude = currentMarker.getPosition().longitude + REGION_RADIUS / 111000f;


        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .build();

        LatLngBounds bounds = new LatLngBounds(new LatLng(lowerLeftLatitude, lowerLeftLongitude), new LatLng(upperRightLatitude, upperRightLongitude));

        mGoogleApiClient.connect();

        Places.GeoDataApi.getAutocompletePredictions( mGoogleApiClient, locationName, bounds, typeFilter )
            .setResultCallback (
                    new ResultCallback<AutocompletePredictionBuffer>() {
                        @Override
                        public void onResult( AutocompletePredictionBuffer buffer ) {

                            if( buffer == null )
                                return;

                            if( buffer.getStatus().isSuccess() ) {
                                ArrayList<String> list = new ArrayList<String>();

                                for( AutocompletePrediction prediction : buffer ) {
                                    //Add as a new item to avoid IllegalArgumentsException when buffer is released
                                    list.add(prediction.getFullText(null).toString());
                                }
                                locationList.setAddressList(list);
                            }

                            //Prevent memory leak by releasing buffer
                            buffer.release();
                        }
                    }, 60, TimeUnit.SECONDS );
    }

    private String getFullAddres(final Address adrss)
    {
        String city = adrss.getLocality();
        String country = adrss.getCountryName();
        String addr = adrss.getAddressLine(0);

        return addr+", "+city+", "+country;
    }

    private String getAddressString(final LatLng latLng)
    {
        Geocoder geocoder = new Geocoder(mActivity, Locale.getDefault());

        List<Address> addresses  = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses == null) return "";
        if (addresses.size() == 0) return "";

        Address address = addresses.get(0);

        return getFullAddres(address);

    }

    public void setPlace(final LatLng latLng) {
        if (currentMarker == null)
        {
            currentMarker = MapUtils.mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
        }
        else
        {
            MapUtils.animateMarker(currentMarker, latLng);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        MapUtils.mMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void setupUI(String map_status)
    {
        if (status.equals(map_status)) return;

        final View view = rootView.findViewById(visibleUI);

        if (map_status.equals(MapStatusType.MAP_PICK))
        {
            visibleUI = R.id.selectPick;
            HomeActivity.mInstance.setToolbarTitleText(this.getString(R.string.nav_home));
        }
        else if (map_status.equals(MapStatusType.SUBMIT_RIDES))
        {
            visibleUI = R.id.submitDialog;
            HomeActivity.mInstance.setToolbarTitleText(this.getString(R.string.router_match));
        }
        else if (map_status.equals(MapStatusType.RIDE_MATCH))
        {
            visibleUI = R.id.ride_match_section;
            HomeActivity.mInstance.setToolbarTitleText(this.getString(R.string.router_matched));
        }
        else if (map_status.equals(MapStatusType.MESSAGE_WINDOW))
        {
            visibleUI = R.id.message_section;
            String title = this.getString(R.string.message_rouner);

            for (int i = 1; i < GlobalData.rideInfo.rideInfos.size(); i ++)
            {
                title += GlobalData.rideInfo.rideInfos.get(i).username;
                if (i < GlobalData.rideInfo.rideInfos.size() - 1)
                    title += ", ";
            }
            HomeActivity.mInstance.setToolbarTitleText(title);
        }
        else if (map_status.equals(MapStatusType.HOSTING_SELECTION))
        {
            visibleUI = R.id.hosting_section;
            HomeActivity.mInstance.setToolbarTitleText(this.getString(R.string.hosting_select));
            rootView.findViewById(R.id.host_select_status).setVisibility(View.GONE);
        }
        else if (map_status.equals(MapStatusType.CALL_CABSERVICE))
        {
            visibleUI = R.id.uber_section;
            HomeActivity.mInstance.setToolbarTitleText(this.getString(R.string.call_cab));
        }
        else if (map_status.equals(MapStatusType.SEND_FARE))
        {
            visibleUI = R.id.pay_request_layout;
            HomeActivity.mInstance.setToolbarTitleText(this.getString(R.string.send_payment_title));
        }
        else if (map_status.equals(MapStatusType.REQUEST_PAYMENT))
        {
            visibleUI = R.id.pay_req_receive_section;
        }
        else if (map_status.equals(MapStatusType.RECEIVE_PAYMENT))
        {
            visibleUI = R.id.pay_receive_layout;
        }
        else if (map_status.equals(MapStatusType.RATE_SUBMIT))
        {
            visibleUI = R.id.co_riders_section;
        }
        else if (map_status.equals(MapStatusType.WAIT_PAYMENT_REQUEST))
        {
            visibleUI = R.id.waitPayment;
        }

        status = map_status;

        final View visibleView = rootView.findViewById(visibleUI);

        if (map_status.equals(MapStatusType.SEND_FARE))
        {
            dlg_progress.show();

            GlobalData.updatePaymentMethod(new GlobalData.UpdatePaymentListener() {
                @Override
                public void finish() {
                    dlg_progress.dismiss();
                    mRequestManager.paymentRequestSetup();
                    Tool.showViewAnimation(view, visibleView);
                }
            }, new GlobalData.UpdatePaymentListener() {
                @Override
                public void finish() {
                    dlg_progress.dismiss();
                }
            });
        }
        else Tool.showViewAnimation(view, visibleView);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
