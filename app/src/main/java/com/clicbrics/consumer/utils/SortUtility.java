package com.clicbrics.consumer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.view.fragment.MapFragment;
import com.clicbrics.consumer.view.fragment.ProjectListFragment;

/**
 * Created by Alok on 22-04-2019.
 */
public class SortUtility {

    private ISortSelectionListener listener;
    private Constants.SortType sortType;
    private Activity mActivity;

    public interface ISortSelectionListener{
        void onSortApplied(Constants.SortType sortType);
    }

    public SortUtility(ProjectListFragment projectListFragment){
        listener = projectListFragment;
        mActivity = projectListFragment.getActivity();
        checkSelectedSortingType(projectListFragment.getContext());
    }

    public SortUtility(MapFragment mapFragment){
        listener = mapFragment;
        checkSelectedSortingType(mapFragment.getContext());
    }

    private void checkSelectedSortingType(Context context){
        if(UtilityMethods.getBooleanInPref(context, Constants.SharedPreferConstants.SORT_TYPE_RELEVANCE, false)){
            setSortType(Constants.SortType.Relevance);
        }else if(UtilityMethods.getBooleanInPref(context, Constants.SharedPreferConstants.SORT_TYPE_LOW_TO_HIGH_PRICE, false)) {
            setSortType(Constants.SortType.PriceASC);
        }else if(UtilityMethods.getBooleanInPref(context, Constants.SharedPreferConstants.SORT_TYPE_HIGH_TO_LOW_PRICE, false)) {
            setSortType(Constants.SortType.PriceDESC);
        }else if(UtilityMethods.getBooleanInPref(context, Constants.SharedPreferConstants.SORT_TYPE_LOW_TO_HIGH_AREA, false)) {
            setSortType(Constants.SortType.AreaASC);
        }else if(UtilityMethods.getBooleanInPref(context, Constants.SharedPreferConstants.SORT_TYPE_HIGH_TO_LOW_AREA, false)) {
            setSortType(Constants.SortType.AreaDESC);
        }
    }

    public void showSortDialog(final Activity activity) {
        AlertDialog.Builder sortDialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sort_dialog, null);

        sortDialogBuilder.setView(dialogView);

        final TextView relevanceSort = dialogView.findViewById(R.id.relevance_sort);
        final TextView lowToHighPriceSort = dialogView.findViewById(R.id.low_to_high_price_sort);
        final TextView highToLowPriceSort = dialogView.findViewById(R.id.high_to_low_price_sort);
        final TextView lowToHighAreaSort = dialogView.findViewById(R.id.low_to_high_area_sort);
        final TextView highToLowAreaSort = dialogView.findViewById(R.id.high_to_low_area_sort);

