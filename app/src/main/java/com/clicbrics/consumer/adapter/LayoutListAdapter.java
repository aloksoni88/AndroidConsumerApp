package com.clicbrics.consumer.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buy.housing.backend.propertyEndPoint.model.PropertyType;
import com.buy.housing.backend.propertyEndPoint.model.Video;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.PropertyLayoutActivity;
import com.clicbrics.consumer.customview.roundedimageview.RoundedImageView;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.ProjectDetailsScreen;
import com.clicbrics.consumer.view.activity.VirtualTourViewActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by root on 19/1/17.
 */
public class LayoutListAdapter extends RecyclerView.Adapter<LayoutListAdapter.LayoutViewHolder> {

    public Context mContext;
    private List<PropertyType> propertyList;
    private Long id;
    String coverImage;

    public LayoutListAdapter(Context context, List<PropertyType> propertyList, Long id) {
        mContext = context;
        this.propertyList = propertyList;
        this.id = id;
    }

    public class LayoutViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView layout3d;
        TextView bed_type, area, price,areaInYards,onwordText;
        ProgressBar mImagePB;
        ImageView virtualTourIcon;
        ImageView soldOutImage;
        public LayoutViewHolder(View itemView) {
            super(itemView);
            layout3d = (RoundedImageView) itemView.findViewById(R.id.layout_img);
            bed_type = (TextView) itemView.findViewById(R.id.bed_type);
            area = (TextView) itemView.findViewById(R.id.layout_area);
            price = (TextView) itemView.findViewById(R.id.layout_price);
            areaInYards = (TextView) itemView.findViewById(R.id.plot_size_in_yard);
            onwordText = (TextView) itemView.findViewById(R.id.onwards);
            mImagePB = (ProgressBar) itemView.findViewById(R.id.id_layout_list_image_pb);
            virtualTourIcon = itemView.findViewById(R.id.id_virtual_tour_icon);
            soldOutImage = itemView.findViewById(R.id.id_sold_out_image);
        }
    }

    @Override
    public LayoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout_list, parent, false);
        return new LayoutViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final LayoutViewHolder holder, final int position) {
        if(position == 0) {
            holder.itemView.setPadding(UtilityMethods.dpToPx(16), 0, 0, 0);
        }else if(position == propertyList.size()-1){
            holder.itemView.setPadding(UtilityMethods.dpToPx(2), 0,  UtilityMethods.dpToPx(16),0);
        }else{
            holder.itemView.setPadding(UtilityMethods.dpToPx(2), 0, 0, 0);
        }
        if(propertyList.get(position).getSoldStatus() != null && propertyList.get(position).getSoldStatus()){
            holder.soldOutImage.setVisibility(View.VISIBLE);
            holder.layout3d.setAlpha(0.7f);
        }else{
            holder.layout3d.setAlpha(1.0f);
            holder.soldOutImage.setVisibility(View.GONE);
        }
        if((propertyList.get(position).getFloorPlan()!=null) && (!propertyList.get(position).getFloorPlan().isEmpty())) {
            if (!TextUtils.isEmpty(propertyList.get(position).getFloorPlan().getSurl3d())) {
                String layoutImageURL = propertyList.get(position).getFloorPlan().getSurl3d()+"=h600";
                holder.mImagePB.setVisibility(View.VISIBLE);
                Picasso.get().load(layoutImageURL)
                        .placeholder(R.drawable.layout_unavailable)
                        .into(holder.layout3d, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.mImagePB.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                holder.mImagePB.setVisibility(View.GONE);
                            }
                        });
            } else if (!TextUtils.isEmpty(propertyList.get(position).getFloorPlan().getSurl2d())) {
                String layoutImageURL = propertyList.get(position).getFloorPlan().getSurl2d()+"=h600";
                holder.mImagePB.setVisibility(View.VISIBLE);
                Picasso.get().load(layoutImageURL)
                        .placeholder(R.drawable.layout_unavailable)
                        .into(holder.layout3d, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.mImagePB.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        holder.mImagePB.setVisibility(View.GONE);
                                    }
                                });
            } else {
                holder.mImagePB.setVisibility(View.GONE);
                Picasso.get().load(R.drawable.layout_placeholder_small)
                        .into(holder.layout3d);
            }
        } else {
            Picasso.get().load(R.drawable.layout_placeholder_small)
                    .into(holder.layout3d);
        }

        if(propertyList.get(position).getType().equalsIgnoreCase("Land")) {
            holder.bed_type.setText("PLOT");
            holder.areaInYards.setVisibility(View.VISIBLE);

            holder.areaInYards.setText("( " + UtilityMethods.getArea(mContext,propertyList.get(position).getSuperArea(),true)
                    + UtilityMethods.getUnit(mContext,true)+" )");
        } else {
            holder.areaInYards.setVisibility(View.VISIBLE);
            String propertyType = propertyList.get(position).getType();

            if(UtilityMethods.hasVilla(propertyType)){
                holder.areaInYards.setText("Villa");
            }else if(propertyType.equalsIgnoreCase(Constants.AppConstants.PropertyType.IndependentFloor.toString())){
                holder.areaInYards.setText(" Independent Floor");
            }else if(UtilityMethods.isCommercial(propertyType)){
                holder.areaInYards.setVisibility(View.GONE);
            }else if(propertyType.equalsIgnoreCase("Studio")){
                holder.areaInYards.setText("Studio");
            }else{
                holder.areaInYards.setText(propertyType);
            }
            if(UtilityMethods.isCommercial(propertyType)){
                holder.bed_type.setText(UtilityMethods.getCommercialTypeName(propertyType));
            }else if(propertyType.equalsIgnoreCase("Studio")){
                holder.bed_type.setText("1RK");
            }else{
                holder.bed_type.setText(propertyList.get(position).getNumberOfBedrooms() + "BHK");
            }
        }
        if(propertyList.get(position).getBsp() == 0){
            holder.onwordText.setVisibility(View.GONE);
            holder.price.setTextSize(14);
            holder.price.setText(mContext.getResources().getString(R.string.price_on_request));
        }else {
            String price = UtilityMethods.getPriceWord(propertyList.get(position).getBsp()
                    * propertyList.get(position).getSuperArea());
            holder.price.setText(price);
        }

        if(propertyList.get(position).getSuperArea() != 0) {
            holder.area.setVisibility(View.VISIBLE);
            if (UtilityMethods.isCommercial(propertyList.get(position).getType())
                    && UtilityMethods.isCommercialLand(propertyList.get(position).getType())) {
                holder.area.setText(UtilityMethods.getArea(mContext,propertyList.get(position).getSuperArea(),true)
                        + UtilityMethods.getUnit(mContext,true));
            } else {
                holder.area.setText(UtilityMethods.getArea(mContext,propertyList.get(position).getSuperArea(),false) + UtilityMethods.getUnit(mContext,false));
            }
        }else{
            holder.area.setVisibility(View.GONE);
        }

        setVirtualIconView(mContext,propertyList.get(position),holder.virtualTourIcon);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!UtilityMethods.isInternetConnected(mContext)){
                    UtilityMethods.showErrorSnackbarOnTop((ProjectDetailsScreen)mContext);
                    return;
                }
                try {
                    if((propertyList.get(holder.getAdapterPosition()).getFloorPlan()!=null)
                            && (!propertyList.get(holder.getAdapterPosition()).getFloorPlan().isEmpty())) {
                        String layoutImageURL = "";
                        boolean is2DPicture = true;
                        if (!TextUtils.isEmpty(propertyList.get(holder.getAdapterPosition()).getFloorPlan().getSurl2d())) {
                            layoutImageURL = propertyList.get(holder.getAdapterPosition()).getFloorPlan().getSurl2d()+"=h600";
                            is2DPicture = true;
                        }else if (!TextUtils.isEmpty(propertyList.get(holder.getAdapterPosition()).getFloorPlan().getSurl3d())) {
                            layoutImageURL = propertyList.get(holder.getAdapterPosition()).getFloorPlan().getSurl3d()+"=h600";
                            is2DPicture = false;
                        }
                        final String customImageURL = layoutImageURL ;
                        final boolean is2DPic = is2DPicture;
                        if(!TextUtils.isEmpty(customImageURL)){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Bitmap imageBitmap = Picasso.get().load(customImageURL).get();
                                        ((HousingApplication) mContext.getApplicationContext()).setLayoutImageBitmap(imageBitmap,
                                                propertyList.get(holder.getAdapterPosition()).getId(), is2DPic);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(mContext, PropertyLayoutActivity.class);
                intent.putExtra("FromLayoutPage",true);
                intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID, propertyList.get(position).getProjectId());
                intent.putExtra(Constants.IntentKeyConstants.PROPERTY_TYPE, propertyList.get(position)
                                                                            .getNumberOfBedrooms().intValue());
                if(!TextUtils.isEmpty(propertyList.get(position).getType())) {
                    intent.putExtra("PropertyTypeValue", propertyList.get(position).getType());
                }
                intent.putExtra(Constants.IntentKeyConstants.PROPERTY_ID, propertyList.get(position)
                                                                            .getId().longValue());
                /*ByteArrayOutputStream outputStream = null;
                try {
                    holder.layout3d.setDrawingCacheEnabled(true);
                    holder.layout3d.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    Bitmap bitmap = holder.layout3d.getDrawingCache();
                    outputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                intent.putExtra("CoverImage",outputStream.toByteArray());*/
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    public void setVirtualIconView(final Context context, PropertyType propertyType, final ImageView virtualTourIcon){
        if(propertyType.getVirtualTour() != null && !propertyType.getVirtualTour().isEmpty() && virtualTourIcon != null){
            final Video video = propertyType.getVirtualTour();
            virtualTourIcon.setVisibility(View.VISIBLE);
            if(video != null && !TextUtils.isEmpty(video.getUrl())){
                virtualTourIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!UtilityMethods.isInternetConnected(mContext)){
                            UtilityMethods.showErrorSnackbarOnTop((ProjectDetailsScreen)mContext);
                            return;
                        }
                        Intent intent = new Intent(virtualTourIcon.getContext(),VirtualTourViewActivity.class);
                        intent.putExtra("URL",video.getUrl().trim());
                        intent.putExtra("Title",video.getName().trim());
                        context.startActivity(intent);
                    }
                });
            }
        }else{
            if(virtualTourIcon != null) {
                virtualTourIcon.setVisibility(View.GONE);
            }
        }
    }

}
