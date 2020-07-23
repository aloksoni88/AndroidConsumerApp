package com.clicbrics.consumer;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.multidex.MultiDex;

import com.buy.housing.backend.blogEndPoint.model.Blog;
import com.buy.housing.backend.decorEndPoint.model.Decor;
import com.buy.housing.backend.newsEndPoint.model.News;
import com.buy.housing.backend.propertyEndPoint.model.Bank;
import com.buy.housing.backend.propertyEndPoint.model.PaymentPlan;
import com.buy.housing.backend.propertyEndPoint.model.PropertyType;
import com.buy.housing.backend.userEndPoint.model.City;
import com.buy.housing.backend.userEndPoint.model.Document;
import com.buy.housing.backend.userEndPoint.model.PaymentDetail;
import com.buy.housing.backend.userEndPoint.model.User;
import com.clicbrics.consumer.interfaces.LoginSuccessCallback;
import com.clicbrics.consumer.interfaces.ZoomCallback;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.crashlytics.android.Crashlytics;
import com.facebook.login.LoginResult;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;
import io.fabric.sdk.android.Fabric;


///**
// * Created by Alok on 05-09-2016.
// */
//public class HousingApplication {
//
//    private Context mContext;
//
//    private HousingApplication() {
//
//    }
//
//    private static class InstanceHolder {
//        private static final HousingApplication mHousingApplication = new HousingApplication();
//    }
//
//    public static HousingApplication getInstance() {
//        return InstanceHolder.mHousingApplication;
//    }
//
//    public void initialize(Context context) {
//        this.mContext = context;
//    }
//
//    public Context getApplicationContext() {
//        return mContext;
//    }
//
//}

public class HousingApplication extends Application {
    public static LoginResult loginResult;
    //ArrayList<PlaceAutocompleteAdapter.PlaceAutocomplete> recentAddr = new ArrayList<PlaceAutocompleteAdapter.PlaceAutocomplete>();
    public static LoginSuccessCallback mLoginSuccessCallback;
    public static ZoomCallback mZoomCallback;
    List<PaymentDetail> mPaymentDetailList;
    List<PaymentPlan> mPaymentPlanList;
    List<Document> mDocumentList;
    List<PropertyType> mPropertyTypeList;
    List<User> customerList;
    List<Bank> mBankLoanList;
    List<Blog> blogList;
    List<News> newsList;
    List<Decor> decorList;
    ArrayList<LatLng> polygonLatLngList;
    private boolean isFromLogin;

    private Bitmap imageBitmap;
    private long projectId;

    private Bitmap layoutImageBitmap;
    private long propertyLayoutId;
    private boolean is2DPicture;

