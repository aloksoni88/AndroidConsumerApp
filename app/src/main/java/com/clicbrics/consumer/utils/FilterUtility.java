package com.clicbrics.consumer.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.clicbrics.consumer.R;

/**
 * Created by Alok on 22-04-2019.
 */
public class FilterUtility {
    private static final String TAG = "FilterUtility";

    private String filterPropertyType=null;
    private String filterBedRoom=null;
    private String filterProjectStatus=null;
    private Integer filterMinPrice=null;
    private Integer filterMaxPrice=null;
    private Integer filterMinArea=null;
    private Integer filterMaxArea=null;
    private String filterSort=null;

    public FilterUtility(Context context){
        checkPropertyStatusFilter(context);
        checkPropertyTypeFilter(context);
        checkBedroomFilter(context);
        checkMinPriceFilter(context);
        checkMaxPriceFilter(context);
    }

    private void checkPropertyStatusFilter(Context context) {
        String filterPropertyStatus = null;
        if(UtilityMethods.getBooleanInPref(context, Constants.SharedPreferConstants.PARAM_NEW, false)){
            filterPropertyStatus = Constants.AppConstants.PropertyStatus.NotStarted.toString();
        }
        if(UtilityMethods.getBooleanInPref(context, Constants.SharedPreferConstants.PARAM_ONGOING, false)){
            if(!TextUtils.isEmpty(filterPropertyStatus)){
                filterPropertyStatus = filterPropertyStatus + "','" + Constants.AppConstants.PropertyStatus.InProgress.toString();
            }else{
                filterPropertyStatus = Constants.AppConstants.PropertyStatus.InProgress.toString();
            }
        }

        if(UtilityMethods.getBooleanInPref(context, Constants.SharedPreferConstants.PARAM_UPCOMING, false)){
            if(!TextUtils.isEmpty(filterPropertyStatus)){
                filterPropertyStatus = filterPropertyStatus + "','" + Constants.AppConstants.PropertyStatus.UpComing.toString();
            }else{
                filterPropertyStatus = Constants.AppConstants.PropertyStatus.UpComing.toString();
            }
        }

        if(UtilityMethods.getBooleanInPref(context, Constants.SharedPreferConstants.PARAM_READY, false)){
            if(!TextUtils.isEmpty(filterPropertyStatus)){
                filterPropertyStatus = filterPropertyStatus + "','" + Constants.AppConstants.PropertyStatus.ReadyToMove.toString();
            }else{
                filterPropertyStatus = Constants.AppConstants.PropertyStatus.ReadyToMove.toString();
            }
        }
        Log.i(TAG, "Filter Project Status : " + filterPropertyStatus);
        setFilterProjectStatus(filterPropertyStatus);
    }

    private void checkPropertyTypeFilter(Context context){
        String filterPropertyType = null;
        if(UtilityMethods.getBooleanInPref(context, Constants.SharedPreferConstants.PARAM_APARTMENT, false)){
            filterPropertyType = createApartmentString();
        }
        if(UtilityMethods.getBooleanInPref(context, Constants.SharedPreferConstants.PARAM_VILLA, false)){
            if(!TextUtils.isEmpty(filterPropertyType)){
                filterPropertyType = filterPropertyType + "','" + createVillaString();
            }else{
                filterPropertyType = createVillaString();
            }
        }
        if(UtilityMethods.getBooleanInPref(context, Constants.SharedPreferConstants.PARAM_PLOTS, false)){
            if(!TextUtils.isEmpty(filterPropertyType)){
                filterPropertyType = filterPropertyType + "','" + Constants.AppConstants.PropertyType.Land.toString();
            }else{
                filterPropertyType = Constants.AppConstants.PropertyType.Land.toString();
            }
        }
        if(UtilityMethods.getBooleanInPref(context, Constants.SharedPreferConstants.PARAM_COMMERCIAL, false)){
            if(!TextUtils.isEmpty(filterPropertyType)){
                filterPropertyType = filterPropertyType + "','" + createCommercialString();
            }else{
                filterPropertyType = createCommercialString();
            }
        }
        if(UtilityMethods.getBooleanInPref(context, Constants.SharedPreferConstants.PARAM_FLOOR, false)){
            if(!TextUtils.isEmpty(filterPropertyType)){
                filterPropertyType = filterPropertyType + "','" + Constants.AppConstants.PropertyType.IndependentFloor.toString();
            }else{
                filterPropertyType = Constants.AppConstants.PropertyType.IndependentFloor.toString();
            }
        }
        Log.i(TAG, "Filter Property Type : " + filterPropertyType);
        setFilterPropertyType(filterPropertyType);
    }

