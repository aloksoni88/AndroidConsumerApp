package com.clicbrics.consumer.adapter;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.clicbrics.consumer.fragment.LayoutViewPagerFragment;
import com.clicbrics.consumer.utils.Constants;
import com.buy.housing.backend.propertyEndPoint.model.PropertyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LayoutViewPagerAdaptor extends FragmentPagerAdapter {
    private Context context;
    private HashMap<Integer,ArrayList<PropertyType>> propertyListMap  =     new HashMap<Integer,ArrayList<PropertyType>>();
    private List<Integer> keys;
    public LayoutViewPagerAdaptor(FragmentManager fm, Context context,ArrayList<PropertyType> propertyList) {
        super(fm);
        this.context = context;
        this.propertyListMap=propertyListMap;
        keys = new ArrayList(propertyListMap.keySet());
    }

    @Override
    public int getCount() {
        return propertyListMap.size();
    }

    @Override
    public Fragment getItem(int position) {
        LayoutViewPagerFragment fragment = LayoutViewPagerFragment.newInstance(position);
        fragment.setData(propertyListMap.get(keys.get(position)));
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return keys.get(position)+ Constants.AppConstants.BHK;
    }




}