        if (UtilityMethods.getBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_RELEVANCE, false)) {
            relevanceSort.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
            relevanceSort.setSelected(true);
            lowToHighPriceSort.setSelected(false);
            highToLowPriceSort.setSelected(false);
            lowToHighAreaSort.setSelected(false);
            highToLowAreaSort.setSelected(false);
        } else if(UtilityMethods.getBooleanInPref(activity,Constants.SharedPreferConstants.SORT_TYPE_HIGH_TO_LOW_PRICE,false)){
            highToLowPriceSort.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
            highToLowPriceSort.setSelected(true);
            lowToHighPriceSort.setSelected(false);
            relevanceSort.setSelected(false);
            lowToHighAreaSort.setSelected(false);
            highToLowAreaSort.setSelected(false);
        } else if(UtilityMethods.getBooleanInPref(activity,Constants.SharedPreferConstants.SORT_TYPE_LOW_TO_HIGH_PRICE,false)){
            lowToHighPriceSort.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
            lowToHighPriceSort.setSelected(true);
            highToLowPriceSort.setSelected(false);
            relevanceSort.setSelected(false);
            lowToHighAreaSort.setSelected(false);
            highToLowAreaSort.setSelected(false);
        } else if (UtilityMethods.getBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_LOW_TO_HIGH_AREA, false)) {
            lowToHighAreaSort.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
            lowToHighAreaSort.setSelected(true);
            highToLowAreaSort.setSelected(false);
            relevanceSort.setSelected(false);
            lowToHighPriceSort.setSelected(false);
            highToLowPriceSort.setSelected(false);
        } else if (UtilityMethods.getBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_HIGH_TO_LOW_AREA, false)) {
            highToLowAreaSort.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
            highToLowAreaSort.setSelected(true);
            lowToHighAreaSort.setSelected(false);
            relevanceSort.setSelected(false);
            lowToHighPriceSort.setSelected(false);
            highToLowPriceSort.setSelected(false);
        }
        relevanceSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relevanceSort.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
                lowToHighPriceSort.setTextColor(ContextCompat.getColor(activity, R.color.text_color_login_reg));
                highToLowPriceSort.setTextColor(ContextCompat.getColor(activity, R.color.text_color_login_reg));
                lowToHighAreaSort.setTextColor(ContextCompat.getColor(activity, R.color.text_color_login_reg));
                highToLowAreaSort.setTextColor(ContextCompat.getColor(activity, R.color.text_color_login_reg));
                //UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_DISTANCE, true);
                setSortType(Constants.SortType.Relevance);
                relevanceSort.setSelected(true);
                lowToHighPriceSort.setSelected(false);
                highToLowPriceSort.setSelected(false);
                lowToHighAreaSort.setSelected(false);
                highToLowAreaSort.setSelected(false);
            }
        });

        lowToHighPriceSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lowToHighPriceSort.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
                highToLowPriceSort.setTextColor(ContextCompat.getColor(activity, R.color.text_color_login_reg));
                relevanceSort.setTextColor(ContextCompat.getColor(activity, R.color.text_color_login_reg));
                lowToHighAreaSort.setTextColor(ContextCompat.getColor(activity, R.color.text_color_login_reg));
                highToLowAreaSort.setTextColor(ContextCompat.getColor(activity, R.color.text_color_login_reg));
                //UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_DISTANCE, false);
                setSortType(Constants.SortType.PriceASC);
                lowToHighPriceSort.setSelected(true);
                highToLowPriceSort.setSelected(false);
                relevanceSort.setSelected(false);
                lowToHighAreaSort.setSelected(false);
                highToLowAreaSort.setSelected(false);
            }
        });

        highToLowPriceSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highToLowPriceSort.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
                lowToHighPriceSort.setTextColor(ContextCompat.getColor(activity, R.color.text_color_login_reg));
                relevanceSort.setTextColor(ContextCompat.getColor(activity, R.color.text_color_login_reg));
                lowToHighAreaSort.setTextColor(ContextCompat.getColor(activity, R.color.text_color_login_reg));
                highToLowAreaSort.setTextColor(ContextCompat.getColor(activity, R.color.text_color_login_reg));

                //UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_DISTANCE, false);
                setSortType(Constants.SortType.PriceDESC);
                highToLowPriceSort.setSelected(true);
                lowToHighPriceSort.setSelected(false);
                relevanceSort.setSelected(false);
                lowToHighAreaSort.setSelected(false);
                highToLowAreaSort.setSelected(false);
            }
        });

        lowToHighAreaSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lowToHighAreaSort.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
                highToLowAreaSort.setTextColor(ContextCompat.getColor(activity, R.color.text_color_login_reg));
                relevanceSort.setTextColor(ContextCompat.getColor(activity, R.color.text_color_login_reg));
                lowToHighPriceSort.setTextColor(ContextCompat.getColor(activity, R.color.text_color_login_reg));
                highToLowPriceSort.setTextColor(ContextCompat.getColor(activity, R.color.text_color_login_reg));
                //UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_DISTANCE, true);
                setSortType(Constants.SortType.AreaASC);
                lowToHighAreaSort.setSelected(true);
                highToLowAreaSort.setSelected(false);
                relevanceSort.setSelected(false);
                lowToHighPriceSort.setSelected(false);
                highToLowPriceSort.setSelected(false);
            }
        });

        highToLowAreaSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highToLowAreaSort.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
                lowToHighAreaSort.setTextColor(ContextCompat.getColor(activity, R.color.text_color_login_reg));
                relevanceSort.setTextColor(ContextCompat.getColor(activity, R.color.text_color_login_reg));
                lowToHighPriceSort.setTextColor(ContextCompat.getColor(activity, R.color.text_color_login_reg));
                highToLowPriceSort.setTextColor(ContextCompat.getColor(activity, R.color.text_color_login_reg));

                //UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_DISTANCE, true);
                setSortType(Constants.SortType.AreaDESC);
                highToLowAreaSort.setSelected(true);
                lowToHighAreaSort.setSelected(false);
                relevanceSort.setSelected(false);
                lowToHighPriceSort.setSelected(false);
                highToLowPriceSort.setSelected(false);
            }
        });

        sortDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(relevanceSort.isSelected()){
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_LOW_TO_HIGH_AREA, false);
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_HIGH_TO_LOW_AREA, false);
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_RELEVANCE, true);
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_HIGH_TO_LOW_PRICE, false);
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_LOW_TO_HIGH_PRICE, false);
                    listener.onSortApplied(Constants.SortType.Relevance);
                }else if(highToLowPriceSort.isSelected()){
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_LOW_TO_HIGH_AREA, false);
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_HIGH_TO_LOW_AREA, false);
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_RELEVANCE, false);
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_HIGH_TO_LOW_PRICE, true);
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_LOW_TO_HIGH_PRICE, false);
                    listener.onSortApplied(Constants.SortType.PriceDESC);
                }else if(lowToHighPriceSort.isSelected()){
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_RELEVANCE, false);
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_HIGH_TO_LOW_PRICE, false);
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_LOW_TO_HIGH_PRICE, true);
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_LOW_TO_HIGH_AREA, false);
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_HIGH_TO_LOW_AREA, false);
                    listener.onSortApplied(Constants.SortType.PriceASC);
                }else if(lowToHighAreaSort.isSelected()){
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_LOW_TO_HIGH_AREA, true);
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_HIGH_TO_LOW_AREA, false);
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_RELEVANCE, false);
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_HIGH_TO_LOW_PRICE, false);
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_LOW_TO_HIGH_PRICE, false);
                    listener.onSortApplied(Constants.SortType.AreaASC);
                }else if(highToLowAreaSort.isSelected()){
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_HIGH_TO_LOW_AREA, true);
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_LOW_TO_HIGH_AREA, false);
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_RELEVANCE, false);
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_HIGH_TO_LOW_PRICE, false);
                    UtilityMethods.saveBooleanInPref(activity, Constants.SharedPreferConstants.SORT_TYPE_LOW_TO_HIGH_PRICE, false);
                    listener.onSortApplied(Constants.SortType.AreaDESC);
                }
                new EventAnalyticsHelper().ItemClickEvent(mActivity, Constants.AnaylticsClassName.HomeScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.ok.toString());
            }
        });
        sortDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("SortUtility", "onClick: =======");
                new EventAnalyticsHelper().ItemClickEvent(mActivity, Constants.AnaylticsClassName.HomeScreen,
                        null, Constants.AnalyticsEvents.ON_CLICK_ACTION,Constants.EventClickName.cancel.toString());
            }
        });

        final AlertDialog alertDialog = sortDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
                Typeface typefaceMedium = Typeface.createFromAsset(activity.getResources().getAssets(), "fonts/FiraSans-Medium.ttf");
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(typefaceMedium);
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(typefaceMedium);
            }
        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

        alertDialog.getWindow().setLayout((int) (UtilityMethods.getScreenWidth(activity) * 0.80f),
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public Constants.SortType getSortType() {
        return sortType;
    }

    private void setSortType(Constants.SortType sortType) {
        this.sortType = sortType;
    }
}
