package com.clicbrics.consumer.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.buy.housing.backend.userEndPoint.model.CommonResponse;
import com.buy.housing.backend.userEndPoint.model.SearchCriteria;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.framework.activity.LoginActivity;
import com.clicbrics.consumer.framework.util.HousingLogs;
import com.clicbrics.consumer.interfaces.LoginSuccessCallback;
import com.clicbrics.consumer.view.activity.HomeScreen;
import com.clicbrics.consumer.wrapper.SaveSearchWrapper;
import com.google.android.gms.maps.model.LatLng;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alok on 10-08-2018.
 */
public class SaveSearchUtility implements LoginSuccessCallback {

    private static final String TAG = "SaveSearchUtility";
    private Activity activity;
    private SaveSearchWrapper saveSearchWrapper;
    private Handler handler;
    private enum LoginFor {
        FAVORITE_LISTING, BOOK_A_VISIT, SAVE_SEARCH
    }
    private enum PropertySearchByEnum{
        SEARCH_BY_CITY, SEARCH_BY_LOCALITY, SEARCH_BY_DEVELOPER;
    }
    private LoginFor loginFor;
    private long searchByBuilderId, searchByCityId;
    private String searchByBuilderName, searchByCityName,address,additionAddress,saveSearchName;
    private LatLng searchByLocation;

    public SaveSearchUtility(Activity activity){
        this.activity = activity;
        handler = new Handler(Looper.getMainLooper());
    }

    public void setSearchByParameter(String saveSearchName,long searchByCityId, String searchByCityName, long searchByBuilderId,
                                     String searchByBuilderName, LatLng searchByLocation,
                                     String address, String additionAddress){
        this.saveSearchName = saveSearchName;
        this.searchByCityId = searchByCityId;
        this.searchByCityName = searchByCityName;
        this.searchByBuilderId = searchByBuilderId;
        this.searchByBuilderName = searchByBuilderName;
        this.searchByLocation = searchByLocation;
        if(TextUtils.isEmpty(address)){
            address = UtilityMethods.getStringInPref(activity,Constants.AppConstants.SAVED_CITY,"");
        }
        this.address = address;
        this.additionAddress = additionAddress;
    }

    public void doSaveSearch(){
        if(!UtilityMethods.isInternetConnected(activity)){
            UtilityMethods.showErrorSnackbarOnTop(activity);
            return;
        }
        if (UtilityMethods.getLongInPref(activity, Constants.ServerConstants.USER_ID, -1) == -1) {
            HousingApplication.mLoginSuccessCallback = this;
            loginFor = LoginFor.SAVE_SEARCH;
            activity.startActivity(new Intent(activity, LoginActivity.class));
        } else {
            showSaveSearchDialog();
        }
    }