    private void checkBedroomFilter(Context context){
        String filterBedroom = null;
        if(UtilityMethods.getIntInPref(context, Constants.SharedPreferConstants.PARAM_ROOM, 0) != 0){
            int rooom = UtilityMethods.getIntInPref(context, Constants.SharedPreferConstants.PARAM_ROOM, 0);
            if (((rooom & 1) != 0)) {
                filterBedroom = "1";
            }
            if (((rooom & 2) != 0)) {
                if(!TextUtils.isEmpty(filterBedroom)){
                    filterBedroom = filterBedroom + ","+ "2";
                }else{
                    filterBedroom = "2";
                }
            }
            if (((rooom & 4) != 0)) {
                if(!TextUtils.isEmpty(filterBedroom)){
                    filterBedroom = filterBedroom + ","+ "3";
                }else{
                    filterBedroom = "3";
                }
            }
            if (((rooom & 8) != 0)) {
                if(!TextUtils.isEmpty(filterBedroom)){
                    filterBedroom = filterBedroom + ","+ "4,5,6";
                }else{
                    filterBedroom = "4,5,6";
                }
            }
        }
        setFilterBedRoom(filterBedroom);
    }

    private void checkMinPriceFilter(Context context){
        int[] valuePriceArray = context.getResources().getIntArray(R.array.price_array_value);
        int savedMinPrice = UtilityMethods.getIntInPref(context, Constants.SharedPreferConstants.PARAM_MIN_PRICE, 0);
        if(savedMinPrice != 0){
            setFilterMinPrice(valuePriceArray[savedMinPrice]);
        }else{
            setFilterMinPrice(null);
        }
    }

    private void checkMaxPriceFilter(Context context){
        int[] valuePriceArray = context.getResources().getIntArray(R.array.price_array_value);
        int savedMaxPrice = UtilityMethods.getIntInPref(context, Constants.SharedPreferConstants.PARAM_MAX_PRICE, 0);
        if(savedMaxPrice != 0 && savedMaxPrice != 21){
            setFilterMaxPrice(valuePriceArray[savedMaxPrice]);
        }else{
           setFilterMaxPrice(null);
        }
    }

    private static String createApartmentString(){
        return  Constants.AppConstants.PropertyType.Apartment.toString() + "','" +
                Constants.AppConstants.PropertyType.Studio.toString() + "','" +
                Constants.AppConstants.PropertyType.Duplex.toString() + "','" +
                Constants.AppConstants.PropertyType.Penthouse.toString();
    }

    private static String createVillaString(){
        return  Constants.AppConstants.PropertyType.IndependentHouse.toString() + "','" +
                Constants.AppConstants.PropertyType.RowHouse.toString() + "','" +
                Constants.AppConstants.PropertyType.IndependentHouseVilla.toString() + "','" +
                Constants.AppConstants.PropertyType.Villa.toString();
    }

    private static String createCommercialString(){
        return  Constants.AppConstants.PropertyType.Shop.toString() + "','" +
                Constants.AppConstants.PropertyType.OfficeSpace.toString() + "','" +
                Constants.AppConstants.PropertyType.Warehouse.toString() + "','" +
                Constants.AppConstants.PropertyType.Hotel.toString() + "','" +
                Constants.AppConstants.PropertyType.GuestHouse.toString() + "','" +
                Constants.AppConstants.PropertyType.IndustrialBuilding.toString() + "','" +
                Constants.AppConstants.PropertyType.InstitutionLand.toString() + "','" +
                Constants.AppConstants.PropertyType.IndustrialLand.toString() + "','" +
                Constants.AppConstants.PropertyType.AgricultureLand.toString() + "','" +
                Constants.AppConstants.PropertyType.CommercialLand.toString();
    }


    private void setFilterPropertyType(String filterPropertyType) {
        this.filterPropertyType = filterPropertyType;
    }

    private void setFilterBedRoom(String filterBedRoom) {
        this.filterBedRoom = filterBedRoom;
    }

    private void setFilterProjectStatus(String filterProjectStatus) {
        this.filterProjectStatus = filterProjectStatus;
    }

    private void setFilterMinPrice(Integer filterMinPrice) {
        this.filterMinPrice = filterMinPrice;
    }

    private void setFilterMaxPrice(Integer filterMaxPrice) {
        this.filterMaxPrice = filterMaxPrice;
    }

    private void setFilterMinArea(Integer filterMinArea) {
        this.filterMinArea = filterMinArea;
    }

    private void setFilterMaxArea(Integer filterMaxArea) {
        this.filterMaxArea = filterMaxArea;
    }

    private void setFilterSort(String filterSort) {
        this.filterSort = filterSort;
    }

    public String getFilterPropertyType() {
        return filterPropertyType;
    }

    public String getFilterBedRoom() {
        return filterBedRoom;
    }

    public String getFilterProjectStatus() {
        return filterProjectStatus;
    }

    public Integer getFilterMinPrice() {
        return filterMinPrice;
    }

    public Integer getFilterMaxPrice() {
        return filterMaxPrice;
    }

    public Integer getFilterMinArea() {
        return filterMinArea;
    }

    public Integer getFilterMaxArea() {
        return filterMaxArea;
    }

    public String getFilterSort() {
        return filterSort;
    }
}
