package com.clicbrics.consumer.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.CommonResponse;
import com.buy.housing.backend.userEndPoint.model.SearchCriteria;
import com.buy.housing.backend.userEndPoint.model.SearchCriteriaCollection;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.wrapper.SaveSearchWrapper;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.clicbrics.consumer.utils.UtilityMethods.getLongInPref;

public class SaveSearchActivity extends BaseActivity{

    private static final String TAG = SaveSearchActivity.class.getSimpleName();

    private SwipeMenuListView mListView;
    TextView mSearchCountView;
    LinearLayout mEmptyView;
    CoordinatorLayout mRootLayout;
    private UserEndPoint mUserEndPoint;
    private ArrayList<SaveSearchWrapper> searchCriteriaList;

    private ListDataAdapter mListDataAdapter;
    private String detailString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_search);
        UtilityMethods.setStatusBarColor(this,R.color.light_white);
        /*FrameLayout containerParent = (FrameLayout) findViewById(R.id.container);
        getLayoutInflater().inflate(R.layout.activity_save_search, containerParent);

        ImageView drawerButton = (ImageView) findViewById(R.id.drawer_button);
        drawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        addBackButton();
        buildUserWebService();
        initControls();
    }

    private void buildUserWebService() {
        mUserEndPoint= EndPointBuilder.getUserEndPoint();
    }

    private void initControls() {

        mListView=(SwipeMenuListView)findViewById(R.id.listView);
        mListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        mRootLayout = (CoordinatorLayout) findViewById(R.id.id_activity_saved_search);

        mSearchCountView = (TextView) findViewById(R.id.id_saved_search_count);
        mEmptyView = (LinearLayout) findViewById(R.id.id_empty_view);
        // mListView.setCloseInterpolator(new BounceInterpolator());


        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete_white_24dp);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                showSavedSearchList(position,detailString);
            }
        });

        // set creator
        mListView.setMenuCreator(creator);

        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                switch (index) {
                    case 0:
                        if(searchCriteriaList != null && searchCriteriaList.size() > 0) {
                            SaveSearchWrapper saveSearchWrapper = searchCriteriaList.get(position);
                            deleteSaveProject(position, saveSearchWrapper.id, saveSearchWrapper.name);
                            UtilityMethods.saveBooleanInPref(SaveSearchActivity.this,Constants.MORE_FRAGMENT_UPDATE,true);
                        }
                        break;
                }
                return true;
            }
        });


        //mListView

        mListView.setOnMenuStateChangeListener(new SwipeMenuListView.OnMenuStateChangeListener() {
            @Override
            public void onMenuOpen(int position) {

            }

            @Override
            public void onMenuClose(int position) {

            }
        });

        mListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {

            }

            @Override
            public void onSwipeEnd(int position) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * get Saved Search result from server
         */
        getSavedSearchList();


        /**
         * get saved search result from local list
         */
        /*searchCriteriaList = UtilityMethods.getSaveSearchList(this);
        if(searchCriteriaList != null && searchCriteriaList.size() > 0) {
            mSearchCountView.setText(searchCriteriaList.size() + " Searches");
            mListDataAdapter = new ListDataAdapter();
            mListView.setAdapter(mListDataAdapter);
        }else{
            mListView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
            mSearchCountView.setVisibility(View.GONE);
        }*/
    }

    private void getSavedSearchList(){
        if (!UtilityMethods.isInternetConnected(this)) {
            showSnackBar(getResources().getString(R.string.no_internet_connection));
            return;
        }
        showProgressBar();
        final long userId = UtilityMethods.getLongInPref(mActivity, Constants.ServerConstants.USER_ID, 0);
        new Thread(new Runnable() {
            String errorMsg = "";
            @Override
            public void run() {
                try {
                    SearchCriteriaCollection getSearchCriteriaList = mUserEndPoint.getSearchCriteriaList(userId).setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    if(getSearchCriteriaList != null){
                        if(getSearchCriteriaList.getItems() != null && !getSearchCriteriaList.getItems().isEmpty()){
                            final List<SearchCriteria> searchCriterias = getSearchCriteriaList.getItems();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(searchCriterias != null && searchCriterias.size() > 0) {
                                        if(searchCriteriaList == null){
                                            searchCriteriaList = new ArrayList<SaveSearchWrapper>();
                                        }else{
                                            searchCriteriaList.clear();
                                        }
                                        for (int i = 0; i < searchCriterias.size(); i++) {
                                            SearchCriteria searchCriteria =  searchCriterias.get(i);
                                            if(searchCriteria != null) {
                                                SaveSearchWrapper saveSearchWrapper = UtilityMethods.getSaveSearchWrapper(searchCriteria);
                                                searchCriteriaList.add(saveSearchWrapper);
                                            }
                                        }
                                        if ((searchCriteriaList != null) && (!searchCriteriaList.isEmpty())) {
                                            Collections.reverse(searchCriteriaList);
                                        }
                                        mSearchCountView.setVisibility(View.VISIBLE);
                                        mSearchCountView.setText(searchCriterias.size() + " Searches");
                                        mListDataAdapter = new ListDataAdapter();
                                        mListView.setAdapter(mListDataAdapter);
                                    }else{
                                        mListView.setVisibility(View.GONE);
                                        mEmptyView.setVisibility(View.VISIBLE);
                                        mSearchCountView.setVisibility(View.GONE);
                                    }
                                    /*if(mListDataAdapter != null){
                                        mListDataAdapter.notifyDataSetChanged();
                                    }*/
                                    dismissProgressBar();
                                }
                            });
                        }else{
                            errorMsg = "No saved search found";
                        }
                    }else{
                        errorMsg = "No saved search found";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = "Error getting saved search\nPlease retry.";
                }
                if(!TextUtils.isEmpty(errorMsg)){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressBar();
                            if(errorMsg.equalsIgnoreCase("No saved search found")) {
                                mListView.setVisibility(View.GONE);
                                mEmptyView.setVisibility(View.VISIBLE);
                                mSearchCountView.setVisibility(View.GONE);
                            }else {
                                showSnackBar(errorMsg);
                            }
                        }
                    });
                }
            }
        }).start();
    }

    class ListDataAdapter extends BaseAdapter {

        ViewHolder holder;
        @Override
        public int getCount() {
            if(searchCriteriaList != null) {
                return searchCriteriaList.size();
            }else{
                return 0;
            }
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if(convertView==null){

                holder=new ViewHolder();

                convertView=getLayoutInflater().inflate(R.layout.saved_search_list_item,null);

                holder.mSearchName=(TextView)convertView.findViewById(R.id.id_saved_search_name);
                holder.mFilterCritera=(TextView)convertView.findViewById(R.id.id_filter_details);
                holder.time=(TextView)convertView.findViewById(R.id.id_time);
                holder.icon = (ImageView) convertView.findViewById(R.id.id_icon);
                holder.deleteIcon = (ImageView) convertView.findViewById(R.id.id_delete_icon);

                convertView.setTag(holder);
            }else {

                holder= (ViewHolder) convertView.getTag();
            }

            if(searchCriteriaList != null && searchCriteriaList.size() > 0) {
                final SaveSearchWrapper saveSearchWrapper = searchCriteriaList.get(position);
                holder.mSearchName.setText(saveSearchWrapper.name); //.getName());
                detailString = "";
                if (!saveSearchWrapper.filterApplied) {
                    if ((saveSearchWrapper.builderName != null) && (!saveSearchWrapper.builderName.isEmpty())) {
                        detailString = saveSearchWrapper.builderName.toString();
                        if ((saveSearchWrapper.cityName != null) && (!saveSearchWrapper.cityName.isEmpty())) {
                            if (!TextUtils.isEmpty(detailString)) {
                                detailString += ", ";
                            }
                            detailString += saveSearchWrapper.cityName.toString();
                        }
                        holder.mFilterCritera.setText(detailString+"");
                    }else {
                        holder.mFilterCritera.setText(getResources().getString(R.string.no_filter_applied));
                        detailString = getResources().getString(R.string.no_filter_applied);
                    }
                } else {
                    if ((saveSearchWrapper.builderName != null) && (!saveSearchWrapper.builderName.isEmpty())) {
                        detailString += saveSearchWrapper.builderName.toString();
                        if ((saveSearchWrapper.cityName != null) && (!saveSearchWrapper.cityName.isEmpty())) {
                            if (!TextUtils.isEmpty(detailString)) {
                                detailString += ", ";
                            }
                            detailString += saveSearchWrapper.cityName.toString();
                        }
                    }
                    if ((saveSearchWrapper.bedList != null) && (!saveSearchWrapper.bedList.isEmpty())) {
                        if (!TextUtils.isEmpty(detailString)) {
                            detailString += " , ";
                        }
                        detailString += saveSearchWrapper.bedList.toString()
                                .replace("[", "").replace("]", "").replace("4", "3+") + " BHK";
                    }
                    Log.d("TAG", "detailstring->" + detailString);
                    if ((saveSearchWrapper.minCost != 0) && (saveSearchWrapper.maxCost != 0)) {
                        if (!TextUtils.isEmpty(detailString)) {
                            detailString += " , ";
                        }
                        detailString += (UtilityMethods.getPriceWord(saveSearchWrapper.minCost) +
                                " - " + UtilityMethods.getPriceWord((int) saveSearchWrapper.maxCost));

                    } else if (saveSearchWrapper.minCost != 0) {
                        if (!TextUtils.isEmpty(detailString)) {
                            detailString += " , ";
                        }
                        detailString += "greater than " + UtilityMethods.getPriceWord(saveSearchWrapper.minCost);
                    } else if ((saveSearchWrapper.maxCost) != 0) {
                        if (!TextUtils.isEmpty(detailString)) {
                            detailString += " , ";
                        }
                        detailString += "less than " + UtilityMethods.getPriceWord(saveSearchWrapper.maxCost);
                    }
                    Log.d("TAG", "detailstring->" + detailString);
                    if ((saveSearchWrapper.propertyTypeEnum != null) &&
                            (saveSearchWrapper.propertyTypeEnum.size() != 0)) {

                        if (!TextUtils.isEmpty(detailString)) {
                            detailString += " , ";
                        }
                        detailString += (searchCriteriaList.get(position).propertyTypeEnum.toString().replace("[", "").replace("]", "")
                                .replace("IndependentHouseVilla", "Villa").replace("IndependentHouse", "Villa")
                                .replace("Land", "Plot")
                                .replace("Shop", "Commercial")) + "";
                    }

                    if ((searchCriteriaList.get(position).propertyStatusList != null) &&
                            (saveSearchWrapper.propertyStatusList.size() != 0)) {
                        if (!TextUtils.isEmpty(detailString)) {
                            detailString += " , ";
                        }
                        detailString += (saveSearchWrapper.propertyStatusList.toString()
                                .replace("[", "").replace("]", ""))
                                .replace(Constants.AppConstants.PropertyStatus.NotStarted.toString(), "New Launch")
                                .replace(Constants.AppConstants.PropertyStatus.UpComing.toString(), "Upcoming")
                                .replace(Constants.AppConstants.PropertyStatus.InProgress.toString(), "Under Construction")
                                .replace(Constants.AppConstants.PropertyStatus.ReadyToMove.toString(), "Ready to Move");
                    }

                    Log.d("TAG", "detailstring->" + detailString);
                    if (TextUtils.isEmpty(detailString)) {
                        detailString = getResources().getString(R.string.no_filter_applied);
                    }
                    Log.d("TAG", "detailstring->" + detailString);
                    holder.mFilterCritera.setText(detailString + "");
                }

                if (saveSearchWrapper.time != null) {
                    Long time = saveSearchWrapper.time;
                    SimpleDateFormat dateAndTime = new SimpleDateFormat("hh:mm a, dd MMM yyyy");
                    String timeString = dateAndTime.format(new Date(time));
                    holder.time.setText(timeString + "");
                }

                holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteSaveProject(position, saveSearchWrapper.id, saveSearchWrapper.name);
                        UtilityMethods.saveBooleanInPref(SaveSearchActivity.this,Constants.MORE_FRAGMENT_UPDATE,true);
                    }
                });
            }
            return convertView;
        }

        class ViewHolder {
            TextView mSearchName,mFilterCritera,time;
            ImageView icon,deleteIcon;
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private void showSavedSearchList(int position, String saveSearchDetails){
        try {
            if (!UtilityMethods.isInternetConnected(this)) {
                UtilityMethods.showErrorSnackBar(mRootLayout, getResources().getString(R.string.network_error_msg),
                        Snackbar.LENGTH_LONG);
                return;
            }

            SaveSearchWrapper saveSearchWrapper = searchCriteriaList.get(position);
            Intent intent = new Intent(this, HomeScreen.class);
            intent.putExtra(Constants.SavedSearchConstants.IS_SAVE_SEARCH,true);
            intent.putExtra(Constants.SavedSearchConstants.LATITUDE, String.valueOf(saveSearchWrapper.latitude));
            intent.putExtra(Constants.SavedSearchConstants.LONGITUDE, String.valueOf(saveSearchWrapper.longitude));
            intent.putExtra(Constants.SavedSearchConstants.ADDRESS, saveSearchWrapper.name);
            intent.putExtra(Constants.SavedSearchConstants.ADDITIONAL_ADDRESS, saveSearchDetails);

            if( (saveSearchWrapper.builderId == null || saveSearchWrapper.builderId == -1)
                    && (saveSearchWrapper.latitude == 0.0 || saveSearchWrapper.longitude == 0.0)){
                intent.putExtra(Constants.SavedSearchConstants.SEARCH_PROPERTY_BY_CITY, true);
            }
            else if(saveSearchWrapper.builderId != null && saveSearchWrapper.builderId != -1
                    && !TextUtils.isEmpty(saveSearchWrapper.builderName)){
                intent.putExtra(Constants.SavedSearchConstants.BUILDER_ID, saveSearchWrapper.builderId);
                intent.putExtra(Constants.SavedSearchConstants.BUILDER_NAME, saveSearchWrapper.builderName);
            }
            if (saveSearchWrapper.cityId != null) {
                intent.putExtra(Constants.SavedSearchConstants.CITY_ID, saveSearchWrapper.cityId);
            }
            if (saveSearchWrapper.cityName != null
                    && !saveSearchWrapper.cityName.isEmpty()) {
                intent.putExtra(Constants.SavedSearchConstants.CITY_NAME, saveSearchWrapper.cityName);
            }
            if(!TextUtils.isEmpty(saveSearchWrapper.name)){
                intent.putExtra(Constants.SavedSearchConstants.SEARCH_NAME,saveSearchWrapper.name);
            }
            Gson gson = new Gson();
            String json = gson.toJson(saveSearchWrapper);
            intent.putExtra(Constants.SavedSearchConstants.SEARCH_WRAPPER, json);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //startActivity(intent);
            setResult(RESULT_OK,intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* *
    * Method responsible for getting save project
    * */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void deleteSaveProject(final int position, final long toDelete_ID, final String name) {
        if (!UtilityMethods.isInternetConnected(mActivity)) {
            Snackbar snackbar = Snackbar
                    .make(mRootLayout, getResources().getString(R.string.network_error_msg), Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.uber_red));
            snackbar.show();
            return;
        }
        showProgressBar();
        //findViewById(R.id.indeterminate_progress).setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String errorMsg = "";
                try {

                    SearchCriteria searchCriteria = new SearchCriteria();
                    searchCriteria.setId(toDelete_ID);
                    searchCriteria.setName(name);

                    final CommonResponse commonResponse = mUserEndPoint
                            .deleteSearchCriteria(getLongInPref(mActivity, Constants.ServerConstants.USER_ID, 0l),
                                    searchCriteria)
                            .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();

                    if ((commonResponse != null) && (commonResponse.getStatus())) {
                        Log.d(TAG, "commonResponse->" + commonResponse.getStatus() + " " + commonResponse.getErrorMessage()
                                + commonResponse.getErrorMessage());

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                dismissProgressBar();
                                UtilityMethods.removeSaveSearchItem(SaveSearchActivity.this, name);
                                /*savedSearchesAdapter.removeItem(position);
                                if(savedSearchesAdapter.getItemCount() == 0){
                                    searchCount.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.GONE);
                                    emptyView.setVisibility(View.VISIBLE);
                                }*/
                                if(searchCriteriaList != null && searchCriteriaList.size() > 0) {
                                    SaveSearchWrapper saveSearchWrapper = searchCriteriaList.get(position);
                                    searchCriteriaList.remove(saveSearchWrapper);
                                    mListDataAdapter.notifyDataSetChanged();
                                    UtilityMethods.showSnackBar(mRootLayout, "Saved search deleted successfully!", Snackbar.LENGTH_LONG);
                                    if (mListDataAdapter != null) {
                                        mSearchCountView.setText(mListDataAdapter.getCount() + " Searches");
                                        if (mListDataAdapter.getCount() == 0) {
                                            mSearchCountView.setVisibility(View.GONE);
                                            mListView.setVisibility(View.GONE);
                                            mEmptyView.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }

                            }
                        });

                    } else {
                        if(commonResponse != null && commonResponse.getErrorMessage() != null) {
                            errorMsg = commonResponse.getErrorMessage();
                        }else{
                            errorMsg = getResources().getString(R.string.saved_search_delet_err_msg);
                        }
                    }
                }
                catch (UnknownHostException e){
                    e.printStackTrace();
                    errorMsg = getResources().getString(R.string.network_error_msg);
                }
                catch (IOException e) {
                    errorMsg = getResources().getString(R.string.saved_search_delet_err_msg);
//                    AnalyticsTrackers.trackException(e);
                    e.printStackTrace();

                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgressBar();
                        //findViewById(R.id.indeterminate_progress).setVisibility(View.GONE);
                    }
                });
                if (!TextUtils.isEmpty(errorMsg)) {
                    final String errmsg = errorMsg;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(errmsg.equalsIgnoreCase(getResources().getString(R.string.network_error_msg))){
                                UtilityMethods.showErrorSnackBar(mRootLayout, errmsg, Snackbar.LENGTH_LONG);
                            }else{
                                UtilityMethods.showSnackBar(mRootLayout, errmsg, Snackbar.LENGTH_LONG);
                            }
                        }
                    });
                }
            }
        }).start();
    }


    long onStartTime;
    @Override
    protected void onStart() {
        super.onStart();
        onStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();
        TrackAnalytics.trackEvent("SavedSearchActivity", Constants.AppConstants.HOLD_TIME,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(), onStartTime), this);
    }

    private void showSnackBar(String errorMsg){
        final Snackbar snackbar = Snackbar
                .make(mRootLayout, errorMsg, Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("TRY AGAIN", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getSavedSearchList();
                        snackbar.dismiss();
                    }
                });

        snackbar.setActionTextColor(Color.WHITE);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.uber_red));
        snackbar.show();
    }
}
