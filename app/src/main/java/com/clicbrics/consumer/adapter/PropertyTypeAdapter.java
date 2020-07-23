package com.clicbrics.consumer.adapter;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.buy.housing.backend.propertyEndPoint.model.PropertyType;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Paras on 14-10-2016.
 */
public
class PropertyTypeAdapter extends RecyclerView.Adapter<PropertyTypeAdapter.MyViewHolder> {

    private List<PropertyType> propertyTypeList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView price,area,possesion,emi;
        public ImageView img;

        public MyViewHolder(View view) {
            super(view);
            price = (TextView) view.findViewById(R.id.price);
            area = (TextView) view.findViewById(R.id.area);
            possesion = (TextView) view.findViewById(R.id.possession);
            emi = (TextView) view.findViewById(R.id.emi);
            img = (ImageView)view.findViewById(R.id.layout_imag);
        }

        public void bind(final int position, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(propertyTypeList.get(position));
                }
            });
            price.setText(UtilityMethods.getPriceWithLACWord(propertyTypeList.get(position).getBsp()*propertyTypeList.get(position).getSuperArea()));
            area.setText(propertyTypeList.get(position).getSuperArea()+" sqft");
            possesion.setText(UtilityMethods.getDate(propertyTypeList.get(position).getPossessionDate(),"MMM ,yy"));
            emi.setText(UtilityMethods.getPriceWithLACWord(1350000));

//        if(TextUtils.isEmpty(projectWrapper.getImage())){
////            mImageLoader.DisplayImage(projectWrapper.getImage(),holder.image,R.drawable.placeholder_list);
////            Picasso.with(context)
////                    .load("https://lh3.googleusercontent.com/2G9wgzkBnsGCrdO_XVfi9cjwrruusE_W2JxEBiyXWd6FW7rPjx15iF4-DIAgfwYLRj0kYKXP1AkyH7RczluOekXKw0VFygwCHgE=s"+deviceWidth)
////                    .placeholderf(R.drawable.apartment) // optional
////                    .error(R.drawable.apartment)
////                    .centerCrop()// optional
////                    .resize(deviceWidth,(int)context.getResources().getDimension(R.dimen.margin_150))
////                    .into(holder.image);
//            holder.image.setImageResource(R.drawable.apartment);
//        }else
            {
                try {
                    if(propertyTypeList.get(position).getFloorPlan()!=null)
                        Picasso.get()
                                .load(propertyTypeList.get(position).getFloorPlan().getSurl2d())
                                .placeholder(R.drawable.ic_filter_apartment) // optional
                                .error(R.drawable.ic_filter_apartment)
//                                .centerCrop()// optional
//                                .resize((int) context.getResources().getDimension(R.dimen.margin_95), (int) context.getResources().getDimension(R.dimen.margin_95))
                                .into(img);
                    else
                        Picasso.get()
                                .load(propertyTypeList.get(position).getImages().get(0).getSURL())
                                .placeholder(R.drawable.ic_filter_apartment) // optional
                                .error(R.drawable.ic_filter_apartment)
//                                .centerCrop()// optional
//                                .resize((int) context.getResources().getDimension(R.dimen.margin_95), (int) context.getResources().getDimension(R.dimen.margin_95))
                                .into(img);
                }catch (Exception ex){


                }
            }
        }

    }

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PropertyType item);
    }
    private int width;
    private Context context;
    public PropertyTypeAdapter(List<PropertyType> propertyTypeList,int deviceWidth,Context context,OnItemClickListener listener) {
        this.listener=listener;
        this.propertyTypeList = propertyTypeList;
        this.width = (int)(deviceWidth*.75f);
        this.context=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.e_property_type, parent, false);
        itemView.getLayoutParams().width=width;
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.bind(position,listener);
    }

    @Override
    public int getItemCount() {
        return propertyTypeList.size();
    }
}
