package com.clicbrics.consumer.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.clicbrics.consumer.model.Project;
import com.clicbrics.consumer.model.SearchProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alok on 30-07-2018.
 */
public class MapViewListAdapter extends FragmentStatePagerAdapter{

    private List<Project> filteredArray;

    public MapViewListAdapter(FragmentManager fm, List<Project> filteredArray , Context context) {
        super(fm);
        this.filteredArray = filteredArray;
    }

    @Override
    public Fragment getItem(int position) {
        MapCarousalItemFragment fragment = new MapCarousalItemFragment(filteredArray.get(position));
        return fragment;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return filteredArray.size();
    }
}
