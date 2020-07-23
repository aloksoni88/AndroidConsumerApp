package com.clicbrics.consumer.clicworth;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.buy.housing.backend.estimateEndPoint.EstimateEndPoint;
import com.buy.housing.backend.estimateEndPoint.model.CalculateEstimateResponse;
import com.buy.housing.backend.userEndPoint.model.City;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.SearchActivity;
import com.clicbrics.consumer.databinding.FragmentClicworthBinding;
import com.clicbrics.consumer.fragment.BaseFragment;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;

import java.net.UnknownHostException;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Alok on 17-09-2019.
 */
public class ClicworthFragment extends BaseFragment implements ClicWorthCallBacksInterface {
    private static final String TAG = ClicworthFragment.class.getSimpleName();
    private FragmentClicworthBinding binding;
    private static final int RESULT_PROPERTY_SEARCH = 9879;
    private static final int RESULT_PROPERTY_SEARCH_ESTIMATION = 23456;
    private static final int RESULT_ESTIMATION = 8998;
    private static final int TOTAL_SECONDS_IN_A_DAY = 86400;
    private Dialog customDialog;
    private EstimateEndPoint estimateEndPoint;
    private String cityName = "New Delhi";
    private long cityId;
    private List<City> cityCollection;
    private double latitude = -1;
    private double longitude = -1;
    private String errMsg;
    private String estimatedSize;
    private String estimatedSizeUnit = "Sq.ft";
    private String estimatedPropertyType;
    private String estimatedNoOfRooms = "2 BHK";
    private String estimatedSelectedFloor = "Ground Floor";
    private String propertyType;
    private float propertySize = 1000;
    private int floorValue = 0;
    private int roomValue = 2;
    private String propertyAddress = "";
    private City cityfromLocal;
    long mLastRequestTime;
    private ClicworthViewmodel clicworthViewmodel;
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(23.63936, 68.14712), new LatLng(28.20453, 97.34466));

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_clicworth, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        clicworthViewmodel = new ClicworthViewmodel(this);
//        ((HomeScreen)getActivity()).Intialiselistener(getActivity());

        /*((HomeScreen)getActivity()).Intialiselistener(new Clicworth_interface() {

            @Override
            public void updatefragvalue(String primartyText) {

                Log.i(TAG, "updatefragvalue: latlong===");
                latitude=((HomeScreen)getActivity()).latitude;
                longitude=((HomeScreen)getActivity()).longitude;
                Log.i(TAG, "updatefragvalue: latlong==="+latitude+";"+longitude);
            }

            @Override
            public void updateViewVisibility(String gone) {
                binding.listSearch.setVisibility(View.GONE);
            }
        });*/
//        getCityList();
        setupSeekBar();
        manageClicks();

       /* mAdapter = new PlaceAutoCompleteLocalityAdapter(getActivity(), R.layout.item_search_list,
                ((HomeScreen)getActivity()).geoDataClient, BOUNDS_INDIA, null, binding.nodataLayout, binding.rootLayout1);*/

//        binding.listSearch.setAdapter(mAdapter);
        //binding.selectCity.setText("New Delhi");
        //binding.floorBar.setInfoText("Ground floor", 0);
        estimatedSize = binding.etSize.getText().toString().trim();
        binding.sizeTitle.setText("Size " + binding.etSize.getText().toString().trim() + " " + binding.sizeType.getText().toString().trim());

        binding.searchLocality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UtilityMethods.isInternetConnected(getActivity())) {
                    Intent intent = new Intent(getActivity(), PropertySearchForEstimation.class);
                    intent.putExtra("city_name", binding.searchLocality.getText().toString());
                 /*   intent.putExtra("lat",latitude);
                    intent.putExtra("lng",longitude);
                    intent.putExtra("address",propertyAddress);*/
                    if (!TextUtils.isEmpty(binding.searchLocality.getText().toString())) {
                        intent.putExtra("search_string", binding.searchLocality.getTag().toString());
                    }
                    startActivityForResult(intent, RESULT_PROPERTY_SEARCH);
                } else {
                    UtilityMethods.showSnackbarOnTop(getActivity(), "Error", getString(R.string.no_network_connection), true, 1500);
                }
            }
        });


    }

    private void manageClicks() {


        binding.btnGetEstimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent i=new Intent(getActivity(),Demo_layout.class);
//                startActivity(i);

                getEstimate();
            }
        });


        binding.sizeTypeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(binding.sizeType.getText().toString().trim())) {
                    showUnitPopupWindow(view);
                }
            }
        });
