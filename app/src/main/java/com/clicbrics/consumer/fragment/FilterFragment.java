package com.clicbrics.consumer.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.appyvet.rangebar.RangeBar;
import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.CommonResponse;
import com.buy.housing.backend.userEndPoint.model.SearchCriteria;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.view.activity.HomeScreen;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.FilterActivity;
import com.clicbrics.consumer.framework.util.HousingLogs;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.clicbrics.consumer.utils.UtilityMethods.getIntInPref;

/**
 * A placeholderf fragment containing a simple view.
 */
public class FilterFragment extends BaseFragment {

    private int roomSelectedIndex = 0;
    private String[] priceArray;
    private int[] valuePriceArray, valueAreaArray;
    private final String TAG = FilterFragment.class.getSimpleName();
    public static boolean isReset;
    public static boolean isFilterApplied;
    public static final int FILTER_REQUEST_CODE = 103;
    /**
     * property types layouts
     */
    private LinearLayout apartmentLayout, villaLayout, plotLayout, commercialLayout, floorLayout;
    private ImageView apartmentImage, villaImage, plotImage,commercialImage, floorImage;
    private View apartmentSelector, villaSelector, plotSelector,commercialSelector, floorSelector;
    private TextView apartmentText, villaText, plotText,commercialText, floorText;

    // Initializes the RangeBar in the application
    private RangeBar priceBar;
    private ToggleButton newProperty, ongoingProperty, readyProperty, upcomingProperty,bhk1, bhk2, bhk3, bhk3plus;
    private TextView minPrice, maxPrice, filterReset;
    private Button applyFilter;

    private UserEndPoint mUserEndPoint;


