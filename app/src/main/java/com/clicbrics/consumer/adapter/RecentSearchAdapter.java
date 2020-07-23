package com.clicbrics.consumer.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.HomeScreen;
import com.clicbrics.consumer.view.activity.ProjectDetailsScreen;
import com.clicbrics.consumer.wrapper.RecentSearch;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * Created by root on 10/1/17.
 */
public class RecentSearchAdapter extends RecyclerView.Adapter<RecentSearchAdapter.RecentSearchViewHolder> {

    public interface RecentSearchInterface {
        void onRecentPlaceClick(ArrayList<RecentSearch> mRecentSearches, int position);
    }

    Activity mContext;
//    RecentSearchInterface mListener;
    ArrayList<RecentSearch> mRecentSearches;
    public RecentSearchAdapter(Activity context, ArrayList<RecentSearch> recentSearches) {
        mContext = context;
        mRecentSearches = recentSearches;
//        this.mListener = (RecentSearchInterface) mContext;
    }

    public class RecentSearchViewHolder extends RecyclerView.ViewHolder {
        public TextView primaryText, secondaryText, searchType;
        ImageView imageView;
        View line;
        public RecentSearchViewHolder(View itemView) {
            super(itemView);
            primaryText = (TextView) itemView.findViewById(R.id.address);
            secondaryText = (TextView) itemView.findViewById(R.id.address_detail);
            searchType = (TextView) itemView.findViewById(R.id.type);
            imageView = (ImageView) itemView.findViewById(R.id.powered_by_google);
            line = (View) itemView.findViewById(R.id.line);
            imageView.setVisibility(View.GONE);
            line.setVisibility(View.VISIBLE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new EventAnalyticsHelper().ItemClickEvent(mContext, Constants.AnaylticsClassName.SearchScreen,
                            mRecentSearches.get(getAdapterPosition()), Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.SelectRecentSearch.toString());
                    setItemOnclick(getAdapterPosition());
                }
            });
        }
    }

    @Override
    public RecentSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_list_new, parent, false);
        return new RecentSearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecentSearchViewHolder holder, final int position) {
        holder.primaryText.setText(mRecentSearches.get(position).primaryText);
        holder.secondaryText.setText(mRecentSearches.get(position).cityName);
        holder.searchType.setText(mRecentSearches.get(position).searchType);
        holder.imageView.setVisibility(View.GONE);
        holder.secondaryText.setVisibility(View.VISIBLE);
    }

    private void setItemOnclick(int adapterPosition) {
        if(mRecentSearches!=null && mRecentSearches.size() > 0 && mRecentSearches.get(adapterPosition) != null){
            String primartyText = "", secondaryText = "";
            if(mRecentSearches.get(adapterPosition).primaryText != null) {
                primartyText = mRecentSearches.get(adapterPosition).primaryText.toString();
            }
            if(mRecentSearches.get(adapterPosition).secondaryText != null) {
                secondaryText =  mRecentSearches.get(adapterPosition).secondaryText.toString();
            }
            long cityId = 0;
            String cityName = "";
            if(mRecentSearches.get(adapterPosition).cityId != 0) {
                cityId = mRecentSearches.get(adapterPosition).cityId;
            }else{
                cityId = UtilityMethods.getLongInPref(mContext,Constants.AppConstants.SAVED_CITY_ID,0);
            }
            if(!TextUtils.isEmpty(mRecentSearches.get(adapterPosition).cityName)) {
                cityName = mRecentSearches.get(adapterPosition).cityName;
            }else{
                cityName = UtilityMethods.getStringInPref(mContext,Constants.AppConstants.SAVED_CITY,"");
            }
            if(mRecentSearches.get(adapterPosition).searchType.equalsIgnoreCase(mContext.getResources().getString(R.string.locality))) {
                Intent intent = new Intent(mContext, HomeScreen.class);
                intent.putExtra("lat", String.valueOf(mRecentSearches.get(adapterPosition).latLng.latitude));
                intent.putExtra("lng", String.valueOf(mRecentSearches.get(adapterPosition).latLng.longitude));
                intent.putExtra("city_id",cityId);
                intent.putExtra("cityName",cityName);
                if(!TextUtils.isEmpty(primartyText)) {
                    intent.putExtra("address", primartyText);
                }
                if(!TextUtils.isEmpty(secondaryText)) {
                    intent.putExtra("additional_address", secondaryText);
                }
                intent.putExtra(Constants.IntentKeyConstants.PROPERTY_SEARCH_BY_LOCATION,true);
                mContext.setResult(RESULT_OK,intent);
                mContext.finish();
            }else if(mRecentSearches.get(adapterPosition).searchType.equalsIgnoreCase(mContext.getResources().getString(R.string.project))){
                Intent intent = new Intent(mContext, ProjectDetailsScreen.class);
                intent.putExtra("ISDirectCall",true);
                Log.i("SearchAdapter", "onClick: ======="+mRecentSearches.get(adapterPosition).projectId+":Project");
                intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID,mRecentSearches.get(adapterPosition).projectId);
                mContext.startActivity(intent);
                mContext.finish();
            }else{
                Intent intent = new Intent(mContext, HomeScreen.class);
                intent.putExtra("lat", String.valueOf(mRecentSearches.get(adapterPosition).latLng.latitude));
                intent.putExtra("lng", String.valueOf(mRecentSearches.get(adapterPosition).latLng.longitude));
                if(!TextUtils.isEmpty(primartyText)) {
                    intent.putExtra("address", primartyText);
                }
                if(!TextUtils.isEmpty(secondaryText)) {
                    intent.putExtra("additional_address", secondaryText);
                }
                intent.putExtra("builder_id",mRecentSearches.get(adapterPosition).builderId);
                intent.putExtra("builder_name",mRecentSearches.get(adapterPosition).builderName);
                intent.putExtra("city_id",cityId);
                intent.putExtra("cityName",cityName);
                intent.putExtra(Constants.IntentKeyConstants.PROPERTY_SEARCH_BY_DEVELOPER,true);
                mContext.setResult(RESULT_OK,intent);
                mContext.finish();
            }
            addToRecentSearches(primartyText, secondaryText,
                    mRecentSearches.get(adapterPosition).latLng,mRecentSearches.get(adapterPosition).searchType,
                    mRecentSearches.get(adapterPosition).projectId,mRecentSearches.get(adapterPosition).builderId,
                    mRecentSearches.get(adapterPosition).builderName,cityId, cityName);
        }
    }


    private void addToRecentSearches(String primaryText, String secondaryText, LatLng latLng, String searchType,
                                     long projectId, long builderId, String builderName, long cityId, String cityName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = prefs.getString("recent_search", "");
        Type type = new TypeToken<List<RecentSearch>>() {
        }.getType();
        List<RecentSearch> recentSearches = gson.fromJson(json, type);

        if (recentSearches == null || recentSearches.isEmpty()) {
            recentSearches = new ArrayList<>();
        }

        RecentSearch recentSearch = new RecentSearch(primaryText, secondaryText, latLng, System.currentTimeMillis(),searchType,
                projectId,builderId,builderName,cityId,cityName);
        for (int i = 0; i < recentSearches.size(); i++) {
            /*if ((recentSearches.get(i).latLng.equals(latLng)) ||
                    ((recentSearches.get(i).primaryText.equals(primaryText)) && (recentSearches.get(i).secondaryText.equals(secondaryText)))) {
                recentSearches.remove(i);
            }*/
            if (((recentSearches.get(i).primaryText.equals(primaryText)) && (recentSearches.get(i).secondaryText.equals(secondaryText)))) {
                recentSearches.remove(i);
            }
        }
        recentSearches.add(recentSearch);
        if (recentSearches.size() > 3) {
            recentSearches.remove(0);
        }

        SharedPreferences.Editor prefsEditor = prefs.edit();
        gson = new Gson();
        json = gson.toJson(recentSearches);
        prefsEditor.putString("recent_search", json);
        prefsEditor.commit();
    }
    
    @Override
    public int getItemCount() {
        return (mRecentSearches.size()>2) ? 3 : mRecentSearches.size() ;
    }
}
