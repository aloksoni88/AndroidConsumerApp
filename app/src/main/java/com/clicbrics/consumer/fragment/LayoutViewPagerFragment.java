package com.clicbrics.consumer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.PropertyLayoutActivity;
import com.clicbrics.consumer.adapter.PropertyTypeAdapter;
import com.clicbrics.consumer.utils.Constants;
import com.buy.housing.backend.propertyEndPoint.model.PropertyType;

import java.util.ArrayList;

/**
 * Created by Paras on 14-10-2016.
 */
public class LayoutViewPagerFragment extends BaseFragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private RecyclerView recyclerView;
    private int mPage;
    private ArrayList<PropertyType> propertyTypeList;
    public static LayoutViewPagerFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        LayoutViewPagerFragment fragment = new LayoutViewPagerFragment();
        fragment.setArguments(args);

        return fragment;
    }
    public void setData(ArrayList<PropertyType> propertyTypeList){
        this.propertyTypeList=propertyTypeList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager_layout, container, false);
//        TextView textView = (TextView) view;
//        textView.setText("Fragment #" + mPage);
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
    }
    private void initView(View view){
        recyclerView = (RecyclerView) view.findViewById(R.id.horizontal_recycler_view);

        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);
        PropertyTypeAdapter propertyTypeAdapter =  new PropertyTypeAdapter(propertyTypeList,deviceWidth,
                                                    mActivity,new PropertyTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PropertyType item) {
                Intent intent = new Intent(mActivity,PropertyLayoutActivity.class);
                intent.putExtra(Constants.IntentKeyConstants.PROPERTY_TYPE,item.getNumberOfBedrooms().intValue());
                intent.putExtra(Constants.IntentKeyConstants.PROPERTY_ID,item.getId().longValue());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(propertyTypeAdapter);

    }
}