    public FilterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        isReset = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter, container, false);
    }

    /**
     * Reponsible for changing selection on click
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.filter_toolbar);
        //Typeface firaSansLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/FiraSans-Light.ttf");
        TextView filterTitle = (TextView) toolbar.findViewById(R.id.filter_title);

        filterReset = (TextView) toolbar.findViewById(R.id.filter_reset);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick of filter: Home Back back button icon");
                if(((FilterActivity)getActivity()).isFilterChanged) {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finishActivity(FILTER_REQUEST_CODE);
                    getActivity().finish();
                }else{
                    getActivity().finish();
                }
            }
        });
        initView(view);

        if (getActivity().getIntent().hasExtra(Constants.IntentKeyConstants.FIRST_TIME)) {
            //for first time
            //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("REQUIREMENTS");
            filterTitle.setText("REQUIREMENTS");
            applyFilter.setText("CONTINUE");
            view.findViewById(R.id.project_status_txt).setVisibility(View.GONE);
            view.findViewById(R.id.project_status_icons).setVisibility(View.GONE);

        } else if (getActivity().getIntent().hasExtra(Constants.IntentKeyConstants.CALL_ME_BACK_REQUEST)) {
            filterTitle.setText("REQUIREMENTS");
            applyFilter.setText("SUBMIT");
        } else {
            //if (getActivity().getIntent().getBooleanExtra(Constants.IntentKeyConstants.FIRST_TIME, false)) {
            //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("FILTER");
            applyFilter.setText("APPLY");
            //}
        }

        setValue();
        filterReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
    }

    private int[] bednBathArray = new int[]{0, 1, 2, 3};


    /*
    * For view inilization
    * */
    float yAxis = 0;
    private void initView(View view) {
        //        openPropertyType();
        Typeface typeface = Typeface.createFromAsset(getResources().getAssets(), "fonts/FiraSans-Book.ttf");

        bhk1 = (ToggleButton) view.findViewById(R.id.bhk1);
        bhk2 = (ToggleButton) view.findViewById(R.id.bhk2);
        bhk3 = (ToggleButton) view.findViewById(R.id.bhk3);
        bhk3plus = (ToggleButton) view.findViewById(R.id.bhk3plus);

        applyFilter = (Button) view.findViewById(R.id.apply_filter);

        minPrice = (TextView) view.findViewById(R.id.minPrice);
        maxPrice = (TextView) view.findViewById(R.id.maxPrice);

        newProperty = (ToggleButton) view.findViewById(R.id.new_property);
        ongoingProperty = (ToggleButton) view.findViewById(R.id.ongoing_property);
        readyProperty = (ToggleButton) view.findViewById(R.id.ready_property);
        upcomingProperty = (ToggleButton) view.findViewById(R.id.upcoming_property);

        bhk1.setTypeface(typeface);
        bhk2.setTypeface(typeface);
        bhk3.setTypeface(typeface);
        bhk3plus.setTypeface(typeface);
        newProperty.setTypeface(typeface);
        ongoingProperty.setTypeface(typeface);
        readyProperty.setTypeface(typeface);
        upcomingProperty.setTypeface(typeface);

        priceBar = (RangeBar) view.findViewById(R.id.priceBar);

        /*
        * for changing size of bar
        */
        priceBar.setBarWeight(8);
        priceBar.setConnectingLineWeight(5);
        /*
        * for changing unselected color of bar
        */
        //priceBar.setBarColor(getResources().getColor(R.color.gray_100));
        priceArray = getResources().getStringArray(R.array.price_array);
        valuePriceArray = getResources().getIntArray(R.array.price_array_value);

        HousingLogs.d(TAG, "Size " + priceArray.length);
        priceBar.setTemporaryPins(true);
        priceBar.setRangeList(Arrays.asList(priceArray));
        int minPriceIndex = UtilityMethods.getIntInPref(getActivity(), Constants.SharedPreferConstants.PARAM_MIN_PRICE, 0);
        int maxPriceIndex = UtilityMethods.getIntInPref(getActivity(), Constants.SharedPreferConstants.PARAM_MAX_PRICE, 0);
        minPrice.setText(priceArray[minPriceIndex]);
        if(maxPriceIndex != 0) {
            maxPrice.setText(priceArray[maxPriceIndex]);
        }else{
            maxPrice.setText(priceArray[priceArray.length - 1]);
        }


        priceBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                yAxis = motionEvent.getY();
                //Log.i(TAG, "onTouch: YAxiz -> " + yAxis);
                if(yAxis > 80 ){
                    priceBar.setOnRangeBarChangeListener(onRangeBarChangeListener);
                    return false;
                }else{
                    priceBar.setOnRangeBarChangeListener(null);
                    return true;
                }
            }
        });
        /*priceBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex,
                                              String leftPinValue, String rightPinValue) {
                Log.d(TAG, "length->" + leftPinValue + " " + rightPinValue + "\n " + leftPinIndex + " " + rightPinIndex);
                if (rightPinIndex > (priceArray.length - 1)) {
                    priceBar.setRangePinsByValue(leftPinIndex, (priceArray.length - 1));
                    return;
                }

                if (leftPinIndex < 0) {
                    priceBar.setRangePinsByValue(0, rightPinIndex);
                    return;
                }
                if (leftPinIndex > -1 && leftPinIndex < priceArray.length)
                    minPrice.setText(priceArray[leftPinIndex]);
                if (rightPinIndex > -1 && rightPinIndex < priceArray.length)
                    maxPrice.setText(priceArray[rightPinIndex]);
            }
        });*/
        /*
        * Passing values to bar
        * */


        valueAreaArray = getResources().getIntArray(R.array.area_array_values);

        apartmentLayout = (LinearLayout) view.findViewById(R.id.apartmentLayout);
        villaLayout = (LinearLayout) view.findViewById(R.id.villaLayout);
        plotLayout = (LinearLayout) view.findViewById(R.id.plotLayout);
        commercialLayout = (LinearLayout) view.findViewById(R.id.commercialLayout);
        floorLayout = view.findViewById(R.id.floorLayout);
        apartmentImage = (ImageView) view.findViewById(R.id.apartment_image);
        villaImage = (ImageView) view.findViewById(R.id.villa_image);
        plotImage = (ImageView) view.findViewById(R.id.plot_image);
        commercialImage = (ImageView) view.findViewById(R.id.commercial_image);
        floorImage = view.findViewById(R.id.floor_image);
        apartmentText = (TextView) view.findViewById(R.id.apartment_txt);
        villaText = (TextView) view.findViewById(R.id.villa_txt);
        plotText = (TextView) view.findViewById(R.id.plot_txt);
        commercialText = (TextView) view.findViewById(R.id.commercial_txt);
        floorText = view.findViewById(R.id.floor_txt);
        apartmentSelector = view.findViewById(R.id.apartment_selector_line);
        villaSelector = view.findViewById(R.id.villa_selector_line);
        plotSelector = view.findViewById(R.id.plot_selector_line);
        commercialSelector = view.findViewById(R.id.commercial_selector_line);
        floorSelector = view.findViewById(R.id.floor_selector_line);

        applyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FilterActivity) getActivity()).isFilterChanged = true;
                new EventAnalyticsHelper().ItemClickEvent(getActivity(), Constants.AnaylticsClassName.FilterScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.applyFilter.toString());
                if (getActivity().getIntent().hasExtra(Constants.IntentKeyConstants.FIRST_TIME)) {
                    saveValue();
                    Intent intent = new Intent(getActivity(), HomeScreen.class);
                    startActivity(intent);
                } else if (getActivity().getIntent().hasExtra(Constants.IntentKeyConstants.CALL_ME_BACK_REQUEST)) {
                    submitCallMeBackRequest();
                }else {
                    if(getActivity() != null) {
                        /*new EventAnalyticsHelper().ItemClickEvent(getActivity(), Constants.AnaylticsClassName.FilterScreen,
                                null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.applyFilter.toString());*/
                       /* TrackAnalytics.trackEvent("applyFilter_click", Constants.AppConstants.HOLD_TIME,
                                1, getActivity());*/
                    }
                    saveValue();
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
            }
        });

        setPropertyTypeLayout();
        buildRegService();
    }

    RangeBar.OnRangeBarChangeListener onRangeBarChangeListener = new RangeBar.OnRangeBarChangeListener() {
        @Override
        public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex,
                                          String leftPinValue, String rightPinValue) {
            Log.d(TAG, "length->" + leftPinValue + " " + rightPinValue + "\n " + leftPinIndex + " " + rightPinIndex);
            if (rightPinIndex > (priceArray.length - 1)) {
                priceBar.setRangePinsByValue(leftPinIndex, (priceArray.length - 1));
                return;
            }

            if (leftPinIndex < 0) {
                priceBar.setRangePinsByValue(0, rightPinIndex);
                return;
            }
            if (leftPinIndex > -1 && leftPinIndex < priceArray.length)
                minPrice.setText(priceArray[leftPinIndex]);
            if (rightPinIndex > -1 && rightPinIndex < priceArray.length)
                maxPrice.setText(priceArray[rightPinIndex]);
        }
    };

    private void setValue() {

        apartmentLayout.setSelected(UtilityMethods.getBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_APARTMENT, false));
        setApartmentSelector(UtilityMethods.getBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_APARTMENT, false));

        villaLayout.setSelected(UtilityMethods.getBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_VILLA, false));
        setVillaSelector(UtilityMethods.getBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_VILLA, false));

        plotLayout.setSelected(UtilityMethods.getBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_PLOTS, false));
        setPlotSelector(UtilityMethods.getBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_PLOTS, false));

        commercialLayout.setSelected(UtilityMethods.getBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_COMMERCIAL, false));
        setCommercialSelector(UtilityMethods.getBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_COMMERCIAL, false));

        floorLayout.setSelected(UtilityMethods.getBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_FLOOR, false));
        setFloorSelector(UtilityMethods.getBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_FLOOR, false));

        Log.d(TAG, "ap,villa, plot->" + UtilityMethods.getBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_APARTMENT, false)
                + ", " + UtilityMethods.getBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_VILLA, false)
                + ", " + UtilityMethods.getBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_PLOTS, false)
                + ", " + UtilityMethods.getBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_COMMERCIAL, false));
        setPropertyTypeLayout();

        newProperty.setChecked(UtilityMethods.getBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_NEW, false));
        ongoingProperty.setChecked(UtilityMethods.getBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_ONGOING, false));
        readyProperty.setChecked(UtilityMethods.getBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_READY, false));
        upcomingProperty.setChecked(UtilityMethods.getBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_UPCOMING, false));

        Log.d(TAG, "new, ongoing, ready ->" + UtilityMethods.getBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_NEW, false)
                + ", " + UtilityMethods.getBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_ONGOING, false)
                + ", " + UtilityMethods.getBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_READY, false));

        int roomCheck = 0;
        if (getIntInPref(mActivity, Constants.SharedPreferConstants.PARAM_ROOM, 0) != 0) {
            roomSelectedIndex = getIntInPref(mActivity, Constants.SharedPreferConstants.PARAM_ROOM, 0);
            roomCheck = roomSelectedIndex & 1;
            Log.d(TAG, "roomCheck->" + roomCheck);
            if (roomCheck != 0) {
                bhk1.setChecked(true);
            } else {
                bhk1.setChecked(false);
            }

            roomCheck = roomSelectedIndex & 2;
            if (roomCheck != 0) {
                bhk2.setChecked(true);
            } else {
                bhk2.setChecked(false);
            }

            roomCheck = roomSelectedIndex & 4;
            if (roomCheck != 0) {
                bhk3.setChecked(true);
            } else {
                bhk3.setChecked(false);
            }

            roomCheck = roomSelectedIndex & 8;
            if (roomCheck != 0) {
                bhk3plus.setChecked(true);
            } else {
                bhk3plus.setChecked(false);
            }

        }
        Log.d(TAG, "roomIndex->" + getIntInPref(mActivity, Constants.SharedPreferConstants.PARAM_ROOM, 0));

        priceBar.setRangePinsByValue(getIntInPref(mActivity, Constants.SharedPreferConstants.PARAM_MIN_PRICE, 0),
                getIntInPref(mActivity, Constants.SharedPreferConstants.PARAM_MAX_PRICE, priceArray.length - 1));
        Log.d(TAG, "PRICEbAR->" + getIntInPref(mActivity, Constants.SharedPreferConstants.PARAM_MIN_PRICE, 0) + ", " +
                getIntInPref(mActivity, Constants.SharedPreferConstants.PARAM_MAX_PRICE, priceArray.length - 1));
    }


    private void saveValue() {
        UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_APARTMENT, apartmentLayout.isSelected());
        UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_VILLA, villaLayout.isSelected());
        UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_PLOTS, plotLayout.isSelected());
        UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_COMMERCIAL, commercialLayout.isSelected());
        UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_FLOOR, floorLayout.isSelected());

        Log.d(TAG, "ap, villa, plot, Commercial ->" + apartmentLayout.isSelected() + " ," + villaLayout.isSelected()
                + " ," + plotLayout.isSelected() + ", " + commercialLayout.isSelected() + ", "+ floorSelector.isSelected());

        UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_NEW, newProperty.isChecked());
        UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_ONGOING, ongoingProperty.isChecked());
        UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_READY, readyProperty.isChecked());
        UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_UPCOMING, upcomingProperty.isChecked());


        UtilityMethods.saveIntInPref(mActivity, Constants.SharedPreferConstants.PARAM_MIN_PRICE, priceBar.getLeftIndex());
        UtilityMethods.saveIntInPref(mActivity, Constants.SharedPreferConstants.PARAM_MAX_PRICE, priceBar.getRightIndex());
        Log.d(TAG, "price->" + priceBar.getLeftIndex() + ", " + priceBar.getRightIndex());

        roomSelectedIndex = 0;
        if (bhk1.isChecked()) {
            roomSelectedIndex = roomSelectedIndex | 1;
        }
        if (bhk2.isChecked()) {
            roomSelectedIndex = roomSelectedIndex | 2;
        }
        if (bhk3.isChecked()) {
            roomSelectedIndex = roomSelectedIndex | 4;
        }
        if (bhk3plus.isChecked()) {
            roomSelectedIndex = roomSelectedIndex | 8;
        }
        UtilityMethods.saveIntInPref(mActivity, Constants.SharedPreferConstants.PARAM_ROOM, roomSelectedIndex);
        Log.d(TAG, "roomIndex->" + roomSelectedIndex);

        if (apartmentLayout.isSelected() || villaLayout.isSelected() || plotLayout.isSelected()
                || commercialLayout.isSelected() || floorLayout.isSelected() ||
                newProperty.isChecked() || ongoingProperty.isChecked() || readyProperty.isChecked() || upcomingProperty.isChecked() ||
                (priceBar.getLeftIndex() != 0) || (priceBar.getRightIndex() != (priceArray.length - 1)) ||
                roomSelectedIndex != 0) {
            UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_FILTER_APPLIED, true);
        } else {
            UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_FILTER_APPLIED, false);
        }

        Log.d(TAG, "FilterApplied->" +
                UtilityMethods.getBooleanInPref(getActivity(), Constants.SharedPreferConstants.PARAM_FILTER_APPLIED, false));
    }

    private void saveDefaultValue() {
        UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_APARTMENT, false);
        UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_VILLA, false);
        UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_PLOTS, false);
        UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_COMMERCIAL, false);
        UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_FLOOR, false);

        UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_NEW, false);
        UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_ONGOING, false);
        UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_READY, false);
        UtilityMethods.saveBooleanInPref(mActivity, Constants.SharedPreferConstants.PARAM_UPCOMING, false);

        UtilityMethods.saveIntInPref(mActivity, Constants.SharedPreferConstants.PARAM_MIN_PRICE, 0);
        UtilityMethods.saveIntInPref(mActivity, Constants.SharedPreferConstants.PARAM_MAX_PRICE, priceArray.length - 1);
        UtilityMethods.saveIntInPref(mActivity, Constants.SharedPreferConstants.PARAM_ROOM, 0);
    }


    private void reset() {
        new EventAnalyticsHelper().ItemClickEvent(getActivity(), Constants.AnaylticsClassName.FilterScreen,
                null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.resetFilter.toString());
        /*TrackAnalytics.trackEvent("resetFilter_click", Constants.AppConstants.HOLD_TIME,
                1, getActivity());*/
        if (!getActivity().getIntent().hasExtra(Constants.IntentKeyConstants.CALL_ME_BACK_REQUEST)) {
            isReset = true;
            isFilterApplied = false;
        }

        priceBar.setRangePinsByValue(0, priceArray.length - 1);
        minPrice.setText(priceArray[0]);
        maxPrice.setText(priceArray[priceArray.length - 1]);

        newProperty.setChecked(false);
        ongoingProperty.setChecked(false);
        readyProperty.setChecked(false);
        upcomingProperty.setChecked(false);

        apartmentLayout.setSelected(false);
        villaLayout.setSelected(false);
        plotLayout.setSelected(false);
        commercialLayout.setSelected(false);
        floorLayout.setSelected(false);
        setApartmentSelector(false);
        setVillaSelector(false);
        setPlotSelector(false);
        setCommercialSelector(false);
        setFloorSelector(false);

        bhk1.setChecked(false);
        bhk2.setChecked(false);
        bhk3.setChecked(false);
        bhk3plus.setChecked(false);
        /*if(!getActivity().getIntent().hasExtra(Constants.IntentKeyConstants.CALL_ME_BACK_REQUEST)) {
            saveValue();
        }*/
    }

    private boolean isPropertyStatusSelected() {
        return (newProperty.isSelected() || ongoingProperty.isSelected() || readyProperty.isSelected() || upcomingProperty.isSelected());
    }

    private boolean isPropertyTypeSelected() {
        return (apartmentLayout.isSelected() || villaLayout.isSelected() || plotLayout.isSelected()
                || commercialLayout.isSelected() || floorLayout.isSelected());
    }

    private void filter() {
        saveValue();
        isFilterApplied = true;
        mActivity.setResult(Activity.RESULT_OK);
        mActivity.finish();
    }

    private void setApartmentSelector(boolean isSelected) {
        if (isSelected) {
            UtilityMethods.setColorFilter(getActivity(),apartmentImage,R.color.filter_red,PorterDuff.Mode.SRC_IN);
            UtilityMethods.setTextViewColor(getActivity(),apartmentText,R.color.filter_red);
            apartmentSelector.setVisibility(View.VISIBLE);
        } else {
            UtilityMethods.setColorFilter(getActivity(),apartmentImage,R.color.black,PorterDuff.Mode.SRC_IN);
            UtilityMethods.setTextViewColor(getActivity(),apartmentText,R.color.black);
            apartmentSelector.setVisibility(View.INVISIBLE);
        }
    }

    private void setVillaSelector(boolean isSelected) {
        if (isSelected) {
            UtilityMethods.setColorFilter(getActivity(),villaImage,R.color.filter_red,PorterDuff.Mode.SRC_IN);
            UtilityMethods.setTextViewColor(getActivity(),villaText,R.color.filter_red);
            villaSelector.setVisibility(View.VISIBLE);
        } else {
            UtilityMethods.setColorFilter(getActivity(),villaImage,R.color.black,PorterDuff.Mode.SRC_IN);
            UtilityMethods.setTextViewColor(getActivity(),villaText,R.color.black);
            villaSelector.setVisibility(View.INVISIBLE);
        }
    }

    private void setPlotSelector(boolean isSelected) {
        if (isSelected) {
            UtilityMethods.setColorFilter(getActivity(),plotImage,R.color.filter_red,PorterDuff.Mode.SRC_IN);
            UtilityMethods.setTextViewColor(getActivity(),plotText,R.color.filter_red);
            plotSelector.setVisibility(View.VISIBLE);
        } else {
            UtilityMethods.setColorFilter(getActivity(),plotImage,R.color.black,PorterDuff.Mode.SRC_IN);
            UtilityMethods.setTextViewColor(getActivity(),plotText,R.color.black);
            plotSelector.setVisibility(View.INVISIBLE);
        }
    }

    private void setCommercialSelector(boolean isSelected) {
        if (isSelected) {
            /*commercialImage
                    .setColorFilter(getResources().getColor(R.color.filter_red), PorterDuff.Mode.SRC_IN);
            plotText.setTextColor(getResources().getColor(R.color.filter_red));*/
            UtilityMethods.setColorFilter(getActivity(),commercialImage,R.color.filter_red,PorterDuff.Mode.SRC_IN);
            UtilityMethods.setTextViewColor(getActivity(),commercialText,R.color.filter_red);
            commercialSelector.setVisibility(View.VISIBLE);
        } else {
            UtilityMethods.setColorFilter(getActivity(),commercialImage,R.color.black,PorterDuff.Mode.SRC_IN);
            UtilityMethods.setTextViewColor(getActivity(),commercialText,R.color.black);
            commercialSelector.setVisibility(View.INVISIBLE);
        }
    }

    private void setFloorSelector(boolean isSelected) {
        if (isSelected) {
            UtilityMethods.setColorFilter(getActivity(),floorImage,R.color.filter_red,PorterDuff.Mode.SRC_IN);
            UtilityMethods.setTextViewColor(getActivity(),floorText,R.color.filter_red);
            floorSelector.setVisibility(View.VISIBLE);
        } else {
            UtilityMethods.setColorFilter(getActivity(),floorImage,R.color.black,PorterDuff.Mode.SRC_IN);
            UtilityMethods.setTextViewColor(getActivity(),floorText,R.color.black);
            floorSelector.setVisibility(View.INVISIBLE);
        }
    }

    private void setPropertyTypeLayout() {
        apartmentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) {
                    v.setSelected(false);
                } else {
                    v.setSelected(true);
                }
                setApartmentSelector(v.isSelected());
            }
        });

        villaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) {
                    v.setSelected(false);
                } else {
                    v.setSelected(true);
                }
                setVillaSelector(v.isSelected());
            }
        });

        plotLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) {
                    v.setSelected(false);
                } else {
                    v.setSelected(true);
                }
                setPlotSelector(v.isSelected());
            }
        });

        commercialLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) {
                    v.setSelected(false);
                } else {
                    v.setSelected(true);
                }
                setCommercialSelector(v.isSelected());
            }
        });

        floorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) {
                    v.setSelected(false);
                } else {
                    v.setSelected(true);
                }
                setFloorSelector(v.isSelected());
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // inflater.inflate(R.menu.menu_filter, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            //    case R.id.menu_done:
            //        filter();
            //        return true;
            case R.id.menu_reset:
                reset();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void buildRegService() {
        mUserEndPoint= EndPointBuilder.getUserEndPoint();
    }

    private void submitCallMeBackRequest() {
        new EventAnalyticsHelper().ItemClickEvent(getActivity(), Constants.AnaylticsClassName.FilterScreen,
                null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.ContactMePage_SubmitClicked.toString());
      /*  TrackAnalytics.trackEvent("ContactMePage_SubmitClicked", Constants.AppConstants.HOLD_TIME,
                1, getActivity());*/
        SearchCriteria sc = new SearchCriteria();
        sc.setId(UtilityMethods.getLongInPref(getActivity(), Constants.ServerConstants.USER_ID, 0l));
        sc.setTime(System.currentTimeMillis());

        ArrayList<String> propertyType = new ArrayList<String>();
        if (apartmentLayout.isSelected()) {
            propertyType.add(Constants.AppConstants.PropertyType.Apartment.toString());
        }
        if (villaLayout.isSelected()) {
            propertyType.add(Constants.AppConstants.PropertyType.IndependentHouseVilla.toString());
        }
        if (plotLayout.isSelected()) {
            propertyType.add(Constants.AppConstants.PropertyType.Land.toString());
        }
        if (commercialLayout.isSelected()) {
            propertyType.add(Constants.AppConstants.PropertyType.Shop.toString());
        }
        if (floorLayout.isSelected()) {
            propertyType.add(Constants.AppConstants.PropertyType.IndependentFloor.toString());
        }
        if (propertyType != null && (!propertyType.isEmpty())) {
            sc.setPropertyTypeEnum(propertyType);
            sc.setFilterApplied(true);
        }

        ArrayList<String> propertyStatusList = new ArrayList<String>();
        if (newProperty.isChecked()) {
            propertyStatusList.add(Constants.AppConstants.PropertyStatus.NotStarted.toString());
        }
        if (ongoingProperty.isChecked()) {
            propertyStatusList.add(Constants.AppConstants.PropertyStatus.InProgress.toString());
        }
        if (readyProperty.isChecked()) {
            propertyStatusList.add(Constants.AppConstants.PropertyStatus.ReadyToMove.toString());
        }
        if (upcomingProperty.isChecked()) {
            propertyStatusList.add(Constants.AppConstants.PropertyStatus.UpComing.toString());
        }

        if ((propertyStatusList != null) && (!propertyStatusList.isEmpty())) {
            sc.setPropertyStatusList(propertyStatusList);
            sc.setFilterApplied(true);
        }

        int[] priceArray = getResources().getIntArray(R.array.price_array_value);
        if ((priceBar.getLeftIndex() != 0) && (priceBar.getRightIndex() != (priceArray.length - 1))) {
            sc.setMinCost((long) priceArray[priceBar.getLeftIndex()]);
            sc.setFilterApplied(true);
        }

        if ((priceBar.getRightIndex() != 0) && (priceBar.getRightIndex() != (priceArray.length - 1))) {
            sc.setMaxCost((long) priceArray[priceBar.getRightIndex()]);
            sc.setFilterApplied(true);
        }

        List<Integer> bedList = new ArrayList<Integer>();
        if (bhk1.isChecked()) {
            bedList.add(1);
        }
        if (bhk2.isChecked()) {
            bedList.add(2);
        }
        if (bhk3.isChecked()) {
            bedList.add(3);
        }
        if (bhk3plus.isChecked()) {
            bedList.add(4);
        }
        if ((bedList != null) && (!bedList.isEmpty())) {
            sc.setBedList(bedList);
            sc.setFilterApplied(true);
        }

        new SendCallMeBackRequest().execute(sc);
    }

    private class SendCallMeBackRequest extends AsyncTask<SearchCriteria, Integer, String> {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        protected void onPreExecute() {
            showProgressBar();
        }

        @Override
        protected String doInBackground(SearchCriteria... params) {

            try {
                UserEndPoint.Callback callMeBack = mUserEndPoint.callback(params[0])
                        .setProjectId(getActivity().getIntent().getLongExtra(Constants.IntentKeyConstants.PROJECT_ID, 0))
                        .setId(UtilityMethods.getLongInPref(getActivity(), Constants.ServerConstants.USER_ID, 0));
                callMeBack.setSource(Constants.AppConstants.SOURCE);
                CommonResponse callMeBackResponse = callMeBack.setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                if (callMeBackResponse.getStatus()) {
                    return Constants.AppConstants.SUCCESS;
                } else {
                    return callMeBackResponse.getErrorMessage();
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return Constants.AppConstants.NETWORK_ERROR;
            }catch (Exception e) {
                e.printStackTrace();
                return Constants.AppConstants.FAILED;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        protected void onPostExecute(String result) {
            dismissProgressBar();
            try {
                if (result.equalsIgnoreCase("SUCCESS")) {
                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.filter_container),
                            getString(R.string.thanks_msg), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(getActivity() != null) {
                                getActivity().finish();
                            }
                        }
                    }, 2000);
                } else {
                    if (result.equalsIgnoreCase(Constants.AppConstants.NETWORK_ERROR)) {
                        showNetworkErrorSnackBarForCallMeBack();
                    } else {
                        Snackbar.make(getActivity().findViewById(R.id.filter_container),
                                result, Snackbar.LENGTH_LONG).show();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void showNetworkErrorSnackBarForCallMeBack() {
        final Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.root_layout_filter_fragment),
                getResources().getString(R.string.network_error_msg),
                Snackbar.LENGTH_INDEFINITE);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.uber_red));
        snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        snackbar.setAction("TRY AGAIN", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                submitCallMeBackRequest();
            }
        });
        snackbar.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if(priceBar != null){
                priceBar.setOnTouchListener(null);
                priceBar.setOnRangeBarChangeListener(null);
                onRangeBarChangeListener = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
