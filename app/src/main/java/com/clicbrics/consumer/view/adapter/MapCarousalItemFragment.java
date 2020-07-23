package com.clicbrics.consumer.view.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.databinding.FragmentMapCarousalItemBinding;
import com.clicbrics.consumer.fragment.BaseFragment;
import com.clicbrics.consumer.framework.activity.LoginActivity;
import com.clicbrics.consumer.interfaces.LoginSuccessCallback;
import com.clicbrics.consumer.model.Image;
import com.clicbrics.consumer.model.Project;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.HomeScreen;
import com.clicbrics.consumer.view.activity.ProjectDetailsScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Alok on 07-08-2018.
 */
public class MapCarousalItemFragment extends BaseFragment implements LoginSuccessCallback{

    private static final String TAG = "MapCarousalitem";
    private Project project;
    private HashMap<Long,FavoriteObj> backupMap;
    private long mapKey;
    private ImageView favoriteIcon;

    public MapCarousalItemFragment(){

    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
    }

    public MapCarousalItemFragment(Project project) {
        this.project = project;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach: ");
        getActivity().registerReceiver(updateFavorite, new IntentFilter(Constants.BroadCastConstants.FAVORITE_CHANGE_MAP));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach: ");
        getActivity().unregisterReceiver(updateFavorite);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final FragmentMapCarousalItemBinding binding = DataBindingUtil.inflate(inflater,R.layout.fragment_map_carousal_item,container,false);
        View view = binding.getRoot();
        favoriteIcon = binding.favoriteProject;
        binding.setMapItemModel(project);
        if(favoriteIcon != null && project != null) {
            favoriteIcon.setTag(project.getId());
        }
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
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
                openProjectDetailsScreen(container.getContext(),project,imageList);
            }
        });

        binding.favoriteProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleFavorite(binding,project.getId());
            }
        });
        return view;
    }

    private void openProjectDetailsScreen(Context context, Project model, List<String> imageList){
        if(!UtilityMethods.isInternetConnected(context)){
            UtilityMethods.showSnackbarOnTop((HomeScreen)context,"Error",context.getString(R.string.no_internet_connection),true,1500);
            return;
        }
        Intent intent = new Intent(context,ProjectDetailsScreen.class);
        if(model.getId() != -1){
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

        context.startActivity(intent);
    }

    private void handleFavorite(FragmentMapCarousalItemBinding binding,long projectId){
        Context context = binding.getRoot().getContext();
        if (UtilityMethods.isInternetConnected(context)) {
            if (UtilityMethods.getLongInPref(context, Constants.ServerConstants.USER_ID, -1) != -1) {
                Set<String> favorites = UtilityMethods.getWishListFromPrefs(context, Constants.AppConstants.PROJECT_ID_SET);
                if ((favorites != null) && (!favorites.isEmpty())
                        && (favorites.contains(projectId + ""))) {
                    binding.favoriteProject.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_border_white_24dp));
                    UtilityMethods.removeFavoriteFromServer(binding.favoriteProject, context, EndPointBuilder.getUserEndPoint(),projectId);
                } else {
                    binding.favoriteProject.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_red));
                    UtilityMethods.addFavoriteToServer(binding.favoriteProject, context, EndPointBuilder.getUserEndPoint(),
                            projectId);
                }
                Intent intent = new Intent(Constants.BroadCastConstants.FAVORITE_CHANGE);
                intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID,projectId);
                getActivity().sendBroadcast(intent);
                UtilityMethods.saveBooleanInPref(context,Constants.MORE_FRAGMENT_UPDATE,true);
            } else {
                HousingApplication.mLoginSuccessCallback = this;
                context.startActivity(new Intent(context, LoginActivity.class));
                backupMap = new HashMap<>();
                FavoriteObj favoriteObj = new FavoriteObj(binding,context,projectId);
                mapKey = System.currentTimeMillis();
                backupMap.put(mapKey,favoriteObj);
            }
        } else {
            UtilityMethods.showSnackbarOnTop((HomeScreen)context,"Error","No Internet connectivity",true, Constants.AppConstants.ALERTOR_LENGTH_LONG);
        }
    }

    @Override
    public void isLoggedin() {
        try {
            if(backupMap != null){
                if(backupMap.containsKey(mapKey)){
                    FavoriteObj favoriteObj = backupMap.get(mapKey);
                    UtilityMethods.addFavoriteToServer(favoriteObj.binding.favoriteProject, favoriteObj.context,
                            EndPointBuilder.getUserEndPoint(),
                            favoriteObj.projectId);
                    favoriteObj.binding.favoriteProject.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite_red));
                    Intent intent = new Intent(Constants.BroadCastConstants.FAVORITE_CHANGE);
                    intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID,favoriteObj.projectId);
                    getActivity().sendBroadcast(intent);
                }
                backupMap.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class FavoriteObj{
        FragmentMapCarousalItemBinding binding;
        Context context;
        long projectId;
        FavoriteObj(FragmentMapCarousalItemBinding binding,Context context, long projectId){
            this.binding = binding;
            this.context = context;
            this.projectId = projectId;
        }
    }

    /**
     * Broadcast receiver to update the favorite in home project list
     */
    private BroadcastReceiver updateFavorite = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == Constants.BroadCastConstants.FAVORITE_CHANGE_MAP) {
                if(intent.hasExtra(Constants.IntentKeyConstants.PROJECT_ID)
                        && intent.getLongExtra(Constants.IntentKeyConstants.PROJECT_ID,-1) != -1){
                    long projectId = intent.getLongExtra(Constants.IntentKeyConstants.PROJECT_ID,-1);
                    Log.i("MapCarousalitem", "favorite selected onReceive: "  + projectId);
                    final Set<String> favoriteList = UtilityMethods.getWishListFromPrefs(context, Constants.AppConstants.PROJECT_ID_SET);
                    boolean isFound = false;
                    if ((favoriteList != null) && (!favoriteList.isEmpty())) {
                        if(favoriteList.contains(projectId+"")) {
                            for(String id :favoriteList)
                            if(id.equalsIgnoreCase(projectId+"") && favoriteIcon != null
                                    && favoriteIcon.getTag().equals(projectId)){
                                Log.i("MapCarousalitem", "onReceive: "  + projectId);
                                favoriteIcon.setImageResource(R.drawable.ic_favorite_red);
                                isFound = true;
                                break;
                            }
                        }
                    }
                    if(!isFound && favoriteIcon.getTag().equals(projectId)){
                        favoriteIcon.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    }
                }
            }
        }
    };

    public static String getBedListNStatus(String bedList, String projectStatus){
        if(!TextUtils.isEmpty(bedList) && !TextUtils.isEmpty(projectStatus)){
            return bedList + " \u2022 " + UtilityMethods.getPropertyStatus(projectStatus);
        }else if(!TextUtils.isEmpty(bedList) && TextUtils.isEmpty(projectStatus)){
            return bedList;
        }else if(TextUtils.isEmpty(bedList) && !TextUtils.isEmpty(projectStatus)){
            return UtilityMethods.getPropertyStatus(projectStatus);
        }else{
            return "";
        }
    }

    public static String getAreaRange(Project project,Context context){
        if(project == null || TextUtils.isEmpty(project.getSizeRange())){
            return "";
        }
        Log.i("HomeProjectListAdapter", "Area Range " + project.getSizeRange());
        /**
         * If area range is 0 then return string as it is coming from server.
         */
        if(!project.getSizeRange().contains("Sq.ft") && !project.getSizeRange().contains("Sq.yd")){
            return project.getSizeRange();
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
        areaRange = areaRange + " " + unit;
        return areaRange;
    }
}
