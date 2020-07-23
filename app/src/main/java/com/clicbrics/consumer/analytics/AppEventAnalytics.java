package com.clicbrics.consumer.analytics;

import android.content.Context;
import android.os.Bundle;


import com.buy.housing.backend.userEndPoint.model.User;
import com.clicbrics.consumer.framework.util.HousingLogs;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Monita on 24-03-2020.
 */

public class AppEventAnalytics {

    public static void trackException(String TAG, String errorMsg, Exception e){
        try {

            HousingLogs.e(TAG,errorMsg,e);
            Crashlytics.logException(e);
            if(errorMsg != null) {
                Crashlytics.log(5, TAG, errorMsg);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static void trackEvent(Context context, String eventType, Bundle params){
        try {
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
            mFirebaseAnalytics.logEvent(eventType, params);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setAnalyticsProperty(Context context, User agent){

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        if(null != agent){
            if(null != agent.getId()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.USER_ID, String.valueOf(agent.getId()));
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.USER_ID, "");
            }
            if(null != agent.getEmail()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.EMAIL_ID, agent.getEmail());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.EMAIL_ID, "");
            }
            if(null != agent.getPhone()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.USER_PHONE, String.valueOf(agent.getPhone()));
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.USER_PHONE, "");
            }
            if(null != agent.getCity()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.CITY_NAME, agent.getCity());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.CITY_NAME, "");
            }
            if(null != agent.getAddress()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.USER_ADDRESS, agent.getAddress());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.USER_ADDRESS, "");
            }
            if(null != agent.getName()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.USER_NAME, agent.getName());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.USER_NAME, "");
            }
            if(null != agent.getCountryName()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.COUNTRY_NAME, agent.getCountryName());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.COUNTRY_NAME, "");
            }
            if(null != agent.getCountrySTDCode()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.COUNTRY_STDCODE, agent.getCountrySTDCode());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.COUNTRY_STDCODE, "");
            }
            if(null != agent.getAdhaarNo()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.ADHAAR_NO, agent.getAdhaarNo());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.ADHAAR_NO, "");
            }
            if(null != agent.getCompany()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.COMPANY, agent.getCompany());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.COMPANY, "");
            }
            if(null != agent.getAddressProofType()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.ADDRESS_PROOF_TYPE, agent.getAddressProofType());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.ADDRESS_PROOF_TYPE, "");
            }
            if(null != agent.getAddressProofUrl()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.ADDRESS_PROOF_URL, agent.getAddressProofUrl());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.ADDRESS_PROOF_URL, "");
            }
            if(null != agent.getAppVersion()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.APP_VERSION, agent.getAppVersion());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.APP_VERSION, "");
            }
            if(null != agent.getAdhaarUrl()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.ADHAR_URL, agent.getAdhaarUrl());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.ADHAR_URL, "");
            }
            if(null != agent.getDesignation()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.DESIGNATION, agent.getDesignation());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.DESIGNATION, "");
            }
            if(null != agent.getChildren()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.CHILDREN, agent.getChildren());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.CHILDREN, "");
            }
            if(null != agent.getDob()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.DOB, String.valueOf(agent.getDob()));
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.DOB, "");
            }
            if(null != agent.getFatherName()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.FATHERNAME, agent.getFatherName());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.FATHERNAME, "");
            }
            if(null != agent.getFax()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.FAX, agent.getFax());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.FAX, "");
            }
            if(null != agent.getFcmkey()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.FCM_KEY, agent.getFcmkey());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.FCM_KEY, "");
            }
            if(null != agent.getForm60()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.FORM_60, agent.getForm60());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.FORM_60, "");
            }
            if(null != agent.getGender()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.GENDER, agent.getGender());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.GENDER, "");
            }
            if(null != agent.getImageURL()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.IMAGE_URL, agent.getImageURL());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.IMAGE_URL, "");
            }
            if(null != agent.getKycCompany()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.KYC_COMPANY, agent.getKycCompany());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.KYC_COMPANY, "");
            }
            if(null != agent.getLat()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.LATITUDE, String.valueOf(agent.getLat()));
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.LATITUDE, "");
            }
            if(null != agent.getLng()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.LONGITUDE, String.valueOf(agent.getLng()));
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.LONGITUDE, "");
            }
            if(null != agent.getLoginType()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.LOGIN_TYPE, agent.getLoginType());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.LOGIN_TYPE, "");
            }
            if(null != agent.getMaritalStatus()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.MARITAL_STATUS, agent.getMaritalStatus());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.MARITAL_STATUS, "");
            }
            if(null != agent.getMemorandumOfArticle()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.MemorandumOfArticle, agent.getMemorandumOfArticle());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.MemorandumOfArticle, "");
            }
            if(null != agent.getMPIN()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.MPIN, agent.getMPIN());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.MPIN, "");
            }
            if(null != agent.getNationality()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.NATIONALITY, agent.getNationality());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.NATIONALITY, "");
            }
            if(null != agent.getNriDeclarationForm()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.NRI_DECALARATION, agent.getNriDeclarationForm());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.NRI_DECALARATION, "");
            } if(null != agent.getOfficeAddress()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.OFFICE_ADDRESS, agent.getOfficeAddress());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.OFFICE_ADDRESS, "");
            }
            if(null != agent.getOfficePhone()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.OFFICE_PHONE, agent.getOfficePhone());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.OFFICE_PHONE, "");
            }
            if(null != agent.getOstype()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.OS_TYPE, agent.getOstype());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.OS_TYPE, "");
            }
            if(null != agent.getOtherEmail()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.OTHER_EMAIL, agent.getOtherEmail());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.OTHER_EMAIL, "");
            }
            if(null != agent.getOtherPhone()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.OTHER_PHONE, agent.getOtherPhone());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.OTHER_PHONE, "");
            }
            if(null != agent.getPanNo()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PAN_NO, agent.getPanNo());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PAN_NO, "");
            }
            if(null != agent.getPanUrl()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PAN_URL, agent.getPanUrl());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PAN_URL, "");
            }
            if(null != agent.getPassport()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PASSPORT, agent.getPassport());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PASSPORT, "");
            }
            if(null != agent.getPassword()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PASSWORD, agent.getPassword());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PASSWORD, "");
            }
            if(null != agent.getPermanentAddress()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PERMANENT_ADDRESS, agent.getPermanentAddress());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PERMANENT_ADDRESS, "");
            }
            if(null != agent.getPermanentCity()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PERMANENT_CITY, agent.getPermanentCity());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PERMANENT_CITY, "");
            }
            if(null != agent.getPermanentPin()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PERMANENT_PIN, agent.getPermanentPin());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PERMANENT_PIN, "");
            }
            if(null != agent.getPermanentState()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PERMANENT_STATE, agent.getPermanentState());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PERMANENT_STATE, "");
            }
            if(null != agent.getProfession()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PROFESSION, agent.getProfession());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PROFESSION, "");
            }
            if(null != agent.getResidencePhone()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.RESIDENTPHONE, agent.getResidencePhone());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.RESIDENTPHONE, "");
            }
            if(null != agent.getResidentialStatus()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.RESIDENTSTATUS, agent.getResidentialStatus());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.RESIDENTSTATUS, "");
            }
            if(null != agent.getResidentType()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.RESIDENT_TYPE, agent.getResidentType());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.RESIDENT_TYPE, "");
            }
            if(null != agent.getSpouseName()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.SPOUSE_NAME, agent.getSpouseName());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.SPOUSE_NAME, "");
            }
            if(null != agent.getState()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.STATE, agent.getState());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.STATE, "");
            }
            if(null != agent.getThirdPartyID()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.ThirdPartyID, agent.getThirdPartyID());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.ThirdPartyID, "");
            } if(null != agent.getTime()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.TIME, String.valueOf(agent.getTime()));
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.TIME, "");
            }
            if(null != agent.getUserType()) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.USER_TYPE, agent.getUserType());
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.USER_TYPE, "");
            }


        }else{
            clearAnalyticsProperty(context);
        }
        UtilityMethods.saveBooleanInPref(context, Constants.isUserPropertySet, true);

    }

    public static void clearAnalyticsProperty(Context context){
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.USER_ID, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.EMAIL_ID, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.CITY_NAME, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.USER_NAME, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.USER_NAME, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.APP_VERSION, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.OS_TYPE, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.USER_PHONE, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.USER_ADDRESS, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.COUNTRY_NAME, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.COUNTRY_STDCODE, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.ADHAAR_NO, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.COMPANY, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.ADDRESS_PROOF_TYPE, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.ADDRESS_PROOF_URL, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.ADHAR_URL, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.CHILDREN, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.DESIGNATION, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.DESIGNATION, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.DOB, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.FATHERNAME, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.FAX, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.FCM_KEY, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.FORM_60, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.GENDER, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.IMAGE_URL, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.KYC_COMPANY, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.LATITUDE, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.LONGITUDE, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.LOGIN_TYPE, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.MARITAL_STATUS, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.MemorandumOfArticle, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.MPIN, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.NATIONALITY, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.NRI_DECALARATION, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.OFFICE_ADDRESS, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.OFFICE_PHONE, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.OTHER_EMAIL, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.OTHER_PHONE, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PAN_NO, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PAN_URL, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PASSPORT, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PASSWORD, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PASSWORD, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PERMANENT_ADDRESS, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PERMANENT_CITY, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PERMANENT_PIN, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PERMANENT_STATE, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.PROFESSION, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.RESIDENTPHONE, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.RESIDENTSTATUS, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.RESIDENT_TYPE, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.SPOUSE_NAME, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.STATE, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.ThirdPartyID, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.TIME, "NA");
        mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.USER_TYPE, "NA");
        UtilityMethods.saveBooleanInPref(context, Constants.isUserPropertySet, false);
    }



  /*  public static void setAnalyticsPropertyFromSplash(Context context){

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
//        if(null != agent){
            if(null != UtilityMethods.getStringInPref(context,Constants.PREF_AGENT_ID,"")) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.AGENT_ID, UtilityMethods.getStringInPref(context,Constants.PREF_AGENT_ID,""));
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.AGENT_ID, "");
            }
            if(null != UtilityMethods.getStringInPref(context,Constants.AGENT_EMAIL,"")) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.EMAIL_ID, UtilityMethods.getStringInPref(context,Constants.AGENT_EMAIL,""));
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.EMAIL_ID, "");
            }
            if(null != UtilityMethods.getStringInPref(context,Constants.AGENT_CITY_ID,"")) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.CITY_ID, UtilityMethods.getStringInPref(context,Constants.AGENT_CITY_ID,""));
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.CITY_ID, "");
            }
            if(null != UtilityMethods.getStringInPref(context,Constants.AGENT_CITY_NAME,"")) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.CITY_NAME, UtilityMethods.getStringInPref(context,Constants.AGENT_CITY_NAME,""));
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.CITY_NAME, "");
            }
            if(null != UtilityMethods.getStringInPref(context,Constants.AGENT_SUBERVISER,"")) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.SUPERVISOR_ID, UtilityMethods.getStringInPref(context,Constants.AGENT_SUBERVISER,""));
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.SUPERVISOR_ID, "");
            }
            if(null != UtilityMethods.getStringInPref(context,Constants.AGENT_SUBERVISER_NAME,"")) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.SUPERVISOR_NAME, UtilityMethods.getStringInPref(context,Constants.AGENT_SUBERVISER_NAME,""));
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.SUPERVISOR_NAME, "");
            }
            if(null != UtilityMethods.getStringInPref(context,Constants.AGENT_DEVICE_IMEI,"")) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.IMEI_NO, UtilityMethods.getStringInPref(context,Constants.AGENT_DEVICE_IMEI,""));
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.IMEI_NO, "");
            }
            if(null != UtilityMethods.getStringInPref(context,Constants.AGENT_ROLE,"")) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.ROLE, UtilityMethods.getStringInPref(context,Constants.AGENT_ROLE,""));
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.ROLE, "");
            }
            if(null != UtilityMethods.getStringInPref(context,Constants.AGENT_DISPLAY_ROLE,"")) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.DISPLAY_ROLE, UtilityMethods.getStringInPref(context,Constants.AGENT_DISPLAY_ROLE,""));
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.DISPLAY_ROLE, "");
            }
            if(null != UtilityMethods.getStringInPref(context,Constants.AGENT_EMPLOYEE_TYPE,"")) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.EMP_TYPE, UtilityMethods.getStringInPref(context,Constants.AGENT_EMPLOYEE_TYPE,""));
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.EMP_TYPE, "");
            }
            if(null != UtilityMethods.getStringInPref(context,Constants.AGENT_NAME,"")) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.AGENT_NAME, UtilityMethods.getStringInPref(context,Constants.AGENT_NAME,""));
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.AGENT_NAME, "");
            }
            if(null != UtilityMethods.getStringInPref(context,Constants.AGENT_CITY_NAME,"")) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.OS_TYPE, UtilityMethods.getStringInPref(context,Constants.AGENT_CITY_NAME,""));
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.OS_TYPE, "");
            }
            if(null != UtilityMethods.getStringInPref(context,Constants.AGENT_MOBILE_NO_PERSONAL,"")) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.AGENT_PHONE, UtilityMethods.getStringInPref(context,Constants.AGENT_MOBILE_NO_PERSONAL,""));
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.AGENT_PHONE, "");
            }
            if(null != UtilityMethods.getStringInPref(context,Constants.ServerConfigConstants.APP_VERSION,"")) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.APP_VERSION, UtilityMethods.getStringInPref(context,Constants.ServerConfigConstants.APP_VERSION,""));
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.APP_VERSION, "");
            }
            if(null != UtilityMethods.getStringInPref(context,Constants.AGENT_STATUS,"")) {
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.AGENT_STATUS, UtilityMethods.getStringInPref(context,Constants.AGENT_STATUS,""));
            }else{
                mFirebaseAnalytics.setUserProperty(Constants.AnalyticsProperty.AGENT_STATUS, "");
            }
        *//*else{
            clearAnalyticsProperty(context);
        }*//*
        UtilityMethods.saveBooleanInPref(context, Constants.isAgentPropertySet, true);

    }*/
}
