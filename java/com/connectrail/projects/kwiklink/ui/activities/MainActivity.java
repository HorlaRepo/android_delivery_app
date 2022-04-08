package com.connectrail.projects.kwiklink.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.TransitMode;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.connectrail.projects.kwiklink.MainApplication;
import com.connectrail.projects.kwiklink.R;
import com.connectrail.projects.kwiklink.core.LocationTask;
import com.connectrail.projects.kwiklink.core.MemoryManager;
import com.connectrail.projects.kwiklink.core.Requests;
import com.connectrail.projects.kwiklink.core.local.DatabaseManager;
import com.connectrail.projects.kwiklink.entities.ServiceCategory;
import com.connectrail.projects.kwiklink.entities.ServiceMode;
import com.connectrail.projects.kwiklink.entities.User;
import com.connectrail.projects.kwiklink.listeners.IOnLocationReady;
import com.connectrail.projects.kwiklink.ui.adapters.ServiceCategoryAdapter;
import com.connectrail.projects.kwiklink.util.L;
import com.connectrail.projects.kwiklink.util.Util;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.vishalsojitra.easylocation.EasyLocationAppCompatActivity;
import com.vishalsojitra.easylocation.EasyLocationRequest;
import com.vishalsojitra.easylocation.EasyLocationRequestBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by root on 9/24/17.
 */

public class MainActivity extends EasyLocationAppCompatActivity implements OnMapReadyCallback, DirectionCallback, AdapterView.OnItemSelectedListener {