//        binding.selectCity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onCityChange();
//            }
//        });
        binding.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSearchBarClicworth();
            }
        });
        binding.etSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(binding.etSize.getText().toString().trim())) {
//                    if (Integer.parseInt(estimatedSize) > 99 && Integer.parseInt(estimatedSize) < 100000)
                    estimatedSize = binding.etSize.getText().toString().trim();
                    binding.sizeTitle.setText("Size " + binding.etSize.getText().toString().trim() + " " + binding.sizeType.getText().toString().trim());

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.decreaseSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(binding.etSize.getText().toString().trim())) {
                    estimatedSize = binding.etSize.getText().toString().trim();
                    if (Integer.parseInt(estimatedSize) > 199 && Integer.parseInt(estimatedSize) < 100000) {
                        int newSize = Integer.parseInt(estimatedSize) - 100;
                        propertySize = newSize;
                        binding.etSize.setText(newSize + "");
                        binding.sizeTitle.setText("Size " + binding.etSize.getText().toString().trim() + " " + binding.sizeType.getText().toString().trim());
                    }
                }

            }
        });


        binding.idCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: Cancel");

                binding.idCancelBtn.setVisibility(View.GONE);
                binding.search.setVisibility(View.VISIBLE);
//                binding.searchLocality.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_round_cancel,0);
                binding.listSearch.setVisibility(View.GONE);
            }
        });


        binding.increaseSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.etSize.getText().toString().trim()) || binding.etSize.getText().toString().trim().equalsIgnoreCase("0")) {
                    int newSize = 100;
                    propertySize = 100;
                    binding.etSize.setText(newSize + "");
                } else {
                    estimatedSize = binding.etSize.getText().toString().trim();
                    if (Integer.parseInt(estimatedSize) > -1 && Integer.parseInt(estimatedSize) <= 99899) {
                        int newSize = Integer.parseInt(estimatedSize) + 100;
                        propertySize = newSize;
                        binding.etSize.setText(newSize + "");
                    }
                    binding.sizeTitle.setText("Size " + binding.etSize.getText().toString().trim() + " " + binding.sizeType.getText().toString().trim());
                }
            }
        });

        /*binding.searchLocality.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(binding.searchLocality.getText().toString().trim())) {
                    Log.i(TAG, "onTextChanged: ===1");
                    binding.idCancelBtn.setVisibility(View.GONE);
                    binding.search.setVisibility(View.VISIBLE);
                } else {
                    binding.idCancelBtn.setVisibility(View.VISIBLE);
                    binding.search.setVisibility(View.GONE);
                    Log.i(TAG, "onTextChanged: ===2");

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
             *//*   if (!isMaptouched) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                }*//*
                binding.propertyTypeOptions.setVisibility(View.VISIBLE);
                binding.nodataLayout.setVisibility(View.GONE);
                binding.btnGetEstimate.setVisibility(View.VISIBLE);
                binding.listSearch.setVisibility(View.VISIBLE);
//                isMaptouched = false;
                Log.i(TAG, "Search editText, onTextChanged -> " + s);
                if(s != null && !TextUtils.isEmpty(s.toString().trim())){
                    long timeDiff = TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - mLastRequestTime);
                    if (timeDiff >= 100 && mAdapter != null) {
//                        mAdapter.getFilter().filter(s.toString());
                        mAdapter.onAutocompleteSearch(s.toString());
                    }
                    mLastRequestTime = System.currentTimeMillis();
                }else{
                    if (mAdapter != null) {
                        mAdapter.clearList();
                    }
                }
            }
        });*/

       /* binding.bhk1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manageBhkToggle(1);
            }
        });
        binding.bhk2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manageBhkToggle(2);
            }
        });
        binding.bhk3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manageBhkToggle(3);
            }
        });
        binding.bhk4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manageBhkToggle(4);
            }
        });
        binding.bhk5plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manageBhkToggle(5);
            }
        });*/


        binding.idApartment.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                managePropertyTypeToggle(1);
            }
        });
        binding.idVilla.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                managePropertyTypeToggle(2);
            }
        });
        binding.idPlot.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                managePropertyTypeToggle(3);
            }
        });
        binding.idIndependent.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                managePropertyTypeToggle(4);
            }
        });

        binding.etSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.etSize.setInputType(InputType.TYPE_CLASS_NUMBER);
                binding.etSize.requestFocus();
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showSoftInput(binding.etSize, InputMethodManager.SHOW_FORCED);
            }
        });

        binding.etSize.setSelection(binding.etSize.getText().length());


       /* binding.sizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                estimatedSizeUnit = String.valueOf(adapterView.getItemAtPosition(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/
    }

    private PopupWindow popupWindow;

    private void showUnitPopupWindow(View view) {
        try {
            LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.drop_down_layout,
                    (ViewGroup) view.findViewById(R.id.id_drop_down_layout));

//                     layout.setBackground(getResources().getDrawable(R.drawable.dialogue_tranparent_rectangle));
            TextView sqftTextview = layout.findViewById(R.id.id_unit_sqft);
            TextView sqydTextview = layout.findViewById(R.id.id_unit_sqyd);
            TextView sqmTextview = layout.findViewById(R.id.id_unit_sqm);

            sqmTextview.setVisibility(View.GONE);
            sqftTextview.setOnClickListener(OnUnitChangeListener);
            sqydTextview.setOnClickListener(OnUnitChangeListener);

//                     sqmTextview.setOnClickListener(OnUnitChangeListener);

//                     TextView areaRange = findViewById(R.id.id_area);
            if (!TextUtils.isEmpty(binding.sizeType.getText().toString().trim())) {
                if (binding.sizeType.getText().toString().toLowerCase().contains("sq.ft")) {
                    //sqftTextview.setSelected(true);
                    UtilityMethods.setColorBackground(getActivity(), sqftTextview, R.color.colorAccent);
                    UtilityMethods.setTextViewColor(getActivity(), sqftTextview, R.color.white);
                    UtilityMethods.setColorBackground(getActivity(), sqydTextview, R.color.white);
                    UtilityMethods.setTextViewColor(getActivity(), sqydTextview, R.color.black);

                } else if (binding.sizeType.getText().toString().toLowerCase().contains("sq.yd")) {
                    UtilityMethods.setColorBackground(getActivity(), sqydTextview, R.color.colorAccent);
                    UtilityMethods.setTextViewColor(getActivity(), sqydTextview, R.color.white);
                    UtilityMethods.setColorBackground(getActivity(), sqftTextview, R.color.white);
                    UtilityMethods.setTextViewColor(getActivity(), sqftTextview, R.color.black);
                }
            }
            popupWindow = new PopupWindow(layout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            //popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    /* if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                         popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.dialogue_tranparent_rectangle));
                     }else {
                         popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.dialogue_tranparent_rectangle));
                     }*/
            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(true);
            binding.sizeType.measure(0, 0);
            int width = binding.sizeType.getMeasuredWidth();
            if (width != 0) {
                popupWindow.setWidth(180);
            } else {
                popupWindow.setWidth(180);
            }
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

            popupWindow.setTouchInterceptor(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popupWindow.dismiss();
                        return true;
                    }
                    return false;
                }
            });

            popupWindow.setContentView(layout);
            popupWindow.showAsDropDown(binding.sizeType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener OnUnitChangeListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void onClick(View view) {
            if (popupWindow != null && popupWindow.isShowing()) {
                try {
                    String unit = "";
                    if (view.getId() == R.id.id_unit_sqft) {
                        Log.i(TAG, "onClick: Selected Unit SQFT");
//                                 unit = Constants.PropertyUnit.SQFT.toString();
                        unit = "Sq.ft";
                        estimatedSizeUnit = "Sq.ft";
                    } else if (view.getId() == R.id.id_unit_sqyd) {
                        Log.i(TAG, "onClick: Selected Unit SQYD");
//                                 unit = Constants.PropertyUnit.SQYD.toString();
                        unit = "Sq.yd";
                        estimatedSizeUnit = "Sq.yd";
                    }
                    binding.sizeType.setText(unit);
                    binding.sizeTitle.setText("Size " + binding.etSize.getText().toString().trim() + " " + binding.sizeType.getText().toString().trim());
                    popupWindow.dismiss();
                    popupWindow = null;
                    if (unit.equalsIgnoreCase(UtilityMethods.getSelectedUnit(getActivity()))) {
                        return;
                    }

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void manageBhkToggle(int i) {
        switch (i) {
            case 1:
                resetCheckBhkToggle();
//                binding.bhk1.setChecked(true);
                estimatedNoOfRooms = "1 BHK";
                roomValue = 1;
                break;
            case 2:
                resetCheckBhkToggle();
//                binding.bhk2.setChecked(true);
                estimatedNoOfRooms = "2 BHK";
                roomValue = 2;
                break;
            case 3:
                resetCheckBhkToggle();
//                binding.bhk3.setChecked(true);
                estimatedNoOfRooms = "3 BHK";
                roomValue = 3;
                break;
            case 4:
                resetCheckBhkToggle();
//                binding.bhk4.setChecked(true);
                estimatedNoOfRooms = "4 BHK";
                roomValue = 4;
                break;
            case 5:
                resetCheckBhkToggle();
//                binding.bhk5plus.setChecked(true);
                estimatedNoOfRooms = "5+ BHK";
                roomValue = 5;
                break;
        }
    }

    private void resetCheckBhkToggle() {
        /*binding.bhk1.setChecked(false);
        binding.bhk2.setChecked(false);
        binding.bhk3.setChecked(false);
        binding.bhk4.setChecked(false);
        binding.bhk5plus.setChecked(false);*/
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void managePropertyTypeToggle(int i) {
        switch (i) {
            case 1:
                resetPropertyTypeToggle();
                binding.idApartment.setChecked(true);
                estimatedPropertyType = "Apartment";
                propertyType = "Apartment";
                binding.idApartment.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_appartment_red, 0, 0);
                binding.idVilla.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_filter_villa, 0, 0);
                binding.idPlot.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_filter_plot, 0, 0);
                binding.idIndependent.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_filter_floor, 0, 0);
                binding.bhkSelectedTxt.setVisibility(View.VISIBLE);
                binding.bhkSeekbar.setVisibility(View.VISIBLE);
                binding.bhkSeekbarIntervalLayout.setVisibility(View.VISIBLE);
                binding.floorSelectedTxt.setVisibility(View.VISIBLE);
                binding.floorSeekbar.setVisibility(View.VISIBLE);
                binding.floorSeekbarIntervalLayout.setVisibility(View.VISIBLE);
               /* binding.roomsTitle.setVisibility(View.VISIBLE);
                binding.roomOptions.setVisibility(View.VISIBLE);*/
                break;
            case 2:
                resetPropertyTypeToggle();
                binding.idVilla.setChecked(true);
                estimatedPropertyType = "Villa";
                propertyType = "Villa";
                binding.idApartment.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_appartment_black, 0, 0);
                binding.idVilla.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_filter_villa_black, 0, 0);
                binding.idPlot.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_filter_plot, 0, 0);
                binding.idIndependent.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_filter_floor, 0, 0);
                binding.bhkSelectedTxt.setVisibility(View.VISIBLE);
                binding.bhkSeekbar.setVisibility(View.VISIBLE);
                binding.bhkSeekbarIntervalLayout.setVisibility(View.VISIBLE);
                binding.floorSelectedTxt.setVisibility(View.GONE);
                binding.floorSeekbar.setVisibility(View.GONE);
                binding.floorSeekbarIntervalLayout.setVisibility(View.GONE);
               /* binding.roomsTitle.setVisibility(View.VISIBLE);
                binding.roomOptions.setVisibility(View.VISIBLE);*/
                break;
            case 3:
                resetPropertyTypeToggle();
                binding.idPlot.setChecked(true);
                estimatedPropertyType = "Plot";
                propertyType = "Land";
                binding.idApartment.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_appartment_black, 0, 0);
                binding.idVilla.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_filter_villa, 0, 0);
                binding.idPlot.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_filter_plot_black, 0, 0);
                binding.idIndependent.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_filter_floor, 0, 0);
                binding.bhkSelectedTxt.setVisibility(View.GONE);
                binding.bhkSeekbar.setVisibility(View.GONE);
                binding.bhkSeekbarIntervalLayout.setVisibility(View.GONE);
                binding.floorSelectedTxt.setVisibility(View.GONE);
                binding.floorSeekbar.setVisibility(View.GONE);
                binding.floorSeekbarIntervalLayout.setVisibility(View.GONE);
               /* binding.roomsTitle.setVisibility(View.GONE);
                binding.roomOptions.setVisibility(View.GONE);*/
                break;
            case 4:
                resetPropertyTypeToggle();
                binding.idIndependent.setChecked(true);
                estimatedPropertyType = "Independent Floor";
                propertyType = "IndependentFloor";
                binding.idApartment.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_appartment_black, 0, 0);
                binding.idVilla.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_filter_villa, 0, 0);
                binding.idPlot.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_filter_plot, 0, 0);
                binding.idIndependent.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_filter_floor_black, 0, 0);
                binding.bhkSelectedTxt.setVisibility(View.VISIBLE);
                binding.bhkSeekbar.setVisibility(View.VISIBLE);
                binding.bhkSeekbarIntervalLayout.setVisibility(View.VISIBLE);
                binding.floorSelectedTxt.setVisibility(View.VISIBLE);
                binding.floorSeekbar.setVisibility(View.VISIBLE);
                binding.floorSeekbarIntervalLayout.setVisibility(View.VISIBLE);
                /*binding.roomsTitle.setVisibility(View.VISIBLE);
                binding.roomOptions.setVisibility(View.VISIBLE);*/
                break;
        }
    }

    private void resetPropertyTypeToggle() {
        binding.idApartment.setChecked(false);
        binding.idIndependent.setChecked(false);
        binding.idPlot.setChecked(false);
        binding.idVilla.setChecked(false);
    }


    private void setupSeekBar() {

        binding.bhkSeekbarIntervalLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    int CurrentLevel = binding.bhkSeekbar.getProgress();
                    if (CurrentLevel <= 5)
//                CurrentLevel = 30;
                        roomValue = CurrentLevel;
                    binding.bhkSeekbar.setProgress(CurrentLevel);
                }
                return false;
            }
        });
        binding.bhkSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                roomValue = i;
                if (i == 5) {
//                    binding.floorBar.setInfoText(i + "+ floor", i);
                    binding.bhkSelectedTxt.setText(4 + "+" + getString(R.string.bhk));
//                    estimatedNoOfRooms = i + "+"+getString(R.string.bhk);
                    estimatedNoOfRooms = "4+" + " " + getString(R.string.bhk);
//                    roomValue= Integer.parseInt("5,6,7,8,9");
                } else {
                    binding.bhkSelectedTxt.setText(i + " " + getString(R.string.bhk));
                    estimatedNoOfRooms = i + " " + getString(R.string.bhk);
//                    roomValue = i;
                }

                Log.i(TAG, "onProgressChanged: estimatedNoOfRooms==== " + estimatedNoOfRooms + roomValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.seekbarframlayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    int CurrentLevel = binding.floorSeekbar.getProgress();
                    if (CurrentLevel <= 20)
//                CurrentLevel = 30;
                        binding.floorSeekbar.setProgress(CurrentLevel);
                }
                return false;
            }
        });
        binding.floorSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                binding.seekbarframlayout.performClick();
