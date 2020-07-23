package com.clicbrics.consumer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buy.housing.backend.propertyEndPoint.model.PropertyType;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.adapter.PropertyLayoutDetailViewPagerAdaptor;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used to show property details
 */
public class PropertyLayoutFragment extends BaseFragment {

    /*
    * Filtered array list for populating ui selection
    * */
    private ArrayList<PropertyType> propertyList;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<PropertyType> propertyTypeList = new ArrayList<PropertyType>();

    public PropertyLayoutFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_property_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
    }

    public void setViewPager(String selectedbhk, final long proprotyId) {

        if(propertyTypeList != null){
            propertyTypeList.clear();
        }
        if (selectedbhk.equalsIgnoreCase("ALL")) {
            propertyTypeList.addAll(((HousingApplication)getContext().getApplicationContext()).getPropertyTypeList());
            /*Collections.sort(propertyTypeList, new Comparator<PropertyType>() {
                @Override
                public int compare(PropertyType propertyType1, PropertyType propertyType2) {
                    return propertyType1.getNumberOfBedrooms().compareTo(propertyType2.getNumberOfBedrooms());
                }
            });*/

            viewPager.setAdapter(new PropertyLayoutDetailViewPagerAdaptor(getChildFragmentManager(),
                    mActivity, propertyTypeList, true));
            tabLayout.setupWithViewPager(viewPager);

        } else {

            List<PropertyType> propertyTypes = ((HousingApplication)getContext().getApplicationContext()).getPropertyTypeList();
            int selectIndex = 0;
            for (int i = 0; i < propertyTypes.size(); i++) {
                PropertyType propertyType = propertyTypes.get(i);
                Log.d("TAG", "selectedBHK->" + selectedbhk + "  " + propertyType.getNumberOfBedrooms().toString());
                if (selectedbhk.equalsIgnoreCase(propertyType.getNumberOfBedrooms().toString())
                        || (selectedbhk.equalsIgnoreCase("PLOT") && propertyType.getNumberOfBedrooms() == 0 && !propertyType.getType().equalsIgnoreCase("Studio"))
                                && !UtilityMethods.isCommercial(propertyType.getType())){
                    propertyTypeList.add(propertyType);
                    if (proprotyId == propertyType.getId()) {
                        selectIndex = propertyTypeList.size() - 1;
                    }
                }else if(selectedbhk.replaceAll(" ","").equalsIgnoreCase(propertyType.getType())){
                    propertyTypeList.add(propertyType);
                    if (proprotyId == propertyType.getId()) {
                        selectIndex = propertyTypeList.size() - 1;
                    }
                }else if(selectedbhk.equalsIgnoreCase("1RK Studio") && propertyType.getType().equalsIgnoreCase("Studio")){
                    propertyTypeList.add(propertyType);
                    if (proprotyId == propertyType.getId()) {
                        selectIndex = propertyTypeList.size() - 1;
                    }
                }
            }
            Log.d("TAG", "sizePagerAdapter->" + propertyTypeList.size());
            viewPager.setAdapter(new PropertyLayoutDetailViewPagerAdaptor(getChildFragmentManager(),
                    mActivity, propertyTypeList, false));
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setCurrentItem(selectIndex);
        }
    }

}
