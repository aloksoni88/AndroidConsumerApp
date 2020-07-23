package com.clicbrics.consumer.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buy.housing.backend.userEndPoint.model.Document;
import com.buy.housing.backend.userEndPoint.model.User;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.MyPropertyDetails;
import com.clicbrics.consumer.adapter.PropertyDocumentListAdapter;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by Alok on 02-02-2017.
 */

@SuppressWarnings("deprecation")
public class PropertyDocumentFragment extends Fragment implements PropertyDocumentListAdapter.PermissionCheckListener{

    private final String TAG = PropertyDocumentFragment.class.getSimpleName();
    TextView emptyListView;
    LinearLayout mParentLayout;
    private final int STORAGE_PERMISSION = 105;
    PropertyDocumentListAdapter documentListAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_property_document, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            emptyListView = (TextView) view.findViewById(R.id.empty_view_property_document);
            mParentLayout = (LinearLayout) view.findViewById(R.id.id_document_fragment);
            RecyclerView mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view_property_document);
            mRecyclerView.setHasFixedSize(true);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            List<Document> documentList = ((HousingApplication)getActivity().getApplicationContext()).getmDocumentList();
            List<User> customerList = ((HousingApplication)getActivity().getApplicationContext()).getCustomerList();

            List<Document> mDocumentList = new ArrayList<>();
            String applicationFormName = "", applicationFormURL = "";
            boolean isDocAvail = false;
            if(getActivity() != null){
                if(getActivity().getIntent().hasExtra("ApplicationFormName")
                        && !TextUtils.isEmpty(getActivity().getIntent().getStringExtra("ApplicationFormName"))){
                    applicationFormName = getActivity().getIntent().getStringExtra("ApplicationFormName");
                }
                if(getActivity().getIntent().hasExtra("ApplicationFormURL")
                        && !TextUtils.isEmpty(getActivity().getIntent().getStringExtra("ApplicationFormURL"))){
                    applicationFormURL = getActivity().getIntent().getStringExtra("ApplicationFormURL");
                }
                if(!TextUtils.isEmpty(applicationFormName) && !TextUtils.isEmpty(applicationFormURL)) {
                    Document document = new Document();
                    document.setName(applicationFormName);
                    document.setUrl(applicationFormURL);
                    mDocumentList.add(0, document);
                    isDocAvail = true;
                }
            }
            if(customerList != null && !customerList.isEmpty()){
                for(int i=0; i<customerList.size(); i++){
                    User user = customerList.get(i);
                    if(user != null){
                        if(!TextUtils.isEmpty(user.getAddressProofUrl())){
                            Document document = new Document();
                            if(!TextUtils.isEmpty(user.getAddressProofType())){
                                document.setName(user.getAddressProofType());
                                document.setRemark("Address Proof");
                            }else if(!TextUtils.isEmpty(user.getAddress())){
                                document.setName(user.getAddress());
                                document.setRemark("Address Proof");
                            }else{
                                document.setName("Address Proof");
                            }
                            document.setUrl(user.getAddressProofUrl());
                            mDocumentList.add(document);
                            isDocAvail = true;
                        }
                        if(!TextUtils.isEmpty(user.getAdhaarUrl())){
                            Document document = new Document();
                            if(!TextUtils.isEmpty(user.getAdhaarNo())){
                                document.setName(user.getAdhaarNo());
                                document.setRemark("Adhaar Card");
                            }else{
                                document.setName("Adhaar Card No");
                            }
                            document.setUrl(user.getAdhaarUrl());
                            mDocumentList.add(document);
                            isDocAvail = true;
                        }
                        if(!TextUtils.isEmpty(user.getPanUrl())){
                            Document document = new Document();
                            if(!TextUtils.isEmpty(user.getPanNo())){
                                document.setName(user.getPanNo());
                                document.setRemark("Pan Card");
                            }else{
                                document.setName("Pan Card");
                            }
                            document.setUrl(user.getPanUrl());
                            mDocumentList.add(document);
                            isDocAvail = true;
                        }
                        if(!TextUtils.isEmpty(user.getForm60())){
                            Document document = new Document();
                            document.setRemark("Form60");
                            document.setUrl(user.getForm60());
                            mDocumentList.add(document);
                            isDocAvail = true;
                        }
                        if(!TextUtils.isEmpty(user.getKycCompany())){
                            Document document = new Document();
                            document.setRemark("KYC Company");
                            document.setUrl(user.getKycCompany());
                            mDocumentList.add(document);
                            isDocAvail = true;
                        }
                        if(!TextUtils.isEmpty(user.getMemorandumOfArticle())){
                            Document document = new Document();
                            document.setRemark("Memorandum of Article");
                            document.setUrl(user.getMemorandumOfArticle());
                            mDocumentList.add(document);
                            isDocAvail = true;
                        }
                        if(!TextUtils.isEmpty(user.getNriDeclarationForm())){
                            Document document = new Document();
                            document.setRemark("NRI Declaration Form");
                            document.setUrl(user.getNriDeclarationForm());
                            mDocumentList.add(document);
                            isDocAvail = true;
                        }
                    }
                }
            }
            if(isDocAvail){
                mDocumentList.addAll(documentList);
                documentListAdapter = new PropertyDocumentListAdapter(getActivity(),getContext(), mDocumentList);
                mRecyclerView.setAdapter(documentListAdapter);
            }else{
                mDocumentList.addAll(documentList);
                if (mDocumentList != null && mDocumentList.size() > 0) {
                    for (int i = 0; i < mDocumentList.size(); i++) {
                        if (mDocumentList.get(i) != null && !mDocumentList.get(i).isEmpty()
                                && mDocumentList.get(i).getUrl() != null && !mDocumentList.get(i).getUrl().isEmpty()
                                && mDocumentList.get(i).getName() != null && !mDocumentList.get(i).getName().isEmpty()) {
                            isDocAvail = true;
                            break;
                        }
                    }
                    if (isDocAvail) {
                        documentListAdapter = new PropertyDocumentListAdapter(getActivity(),getContext(), mDocumentList);
                        mRecyclerView.setAdapter(documentListAdapter);
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        emptyListView.setVisibility(View.VISIBLE);
                    }
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    emptyListView.setVisibility(View.VISIBLE);
                }
            }

            /*String applicationFormName = "", applicationFormURL = "";
            if(getActivity() != null && getActivity().getIntent().hasExtra("ApplicationFormName")
                    && getActivity().getIntent().getStringExtra("ApplicationFormName") != null){
                applicationFormName = getActivity().getIntent().getStringExtra("ApplicationFormName");
            }
            if(getActivity() != null && getActivity().getIntent().hasExtra("ApplicationFormURL")
                    && getActivity().getIntent().getStringExtra("ApplicationFormURL") != null){
                applicationFormURL = getActivity().getIntent().getStringExtra("ApplicationFormURL");
            }
            if(!TextUtils.isEmpty(applicationFormName) && !TextUtils.isEmpty(applicationFormURL)){
                boolean isExist = false;
                if(mDocumentList.size() > 0){
                    for(int i=0; i< mDocumentList.size() ; i++){
                        if(applicationFormURL.equalsIgnoreCase(mDocumentList.get(i).getUrl())){
                            isExist = true;
                            break;
                        }
                    }
                    if(!isExist){
                        Document document = new Document();
                        document.setName(applicationFormName);
                        document.setUrl(applicationFormURL);
                        mDocumentList.add(document);
                    }
                }
                documentListAdapter = new PropertyDocumentListAdapter(getActivity(),getContext(), mDocumentList);
                mRecyclerView.setAdapter(documentListAdapter);
                //checkPermission();
            }else {
                boolean isDocAvail = false;
                if (mDocumentList != null && mDocumentList.size() > 0) {
                    for (int i = 0; i < mDocumentList.size(); i++) {
                        if (mDocumentList.get(i) != null && !mDocumentList.get(i).isEmpty()
                                && mDocumentList.get(i).getUrl() != null && !mDocumentList.get(i).getUrl().isEmpty()
                                && mDocumentList.get(i).getName() != null && !mDocumentList.get(i).getName().isEmpty()) {
                            isDocAvail = true;
                            break;
                        }
                    }
                    if (isDocAvail) {
                        documentListAdapter = new PropertyDocumentListAdapter(getActivity(),getContext(), mDocumentList);
                        mRecyclerView.setAdapter(documentListAdapter);
                        //checkPermission();
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        emptyListView.setVisibility(View.VISIBLE);
                    }
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    emptyListView.setVisibility(View.VISIBLE);
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            emptyListView = (TextView) view.findViewById(R.id.empty_view_property_document);
            emptyListView.setVisibility(View.VISIBLE);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResume() {
        super.onResume();
        ((MyPropertyDetails)getActivity()).mToolbarText.setText(getResources().getString(R.string.documents));
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((MyPropertyDetails)getActivity()).mToolbarLayout.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorAccent));
        }else {
            ((MyPropertyDetails)getActivity()).mToolbarLayout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_red));
        }*/
    }

    private void checkPermission(){
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (!UtilityMethods.selfPermissionGranted(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},getActivity())) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION);
            }
        }else{

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case STORAGE_PERMISSION:{
                boolean isPermissionGranted = false;
                for( int i = 0; i < permissions.length; i++ ) {
                    if( grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                        isPermissionGranted = true;
                    } else if( grantResults[i] == PackageManager.PERMISSION_DENIED ) {
                        isPermissionGranted = false;
                        UtilityMethods.showErrorSnackBar(mParentLayout,getResources().getString(R.string.storage_permission_denied), Snackbar.LENGTH_SHORT);
                    }
                }
                if(isPermissionGranted){
                    if(documentListAdapter != null){
                        documentListAdapter.downloadDocTask.execute(fileURL);
                    }
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    long onStartTime;
    @Override
    public void onStart() {
        super.onStart();
        onStartTime = System.currentTimeMillis();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(getActivity() != null) {
            TrackAnalytics.trackEvent("PropertyDocumentScreen", Constants.AppConstants.HOLD_TIME,
                    UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(), onStartTime), getActivity());
        }
    }

    String fileURL;
    @Override
    public void checkStoragePermission(String url) {
        this.fileURL = url;
        checkPermission();
    }
}