//                binding.floorSeekbar.onProgressChanged(seekBar, i, b);
                Log.i(TAG, "onProgressChanged: estimatedSelectedFloor==== " + i);
                floorValue = i;
                if (i == 0) {
//                    binding.floorSeekbar.setInfoText("Ground floor", i);
                    estimatedSelectedFloor = "Ground floor";
                } else if (i == 1) {
//                    binding.floorBar.setInfoText(i + "st floor", i);
                    estimatedSelectedFloor = "1st Floor";
                } else if (i == 2) {
//                    binding.floorBar.setInfoText(i + "nd floor", i);
                    estimatedSelectedFloor = "2nd Floor";
                } else if (i == 3) {
//                    binding.floorBar.setInfoText(i + "rd floor", i);
                    estimatedSelectedFloor = "3rd Floor";
                } else if (i == 20) {
//                    binding.floorBar.setInfoText(i + "+ floor", i);
                    estimatedSelectedFloor = i + "+ floor";
                } else {
//                    binding.floorBar.setInfoText(i + "th floor", i);
                    estimatedSelectedFloor = i + "th floor";
                }
                Log.i(TAG, "onProgressChanged: estimatedSelectedFloor==== " + estimatedSelectedFloor);
                binding.floorSelectedTxt.setText(estimatedSelectedFloor);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void onClickSearchBarClicworth() {
        Log.i(TAG, "onClickSearchBar: ");
        if (!UtilityMethods.isInternetConnected(getActivity())) {
            UtilityMethods.showSnackbarOnTop(getActivity(), "Network error", "Please check network connection.", true, 1500);
            return;
        }
        Intent intent = new Intent(getActivity(), GetLocationActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        if (!TextUtils.isEmpty(binding.searchLocality.getText().toString().trim())) {
            intent.putExtra("address", binding.searchLocality.getText().toString().trim());
        }
        startActivityForResult(intent, RESULT_PROPERTY_SEARCH);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == RESULT_PROPERTY_SEARCH) {
            if (data != null) {
                handleOnActivityResult(data);
            }
        } else if (requestCode == RESULT_ESTIMATION) {
            if (resultCode == RESULT_OK) {
                resetAllField();
            } else {
            }
        }
    }


    /*private void setCityList(CityCollection cityList) {

        if(getActivity() == null){
            return;
        }
        Collections.sort(cityList.getItems(), new Comparator<City>() {
            @Override
            public int compare(City lhs, City rhs) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });
        cityCollection = cityList.getItems();
        ((HousingApplication) getActivity().getApplicationContext()).setCityListEstimation(cityCollection);
        for (int i = 0; i < cityCollection.size(); i++) {
            if (cityCollection != null &&
                    (cityCollection.get(i).getName().equalsIgnoreCase("New Delhi")
                            || cityCollection.get(i).getName().equalsIgnoreCase("NewDelhi"))) {
                cityfromLocal = cityCollection.get(i);
            }
        }
        if (cityfromLocal != null) {
            latitude = cityfromLocal.getLat();
            longitude = cityfromLocal.getLng();
            UtilityMethods.saveStringInPref(getActivity(),
                    Constants.AppConstants.ESTIMATE_PNAME, cityfromLocal.getPName());
            UtilityMethods.saveStringInPref(getActivity(),
                    Constants.AppConstants.ESTIMATE_CITY, cityfromLocal.getName());
            UtilityMethods.saveLongInPref(getActivity(),
                    Constants.AppConstants.ESTIMATE_CITY_ID, cityfromLocal.getId());
            UtilityMethods.saveStringInPref(getActivity(), Constants.AppConstants.ESTIMATE_STATE,
                    cityfromLocal.getState());
        }
    }*/


    private void getEstimate() {
        if (allFieldsSelected()) {
            if (!UtilityMethods.isInternetConnected(getActivity())) {
                UtilityMethods.showNetworkErrorSnackBar(binding.rootLayout, getActivity());
                return;
            }

            propertySize = Float.parseFloat(binding.etSize.getText().toString());
            if (estimatedSizeUnit.equalsIgnoreCase("Sq.yd")) {
                propertySize = UtilityMethods.getAreaInSqyd(propertySize);
            }
            propertyAddress = binding.searchLocality.getText().toString().trim();
            clicworthViewmodel.GetEstimateApi(String.valueOf(latitude).trim(),
                    String.valueOf(longitude).trim(),
                    String.valueOf(propertySize).trim(), propertyType, propertyAddress, roomValue, floorValue);

           /* showProgressBar();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String errorMsg = "";
                    try {
                        estimateEndPoint = EndPointBuilder.getEstimateEndPoint();
                        propertySize = Float.parseFloat(binding.etSize.getText().toString());
                        if (estimatedSizeUnit.equalsIgnoreCase("Sq.yd")) {
                            propertySize = UtilityMethods.getAreaInSqyd(propertySize);
                        }

                        CalculateEstimateResponse calculateEstimateResponse = null;
                        if (propertyType.equalsIgnoreCase("Land")) {
                            calculateEstimateResponse =
                                    estimateEndPoint.calculateEstimation(latitude, longitude, propertySize, propertyType)
                                            .setAddress(propertyAddress)
                                            .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                        }
                        else if (propertyType.equalsIgnoreCase("Villa")) {

                            calculateEstimateResponse =
                                    estimateEndPoint.calculateEstimation(latitude, longitude, propertySize, propertyType)
                                            .setAddress(propertyAddress).setBed(roomValue)
                                            .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                        }
                        else {
                            if (floorValue != 0) {
                                calculateEstimateResponse =
                                        estimateEndPoint.calculateEstimation(latitude, longitude, propertySize, propertyType)
                                                .setAddress(propertyAddress)
                                                .setBed(roomValue).setFloor(floorValue)
                                                .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                            } else {
                                calculateEstimateResponse =
                                        estimateEndPoint.calculateEstimation(latitude, longitude, propertySize, propertyType)
                                                .setAddress(propertyAddress)
                                                .setBed(roomValue)
                                                .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                            }
                        }
                        if (calculateEstimateResponse != null && calculateEstimateResponse.getStatus()) {
                            final CalculateEstimateResponse finalCalculateEstimateResponse = calculateEstimateResponse;
                            mHandler.post(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                                @Override
                                public void run() {
                                    dismissProgressBar();
                                    moveToEstimatedPage(finalCalculateEstimateResponse);
                                }
                            });
                        } else {
                            mHandler.post(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                                @Override
                                public void run() {
                                    Intent i =new Intent(getActivity(),NoPropertyFoundScreen.class);
                                    startActivity(i);
                                    *//*binding.nodataLayout.setVisibility(View.VISIBLE);
                                    binding.btnGetEstimate.setVisibility(View.GONE);
                                    binding.propertyTypeOptions.setVisibility(View.GONE);*//*
                                }
                            });


                            errorMsg = "Error";
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        errorMsg = getResources().getString(R.string.network_error_msg);
                    } catch (Exception e) {
                        errorMsg = getString(R.string.something_went_wrong);
                        e.printStackTrace();

                    }


                    if (!TextUtils.isEmpty(errorMsg)) {
                        Log.d(TAG, errorMsg);

                        final String finalErrorMsg = errorMsg;
                        mHandler.post(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                            @Override
                            public void run() {
                                dismissProgressBar();
                                if (finalErrorMsg != null) {
//                                    UtilityMethods.showSnackbarOnTop(getActivity(), "Error", finalErrorMsg, true, 1500);
                                }
                            }
                        });
                    }
                }
            }).start();*/

        } else {
            UtilityMethods.showSnackbarOnTop(getActivity(), "Error", errMsg, true, 1500);
        }
    }

    private void moveToEstimatedPage(GetEStimateModel calculateEstimateResponse) {
        EstimatedPropertyModel model = new EstimatedPropertyModel();
        if (!estimatedPropertyType.equalsIgnoreCase("Plot")) {
            model.setNoOfRoom(estimatedNoOfRooms);
            if (!estimatedPropertyType.equalsIgnoreCase("Villa") || !estimatedPropertyType.equalsIgnoreCase("Plot")) {
                model.setSelectedFloor(estimatedSelectedFloor);
            }
        }
        if (estimatedSize != null) {
            model.setPropertySize(Integer.parseInt(binding.etSize.getText().toString()) + " " + estimatedSizeUnit);
        }

        Log.i(TAG, "moveToEstimatedPage: ========" + propertyAddress + " " + roomValue + " " + floorValue);
        model.setPropertyAddress(propertyAddress);
        model.setPropertyPrice(UtilityMethods.getPriceWord((long) ((calculateEstimateResponse.getMinPrice() + calculateEstimateResponse.getMaxPrice()) / 2)));
        model.setPropertyType(propertyType);
        model.setEstimatePropertyType(estimatedPropertyType);

        model.setPropertyPriceRange(UtilityMethods.getPriceRangeFloatWithdash(calculateEstimateResponse.getMinPrice()
                , calculateEstimateResponse.getMaxPrice()));
        model.setEstimatedAccuracy(String.valueOf(calculateEstimateResponse.getAccuracy() + "%"));
        model.setLatitude(latitude);
        model.setLongitude(longitude);
        model.setFloor(floorValue);
        model.setBed(roomValue);
        model.setSizeValue(propertySize);



        int newPropertySize = Integer.parseInt(binding.etSize.getText().toString());
        double avrage_cost = ((calculateEstimateResponse.getMinPrice() + calculateEstimateResponse.getMaxPrice()) / 2);
        model.setAverate_rate(avrage_cost);
        int bsp = (int) ((calculateEstimateResponse.getMinPrice() + calculateEstimateResponse.getMaxPrice()) / (2 * newPropertySize));
        model.setBspValue(bsp);
        model.setPropertyBsp(UtilityMethods.getPriceWord(bsp) + "/" + estimatedSizeUnit);
        String estimatedResult = new Gson().toJson(model);

        Intent intent = new Intent(getActivity(), GetEstimateActivity.class);
        intent.putExtra("estimatedResult", estimatedResult);
        intent.putExtra("propertyAddress", propertyAddress);
        startActivityForResult(intent, RESULT_ESTIMATION);

    }


    private boolean allFieldsSelected() {

        if (TextUtils.isEmpty(binding.searchLocality.getText().toString().trim())) {
            errMsg = getString(R.string.select_locality);
            return false;
        }
        if (TextUtils.isEmpty(estimatedPropertyType)) {
            errMsg = getString(R.string.select_property);
            return false;
        }
        if (!estimatedPropertyType.equalsIgnoreCase("Plot") &&
                TextUtils.isEmpty(estimatedNoOfRooms)) {
            errMsg = getString(R.string.select_no_of_rooms);
            return false;
        }
        if (TextUtils.isEmpty(binding.etSize.getText().toString()) || Integer.parseInt(binding.etSize.getText().toString()) == 0) {
            errMsg = getString(R.string.plot_size_cannot_be_empty);
            return false;
        }

        return true;
    }


    private void resetAllField() {
        resetCheckBhkToggle();
        resetPropertyTypeToggle();
        binding.searchLocality.setText("");
//        binding.sizeSpinner.setSelection(0);
        //binding.floorBar.setProgessValue(0);
        binding.bhkSeekbar.setProgress(0);
        binding.floorSeekbar.setProgress(0);
        binding.etSize.setText("1000");
        floorValue = 0;
        roomValue = 0;
        propertySize = 1000;
        propertyAddress = "";
        errMsg = "";
        estimatedSize = "";
        estimatedSizeUnit = "Sq.ft";
        estimatedPropertyType = "";
        estimatedNoOfRooms = "";
        estimatedSelectedFloor = "Ground Floor";
        propertyType = "";
        binding.bhkSelectedTxt.setVisibility(View.VISIBLE);
        binding.bhkSeekbar.setVisibility(View.VISIBLE);
        binding.bhkSeekbarIntervalLayout.setVisibility(View.VISIBLE);
        /*binding.roomsTitle.setVisibility(View.VISIBLE);
        binding.roomOptions.setVisibility(View.VISIBLE);*/


    }

  /*  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void getCityList() {
        showProgressBar();
        long cityListFetchTime = UtilityMethods.getLongInPref(getActivity(), Constants.SharedPreferConstants.ESTIMATE_CITY_LIST_FETCH_TIME, 0);
        long currentTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        if (cityListFetchTime != 0 && (currentTime - cityListFetchTime) < TOTAL_SECONDS_IN_A_DAY) {
            loadCityFromLocal();
        } else {
            if (!UtilityMethods.isInternetConnected(getActivity())) {
                showNetworkErrorSnackbar();
                dismissProgressBar();
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String errorMsg = "";
                    try {
                        UserEndPoint userEndPoint = EndPointBuilder.getUserEndPoint();
                        final CityCollection cityList = userEndPoint.getCityListAll()
                                .setEstimate(true)
                                .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();

                        if (cityList != null) {
                            mHandler.post(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                                @Override
                                public void run() {
                                    UtilityMethods.saveLongInPref(getActivity(), Constants.SharedPreferConstants.ESTIMATE_CITY_LIST_FETCH_TIME,
                                            TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                                    UtilityMethods.putEstimateCityListInPrefs(getActivity(), cityList.getItems());
                                    dismissProgressBar();
                                    setCityList(cityList);

                                }
                            });
                        } else {
                            errorMsg = "No city found";
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        errorMsg = getResources().getString(R.string.network_error_msg);
                    } catch (Exception e) {
                        errorMsg = getString(R.string.something_went_wrong);
                        e.printStackTrace();

                    }

                    if (!TextUtils.isEmpty(errorMsg)) {
                        final String errmsg = errorMsg;
                        mHandler.post(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                            @Override
                            public void run() {
                                if (!loadCityFromLocal()) {
                                    dismissProgressBar();
                                    UtilityMethods.showErrorSnackBar(binding.rootLayout, errmsg, Snackbar.LENGTH_LONG);
                                }
                            }
                        });
                    }
                }
            }).start();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean loadCityFromLocal() {
        try {
            ArrayList<com.clicbrics.consumer.utils.City> cityList = UtilityMethods.getEstimateCityList(getActivity());
            CityCollection cityCollection = new CityCollection();
            List<City> cities = new ArrayList<>();
            if (cityList != null && cityList.size() > 0) {
                int size = cityList.size();
                for (int i = 0; i < size; i++) {
                    com.clicbrics.consumer.utils.City city = cityList.get(i);
                    if (city != null) {
                        City endPointCity = new City();
                        endPointCity.setId(city.getId());
                        endPointCity.setName(city.getName());
                        endPointCity.setCountry(city.getCountry());
                        endPointCity.setPName(city.getpName());
                        endPointCity.setState(city.getState());
                        endPointCity.setLat(Double.valueOf(city.getLat()));
                        endPointCity.setLng(Double.valueOf(city.getLng()));
                        cities.add(endPointCity);
                    }
                }
                cityCollection.setItems(cities);
                setCityList(cityCollection);
                dismissProgressBar();
                return true;
            }
            dismissProgressBar();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            dismissProgressBar();
        }
        return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showNetworkErrorSnackbar() {
        dismissProgressBar();
        final Snackbar snackbar = Snackbar
                .make(binding.rootLayout, getResources().getString(R.string.network_error_msg), Snackbar.LENGTH_INDEFINITE);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.uber_red));
        snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        snackbar.setAction("TRY AGAIN", new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
                getCityList();
            }
        });
        snackbar.show();
    }*/


    private void handleOnActivityResult(Intent intent) {


        Log.i(TAG, "handleOnActivityResult: =========" + intent.getStringExtra("address") + ";"
                + intent.getStringExtra("lat"));
        if (intent.hasExtra("lat")) {

            if (intent.hasExtra("lat") && !TextUtils.isEmpty(intent.getStringExtra("lat"))) {
                if (Double.parseDouble(intent.getStringExtra("lat")) != 0) {
                    latitude = Double.parseDouble(intent.getStringExtra("lat"));
                }

            }
            if (intent.hasExtra("lng") && !TextUtils.isEmpty(intent.getStringExtra("lng"))) {
                if (Double.parseDouble(intent.getStringExtra("lng")) != 0) {
                    longitude = Double.parseDouble(intent.getStringExtra("lng"));
                }
//                longitude = Double.parseDouble(intent.getStringExtra("lng"));
            }
            if (intent.hasExtra("cityName")) {
                if (intent.getStringExtra("cityName") != null) {
                    cityName = intent.getStringExtra("cityName");
//                    binding.selectCity.setText(cityName);
                } else {
//                    binding.selectCity.setText(cityName);
                }
            }

            if (intent.hasExtra("address")) {
                if (!TextUtils.isEmpty(intent.getStringExtra("address"))) {
                    propertyAddress = intent.getStringExtra("address");
                    binding.searchLocality.setText(propertyAddress);
                }
                if (!TextUtils.isEmpty(intent.getStringExtra("search_string"))) {
                    search_string = intent.getStringExtra("search_string");
                    binding.searchLocality.setTag(search_string);
                }

                if (!TextUtils.isEmpty(binding.searchLocality.getText().toString().trim())) {
                    binding.searchTitleLayout.setBackground(getResources().getDrawable(R.drawable.edittext_redborder));
                } else {
                    binding.searchTitleLayout.setBackground(getResources().getDrawable(R.drawable.edittext_grayborder));
                }
            }


           /* binding.nodataLayout.setVisibility(View.GONE);
            binding.btnGetEstimate.setVisibility(View.VISIBLE);
            binding.propertyTypeOptions.setVisibility(View.VISIBLE);*/

        }

    }

    private String search_string;

    @Override
    public void onResume() {
        super.onResume();
        binding.etSize.setInputType(InputType.TYPE_NULL);
    }

    public void onCitySelect(City city) {
        customDialog.dismiss();
//        binding.selectCity.setText(city.getName());
        binding.searchLocality.setText("");
        cityName = city.getName();
        cityId = city.getId();
        latitude = city.getLat();
        longitude = city.getLng();
        UtilityMethods.saveStringInPref(getActivity(),
                Constants.AppConstants.ESTIMATE_PNAME, city.getPName());
        UtilityMethods.saveStringInPref(getActivity(),
                Constants.AppConstants.ESTIMATE_CITY, city.getName());
        UtilityMethods.saveLongInPref(getActivity(),
                Constants.AppConstants.ESTIMATE_CITY_ID, city.getId());
        UtilityMethods.saveStringInPref(getActivity(), Constants.AppConstants.ESTIMATE_STATE,
                city.getState());
    }


    @Override
    public boolean isInternetConnected() {

        if(getActivity() == null) {
            return false;
        }

        if (UtilityMethods.isInternetConnected(getActivity())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void showLoader() {
        if(getActivity() != null) {
            showProgressBar();
        }
    }

    @Override
    public void hideLoader() {

    }

    @Override
    public void onError(String errorMsg) {

        if(getActivity() != null)
        {
            dismissProgressBar();
            Intent i =new Intent(getActivity(),NoPropertyFoundScreen.class);
            startActivity(i);
        }


    }

    @Override
    public void onSuccess(GetEStimateModel body) {
        dismissProgressBar();
        moveToEstimatedPage(body);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
