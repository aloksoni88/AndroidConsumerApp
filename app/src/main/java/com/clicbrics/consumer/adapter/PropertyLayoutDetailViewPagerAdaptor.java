package com.clicbrics.consumer.adapter;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.buy.housing.backend.propertyEndPoint.model.PropertyType;
import com.clicbrics.consumer.fragment.PropertyLayoutViewPagerFragment;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.util.ArrayList;
import java.util.List;

public class PropertyLayoutDetailViewPagerAdaptor extends FragmentStatePagerAdapter {
    private Context context;
    private List<PropertyType> propertyList = new ArrayList<PropertyType>();
    private List<Integer> keys;
    private boolean isAllSelected;

    public PropertyLayoutDetailViewPagerAdaptor(FragmentManager fm, Context context,
                                                List<PropertyType> propertyList, boolean isAllSelected) {
        super(fm);
        this.isAllSelected = isAllSelected;
        this.context = context;
        this.propertyList = propertyList;
    }

    @Override
    public int getCount() {
        return propertyList.size();
    }

    @Override
    public Fragment getItem(int position) {
        PropertyLayoutViewPagerFragment fragment = PropertyLayoutViewPagerFragment.newInstance(position);
        fragment.setData(propertyList.get(position));
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        PropertyType propertyType = propertyList.get(position);
        if (isAllSelected) {
            if (propertyType.getType().equalsIgnoreCase("LAND")) {
                String areaStr = UtilityMethods.getArea(context,propertyType.getSuperArea(),true);
                String unit = UtilityMethods.getUnit(context,true);
                return "PLOT" + "  " + areaStr + unit;
            }else if (propertyType.getType().equalsIgnoreCase("Studio")) {
                String areaStr = UtilityMethods.getArea(context,propertyType.getSuperArea(),false);
                String unit = UtilityMethods.getUnit(context,false);
                return "1RK Studio " + "  " + areaStr + unit;
            }else if(UtilityMethods.isCommercial(propertyType.getType())){
                if(UtilityMethods.isCommercialLand(propertyType.getType())){
                    String areaStr = UtilityMethods.getArea(context,propertyType.getSuperArea(),true);
                    String unit = UtilityMethods.getUnit(context,true);
                    return UtilityMethods.getCommercialTypeName(propertyType.getType()) + "  " + areaStr + unit;
                }else {
                    String areaStr = UtilityMethods.getArea(context,propertyType.getSuperArea(),false);
                    String unit = UtilityMethods.getUnit(context,false);
                    return UtilityMethods.getCommercialTypeName(propertyType.getType()) + "  " + areaStr + unit;
                }
            }/*else if(propertyType.getType().equalsIgnoreCase(Constants.AppConstants.PropertyType.IndependentFloor.toString())){
                return propertyType.getNumberOfBedrooms() + Constants.AppConstants.BHK
                        + " Floor " + propertyType.getSuperArea() + " sqft";
            }*/else {
                String areaStr = UtilityMethods.getArea(context,propertyType.getSuperArea(),false);
                String unit = UtilityMethods.getUnit(context,false);
                return propertyType.getNumberOfBedrooms() + Constants.AppConstants.BHK
                        + "  " + areaStr + unit;
            }
        } else {
            if(propertyType.getType().equalsIgnoreCase("Land")){
                String areaStr = UtilityMethods.getArea(context,propertyType.getSuperArea(),true);
                String unit = UtilityMethods.getUnit(context,true);
                return areaStr + unit;
            }else if(UtilityMethods.isCommercial(propertyType.getType())){
                if(UtilityMethods.isCommercialLand(propertyType.getType())){
                    String areaStr = UtilityMethods.getArea(context,propertyType.getSuperArea(),true);
                    String unit = UtilityMethods.getUnit(context,true);
                    return areaStr+unit;
                }else {
                    String areaStr = UtilityMethods.getArea(context,propertyType.getSuperArea(),false);
                    String unit = UtilityMethods.getUnit(context,false);
                    return areaStr+unit;
                }
            }else {
                String areaStr = UtilityMethods.getArea(context,propertyType.getSuperArea(),false);
                String unit = UtilityMethods.getUnit(context,false);
                return areaStr+unit;
            }
        }
    }
}