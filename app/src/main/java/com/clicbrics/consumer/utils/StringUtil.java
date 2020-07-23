package com.clicbrics.consumer.utils;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 * Created by Alok on 30-07-2018.
 */
public class StringUtil {

    public static String getPriceRange(long minPirce,long maxPirce){
        String projectPriceRange = "";
        if(minPirce == 0){
            return "Price on request";
        }else {
            if ((minPirce == maxPirce) || (maxPirce == 0)) {
                projectPriceRange = UtilityMethods.getPriceWordWithoutSymbol(minPirce);
            } else {
                projectPriceRange = UtilityMethods.getPriceWordWithoutSymbol(minPirce) + " - " +
                        UtilityMethods.getPriceWordWithoutSymbol(maxPirce);
            }
            return ("\u20B9" + " " + projectPriceRange);
        }
    }

    public static String getBedString(SortedSet<Integer> bedList,
                                      List<String> typeList, String type){
        String bedString = new String();
        ArrayList<Integer> keys;
        if(bedList == null){
             return "";
        }else{
            keys = new ArrayList<>(bedList);
        }
        boolean isOnlyPlot = false, hasPlot = false, hasCommercial = false, hasApartment = false,
                hasVilla = false, hasFloor = false;
        String commercialStr = "";
        if (typeList != null && !typeList.isEmpty()) {
            if(UtilityMethods.hasApartment(typeList)){
                hasApartment = true;
            }
            else if(UtilityMethods.hasVilla(typeList)){
                hasVilla = true;
            }
            else if (typeList.contains("Land") || typeList.contains("land") || typeList.contains("LAND")) {
                hasPlot = true;
                HashSet<String> typeHashSet = new HashSet<>(typeList);
                if (typeHashSet.size() == 1 && (typeHashSet.contains("land")
                        || typeHashSet.contains("Land") || typeHashSet.contains("LAND"))) {
                    isOnlyPlot = true;
                }
            }else if(UtilityMethods.isCommercial(typeList)){
                hasCommercial = true;
                if (typeList != null) {
                    commercialStr = UtilityMethods.getCommercialTypeName(typeList);
                }else{
                    commercialStr = "Commercial";
                }
            }else if(typeList.contains(Constants.AppConstants.PropertyType.IndependentFloor.toString())){
                hasFloor = true;
            }
        }
        if (bedList.size() == 1) {
            if(keys.get(0) == 0) {
                if(type != null) {
                    if (type.equalsIgnoreCase("Commercial") || UtilityMethods.isCommercial(type)) {
                        bedString = UtilityMethods.getCommercialTypeName(type);
                    } else if (type.equalsIgnoreCase("LAND")) {
                        bedString = "PLOT";
                    } else if (UtilityMethods.hasVilla(type)) {
                        bedString = "Villa";
                    } else if (type.equalsIgnoreCase("Floor")) {
                        bedString = "BHK Independent Floor";
                    }
                }
            }else{
                if(hasApartment) {
                    bedString = keys.get(0) + " BHK Apartment";
                }else if(hasVilla){
                    bedString = keys.get(0) + " BHK Villa";
                } else if(hasFloor){
                    bedString = keys.get(0) + " BHK Independent Floor";
                } else {
                    bedString = keys.get(0) + " BHK Apartment";
                }
            }
        } else {
            List<Integer> bhks = keys;
            Collections.sort(bhks);

            if (bhks != null && bhks.size() > 0) {
                if (bhks.size() > 1) {
                    int index = 0;
                    for(int i=0; i<bhks.size();i++){
                        if(bhks.get(i) != 0){
                            index = i;
                            break;
                        }
                    }
                    if(index == bhks.size() - 1){
                        bedString = String.valueOf(bhks.get(index));
                    }else {
                        bedString = String.valueOf(bhks.get(index)) + "-" + String.valueOf(bhks.get(bhks.size() - 1));
                    }
                } else {
                    if(bhks.get(0) != 0) {
                        bedString = String.valueOf(bhks.get(0));
                    }
                }
            }
            //bedString += " BHK";
            if(hasApartment) {
                bedString = bedString + " BHK Apartment";
            }else if(hasVilla){
                bedString = bedString + " BHK Villa";
            }else if(hasFloor){
                bedString = bedString + " BHK Independent Floor";
            } else {
                bedString = bedString + " BHK Apartment";
            }

        }
        if (hasPlot) {
            if (!bedString.isEmpty()) {
                //bedString = ""+Html.fromHtml(" \u00B7 " + bedString);
            } else {
                bedString = "PLOT";
            }
        }else if(hasCommercial){
            bedString = commercialStr;
        }
        if(!TextUtils.isEmpty(bedString)) {
            return Html.fromHtml(" \u00B7 " + bedString) + "";
        }else{
            return "";
        }
    }

