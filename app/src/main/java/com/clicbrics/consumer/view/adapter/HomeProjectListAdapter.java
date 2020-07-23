package com.clicbrics.consumer.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.FavoritesActivity;
import com.clicbrics.consumer.activities.RecentActivity;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.databinding.ItemHomeProjectLayoutBinding;
import com.clicbrics.consumer.framework.activity.LoginActivity;
import com.clicbrics.consumer.interfaces.LoginSuccessCallback;
import com.clicbrics.consumer.model.Image;
import com.clicbrics.consumer.model.Project;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.PreCachingLayoutManager;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.HomeScreen;
import com.clicbrics.consumer.view.activity.ProjectDetailsScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Alok on 30-07-2018.
 */
public class HomeProjectListAdapter extends RecyclerView.Adapter<HomeProjectListAdapter.BindingHolder>
                                            implements LoginSuccessCallback{

    private List<Project> projectList;
    private HashMap<Long,FavoriteObj> backupMap;
    private long mapKey;
    public HomeProjectListAdapter(List<Project> projectList) {
        this.projectList = projectList;
    }

    @NonNull
    @Override
    public HomeProjectListAdapter.BindingHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final ItemHomeProjectLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_home_project_layout,parent,false);

        return new BindingHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeProjectListAdapter.BindingHolder holder, final int position) {
        final ItemHomeProjectLayoutBinding binding = holder.binding;
        final Project project = projectList.get(position);
        binding.setProjectItem(project);

        //binding.getRoot().setPadding(UtilityMethods.dpToPx(16),0,0,0);

        binding.favoriteSearchedProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EventAnalyticsHelper().ItemClickEvent((HomeScreen)view.getContext(), Constants.AnaylticsClassName.ProjectListScreen,
                        project, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.favouriteClick.toString());
                handleFavorite(binding,project.getId(),position);
            }
        });

        binding.shareSearchedProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EventAnalyticsHelper().ItemClickEvent((HomeScreen)view.getContext(), Constants.AnaylticsClassName.ProjectListScreen,
                        project, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.shareclick.toString());
                UtilityMethods.shareProject(binding.getRoot().getContext(), project.getName(),
                        project.getsCoverImage(),
                        project.getId());
            }
        });

        binding.idItemHomeProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> imageList = new ArrayList<>();
                if(project.getCoverImage() != null){
                    imageList.add(project.getCoverImage());
                }
                if(project.getImages() != null){
                    List<Image> images = project.getImages();
                    for(int i=0; i<images.size(); i++){
                        Image image = images.get(i);
                        if(image != null && !TextUtils.isEmpty(image.getSURL())){
                            imageList.add(image.getSURL());
                        }
                    }
                }
                new EventAnalyticsHelper().ItemClickEvent((HomeScreen)view.getContext(), Constants.AnaylticsClassName.ProjectListScreen,
                        project, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.ClickProject.toString());
                openProjectDetailsScreen(view.getContext(),project,imageList,binding);
            }
        });

        List<Image> imageList = new ArrayList<>();
        if(!TextUtils.isEmpty(project.getCoverImage())){
            Image image = new Image();
            image.setSURL(project.getCoverImage());
            imageList.add(image);
        }
        if(project.getImages() != null && !project.getImages().isEmpty()){
            imageList.addAll(project.getImages());
        }

        ImageListAdapter adapter = new ImageListAdapter(imageList,project);
        holder.recyclerView.setAdapter(adapter);

    }

    @Override
    public int getItemCount() {
        if(projectList != null) {
            return projectList.size();
        }
        return 0;
    }



    class BindingHolder extends RecyclerView.ViewHolder{
        ItemHomeProjectLayoutBinding binding;
        RecyclerView recyclerView;
        BindingHolder(ItemHomeProjectLayoutBinding binding) {
            super(binding.idItemHomeProject);
            this.binding = binding;
            recyclerView = binding.homeProjectImageList;
            /*LinearLayoutManager layoutManager = new LinearLayoutManager(binding.idItemHomeProject.getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(layoutManager);*/

            try {
                PreCachingLayoutManager llm = new PreCachingLayoutManager(binding.getRoot().getContext());
                llm.setOrientation(PreCachingLayoutManager.HORIZONTAL);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(llm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    private void openProjectDetailsScreen(Context context, Project model,
                                          List<String> imageList, ItemHomeProjectLayoutBinding binding){
        try {
            if(!UtilityMethods.isInternetConnected(context)){
                if(context instanceof HomeScreen) {
                    UtilityMethods.showSnackbarOnTop((HomeScreen) context, "Error", context.getString(R.string.no_internet_connection), true, 1500);
                }else if(context instanceof FavoritesActivity) {
                    UtilityMethods.showSnackbarOnTop((FavoritesActivity) context, "Error", context.getString(R.string.no_internet_connection), true, 1500);
                }else{
                    UtilityMethods.showErrorSnackBar(binding.getRoot(),context.getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
                }
                return;
            }
            Intent intent = new Intent(context,ProjectDetailsScreen.class);
            if(model.getId() != null && model.getId() != -1){
                Log.i("Home Adapter", "Project Id : " + model.getId());
                intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID, model.getId());
            }
            if(!TextUtils.isEmpty(model.getName())){
                intent.putExtra(Constants.IntentKeyConstants.PROJECT_NAME, model.getName());
            }
            if(!TextUtils.isEmpty(model.getCoverImage())){
                intent.putExtra(Constants.IntentKeyConstants.PROJECT_COVER_IMAGE, model.getCoverImage());
            }
            if(imageList != null && !imageList.isEmpty()) {
                intent.putStringArrayListExtra(Constants.IntentKeyConstants.IMAGE_LIST, new ArrayList<>(imageList));
            }
            if (!TextUtils.isEmpty(model.getPriceRange())) {
                intent.putExtra(Constants.IntentKeyConstants.PRICE_RANGE, "\u20B9" + " " + model.getPriceRange());
            }else{
                intent.putExtra(Constants.IntentKeyConstants.PRICE_RANGE, context.getString(R.string.price_on_request));
            }
            if(!TextUtils.isEmpty(model.getPropertyTypeRange())){
                intent.putExtra(Constants.IntentKeyConstants.BED_LIST,model.getPropertyTypeRange());
            }

            if (!TextUtils.isEmpty(model.getSizeRange())) {
                intent.putExtra(Constants.IntentKeyConstants.AREA_RANGE, model.getSizeRange());
            }

            if(model.getProjectStatus() != null && !TextUtils.isEmpty(model.getProjectStatus().toString())){
                intent.putExtra(Constants.IntentKeyConstants.PROJECT_STATUS,UtilityMethods.getPropertyStatus(model.getProjectStatus().toString()));
            }

            if(!TextUtils.isEmpty(model.getBspRange())){
                intent.putExtra(Constants.IntentKeyConstants.PROJECT_BSP_RANGE,model.getBspRange());
            }

            if(!TextUtils.isEmpty(model.getAddress())){
                intent.putExtra(Constants.IntentKeyConstants.ADDRESS,model.getAddress());
            }
            if(model.getOfferAvailable() !=null && model.getOfferAvailable()){
                intent.putExtra(Constants.IntentKeyConstants.OFFER,true);
            }
            if(model.getCommercial() != null && model.getCommercial()){
                intent.putExtra(Constants.IntentKeyConstants.IS_COMMERCIAL,true);
            }

            if(context instanceof FavoritesActivity){
                ((FavoritesActivity) context).startActivityForResult(intent,Constants.ActivityRequestCode.FAVORITE_ACT_RESULT_CODE);
            }else if(context instanceof RecentActivity){
                ((RecentActivity) context).startActivityForResult(intent,Constants.ActivityRequestCode.RECENT_ACT_RESULT_CODE);
            }else {
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleFavorite(ItemHomeProjectLayoutBinding binding,long projectId, int position){
        Context context = binding.getRoot().getContext();
        if (UtilityMethods.isInternetConnected(context)) {
            if (UtilityMethods.getLongInPref(context, Constants.ServerConstants.USER_ID, -1) != -1) {
                Set<String> favorites = UtilityMethods.getWishListFromPrefs(context, Constants.AppConstants.PROJECT_ID_SET);
                if ((favorites != null) && (!favorites.isEmpty())
                        && (favorites.contains(projectId + ""))) {
                    binding.favoriteSearchedProperty.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_border_gray_24dp));
                    UtilityMethods.removeFavoriteFromServer(binding.favoriteSearchedProperty, context, EndPointBuilder.getUserEndPoint(),projectId);
                } else {
                    binding.favoriteSearchedProperty.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_red));
                    UtilityMethods.addFavoriteToServer(binding.favoriteSearchedProperty, context, EndPointBuilder.getUserEndPoint(),
                            projectId);
                }
                Intent intent = new Intent(Constants.BroadCastConstants.FAVORITE_CHANGE_MAP);
                UtilityMethods.saveBooleanInPref(context,Constants.MORE_FRAGMENT_UPDATE,true);
                intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID,projectId);
                context.sendBroadcast(intent);
                UtilityMethods.saveBooleanInPref(context,Constants.MORE_FRAGMENT_UPDATE,true);
                //notifyItemChanged(position);
            } else {
                HousingApplication.mLoginSuccessCallback = this;
                //favoritedPosition = position;
                context.startActivity(new Intent(context, LoginActivity.class));
                backupMap = new HashMap<>();
                FavoriteObj favoriteObj = new FavoriteObj(binding,context,projectId,position);
                mapKey = System.currentTimeMillis();
                backupMap.put(mapKey,favoriteObj);
            }
        } else {
            /*if (context instanceof HomeScreen) {
                //((HomeScreen) context).showNetworkErrorSnackBar();
            } else if (context instanceof Re) {
                Favorite_Fragment favorite_fragment = (Favorite_Fragment) ((FavoritesActivity) mContext).getSupportFragmentManager().findFragmentByTag(Constants.AppConstants.TAG_FAVORITE_FRAGMENT);
                if (favorite_fragment != null) {
                    favorite_fragment.showNetworkErrorSnackBar();
                }
            }*/
            UtilityMethods.showSnackbarOnTop((HomeScreen)context,"Error","No Internet connectivity",true, Constants.AppConstants.ALERTOR_LENGTH_LONG);
        }
    }

    @Override
    public void isLoggedin() {
        try {
            if(backupMap != null){
                if(backupMap.containsKey(mapKey)){
                    FavoriteObj favoriteObj = backupMap.get(mapKey);
                    UtilityMethods.addFavoriteToServer(favoriteObj.binding.favoriteSearchedProperty, favoriteObj.context,
                            EndPointBuilder.getUserEndPoint(),
                            favoriteObj.projectId);
                    notifyItemChanged(favoriteObj.position);
                    Intent intent = new Intent(Constants.BroadCastConstants.FAVORITE_CHANGE_MAP);
                    intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID,favoriteObj.projectId);
                    favoriteObj.context.sendBroadcast(intent);
                }
                backupMap.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class FavoriteObj{
        ItemHomeProjectLayoutBinding binding;
        Context context;
        long projectId;
        int position;
        FavoriteObj(ItemHomeProjectLayoutBinding binding,Context context, long projectId, int position){
            this.binding = binding;
            this.context = context;
            this.projectId = projectId;
            this.position = position;
        }
    }

    public static String getPriceRange(String priceRange){
        if(priceRange == null){
            return "";
        }
        if(priceRange.equalsIgnoreCase("price on request")){
            return priceRange;
        }else{
            return "\u20B9" + priceRange;
        }
    }

    public static String bedRange(Project project){
        if(project == null){
            return "";
        }
        String bulletSymbol = "";
        if(!TextUtils.isEmpty(project.getPriceRange())){
            bulletSymbol = "\u2022 ";
        }
        return bulletSymbol + project.getPropertyTypeRange();
    }

    public static String getAreaRange(Project project,Context context){
        if(project == null || TextUtils.isEmpty(project.getSizeRange())){
            return "";
        }
        Log.i("HomeProjectListAdapter", "Area Range " + project.getSizeRange());
        String bulletSymbolPrefix = "";
        if(project.getProjectStatus() != null || !project.getProjectStatus().toString().isEmpty()){
            bulletSymbolPrefix = "\u2022 ";
        }
        /**
         * If area range is 0 then return string as it is coming from server.
         */
        if(!project.getSizeRange().contains("Sq.ft") && !project.getSizeRange().contains("Sq.yd")){
            return bulletSymbolPrefix + project.getSizeRange();
        }

        boolean isLand = false;
        if(!TextUtils.isEmpty(project.getPropertyTypeRange()) && project.getPropertyTypeRange().equalsIgnoreCase("Plot")){
            isLand = true;
        }
        String areaRange = UtilityMethods.getAreaRange(context,project.getSizeRange(),isLand);
        String selectedUnit = UtilityMethods.getSelectedUnit(context);
        String unit = "";
        if(selectedUnit.equalsIgnoreCase(Constants.PropertyUnit.SQFT.toString())){
            unit = "Sq.ft";
        }else if(selectedUnit.equalsIgnoreCase(Constants.PropertyUnit.SQYD.toString())){
            unit = "Sq.yd";
        }else if(selectedUnit.equalsIgnoreCase(Constants.PropertyUnit.SQM.toString())){
            unit = "Sq.m";
        }
        areaRange = bulletSymbolPrefix + areaRange + " " + unit;
        return areaRange;
    }


}