    @BindView(R.id.toolbar_main)
    Toolbar mToolbar;
    @BindView(R.id.drawer_layout_main)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.tv_destination_name_activity_main)
    TextView destinationTextView;
    @BindView(R.id.tv_place_name_activity_main)
    TextView currentAddressTextView;
    @BindView(R.id.layout_select_category_activity_main)
    View categorySelectionView;
    @BindView(R.id.layout_courier_not_found_activity_main)
    View courierNotFoundView;
    @BindView(R.id.layout_distance_not_supported_main)
    View distanceNotSupportedView;
    @BindView(R.id.layout_courier_details_main)
    View layoutCourierDetails;
    @BindView(R.id.tv_courier_name_courier_details)
    TextView courierNameTextView;
    @BindView(R.id.tv_courier_not_found)
    TextView courierNotFoundTextView;
    @BindView(R.id.tv_mode_courier_details)
    TextView courierModeTextView;
    @BindView(R.id.fab_call_courier)
    FloatingActionButton callCourier;
    @BindView(R.id.fab_message_courier)
    FloatingActionButton messageCourier;
    @BindView(R.id.operation_layout_main)
    LinearLayout operationLayout;
    @BindView(R.id.pw_bottom_sheet_main)
    ProgressWheel progressWheel;
    @BindView(R.id.pw_find_courier)
    ProgressWheel courierDetailsProgressWheel;
    @BindView(R.id.spinner_select_category_activity_main)
    Spinner spinner;
    @BindView(R.id.btn_request_service_bottom_sheet_main)
    Button requestService;
    @BindView(R.id.courier_details_layout)
    LinearLayout courierDetailsLinearLayout;
    @BindView(R.id.layout_bottom_sheet_content)
    LinearLayout categorySelectionLinearLayout;


    private GoogleMap mGoogleMap;
    private View bottomSheetView;

    public static final int REQUEST_SET_DESTINATION = 100;
    public static final int REQUEST_SET_PICK_UP_LOCATION = 101;

    private LatLng destinationLatLng, pickUpLatLng;
    private Marker mCurrentLocationMarker, mDestinationMarker;

    private Polyline mPolyline;

    private String currentAddress, destinationAddress = "";

    private List<ServiceCategory> serviceCategories = new ArrayList<>();

    private CircleImageView courierImageView;

    private User courier = new User();

    long mainDistance = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_main);

        ButterKnife.bind(this);

        findUserLocation();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        mToolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map_main);
        if(supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        }

        serviceCategories = DatabaseManager.getInstance().all();
    }

    int getStatusBarHeight() {
        int result = 0;
        int resourceID = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(resourceID > 0)
            result = getResources().getDimensionPixelSize(resourceID);

        return result;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        mGoogleMap.setOnMapClickListener(mapClickListener);
    }
    void findUserLocation() {

        LocationRequest locationRequest = new LocationRequest()
                .setInterval(1000).setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        EasyLocationRequest easyLocationRequest = new EasyLocationRequestBuilder()
                .setLocationRequest(locationRequest)
                .setFallBackToLastLocationTime(5000)
                .build();
        requestSingleLocationFix(easyLocationRequest);
    }

    @Override
    public void onLocationPermissionGranted() {

    }

    @Override
    public void onLocationPermissionDenied() {

    }

    @Override
    public void onLocationReceived(Location location) {
        if(location == null)
            return;

        MainApplication.getExecutorService().execute(new LocationTask(this, onLocationReady, location));

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        pickUpLatLng = latLng;

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(pickUpLatLng);
        if(mCurrentLocationMarker != null) {
            mCurrentLocationMarker.remove();
        }
        mCurrentLocationMarker = mGoogleMap.addMarker(markerOptions);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f));

    }

    @Override
    public void onLocationProviderEnabled() {

    }

    @Override
    public void onLocationProviderDisabled() {

    }
    private IOnLocationReady onLocationReady = new IOnLocationReady() {
        @Override
        public void onReady(Address address) {

            String text = address.getAddressLine(0);
            currentAddressTextView.setText(text);
            currentAddress = text;
        }

        @Override
        public void onError(Throwable throwable) {
            L.WTF(throwable);
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @OnClick(R.id.layout_set_destination_activity_main) public void onSetDestinationClick() {
        try {
            AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(Place.TYPE_COUNTRY)
                    .setCountry("NG").build();
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setFilter(autocompleteFilter)
                    .build(this);
            startActivityForResult(intent, REQUEST_SET_DESTINATION);
        } catch (GooglePlayServicesRepairableException e) {
            L.WTF(e);
        } catch (GooglePlayServicesNotAvailableException e) {
            new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                    .setTitle("Play Services Unavailable")
                    .setMessage("Please update your device google play service")
                    .create().show();
        }
    }
    @OnClick(R.id.layout_set_pick_up_location) public void onSetPickUpLocation() {
        try {
            AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(Place.TYPE_COUNTRY)
                    .setCountry("NG").build();
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setFilter(autocompleteFilter)
                    .build(this);
            startActivityForResult(intent, REQUEST_SET_PICK_UP_LOCATION);
        } catch (GooglePlayServicesRepairableException e) {
            L.WTF(e);
        } catch (GooglePlayServicesNotAvailableException e) {
            new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                    .setTitle("Play Services Unavailable")
                    .setMessage("Please update your device google play service")
                    .create().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {

            Place place = PlaceAutocomplete.getPlace(this, data);
            switch (requestCode) {
                case REQUEST_SET_DESTINATION:

                    String text = place.getAddress().toString();
                    destinationTextView.setText(text);
                    destinationAddress = text;

                    destinationLatLng = place.getLatLng();
                    if(mDestinationMarker != null) {
                        mDestinationMarker.remove();
                    }

                    mDestinationMarker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(destinationLatLng).title(text).snippet(text));

                    GoogleDirection.withServerKey(getResources().getString(R.string.google_maps_key))
                            .from(destinationLatLng).to(pickUpLatLng)
                            .transportMode(TransportMode.WALKING).execute(this);

                    categorySelectionView = getCategorySelectionView();
                    hideAllView();
                    progressWheel.setVisibility(View.VISIBLE);
                    categorySelectionView.setVisibility(View.VISIBLE);
                    break;
                case REQUEST_SET_PICK_UP_LOCATION:

                    String addressText = place.getAddress().toString();
                    currentAddressTextView.setText(addressText);
                    currentAddress = addressText;

                    pickUpLatLng = place.getLatLng();

                    if(mCurrentLocationMarker != null) {
                        mCurrentLocationMarker.remove();
                    }
                    mCurrentLocationMarker = mGoogleMap.addMarker(new MarkerOptions().position(pickUpLatLng)
                    .title(addressText).snippet(addressText));
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickUpLatLng, 17f));
                    break;
            }
        }
    }
    private GoogleMap.OnMapClickListener mapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {

            pickUpLatLng = latLng;

            MainApplication.getExecutorService().execute(new LocationTask(MainActivity.this, onLocationReady, pickUpLatLng));

            if(mCurrentLocationMarker != null)
                mCurrentLocationMarker.remove();

            mCurrentLocationMarker = mGoogleMap.addMarker(new MarkerOptions().position(pickUpLatLng));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickUpLatLng, 17f));
        }
    };

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {

        L.fine("Route Raw Body => " + rawBody);
        if(direction.isOK()) {

            if(mCurrentLocationMarker != null)
                mCurrentLocationMarker.remove();

            mCurrentLocationMarker = mGoogleMap.addMarker(new MarkerOptions().position(pickUpLatLng)
            .title(currentAddress).snippet(currentAddress));
            if (mDestinationMarker != null)
                mDestinationMarker.remove();
            mDestinationMarker = mGoogleMap.addMarker(new MarkerOptions().position(destinationLatLng)
            .title(destinationAddress).snippet(destinationAddress));

            List<Route> routes = direction.getRouteList();
            String distance = "";
            for (int i = 0; i < routes.size(); i++) {
                distance = routes.get(i).getLegList().get(i).getDistance().getText();
            }
            L.fine("Route Size => " + routes.size() + ", Distance ===> " + distance);
            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
            if(mPolyline != null)
                mPolyline.remove();

            mPolyline = mGoogleMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.RED));
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng latLng : directionPositionList) {
                builder.include(latLng);
            }
            LatLngBounds latLngBounds = builder.build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 7));

            long distanceRounded = 0;
            try {
                distanceRounded = Math.round(Double.parseDouble(distance.split(" ")[0]));
            }catch (Exception e) {
                distanceRounded = 0;
            }

            mainDistance = distanceRounded;

            if(distanceRounded > 7) {

                distanceNotSupportedView = getDistanceNotSupportedView();
                hideAllView();
                distanceNotSupportedView.setVisibility(View.VISIBLE);

            }else {

                categorySelectionView = getCategorySelectionView();
                hideAllView();
                categorySelectionView.setVisibility(View.VISIBLE);
                progressWheel.setVisibility(View.GONE);
                categorySelectionLinearLayout.setVisibility(View.VISIBLE);

                List<String> strings = Util.convertToString(serviceCategories);

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                        strings);
                spinner.setAdapter(arrayAdapter);
                spinner.setOnItemSelectedListener(this);

                requestService.setOnClickListener(requestServiceClickListener);
            }
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        L.WTF(t);
    }

    public View getDistanceNotSupportedView() {
        return distanceNotSupportedView;
    }

    public View getCategorySelectionView() {
        return categorySelectionView;
    }
    private View.OnClickListener requestServiceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sendRequest();
        }
    };
    void sendRequest() {

        if(pickUpLatLng == null) {
            toast("Please set pick up location");
            return;
        }
        if(destinationLatLng == null) {
            toast("Please set destination for this pick up");
            return;
        }

        ServiceMode serviceMode = DatabaseManager.getInstance().getServiceMode(selectedServiceCategory.getModeID());
        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", MemoryManager.getInstance().getUser().getUserID());
        requestParams.put("origin_lat", pickUpLatLng.latitude);
        requestParams.put("origin_lon", pickUpLatLng.longitude);
        requestParams.put("destination_lat", destinationLatLng.latitude);
        requestParams.put("destination_lon", destinationLatLng.longitude);
        requestParams.put("distance", mainDistance);
        requestParams.put("service_category_id", selectedServiceCategory.getId());
        requestParams.put("origin_address", currentAddress);
        requestParams.put("destination_address", destinationAddress);
        requestParams.put("amount", serviceMode.getPrice());

        layoutCourierDetails = getLayoutCourierDetails();
        hideAllView();
        layoutCourierDetails.setVisibility(View.GONE);
        Requests.post("/service/request", requestParams, httpResponseHandler);
    }

    void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    private ServiceCategoryAdapter.IOnServiceCategoryClick serviceCategoryClick = new ServiceCategoryAdapter.IOnServiceCategoryClick() {
        @Override
        public void onClick(ServiceCategory serviceCategory) {
            selectedServiceCategory = serviceCategory;
        }
    };
    private ServiceCategory selectedServiceCategory = null;

    private TextHttpResponseHandler httpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            L.WTF(responseString, throwable);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            L.fine(responseString);

            try {
                JSONObject jsonObject = new JSONObject(responseString);
                if(!jsonObject.getBoolean("courier_available")) {
                    courierNotFoundView = getCourierNotFoundView();
                    courierNotFoundTextView.setText(jsonObject.getString("message"));

                    hideAllView();
                    courierNotFoundView.setVisibility(View.VISIBLE);
                }else {
                    if(jsonObject.has("courier")) {
                        courier = new User(jsonObject.getJSONObject("courier"));
                        layoutCourierDetails = getLayoutCourierDetails();

                        courierNameTextView.setText(courier.getFullname());
                        courierModeTextView.setText("Bicycle");

                        hideAllView();
                        layoutCourierDetails.setVisibility(View.VISIBLE);
                    }else {
                        
                    }
                }
            }catch (JSONException e) {
                L.WTF(e);
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            courierDetailsProgressWheel.setVisibility(View.VISIBLE);
            courierDetailsLinearLayout.setVisibility(View.GONE);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            courierDetailsProgressWheel.setVisibility(View.GONE);
            courierDetailsLinearLayout.setVisibility(View.VISIBLE);

            categorySelectionLinearLayout.setVisibility(View.GONE);
        }
    };

    public View getLayoutCourierDetails() {

        callCourier.setOnClickListener(courierOperationClickListener);
        messageCourier.setOnClickListener(courierOperationClickListener);
        return layoutCourierDetails;
    }

    private View.OnClickListener courierOperationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view == callCourier) {

            }else if(view == messageCourier) {

            }
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedServiceCategory = serviceCategories.get(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        selectedServiceCategory = null;
    }

    public View getCourierNotFoundView() {
        return courierNotFoundView;
    }
    void hideAllView() {

        courierNotFoundView.setVisibility(View.GONE);
        categorySelectionView.setVisibility(View.GONE);
        distanceNotSupportedView.setVisibility(View.GONE);
        layoutCourierDetails.setVisibility(View.GONE);

        operationLayout.setVisibility(View.VISIBLE);
    }
}
