package com.clicbrics.consumer.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buy.housing.backend.propertyEndPoint.model.Amenity;
import com.buy.housing.backend.propertyEndPoint.model.PropertyType;
import com.buy.housing.backend.propertyEndPoint.model.Video;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.EmiActivity;
import com.clicbrics.consumer.activities.FurnishingListActivity;
import com.clicbrics.consumer.activities.PhotoZoomActivity;
import com.clicbrics.consumer.activities.PropertyLayoutActivity;
import com.clicbrics.consumer.adapter.AmenitiesGridAdapter;
import com.clicbrics.consumer.adapter.FurnishingAdapter;
import com.clicbrics.consumer.adapter.LayoutConfigurationAdapter;
import com.clicbrics.consumer.animators.SlideInDownAnimator;
import com.clicbrics.consumer.customview.ThreeTwoImageView;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.GridLayoutManagerWrapped;
import com.clicbrics.consumer.utils.Item;
import com.clicbrics.consumer.utils.PropertyDetail;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.VirtualTourViewActivity;
import com.clicbrics.consumer.wrapper.AmenityObject;
import com.clicbrics.consumer.wrapper.ListSectionItem;
import com.clicbrics.consumer.wrapper.ListSectionRowItems;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.clicbrics.consumer.R.string.fittings;


/**
 * Created by Paras on 14-10-2016.
 */
public class PropertyLayoutViewPagerFragment extends BaseFragment {
    public static final String TAG = PropertyLayoutViewPagerFragment.class.getSimpleName();
    public static final String ARG_PAGE = "ARG_PAGE";
    private ArrayList<Item> furnishingsList = new ArrayList<Item>();
    private int mPage;
    private PropertyType propertyType;
    private ThreeTwoImageView img;

    private TextView downPaymentValue, emiAmountValue, interestRateValue, text2d, text3d;

    private SwitchCompat layout_switch;
    private TextView propertyPrice, possessionDetails, superAreaDetail,
            builtupAreaDetail, carpetAreaDetail, furnishingStatus, propertyTypeValue, bsp;
    private ProgressBar superAreaProgress, builtupAreaProgress, carpetAreaProgress;
    private TextView dotView;
    private ProgressBar mImagePB;
    private LinearLayout emiLayout;
    private RelativeLayout furnishLayout;
    ImageView virtualTourIcon;
    private ImageView soldOutImage;

    boolean expand = true;
    boolean expand_config = true;
    boolean isFirstTimeLoad = true;


    public static PropertyLayoutViewPagerFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PropertyLayoutViewPagerFragment fragment = new PropertyLayoutViewPagerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public void setData(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager_property_layout, container, false);
        text2d = (TextView) view.findViewById(R.id.text_2d);
        text3d = (TextView) view.findViewById(R.id.text_3d);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);

        /*if (propertyType!= null && !TextUtils.isEmpty(propertyType.getFurnishedType())) {

            if (propertyType.getFurnishedType().equals("SemiFurnished")) {
                furnishingStatus.setText("Semi-Furnished");
            } else if (propertyType.getFurnishedType().equals("FullFurnished")) {
                furnishingStatus.setText("Full-Furnished");
            } else {
                if (propertyType.getType().equalsIgnoreCase("LAND")) {
                    dotView.setVisibility(View.VISIBLE);
                    propertyTypeValue.setText("PLOT");
                    furnishingStatus.setText(UtilityMethods.getAreaInYards(propertyType.getSuperArea()) + " Sq. yd");
                } else {
                    furnishingStatus.setText("Unfurnished");
                }
            }
        }*/
        if(propertyType != null && propertyType.getType() != null && !propertyType.getType().isEmpty()){
            if (propertyType.getType().equalsIgnoreCase(Constants.AppConstants.PropertyType.Land.toString())) {
                propertyTypeValue.setText("PLOT");
                String areaStr = UtilityMethods.getArea(mActivity,propertyType.getSuperArea(),true);
                String unit = UtilityMethods.getUnit(mActivity,true);
                furnishingStatus.setText(areaStr + unit);
            }else {
                if (propertyType.getType().equalsIgnoreCase(Constants.AppConstants.PropertyType.IndependentHouseVilla.toString())) {
                    propertyTypeValue.setText("Villa");
                }else if (propertyType.getType().equalsIgnoreCase(Constants.AppConstants.PropertyType.IndependentFloor.toString())) {
                    propertyTypeValue.setText("Independent Floor");
                }else{
                    propertyTypeValue.setText(UtilityMethods.getCommercialTypeName(propertyType.getType()));
                }
                if(!TextUtils.isEmpty(propertyType.getFurnishedType()) && !UtilityMethods.isCommercial(propertyType.getType())){
                    dotView.setVisibility(View.VISIBLE);
                    furnishingStatus.setVisibility(View.VISIBLE);
                    if (propertyType.getFurnishedType().equalsIgnoreCase("SemiFurnished")) {
                        furnishingStatus.setText("Semi-Furnished");
                    } else if (propertyType.getFurnishedType().equalsIgnoreCase("FullFurnished")) {
                        furnishingStatus.setText("Full-Furnished");
                    } else {
                        furnishingStatus.setText("Unfurnished");
                    }
                }else{
                    dotView.setVisibility(View.GONE);
                    furnishingStatus.setVisibility(View.GONE);
                }
            }
        }

        if (propertyType != null && (propertyType.getBsp() != null) && (propertyType.getBsp() != 0)) {
            if(propertyType.getType() != null && (propertyType.getType().equalsIgnoreCase("Land")
                    || UtilityMethods.isCommercialLand(propertyType.getType()))){
                String bspStr = UtilityMethods.getBSP(mActivity,propertyType.getBsp(),true);
                String unit = UtilityMethods.getUnit(mActivity,true);
                bsp.setText("BSP " + "₹" +bspStr + "/"+unit);
            }else {
                String bspStr = UtilityMethods.getBSP(mActivity,propertyType.getBsp(),false);
                String unit = UtilityMethods.getUnit(mActivity,false);
                bsp.setText("BSP " + "₹" +bspStr + "/" + unit);
            }
        }

        if(propertyType != null) {
            amenitiesList(view, propertyType.getFlatAmenities());
        }
        setPropertyDetails(view);
        setEmiViewAmount();
        setAreaProgressBars();
        if(propertyType != null && !UtilityMethods.isCommercial(propertyType.getType())) {
            setFurnishingList(view, propertyType.getFlooring(), propertyType.getFittings(), propertyType.getWalls());
        }
        setVirtualIconView();
    }

    private void setFurnishingList(View view, final List<Amenity> flooring,
                                   final List<Amenity> fittings, final List<Amenity> walls) {


        if ((flooring == null || flooring.isEmpty()) && (fittings == null || fittings.isEmpty())
                && (walls == null || walls.isEmpty()) && view != null) {
            view.findViewById(R.id.furnishing_layout).setVisibility(View.GONE);
            return;
        }

        boolean isFurnishingAvail = false;
        if(flooring != null && !flooring.isEmpty() && flooring.size() > 0){
            for(int i=0; i<flooring.size(); i++){
                Amenity furnish = flooring.get(i);
                if(furnish != null && furnish.getName() != null && !furnish.getName().isEmpty()
                        && furnish.getDetails() != null && !furnish.getDetails().isEmpty()){
                    isFurnishingAvail = true;
                    break;
                }
            }
        }
        if(!isFurnishingAvail && fittings != null && !fittings.isEmpty() && fittings.size() > 0){
            for(int i=0; i<fittings.size(); i++){
                Amenity fitting = fittings.get(i);
                if(fitting != null && fitting.getName() != null && !fitting.getName().isEmpty()
                        && fitting.getDetails() != null && !fitting.getDetails().isEmpty()){
                    isFurnishingAvail = true;
                    break;
                }
            }
        }
        if(!isFurnishingAvail && walls != null && !walls.isEmpty() && walls.size() > 0){
            for(int i=0; i<walls.size(); i++){
                Amenity wall = walls.get(i);
                if(wall != null && wall.getName() != null && !wall.getName().isEmpty()
                        && wall.getDetails() != null && !wall.getDetails().isEmpty()){
                    isFurnishingAvail = true;
                    break;
                }
            }
        }
        if(!isFurnishingAvail){
            view.findViewById(R.id.furnishing_layout).setVisibility(View.GONE);
            return;
        }
        //getActivity().findViewById(R.id.furnishing_layout).setVisibility(View.VISIBLE);
        if(view != null){
            view.findViewById(R.id.furnishing_layout).setVisibility(View.VISIBLE);
        }
        TextView viewAll = (TextView) view.findViewById(R.id.view_all_furnishing);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.furnishing_list_mini);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

            int count;
            if (flooring != null && !flooring.isEmpty()) {
                if(flooring.size() > 3) {
                    count = 4;
                }
                else{
                    count = flooring.size();
                }
            } else if(fittings != null && !fittings.isEmpty()){
                if(fittings.size() > 3) {
                    count = 4;
                }
                else{
                    count = fittings.size();
                }
            }
            else if(walls != null && !walls.isEmpty()){
                if(walls.size() > 3) {
                    count = 4;
                }
                else{
                    count = walls.size();
                }
            }else{
                view.findViewById(R.id.furnishing_layout).setVisibility(View.GONE);
                return;
            }
            viewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), FurnishingListActivity.class);
                    List<AmenityObject> flooringList = new ArrayList<AmenityObject>();
                    for (int i = 0; i < flooring.size(); i++) {
                        AmenityObject amenityObject = new AmenityObject();
                        amenityObject.Name = flooring.get(i).getName();
                        amenityObject.Type = flooring.get(i).getDetails();
                        flooringList.add(amenityObject);
                    }

                    List<AmenityObject> fittingList = new ArrayList<AmenityObject>();
                    for (int i = 0; i < fittings.size(); i++) {
                        AmenityObject amenityObject = new AmenityObject();
                        amenityObject.Name = fittings.get(i).getName();
                        amenityObject.Type = fittings.get(i).getDetails();
                        fittingList.add(amenityObject);
                    }

                    List<AmenityObject> wallsList = new ArrayList<AmenityObject>();
                    for (int i = 0; i < walls.size(); i++) {
                        AmenityObject amenityObject = new AmenityObject();
                        amenityObject.Name = walls.get(i).getName();
                        amenityObject.Type = walls.get(i).getDetails();
                        wallsList.add(amenityObject);
                    }

                    Gson gson = new Gson();
                    String json = gson.toJson(flooringList);
                    intent.putExtra("flooring_list", json);

                    json = gson.toJson(fittingList);
                    intent.putExtra("fitting_list", json);

                    json = gson.toJson(wallsList);
                    intent.putExtra("walls_list", json);

                    startActivity(intent);
                }
            });
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(new FurnishingAdapter(getActivity(), flooring, count));
    }

    private void initView(View view) {
        if (propertyType == null)
            return;

        propertyPrice = (TextView) view.findViewById(R.id.property_price);
        possessionDetails = (TextView) view.findViewById(R.id.possession_detail);
        superAreaDetail = (TextView) view.findViewById(R.id.super_area_detail);
        builtupAreaDetail = (TextView) view.findViewById(R.id.builtup_area_detail);
        carpetAreaDetail = (TextView) view.findViewById(R.id.carpet_area_detail);

        downPaymentValue = (TextView) view.findViewById(R.id.downpaymentAmountValue);
        emiAmountValue = (TextView) view.findViewById(R.id.emiAmountValue);
        interestRateValue = (TextView) view.findViewById(R.id.interestRateValue);

        superAreaProgress = (ProgressBar) view.findViewById(R.id.super_area_progress);
        builtupAreaProgress = (ProgressBar) view.findViewById(R.id.built_up_area_progress);
        carpetAreaProgress = (ProgressBar) view.findViewById(R.id.carpet_area_progress);

        bsp = (TextView) view.findViewById(R.id.bsp_property);
        propertyTypeValue = (TextView) view.findViewById(R.id.id_property_type_value);
        dotView = (TextView)view.findViewById(R.id.id_dot);
        furnishingStatus = (TextView) view.findViewById(R.id.furnishing_status);
        emiLayout = (LinearLayout) view.findViewById(R.id.emi);
        furnishLayout = (RelativeLayout) view.findViewById(R.id.furnishing_layout);

        virtualTourIcon = view.findViewById(R.id.id_virtual_tour_icon);

        TextView customizeEmi = (TextView) view.findViewById(R.id.customizeemi);
        customizeEmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, EmiActivity.class);
                long housePrice = propertyType.getBsp() * propertyType.getSuperArea();
                intent.putExtra(Constants.IntentKeyConstants.IS_FROM_PROJECT_DETAIL, false);      //flag to evaluate from where we are calling this activity.
                intent.putExtra(Constants.IntentKeyConstants.IS_FROM_LAYOUT, true);
                intent.putExtra(Constants.IntentKeyConstants.PROPERTY_VALUE, housePrice);
                intent.putExtra(Constants.IntentKeyConstants.PROPERTY_ID, propertyType.getId());
                intent.putExtra(Constants.IntentKeyConstants.PROJECT_ID, propertyType.getProjectId());
                intent.putExtra(Constants.IntentKeyConstants.TYPE, propertyType.getType());
                intent.putExtra(Constants.IntentKeyConstants.PROPERTY_SIZE, propertyType.getSuperArea());
                intent.putExtra(Constants.IntentKeyConstants.PROPERTY_TYPE, propertyType.getNumberOfBedrooms().intValue());
                startActivity(intent);
            }
        });

        soldOutImage = view.findViewById(R.id.id_sold_out_image);
        layout_switch = (SwitchCompat) view.findViewById(R.id.layout_switch);
        layout_switch.setChecked(true);

        img = (ThreeTwoImageView) view.findViewById(R.id.img);
        mImagePB = (ProgressBar) view.findViewById(R.id.id_layout_image_pb);

        if(getActivity() != null && getActivity().getIntent().hasExtra("FromLayoutPage")
                && getActivity().getIntent().getBooleanExtra("FromLayoutPage",false)) {
            set2d3DClickHandle(true,true);
        }else{

        }
        if(propertyType.getSoldStatus() != null && propertyType.getSoldStatus()){
            img.setAlpha(0.7f);
            soldOutImage.setVisibility(View.VISIBLE);
        }else{
            img.setAlpha(1.0f);
            soldOutImage.setVisibility(View.GONE);
        }
        if (propertyType.getFloorPlan() != null) {
            Log.d(TAG, "mPage->" + mPage + "\n" + propertyType.getFloorPlan().getSurl2d() + "\n" +
                    propertyType.getFloorPlan().getSurl3d());
            if (propertyType.getFloorPlan()!= null && propertyType.getFloorPlan().getSurl2d() != null
                    && propertyType.getFloorPlan().getSurl3d() != null
                    && !TextUtils.isEmpty(propertyType.getFloorPlan().getSurl3d())
                    && !TextUtils.isEmpty(propertyType.getFloorPlan().getSurl2d())) {
                //layout_btn_2d_3d.setVisibility(View.VISIBLE);
                layout_switch.setVisibility(View.VISIBLE);

                text2d.setVisibility(View.VISIBLE);
                text3d.setVisibility(View.VISIBLE);
                Log.d(TAG, "mPage->" + mPage + "   VISIBLE");

            } else {

                layout_switch.setVisibility(View.VISIBLE);
                text2d.setVisibility(View.VISIBLE);
                text3d.setVisibility(View.VISIBLE);

                layout_switch.setAlpha(0.4f);
                text2d.setAlpha(0.4f);
                text3d.setAlpha(0.4f);
                layout_switch.setEnabled(false);

                Log.d(TAG, "mPage->" + mPage + "   GONE");
            }
        } else {
            layout_switch.setVisibility(View.VISIBLE);
            text2d.setVisibility(View.VISIBLE);
            text3d.setVisibility(View.VISIBLE);

            layout_switch.setAlpha(0.4f);
            text2d.setAlpha(0.4f);
            text3d.setAlpha(0.4f);
            layout_switch.setEnabled(false);

            Log.d(TAG, "mPage->" + mPage + "   null");
        }

        layout_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                set2d3DClickHandle(isChecked,false);
            }
        });

        final TextView price = (TextView) view.findViewById(R.id.price);
        if(propertyType.getBsp() == 0){
            setPriceOnRequestUI(propertyPrice,emiLayout);
        }else {
            price.setText(UtilityMethods.getPriceWithLACWord(propertyType.getBsp() * propertyType.getSuperArea()) + "  ");
        }

        TextView bhks = (TextView) view.findViewById(R.id.bhks);
        bhks.setText(propertyType.getNumberOfBedrooms() + Constants.AppConstants.BHK);

        ((TextView) view.findViewById(R.id.possession)).setText(getString(R.string.possession_by) + " " + UtilityMethods.getDate(propertyType.getPossessionDate(), "MMM yyyy"));
        ((TextView) view.findViewById(R.id.area)).setText(propertyType.getArea() + "sqft");


        if (propertyType.getFlooring() != null && propertyType.getFlooring().size() != 0) {

            furnishingsList.add(new ListSectionItem(getString(R.string.floors).toUpperCase()));

            for (int i = 0; i < propertyType.getFlooring().size(); i++) {
                furnishingsList.add(new ListSectionRowItems(propertyType.getFlooring().get(i).getName()
                        , propertyType.getFlooring().get(i).getDetails()));
            }
        }

        if (propertyType.getFittings() != null && propertyType.getFittings().size() != 0) {

            furnishingsList.add(new ListSectionItem(getString(fittings).toUpperCase()));

            for (int i = 0; i < propertyType.getFittings().size(); i++) {
                furnishingsList.add(new ListSectionRowItems(propertyType.getFittings().get(i).getName()
                        , propertyType.getFittings().get(i).getDetails()));
            }

        }

        if (propertyType.getWalls() != null && propertyType.getWalls().size() != 0) {

            furnishingsList.add(new ListSectionItem(getString(R.string.walls).toUpperCase()));

            for (int i = 0; i < propertyType.getWalls().size(); i++) {
                furnishingsList.add(new ListSectionRowItems(propertyType.getWalls().get(i).getName()
                        , propertyType.getWalls().get(i).getDetails()));
            }
        }
        if(propertyType.getBsp() == 0){
            setPriceOnRequestUI(propertyPrice,emiLayout);
        }else {
            propertyPrice.setText(UtilityMethods.getPriceWithLACWord(propertyType.getBsp() * propertyType.getSuperArea()) + "  ");
        }
        if (propertyType.getStatus().equals("InProgress")) {
            if(propertyType.getPossessionDate() != 0) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                possessionDetails.setText(getResources().getString(R.string.possession_by) + " " + UtilityMethods.getDate(propertyType.getPossessionDate(), "MMM yyyy"));//getResources().getStringArray(R.array.months)[cal.get(Calendar.MONTH)-1]
            }else{
                possessionDetails.setText("Possession date unavailable");
            }
            //+ " " + cal.get(Calendar.YEAR));
        } else if (propertyType.getStatus().equals("ReadyToMove")) {
            possessionDetails.setText("Ready To Move");
        } else {
            possessionDetails.setText("Possession date unavailable");
        }
        Log.d(TAG, "propertyTypeStatus->" + propertyType.getStatus());

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((propertyType.getFloorPlan() != null) && (!propertyType.getFloorPlan().isEmpty())) {
                    Intent intent = new Intent(getActivity(), PhotoZoomActivity.class);
                    if (layout_switch.isChecked()) {
                        if ((propertyType.getFloorPlan().getSurl2d() != null) && (!propertyType.getFloorPlan().getSurl2d().isEmpty())) {
                            intent.putExtra(Constants.IntentKeyConstants.FLOOR_PLAN, propertyType.getFloorPlan().getSurl2d());
                            startActivity(intent);
                        }
                    } else {
                        if ((propertyType.getFloorPlan().getSurl3d() != null) && (!propertyType.getFloorPlan().getSurl3d().isEmpty())) {
                            intent.putExtra(Constants.IntentKeyConstants.FLOOR_PLAN, propertyType.getFloorPlan().getSurl3d());
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }

    private void setEmiViewAmount() {
        if(propertyType == null){
            return;
        }
        long housePrice = propertyType.getBsp() * propertyType.getSuperArea();
        float interestRate = UtilityMethods.getFloatInPref(getActivity(), Constants.AppConfigConstants.DEFAULT_INTEREST_RATE, 8.60f);
        String downPaymentPercentageStr = "";
        double downPaymentPercentage = 0.0;

        if(propertyType.getType().equalsIgnoreCase("Land") || UtilityMethods.isCommercial(propertyType.getType())) {
            downPaymentPercentage = 0.40;
            downPaymentPercentageStr = " (40%)";
        }else{
            downPaymentPercentage = 0.20;
            downPaymentPercentageStr = " (20%)";
        }

        double downPayment = housePrice * downPaymentPercentage;
        double principleAmount = housePrice - downPayment;
        String emiValue = UtilityMethods.calculateEmiAmount(principleAmount, interestRate, 20);
        downPaymentValue.setText(UtilityMethods.getPriceWordTillThousand((long) (downPayment)) + downPaymentPercentageStr);
        emiAmountValue.setText(emiValue);
        interestRateValue.setText(String.format("%.2f",interestRate) + "");

    }

    private void setPropertyDetails(View v) {
        final List<PropertyDetail> details = new ArrayList<>();
        if (propertyType != null && propertyType.getNumberOfBedrooms() != null && propertyType.getNumberOfBedrooms() > 0) {
            PropertyDetail detail = new PropertyDetail();

            detail.count = propertyType.getNumberOfBedrooms();
            if (propertyType.getNumberOfBedrooms() == 1) {
                detail.label = mActivity.getString(R.string.bedroom);
            } else {
                detail.label = mActivity.getString(R.string.bedrooms);
            }
            detail.icon = ContextCompat.getDrawable(getActivity(), R.drawable.bedroom_black);
            details.add(detail);
        }
        if (propertyType != null && propertyType.getNumberBathroom() != null && propertyType.getNumberBathroom() > 0) {
            PropertyDetail detail = new PropertyDetail();

            detail.count = propertyType.getNumberBathroom();
            if (propertyType.getNumberBathroom() == 1) {
                detail.label = mActivity.getString(R.string.bathroom);
            } else {
                detail.label = mActivity.getString(R.string.bathrooms);
            }

            detail.icon = ContextCompat.getDrawable(getActivity(), R.drawable.bathroom_black);
            details.add(detail);
        }
        if (propertyType != null && propertyType.getNumberOfBalconies() != null && propertyType.getNumberOfBalconies() > 0) {
            PropertyDetail detail = new PropertyDetail();
            detail.count = propertyType.getNumberOfBalconies();
            if (propertyType.getNumberOfBalconies() == 1) {
                detail.label = mActivity.getString(R.string.balcony);
            } else {
                detail.label = mActivity.getString(R.string.balconies);
            }

            detail.icon = ContextCompat.getDrawable(getActivity(), R.drawable.balcony_black);
            details.add(detail);
        }
        if (propertyType != null && propertyType.getNumberOfStoreRoom() != null && propertyType.getNumberOfStoreRoom() > 0) {
            PropertyDetail detail = new PropertyDetail();
            detail.count = propertyType.getNumberOfStoreRoom();
            if (propertyType.getNumberOfStoreRoom() == 1) {
                detail.label = mActivity.getString(R.string.store_room);
            } else {
                detail.label = mActivity.getString(R.string.store_rooms);
            }
            detail.icon = ContextCompat.getDrawable(getActivity(), R.drawable.store_black);
            details.add(detail);
        }
        if (propertyType != null && propertyType.getNumberOfStudyRoom() != null && propertyType.getNumberOfStudyRoom() > 0) {
            PropertyDetail detail = new PropertyDetail();
            detail.count = propertyType.getNumberOfStudyRoom();
            if (propertyType.getNumberOfStudyRoom() == 1) {
                detail.label = mActivity.getString(R.string.study_room);
            } else {
                detail.label = mActivity.getString(R.string.study_rooms);
            }
            detail.icon = ContextCompat.getDrawable(getActivity(), R.drawable.study_black);
            details.add(detail);
        }
        if (propertyType != null && propertyType.getNumberOfSarventRoom() != null && propertyType.getNumberOfSarventRoom() > 0) {
            PropertyDetail detail = new PropertyDetail();
            detail.count = propertyType.getNumberOfSarventRoom();
            if (propertyType.getNumberOfSarventRoom() == 1) {
                detail.label = mActivity.getString(R.string.servant_room);
            } else {
                detail.label = mActivity.getString(R.string.servant_rooms);
            }

            detail.icon = ContextCompat.getDrawable(getActivity(), R.drawable.servant_room_black);
            details.add(detail);
        }
        if (propertyType != null && propertyType.getNumberOfPoojaRoom() != null && propertyType.getNumberOfPoojaRoom() > 0) {
            PropertyDetail detail = new PropertyDetail();
            detail.count = propertyType.getNumberOfPoojaRoom();
            if (propertyType.getNumberOfPoojaRoom() == 1) {
                detail.label = mActivity.getString(R.string.pooja_room);
            } else {
                detail.label = mActivity.getString(R.string.pooja_rooms);
            }

            detail.icon = ContextCompat.getDrawable(getActivity(), R.drawable.prayer_room_black);
            details.add(detail);
        }
        if (propertyType != null && propertyType.getKidsRoom() != null && propertyType.getKidsRoom() > 0) {
            PropertyDetail detail = new PropertyDetail();
            detail.count = propertyType.getKidsRoom();
            if (propertyType.getKidsRoom() == 1) {
                detail.label = mActivity.getString(R.string.kids_room);
            } else {
                detail.label = mActivity.getString(R.string.kids_rooms);
            }
            detail.icon = ContextCompat.getDrawable(getActivity(), R.drawable.childrenroom_black);
            details.add(detail);
        }

        final List<PropertyDetail> detailArrayList = details;

        if (detailArrayList == null || detailArrayList.isEmpty()) {
            if(v != null) {
                v.findViewById(R.id.configuration_layout).setVisibility(View.GONE);
            }
        }else if (propertyType != null && UtilityMethods.isCommercial(propertyType.getType())) {
            if(v != null) {
                v.findViewById(R.id.configuration_layout).setVisibility(View.GONE);
            }
        }else {
            final TextView moreDetails = (TextView) v.findViewById(R.id.configuration_more_less);
            RecyclerView layoutConfigurationList = (RecyclerView) v.findViewById(R.id.configuration_list);
            layoutConfigurationList.setNestedScrollingEnabled(false);
            layoutConfigurationList.setLayoutManager(new GridLayoutManagerWrapped(getActivity(),
                    getResources().getInteger(R.integer.ameities_column_count)));
            final LayoutConfigurationAdapter layoutConfigurationAdapter = new LayoutConfigurationAdapter(getActivity(), detailArrayList);
            layoutConfigurationList.setItemAnimator(new SlideInDownAnimator(new OvershootInterpolator(0.5f)));
            layoutConfigurationList.setAdapter(layoutConfigurationAdapter);

            if (detailArrayList.size() > getResources().getInteger(R.integer.ameities_column_count)) {
                moreDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (expand_config) {
                            moreDetails.setText(getResources().getString(R.string.less));
                            layoutConfigurationAdapter.itemCount = detailArrayList.size();
                            layoutConfigurationAdapter.notifyItemRangeInserted(getResources().getInteger(R.integer.ameities_column_count),
                                    detailArrayList.size() - getResources().getInteger(R.integer.ameities_column_count));
                            //layoutConfigurationAdapter.notifyDataSetChanged();
                        } else {
                            moreDetails.setText(getResources().getString(R.string.view_all));
                            layoutConfigurationAdapter.itemCount = getResources().getInteger(R.integer.ameities_column_count);
                            layoutConfigurationAdapter.notifyItemRangeRemoved(getResources().getInteger(R.integer.ameities_column_count),
                                    detailArrayList.size() - getResources().getInteger(R.integer.ameities_column_count));
                            //layoutConfigurationAdapter.notifyDataSetChanged();
                        }
                        expand_config = !expand_config;
                    }
                });
            } else {
                moreDetails.setVisibility(View.GONE);
            }
        }
    }

    private void set2d3DClickHandle(boolean isChecked, boolean isFromLayout) {
        if(isFirstTimeLoad) {
            isFirstTimeLoad = false;
            try {
                Bitmap imageBitmap = ((HousingApplication) getActivity().getApplicationContext()).getLayoutImageBitmap();
                long propertyId = ((HousingApplication) getActivity().getApplicationContext()).getPropertyLayoutId();
                boolean is2DPicture = ((HousingApplication) getActivity().getApplicationContext()).isIs2DPicture();
                if (imageBitmap != null && propertyId == propertyType.getId()) {
                    Log.i(TAG, "Shown already loaded picture....");
                    if (is2DPicture) {
                        img.setImageBitmap(imageBitmap);
                    } else {
                        layout_switch.setChecked(false);
                        img.setImageBitmap(imageBitmap);
                    }
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (isChecked) {
            img.setImageResource(R.drawable.layout_unavailable);
            try {
                if(propertyType.getFloorPlan() != null && !propertyType.getFloorPlan().isEmpty()) {
                    if (propertyType.getFloorPlan().getSurl2d() != null && !propertyType.getFloorPlan().getSurl2d().isEmpty()) {
                        mImagePB.setVisibility(View.VISIBLE);
                        Picasso.get()
                                .load(propertyType.getFloorPlan().getSurl2d())
                                .placeholder(R.drawable.layout_unavailable)
                                .error(R.drawable.layout_placeholder_small)
                                .into(img, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        mImagePB.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        mImagePB.setVisibility(View.GONE);
                                    }
                                });
                    } else if (propertyType.getImages() != null && propertyType.getImages().get(0) != null
                            && propertyType.getImages().get(0).getSURL() != null && !propertyType.getImages().get(0).getSURL().isEmpty()) {
                        mImagePB.setVisibility(View.VISIBLE);
                        Picasso.get()
                                .load(propertyType.getImages().get(0).getSURL())
                                .placeholder(R.drawable.layout_unavailable)
                                .error(R.drawable.layout_placeholder_small)
                                .into(img, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        mImagePB.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        mImagePB.setVisibility(View.GONE);
                                    }
                                });
                    } else if(isFromLayout && propertyType.getFloorPlan().getSurl3d() != null
                            && !propertyType.getFloorPlan().getSurl3d().isEmpty()){
                        layout_switch.setChecked(false);
                        mImagePB.setVisibility(View.VISIBLE);
                        Picasso.get()
                                .load(propertyType.getFloorPlan().getSurl3d())
                                .placeholder(R.drawable.layout_unavailable)
                                .error(R.drawable.layout_placeholder_small)
                                .into(img, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        mImagePB.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        mImagePB.setVisibility(View.GONE);
                                    }
                                });
                    } else {
                        img.setImageResource(R.drawable.layout_placeholder_small);
                    }
                }
            } catch (Exception ex) {
                img.setImageResource(R.drawable.layout_placeholder_small);
            }
        } else {
            try {
                if(propertyType.getFloorPlan() != null && !propertyType.getFloorPlan().isEmpty()) {
                    mImagePB.setVisibility(View.VISIBLE);
                    if (propertyType.getFloorPlan().getSurl3d() != null
                            && !propertyType.getFloorPlan().getSurl3d().isEmpty()) {
                        Picasso.get()
                                .load(propertyType.getFloorPlan().getSurl3d())
                                .placeholder(R.drawable.layout_unavailable)
                                .error(R.drawable.layout_placeholder_small)
                                .into(img, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        mImagePB.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        mImagePB.setVisibility(View.GONE);
                                    }
                                });
                    } else if(isFromLayout && propertyType.getFloorPlan().getSurl2d() != null
                            && !propertyType.getFloorPlan().getSurl2d().isEmpty()){
                        layout_switch.setChecked(true);
                        mImagePB.setVisibility(View.VISIBLE);
                        Picasso.get()
                                .load(propertyType.getFloorPlan().getSurl2d())
                                .placeholder(R.drawable.layout_unavailable)
                                .error(R.drawable.layout_placeholder_small)
                                .into(img, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        mImagePB.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        mImagePB.setVisibility(View.GONE);
                                    }
                                });
                    } else {
                        img.setImageResource(R.drawable.layout_placeholder_small);
                    }
                }
            } catch (Exception ex) {
                img.setImageResource(R.drawable.layout_placeholder_small);
            }
        }
    }

    private void setAreaProgressBars() {
        if(propertyType != null) {
            int superArea = propertyType.getSuperArea();
            int builtArea = propertyType.getArea();
            int carpetArea = propertyType.getCarpetArea();
            int builtAreaOccupied = superArea != 0 ? ((builtArea * 100) / superArea) : 0;
            int carpetAreaOccupied = superArea != 0 ? ((carpetArea * 100) / superArea) : 0;

            Log.d(TAG, "builtArea->" + builtAreaOccupied + " carpetArea->" + carpetAreaOccupied);
            superAreaProgress.setProgress(100);
            builtupAreaProgress.setProgress(builtAreaOccupied);
            carpetAreaProgress.setProgress(carpetAreaOccupied);

            superAreaDetail.setText(propertyType.getSuperArea() + " Sqft.");
            builtupAreaDetail.setText(propertyType.getArea() + " Sqft. (" + builtAreaOccupied + "%)");
            carpetAreaDetail.setText(propertyType.getCarpetArea() + " Sqft. (" + carpetAreaOccupied + "%)");
        }
    }

    private void amenitiesList(View view, final List<Amenity> amenityList) {
        if (( (amenityList == null) || (amenityList.isEmpty()) ) && view != null) {
            //getActivity().findViewById(R.id.content_amenities_layout).setVisibility(View.GONE);
            view.findViewById(R.id.content_amenities_layout).setVisibility(View.GONE);
            return;
        }
        boolean isAmenitiesAvail = false;
        if(amenityList != null && !amenityList.isEmpty() && amenityList.size() > 0){
            for(int i=0; i<amenityList.size(); i++){
                Amenity amenity = amenityList.get(i);
                if(amenity != null && amenity.getName() != null && !amenity.getName().isEmpty()
                        && amenity.getDetails() != null && !amenity.getDetails().isEmpty()){
                    isAmenitiesAvail = true;
                    break;
                }
            }
        }
        if(!isAmenitiesAvail){
            view.findViewById(R.id.content_amenities_layout).setVisibility(View.GONE);
            return;
        }

//        getActivity().findViewById(R.id.content_amenities_layout).setVisibility(View.VISIBLE);
        if(view != null) {
            view.findViewById(R.id.content_amenities_layout).setVisibility(View.VISIBLE);
        }
        final TextView moreAmenities = (TextView) view.findViewById(R.id.amenities_more_less);
        final RecyclerView amenities = (RecyclerView) view.findViewById(R.id.amenities_list);
        amenities.setNestedScrollingEnabled(false);
        final AmenitiesGridAdapter amenitiesGridAdapter = new AmenitiesGridAdapter(getActivity(), amenityList);
        amenities.setLayoutManager(new GridLayoutManagerWrapped(getActivity(),
                getResources().getInteger(R.integer.ameities_column_count)));
        amenities.setItemAnimator(new SlideInDownAnimator(new OvershootInterpolator(0.5f)));

        amenities.setAdapter(amenitiesGridAdapter);
        if (amenityList.size() > getResources().getInteger(R.integer.ameities_column_count)) {
            moreAmenities.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d(TAG, "clicked On more!");

                    if (expand) {
                        Log.d(TAG, "Expand!");
                        moreAmenities.setText(getResources().getString(R.string.less));
                        amenitiesGridAdapter.itemCount = amenityList.size();
                        amenitiesGridAdapter.notifyItemRangeInserted(getResources().getInteger(R.integer.ameities_column_count),
                                amenityList.size() - getResources().getInteger(R.integer.ameities_column_count));

                        //amenitiesGridAdapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "Collapse!");
                        moreAmenities.setText(getResources().getString(R.string.view_all));
                        amenitiesGridAdapter.itemCount = getResources().getInteger(R.integer.ameities_column_count);
                        amenitiesGridAdapter.notifyItemRangeRemoved(getResources().getInteger(R.integer.ameities_column_count),
                                amenityList.size() - getResources().getInteger(R.integer.ameities_column_count));
                        //amenitiesGridAdapter.notifyDataSetChanged();
                    }
                    expand = !expand;
                    //amenitiesGridAdapter.notifyDataSetChanged();
                }
            });
        } else {
            moreAmenities.setVisibility(View.GONE);
        }
    }

    private void setPriceOnRequestUI(TextView propertyPrice, LinearLayout emiLayout){
        //priceRange Text UI change
        propertyPrice.setText(getResources().getString(R.string.price_on_request));
        propertyPrice.setTextSize(14);
        propertyPrice.setPadding(10,2,10,5);
        UtilityMethods.setDrawableBackground(getActivity(),propertyPrice,R.drawable.button_red_border);
        propertyPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: Price on request");
                if(getActivity() != null){
                    ((PropertyLayoutActivity)getActivity()).handleCallMeBackRequest();
                }
            }
        });

        //bsp Text UI change
        emiLayout.setVisibility(View.GONE);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,getActivity().getResources().getDimensionPixelSize(R.dimen.margin_40));
        furnishLayout.setLayoutParams(layoutParams);
    }


    public void setVirtualIconView(){
        try {
            if(propertyType.getVirtualTour() != null && !propertyType.getVirtualTour().isEmpty() && virtualTourIcon != null){
                final Video video = propertyType.getVirtualTour();
                virtualTourIcon.setVisibility(View.VISIBLE);
                if(video != null && !TextUtils.isEmpty(video.getUrl())){
                    virtualTourIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!UtilityMethods.isInternetConnected(virtualTourIcon.getContext())){
                                UtilityMethods.showErrorSnackbarOnTop((PropertyLayoutActivity)virtualTourIcon.getContext());
                                return;
                            }
                            Intent intent = new Intent(virtualTourIcon.getContext(),VirtualTourViewActivity.class);
                            intent.putExtra("URL",video.getUrl().trim());
                            intent.putExtra("Title",video.getName().trim());
                            startActivity(intent);
                        }
                    });
                }
            }else{
                if(virtualTourIcon != null) {
                    virtualTourIcon.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