    private List<City> cityListEstimation;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=this;
        Fabric.with(this, new Crashlytics());
        new BuildConfigConstants(getApplicationContext()); // load buildconfig
    }
    private static HousingApplication mInstance;
    public static synchronized HousingApplication getInstance() {
        return mInstance;
    }


    /*public synchronized ArrayList<PlaceAutocompleteAdapter.PlaceAutocomplete> getRecent() {
        if(recentAddr.isEmpty()){
            readRecentAddr();
        }
        return recentAddr;
    }*/

    /*private void readRecentAddr(){
        ObjectInputStream inputStream = null;
        File file = new File(getFilesDir(), "recentAddr");
        try {
            inputStream = new ObjectInputStream(new FileInputStream(file));
            while (true) {
                PlaceAutocompleteAdapter.PlaceAutocomplete p = (PlaceAutocompleteAdapter.PlaceAutocomplete) inputStream.readObject();
                recentAddr.add(p);
            }
        } catch (EOFException eofException) {
            return ;
        } catch (ClassNotFoundException classNotFoundException) {
            System.err.println("Object creation failed.");
        } catch (IOException ioException) {
            System.err.println("Error opening file.");
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException ioException) {
                System.err.println("Error closing file.");
            }
        }
    }*/

    /*public synchronized void addPlace(PlaceAutocompleteAdapter.PlaceAutocomplete autocomplete) {
        boolean isExist = false;
        if(recentAddr != null){
            for(int i=0 ; i< recentAddr.size(); i++){
                if(recentAddr.get(i).getDescription().equals(autocomplete.getDescription())){
                    isExist = true;
                    break;
                }
            }
        }
		*//*if (!recentAddr.contains(autocomplete))
			recentAddr.add(autocomplete);*//*
        if(!isExist || recentAddr.isEmpty()){
            recentAddr.add(autocomplete);
        }
        while (recentAddr.size() > 10) {
            recentAddr.remove(recentAddr.get(recentAddr.size() - 1));
        }
        saveAddressInMemory();
    }*/
    /*private void saveAddressInMemory(){
        if(!recentAddr.isEmpty()){
            try {
                File file = new File(getFilesDir(), "recentAddr");
                if(file.exists()){
                    file.delete();
                }
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
                for(PlaceAutocompleteAdapter.PlaceAutocomplete placeAutocomplete: recentAddr){
                    objectOutputStream.writeObject(placeAutocomplete);
                }
                objectOutputStream.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }*/

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
    }

    public List<PaymentDetail> getPaymentDetailList(){
        if(mPaymentDetailList == null){
            mPaymentDetailList = new ArrayList<>();
        }
        return mPaymentDetailList;
    }

    public void setmPaymentDetailList(List<PaymentDetail> paymentDetailList){
        this.mPaymentDetailList = paymentDetailList;
    }

    public List<PaymentPlan> getPaymentPlanList(){
        if(mPaymentPlanList == null){
            mPaymentPlanList = new ArrayList<>();
        }
        return mPaymentPlanList;
    }

    public void setmPaymentPlanList(List<PaymentPlan> paymentPlanList){
        this.mPaymentPlanList = paymentPlanList;
    }

    public List<Document> getmDocumentList(){
        if(mDocumentList == null){
            mDocumentList = new ArrayList<>();
        }
        return mDocumentList;
    }

    public void setmDocumentList(List<Document> documentList){
        this.mDocumentList = documentList;
    }

    public List<User> getCustomerList() {
        if(customerList == null){
            customerList = new ArrayList<>();
        }
        return customerList;
    }

    public void setCustomerList(List<User> customerList) {
        this.customerList = customerList;
    }

    public List<PropertyType> getPropertyTypeList() {
        if(mPropertyTypeList == null){
            mPropertyTypeList = new ArrayList<>();
        }
        return mPropertyTypeList;
    }

    public List<City> getCityListEstimation() {
        return cityListEstimation;
    }

    public void setCityListEstimation(List<City> cityListEstimation) {
        this.cityListEstimation = cityListEstimation;
    }

    public void setPropertyTypeList(List<PropertyType> propertyTypeList) {
        this.mPropertyTypeList = propertyTypeList;
    }

    public List<Bank> getBankLoanList() {
        if(mBankLoanList == null){
            mBankLoanList = new ArrayList<>();
        }
        return mBankLoanList;
    }

    public void setBankLoanList(List<Bank> mBankLoanList) {
        this.mBankLoanList = mBankLoanList;
    }

    public void setProjectDetailImageBitmap(Bitmap bitmap, long projectId){
        imageBitmap = bitmap;
        this.projectId = projectId;
    }

    public Bitmap getProjectDetailImageBitmap(){
        return imageBitmap;
    }


    public long getProjectDetailId(){
        return projectId;
    }

    public void setLayoutImageBitmap(Bitmap bitmap, long propertyLayoutId, boolean is2DPicture){
        this.layoutImageBitmap = bitmap;
        this.propertyLayoutId = propertyLayoutId;
        this.is2DPicture = is2DPicture;
    }

    public Bitmap getLayoutImageBitmap(){
        return layoutImageBitmap;
    }

    public long getPropertyLayoutId(){
        return propertyLayoutId;
    }

    public boolean isIs2DPicture(){
        return is2DPicture;
    }

    public List<Blog> getBlogList() {
        if(blogList == null){
            blogList = new ArrayList<>();
        }
        return blogList;
    }

    public void setBlogList(List<Blog> blogList) {
        this.blogList = blogList;
    }

    public List<News> getNewsList() {
        if(newsList == null){
            newsList = new ArrayList<>();
        }
        return newsList;
    }

    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
    }

    public List<Decor> getDecorList() {
        if(decorList == null){
            decorList = new ArrayList<>();
        }
        return decorList;
    }

    public void setDecorList(List<Decor> decorList) {
        this.decorList = decorList;
    }

    public ArrayList<LatLng> getPolygonLatLngList() {
        return polygonLatLngList;
    }

    public void setPolygonLatLngList(ArrayList<LatLng> polygonLatLngList) {
        this.polygonLatLngList = polygonLatLngList;
    }

    public boolean isFromLogin() {
        return isFromLogin;
    }

    public void setFromLogin(boolean fromLogin) {
        isFromLogin = fromLogin;
    }
}