    private void showSaveSearchDialog() {
        try {
            String cityName = "";
            if(!TextUtils.isEmpty(saveSearchName)){
                cityName = saveSearchName;
            }else{
                if (!TextUtils.isEmpty(searchByBuilderName)) {
                    cityName = searchByBuilderName;
                    if (!TextUtils.isEmpty(searchByCityName)) {
                        if (!TextUtils.isEmpty(cityName)) {
                            cityName += ", ";
                        }
                        cityName += searchByCityName;
                    } else {
                        if (!TextUtils.isEmpty(cityName)) {
                            cityName += ", ";
                        }
                        cityName += UtilityMethods.getStringInPref(activity, Constants.AppConstants.SAVED_CITY, "");
                    }
                } else if (TextUtils.isEmpty(address) && TextUtils.isEmpty(additionAddress)) {
                    cityName = UtilityMethods.getStringInPref(activity, Constants.AppConstants.SAVED_CITY, "");
                } else {
                    if (!TextUtils.isEmpty(address)) {
                        cityName = cityName + address;
                    }
                    if (!TextUtils.isEmpty(additionAddress)) {
                        cityName = cityName + " " + additionAddress;
                    }
                }
            }
            LayoutInflater li = LayoutInflater.from(activity);
            View promptsView = li.inflate(R.layout.dialog_save_search, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    activity);
            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);
            final EditText userInput =  promptsView.findViewById(R.id.editTextDialogUserInput);

            userInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    userInput.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            if (cityName != null) {
                userInput.setText(cityName);
                userInput.setSelection(cityName.length());
            } else {
                userInput.setError("Please enter a name for your search criteria!");
            } // set dialog message

            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton(activity.getResources().getString(R.string.save),
                            null)
                    .setNegativeButton(activity.getResources().getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
                    positiveButton.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (userInput.getText().toString().isEmpty() || userInput.getText().toString().trim().isEmpty()) {
                                userInput.setError("Please enter a name for your search criteria!");
                            } else {
                                saveSearch(userInput.getText().toString());
                                alertDialog.dismiss();
                            }
                        }
                    });
                }
            });


            // show it
            alertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            ((HomeScreen) activity).dismissProgressBar();
            HousingLogs.d(TAG, e.getMessage());
        }

    }
    
    private void saveSearch(final String searchName) {

        if (!UtilityMethods.isInternetConnected(activity)) {
            UtilityMethods.showErrorSnackbarOnTop(activity);
            return;
        }

        ((HomeScreen)activity).showProgressBar();
        new Thread(new Runnable() {
            String errorMsg = "";
            @Override
            public void run() {
                try {
                    SearchCriteria sc = new SearchCriteria();
                    sc.setId(UtilityMethods.getLongInPref(activity, Constants.ServerConstants.USER_ID, 0l));
                    if (!TextUtils.isEmpty(searchByCityName)) {
                        sc.setCity(searchByCityName);
                    } else if (!TextUtils.isEmpty(UtilityMethods.getStringInPref(activity, Constants.AppConstants.SAVED_CITY, ""))) {
                        sc.setCity(UtilityMethods.getStringInPref(activity, Constants.AppConstants.SAVED_CITY, ""));
                    }
                    if (searchByCityId != -1 && searchByCityId != 0) {
                        sc.setCityId(searchByCityId);
                    } else if (UtilityMethods.getLongInPref(activity, Constants.AppConstants.SAVED_CITY_ID, -1) != -1) {
                        sc.setCityId(UtilityMethods.getLongInPref(activity, Constants.AppConstants.SAVED_CITY_ID, -1));
                    }
                    if (searchByBuilderId != -1 && searchByBuilderId != 0) {
                        sc.setBuilderId(searchByBuilderId);
                    }
                    if (!TextUtils.isEmpty(searchByBuilderName)) {
                        sc.setBuilder(searchByBuilderName);
                    }
                    sc.setName(searchName);
                    if (searchByLocation != null) {
                        sc.setLatitude(searchByLocation.latitude);
                    }
                    if (searchByLocation != null) {
                        sc.setLongitude(searchByLocation.longitude);
                    }
                    //sc.setZoomLevel(zoom);
                    /*if (drawBtnState == MapFragment.DrawBtnState.drawing) {
                        ArrayList<LatLong> drawLatLong = new ArrayList<>();
                        List<LatLng> val = ((HousingApplication) activity.getApplicationContext()).getPolygonLatLngList();
                        if(val != null && !val.isEmpty()) {
                            for (int i = 0; i < val.size(); i++) {
                                LatLong latLong = new LatLong();
                                latLong.setLongitude(val.get(i).longitude);
                                latLong.setLatitude(val.get(i).latitude);
                                drawLatLong.add(latLong);
                            }
                            sc.setLatLongList(drawLatLong);
                        }else if(UtilityMethods.getBooleanInPref(activity, Constants.SharedPreferConstants.SAVE_SEARCH_APPLIED,false)){
                            if(saveSearchWrapper != null && saveSearchWrapper.latLongList != null && !saveSearchWrapper.latLongList.isEmpty()) {
                                sc.setLatLongList(saveSearchWrapper.latLongList);
                            }
                        }
                    }else if(drawBtnState == MapFragment.DrawBtnState.drawn && activity != null
                            && ((HousingApplication) activity.getApplicationContext()).getPolygonLatLngList() != null
                            && !((HousingApplication) activity.getApplicationContext()).getPolygonLatLngList().isEmpty()){

                        List<LatLng> val = ((HousingApplication) activity.getApplicationContext()).getPolygonLatLngList();
                        if(val != null && !val.isEmpty()) {
                            ArrayList<LatLong> drawLatLong = new ArrayList<>();
                            for (int i = 0; i < val.size(); i++) {
                                LatLong latLong = new LatLong();
                                latLong.setLongitude(val.get(i).longitude);
                                latLong.setLatitude(val.get(i).latitude);
                                drawLatLong.add(latLong);
                            }
                            sc.setLatLongList(drawLatLong);
                        }
                    }*/

                    Log.d(TAG, "FilterAplied->" +
                            UtilityMethods.getBooleanInPref(activity, Constants.SharedPreferConstants.PARAM_FILTER_APPLIED, false));

                    if (UtilityMethods.getBooleanInPref(activity, Constants.SharedPreferConstants.PARAM_FILTER_APPLIED, false)) {
                        sc.setFilterApplied(true);
                        ArrayList<String> propertyType = new ArrayList();
                        if (UtilityMethods.getBooleanInPref(activity, Constants.SharedPreferConstants.PARAM_APARTMENT, false))
                            propertyType.add(Constants.AppConstants.PropertyType.Apartment.toString());
                        if (UtilityMethods.getBooleanInPref(activity, Constants.SharedPreferConstants.PARAM_VILLA, false))
                            propertyType.add(Constants.AppConstants.PropertyType.IndependentHouseVilla.toString());
                        if (UtilityMethods.getBooleanInPref(activity, Constants.SharedPreferConstants.PARAM_PLOTS, false))
                            propertyType.add(Constants.AppConstants.PropertyType.Land.toString());
                        if (UtilityMethods.getBooleanInPref(activity, Constants.SharedPreferConstants.PARAM_COMMERCIAL, false))
                            propertyType.add(Constants.AppConstants.PropertyType.Shop.toString());
                        if (UtilityMethods.getBooleanInPref(activity, Constants.SharedPreferConstants.PARAM_FLOOR, false))
                            propertyType.add(Constants.AppConstants.PropertyType.IndependentFloor.toString());
                        sc.setPropertyTypeEnum(propertyType);

                        ArrayList<String> propertyStatusList = new ArrayList();
                        if (UtilityMethods.getBooleanInPref(activity, Constants.SharedPreferConstants.PARAM_NEW, false))
                            propertyStatusList.add(Constants.AppConstants.PropertyStatus.NotStarted.toString());
                        if (UtilityMethods.getBooleanInPref(activity, Constants.SharedPreferConstants.PARAM_ONGOING, false))
                            propertyStatusList.add(Constants.AppConstants.PropertyStatus.InProgress.toString());
                        if (UtilityMethods.getBooleanInPref(activity, Constants.SharedPreferConstants.PARAM_READY, false))
                            propertyStatusList.add(Constants.AppConstants.PropertyStatus.ReadyToMove.toString());
                        if (UtilityMethods.getBooleanInPref(activity, Constants.SharedPreferConstants.PARAM_UPCOMING, false))
                            propertyStatusList.add(Constants.AppConstants.PropertyStatus.UpComing.toString());
                        sc.setPropertyStatusList(propertyStatusList);

                        int[] priceArray = activity.getResources().getIntArray(R.array.price_array_value);
                        if (priceArray[UtilityMethods.getIntInPref(activity, Constants.SharedPreferConstants.PARAM_MIN_PRICE, 0)] != priceArray[0]) {
                            sc.setMinCost((long) priceArray[UtilityMethods.getIntInPref(activity, Constants.SharedPreferConstants.PARAM_MIN_PRICE, 0)]);
                        }
                        if (priceArray[UtilityMethods.getIntInPref(activity, Constants.SharedPreferConstants.PARAM_MAX_PRICE, 0)] != priceArray[priceArray.length - 1]) {
                            sc.setMaxCost((long) priceArray[UtilityMethods.getIntInPref(activity, Constants.SharedPreferConstants.PARAM_MAX_PRICE, priceArray.length - 1)]);
                        }
                        int[] areaArray = activity.getResources().getIntArray(R.array.area_array_values);

                        int roomIndex = UtilityMethods.getIntInPref(activity, Constants.SharedPreferConstants.PARAM_ROOM, 0);
                        List<Integer> bedList = new ArrayList<Integer>();
                        if ((roomIndex & 1) == 1) {
                            bedList.add(1);
                        }
                        if ((roomIndex & 2) == 2) {
                            bedList.add(2);
                        }
                        if ((roomIndex & 4) == 4) {
                            bedList.add(3);
                        }
                        if ((roomIndex & 8) == 8) {
                            bedList.add(4);
                        }
                        if ((bedList != null) && (!bedList.isEmpty())) {
                            sc.setBedList(bedList);
                        }

                    } else {
                        sc.setFilterApplied(false);
                    }
                    sc.setTime(System.currentTimeMillis());

                    CommonResponse commonResponse = EndPointBuilder.getUserEndPoint().
                            addSearchCriteria(UtilityMethods.getLongInPref(activity, Constants.ServerConstants.USER_ID, 0l), sc)
                            .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    if (commonResponse.getStatus()) {
                        SaveSearchWrapper saveSearchWrapper = UtilityMethods.getSaveSearchWrapper(sc);
                        UtilityMethods.addToSaveSearchList(activity, saveSearchWrapper);
                        UtilityMethods.saveBooleanInPref(activity,Constants.MORE_FRAGMENT_UPDATE,true);
                        int savedCount = UtilityMethods.getIntInPref(activity, Constants.AppConstants.SAVED_SEARCH_COUNT, 0);
                        UtilityMethods.saveIntInPref(activity, Constants.AppConstants.SAVED_SEARCH_COUNT, savedCount);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                UtilityMethods.showSnackbarOnTop(activity,"Success","Your search has been saved.",false, Constants.AppConstants.ALERTOR_LENGTH_LONG);
                            }
                        });
                        Log.d(TAG, "STatus->" + commonResponse.getStatus());
                    } else {
                        if (commonResponse != null && commonResponse.getErrorMessage() != null) {
                            errorMsg = commonResponse.getErrorMessage();
                        } else {
                            errorMsg = "Could not save search, Please try again!";
                        }
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ((HomeScreen)activity).dismissProgressBar();
                        }
                    });

                    Log.d(TAG, "errMsg->" + errorMsg + "\n" + commonResponse.getErrorId());

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    errorMsg = activity.getResources().getString(R.string.network_error_msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = "Could not save search, Please try again!";
                }
                if (!TextUtils.isEmpty(errorMsg)) {
                    final String errmsg = errorMsg;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ((HomeScreen)activity).dismissProgressBar();
                            UtilityMethods.showSnackbarOnTop(activity,"Error", errorMsg,true, Constants.AppConstants.ALERTOR_LENGTH_LONG);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void isLoggedin() {
        if (loginFor.equals(LoginFor.SAVE_SEARCH)) {
            showSaveSearchDialog();
        }
    }
}