    public static String getStatusString(List<String> propertyStatusList){
        if(propertyStatusList == null || propertyStatusList.isEmpty()){
            return "";
        }
        String propertyStatus = "";
        if(propertyStatusList.contains(Constants.AppConstants.PropertyStatus.ReadyToMove.toString())){
            propertyStatus = Html.fromHtml(UtilityMethods.getPropertyStatus(Constants.AppConstants.PropertyStatus.ReadyToMove.toString())).toString();
        }else if(propertyStatusList.contains(Constants.AppConstants.PropertyStatus.InProgress.toString())){
            propertyStatus = Html.fromHtml(UtilityMethods.getPropertyStatus(Constants.AppConstants.PropertyStatus.InProgress.toString())).toString();
        }else if(propertyStatusList.contains(Constants.AppConstants.PropertyStatus.NotStarted.toString())){
            propertyStatus = Html.fromHtml(UtilityMethods.getPropertyStatus(Constants.AppConstants.PropertyStatus.NotStarted.toString())).toString();
        }else if(propertyStatusList.contains(Constants.AppConstants.PropertyStatus.UpComing.toString())){
            propertyStatus = Html.fromHtml(UtilityMethods.getPropertyStatus(Constants.AppConstants.PropertyStatus.UpComing.toString())).toString();
        }else{
            propertyStatus = Html.fromHtml(UtilityMethods.getPropertyStatus(propertyStatusList.get(0)))+"";
        }
        return propertyStatus;
    }

    public static String getAreaRange(Context context,long areaValue, long maxArea, List<String> typeList){
        String areaRange = "";
        boolean isOnlyPlot = false;
        if (typeList != null && (typeList.contains("Land") || typeList.contains("land") || typeList.contains("LAND")) ) {
            HashSet<String> typeHashSet = new HashSet<>(typeList);
            if (typeHashSet.size() == 1 && (typeHashSet.contains("land")
                    || typeHashSet.contains("Land") || typeHashSet.contains("LAND"))) {
                isOnlyPlot = true;
            }
        }

        if ((maxArea != 0) && (areaValue == maxArea)) {
            if (isOnlyPlot) {
                areaRange = " \u00B7 " + UtilityMethods.getArea(context,areaValue,true) +
                                    " "+ UtilityMethods.getUnit(context,true);
                //areaRange = Html.fromHtml(" \u00B7 " + UtilityMethods.getAreaInYards(areaValue) + " Sq. yd") + "";
            } else {
                areaRange = " \u00B7 " + UtilityMethods.getArea(context,areaValue,false) +
                        " "+ UtilityMethods.getUnit(context,false);
                //areaRange = Html.fromHtml(" \u00B7 " + areaValue + " Sq. ft")+"";
            }
        } else {
            if (maxArea > areaValue) {
                if (isOnlyPlot) {
                    areaRange = " \u00B7 " + UtilityMethods.getArea(context,areaValue,true) +
                            " - " + UtilityMethods.getArea(context,maxArea,true) +
                            " "+ UtilityMethods.getUnit(context,true);
                    /*areaRange = Html.fromHtml(" \u00B7 " + UtilityMethods.getAreaInYards(areaValue) + " - "
                            + UtilityMethods.getAreaInYards(maxArea) + " Sq. yd")+"";*/
                } else {
                    areaRange = " \u00B7 " + UtilityMethods.getArea(context,areaValue,false) +
                            " - " + UtilityMethods.getArea(context,maxArea,false) +
                            " "+ UtilityMethods.getUnit(context,false);
                    /*areaRange = Html.fromHtml(" \u00B7 " + areaValue + " - "
                            + maxArea + " Sq. ft")+"";*/
                }
            } else {
                if (isOnlyPlot) {
                    areaRange = " \u00B7 " + UtilityMethods.getArea(context,maxArea,true) +
                            " "+ UtilityMethods.getUnit(context,true);
                    /*areaRange = Html.fromHtml(" \u00B7 " + UtilityMethods.getAreaInYards(maxArea) + " - "
                            + UtilityMethods.getAreaInYards(areaValue) + " Sq. yd")+"";*/
                } else {
                    areaRange = " \u00B7 " + UtilityMethods.getArea(context,maxArea,false) +
                            " "+ UtilityMethods.getUnit(context,false);
                    /*areaRange = Html.fromHtml(" \u00B7 " + maxArea + " - "
                            + areaValue + " Sq. ft") +"";*/
                }
            }
        }
        return areaRange;
    }

    public static boolean isAreaNBedVisible(long areaValue, long maxArea){
        if(maxArea == 0 && areaValue == 0){
            return false;
        }else{
            return true;
        }
    }

    public static String getAddress(String address, Context context){
        String cityName = UtilityMethods.getStringInPref(context,Constants.AppConstants.SAVED_CITY,"");
        if (address.contains(cityName)) {
            return address;
        } else {
            return (address + " " + cityName);
        }

    }

    public static String getAddressRecent(String address, Context context){
        return address;
    }

    public static String getArea(String bedRoomStr, String propertyType){
        if(!TextUtils.isEmpty(propertyType)){
            return (bedRoomStr + " " + propertyType);
        }else{
            return bedRoomStr;
        }
    }

