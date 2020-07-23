package com.clicbrics.consumer.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.FavoritesActivity;
import com.clicbrics.consumer.activities.RecentActivity;
import com.clicbrics.consumer.databinding.ItemImageListLayoutBinding;
import com.clicbrics.consumer.model.Image;
import com.clicbrics.consumer.model.Project;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.HomeScreen;
import com.clicbrics.consumer.view.activity.ProjectDetailsScreen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alok on 31-07-2018.
 */
public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.BindingHolder> {

    private List<Image> imageList;
    private Project project;
    ImageListAdapter(List<Image> imageList, Project project) {
        this.imageList = imageList;
        this.project = project;
    }

    class BindingHolder extends RecyclerView.ViewHolder{
        ItemImageListLayoutBinding binding;
        BindingHolder(ItemImageListLayoutBinding binding) {
            super(binding.idImageListView);
            this.binding = binding;
        }
    }


    @NonNull
    @Override
    public ImageListAdapter.BindingHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final ItemImageListLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_image_list_layout,parent,false);
        return new BindingHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageListAdapter.BindingHolder holder, int position) {
        final ItemImageListLayoutBinding binding = holder.binding;
        if(position == 0) {
            binding.getRoot().setPadding(UtilityMethods.dpToPx(10), 0, 0, 0);
        }else if(position == imageList.size()-1){
            binding.getRoot().setPadding(UtilityMethods.dpToPx(2), 0,  UtilityMethods.dpToPx(10),0);
        }else{
            binding.getRoot().setPadding(UtilityMethods.dpToPx(2), 0, 0, 0);
        }
        Image image = imageList.get(position);
        binding.setImageItem(image);

        if(position == 0 && project.getCommercial() != null && project.getCommercial()){
            binding.commercialTag.setVisibility(View.VISIBLE);
        }else{
            binding.commercialTag.setVisibility(View.GONE);
        }
        if(position == 0 && project.getOfferAvailable() != null && project.getOfferAvailable()){
            binding.offerImage.setVisibility(View.VISIBLE);
        }else{
            binding.offerImage.setVisibility(View.GONE);
        }
        if(position == 0 && project.getVirtualTour() != null && project.getVirtualTour()){
            binding.idVirtualTourIcon.setVisibility(View.VISIBLE);
        }else{
            binding.idVirtualTourIcon.setVisibility(View.GONE);
        }

        if(position == 0 && project.getSold() != null && project.getSold()){
            binding.idSoldoutTag.setVisibility(View.VISIBLE);
        }else{
            binding.idSoldoutTag.setVisibility(View.GONE);
        }

        binding.idImageListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> images = new ArrayList<>();
                if(imageList != null){
                    for(int i=0; i<imageList.size(); i++){
                        Image image = imageList.get(i);
                        if(image != null && !TextUtils.isEmpty(image.getSURL())){
                            images.add(image.getSURL());
                        }
                    }
                }
                openProjectDetailsScreen(view.getContext(),project,images,binding);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(imageList != null) {
            return imageList.size();
        }
        return 0;
    }

    private void openProjectDetailsScreen(Context context, Project model,List<String> imageList,
                                          ItemImageListLayoutBinding binding){
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

            if(!TextUtils.isEmpty(model.getBspRange())){
                intent.putExtra(Constants.IntentKeyConstants.PROJECT_BSP_RANGE,model.getBspRange());
            }

            if (!TextUtils.isEmpty(model.getSizeRange())) {
                intent.putExtra(Constants.IntentKeyConstants.AREA_RANGE, model.getSizeRange());
            }

            if(model.getProjectStatus() != null && !TextUtils.isEmpty(model.getProjectStatus().toString())){
                intent.putExtra(Constants.IntentKeyConstants.PROJECT_STATUS,UtilityMethods.getPropertyStatus(model.getProjectStatus().toString()));
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
}