    public static String getArticleCreationTime(long creationTime){
        return UtilityMethods.getDate(creationTime,"dd MMM, yyyy");
    }

    public static boolean isFavorite(Context context, long projectId){
        Set<String> favoriteList = UtilityMethods.getWishListFromPrefs(context, Constants.AppConstants.PROJECT_ID_SET);
        if(favoriteList == null || favoriteList.isEmpty()){
            return true;
        }else{
            if (favoriteList.contains(projectId + "")) {
                return true;
            }else{
                return false;
            }
        }
    }

    public static String getBedStringWithStatus(SortedSet<Integer> bedList,
                                      List<String> typeList, String type, String status){
        String bedString = new String();
        ArrayList<Integer> keys;
        if(bedList == null){
            return "";
        }else{
            keys = new ArrayList<>(bedList);
        }
        boolean isOnlyPlot = false, hasPlot = false, hasCommercial = false, hasApartment = false,
                hasVilla = false, hasFloor = false;
        String commercialStr = "";
        if (typeList != null && !typeList.isEmpty()) {
            if(UtilityMethods.hasApartment(typeList)){
                hasApartment = true;
            }
            else if(UtilityMethods.hasVilla(typeList)){
                hasVilla = true;
            }
            else if (typeList.contains("Land") || typeList.contains("land") || typeList.contains("LAND")) {
                hasPlot = true;
                HashSet<String> typeHashSet = new HashSet<>(typeList);
                if (typeHashSet.size() == 1 && (typeHashSet.contains("land")
                        || typeHashSet.contains("Land") || typeHashSet.contains("LAND"))) {
                    isOnlyPlot = true;
                }
            }else if(UtilityMethods.isCommercial(typeList)){
                hasCommercial = true;
                HashSet<String> typeHashSet = new HashSet<>(typeList);
                if (typeHashSet.size() == 1) {
                    commercialStr = UtilityMethods.getCommercialTypeName(typeList.get(0));
                }else{
                    commercialStr = "Commercial";
                }
            }else if(typeList.contains(Constants.AppConstants.PropertyType.IndependentFloor.toString())){
                hasFloor = true;
            }
        }
        if (bedList.size() == 1) {
            if(keys.get(0) == 0) {
                if(type != null) {
                    if (type.equalsIgnoreCase("Commercial") || UtilityMethods.isCommercial(type)) {
                        bedString = UtilityMethods.getCommercialTypeName(type);
                    } else if (type.equalsIgnoreCase("LAND")) {
                        bedString = "PLOT";
                    } else if (UtilityMethods.hasVilla(type)) {
                        bedString = "Villa";
                    } else if (type.equalsIgnoreCase("Floor")) {
                        bedString = "BHK Independent Floor";
                    }
                }
            }else{
                if(hasApartment) {
                    bedString = keys.get(0) + " BHK Apartment";
                }else if(hasVilla){
                    bedString = keys.get(0) + " BHK Villa";
                } else if(hasFloor){
                    bedString = keys.get(0) + " BHK Independent Floor";
                } else {
                    bedString = keys.get(0) + " BHK Apartment";
                }
            }
        } else {
            List<Integer> bhks = keys;
            Collections.sort(bhks);

            if (bhks != null && bhks.size() > 0) {
                if (bhks.size() > 1) {
                    int index = 0;
                    for(int i=0; i<bhks.size();i++){
                        if(bhks.get(i) != 0){
                            index = i;
                            break;
                        }
                    }
                    if(index == bhks.size() - 1){
                        bedString = String.valueOf(bhks.get(index));
                    }else {
                        bedString = String.valueOf(bhks.get(index)) + "-" + String.valueOf(bhks.get(bhks.size() - 1));
                    }
                } else {
                    if(bhks.get(0) != 0) {
                        bedString = String.valueOf(bhks.get(0));
                    }
                }
            }
            //bedString += " BHK";
            if(hasApartment) {
                bedString = bedString + " BHK Apartment";
            }else if(hasVilla){
                bedString = bedString + " BHK Villa";
            }else if(hasFloor){
                bedString = bedString + " BHK Independent Floor";
            } else {
                bedString = bedString + " BHK Apartment";
            }

        }
        if (hasPlot) {
            if (!bedString.isEmpty()) {
                //bedString = ""+Html.fromHtml(" \u00B7 " + bedString);
            } else {
                bedString = "PLOT";
            }
        }else if(hasCommercial){
            bedString = commercialStr;
        }
        String propertyStatus = Html.fromHtml(status
                .replace("NotStarted", "New Launch").replace("InProgress", "Under Construction")
                .replace("ReadyToMove", "Ready To Move")) + "";
        return Html.fromHtml(bedString + " " + "\u00B7" + " " + propertyStatus)+"";
    }

    public static String getRecentBHKType(String bhkType){
        if(!TextUtils.isEmpty(bhkType)){
            return "\u00B7" + " " + bhkType;
        }
        return "";
    }
}
