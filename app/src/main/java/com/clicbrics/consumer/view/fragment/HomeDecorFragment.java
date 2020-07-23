package com.clicbrics.consumer.view.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buy.housing.backend.decorEndPoint.model.Decor;
import com.buy.housing.backend.decorEndPoint.model.DecorListResponse;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.Presenter.HomeDecorPresenterContract;
import com.clicbrics.consumer.Presenter.HomeDecorPresenterImpl;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.DecorDetailViewActivity;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.databinding.FragmentHomeDecorBinding;
import com.clicbrics.consumer.fragment.BaseFragment;
import com.clicbrics.consumer.model.HomeDecorModel;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.adapter.HomeDecorListAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeDecorFragment extends BaseFragment implements HomeDecorPresenterContract.View{

    private static final String TAG = "HomeDecorFragment";
    private HomeDecorPresenterContract.Presenter presenter;
    private int mDecorTotalCount = 0;
    private final int OFFSET_DIFF = 15;
    private boolean isLoading = false;
    private HomeDecorListAdapter mHomeDecorListAdapter;
    private List<Decor> mDecorList = new ArrayList<>();
    private View mView;
    private FragmentHomeDecorBinding binding;

    public HomeDecorFragment() {
        // Required empty public constructor
    }

    public static HomeDecorFragment newInstance(){
        return new HomeDecorFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    long onStartTime;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home_decor,container,false);
        mView  = binding.getRoot();
        onStartTime = System.currentTimeMillis();
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new HomeDecorPresenterImpl(this,new HomeDecorModel());
        presenter.getView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setRecyclerView(List<Decor> decorList){
        mDecorList = decorList;
        if(getActivity() == null){
            return;
        }
        RecyclerView recyclerView = mView.findViewById(R.id.home_decor_recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mHomeDecorListAdapter = new HomeDecorListAdapter(getActivity(),decorList,mDecorTotalCount);
        ((HousingApplication) getActivity().getApplicationContext()).setDecorList(mDecorList);
        recyclerView.setAdapter(mHomeDecorListAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    Log.i(TAG, "onScrollStateChanged: SCROLL_STATE_FLING");
                } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    Log.i(TAG, "onScrollStateChanged: SCROLL_STATE_TOUCH_SCROLL");
                } else {
                    Log.i(TAG, "onScrollStateChanged: SCROLL_STATE_IDLE");
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0){
                    Log.i(TAG, "onScrolled: Scrolling UP");
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && totalItemCount < mDecorTotalCount) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0) {
                            presenter.loadNextPage(totalItemCount,OFFSET_DIFF);
                        }
                    }
                }else{
                    Log.i(TAG, "onScrolled: Scrolling DOWN");
                }
                /*int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPos = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPos = layoutManager.findLastVisibleItemPosition();


                if(totalItemCount < 0 || firstVisibleItemPos < 0 || lastVisibleItemPos < 0){
                    return;
                }
                if(totalItemCount < mDecorTotalCount){
                    *//*if(!UtilityMethods.isInternetConnected(getActivity())){
                        return;
                    }*//*
                    if(!isLoading && (lastVisibleItemPos+1) == totalItemCount){
                        isLoading = true;
                        //currentOffset = currentOffset + OFFSET_DIFF;
                        currentOffset = totalItemCount + OFFSET_DIFF;
                        presenter.loadNextPage(currentOffset,OFFSET_DIFF);
                    }
                }*/
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(presenter != null){
            presenter.onDestroy();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        new EventAnalyticsHelper().logUsageStatsEvents(getActivity(),onStartTime,System.currentTimeMillis(), Constants.AnaylticsClassName.HomeDecor);
    }

    @Override
    public void showProgress() {
        isLoading = true;
        //binding.idProgressLayout.setVisibility(View.VISIBLE);
        //showProgressBar();
        if(getActivity() != null) {
            binding.idShimmerLayout.setVisibility(View.VISIBLE);
            binding.idShimmerLayout.startShimmer();
        }
    }

    @Override
    public void hideProgress() {
        //binding.idProgressLayout.setVisibility(View.GONE);
        //dismissProgressBar();
        if(getActivity() != null) {
            binding.idShimmerLayout.stopShimmer();
            binding.idShimmerLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void setDecorView(DecorListResponse decorListResponse) {
        String errorMsg = "";
        if(decorListResponse != null){
            if(decorListResponse.getStatus()){
                isLoading = false;
                if(decorListResponse.getDecors() != null && !decorListResponse.getDecors().isEmpty()) {
                    new EventAnalyticsHelper().logAPICallEvent(getActivity(), Constants.AnaylticsClassName.HomeDecor,
                            null, Constants.ApiName.getDecorList.toString(),Constants.AnalyticsEvents.SUCCESS,null);
                    mDecorTotalCount = decorListResponse.getCount() != null ? decorListResponse.getCount() : decorListResponse.getDecors().size() ;
                    Log.i(TAG, "setDecorView: Total Decor count " + mDecorTotalCount);
                    if(getActivity() != null) {
                        setSuccessView();
                        setRecyclerView(decorListResponse.getDecors());
                    }
                }else{
                    errorMsg = "Decor list empty";
                }
            }else if(decorListResponse.getErrorMessage() != null && !decorListResponse.getErrorMessage().isEmpty()){
                errorMsg = decorListResponse.getErrorMessage();
            }else{
                errorMsg = "Error getting home decor list,please retry.";
            }
        }else{
            errorMsg = "Error getting home decor list,please retry.";
        }
        if(!TextUtils.isEmpty(errorMsg)){
            setErrorView(errorMsg);
        }
    }

    @Override
    public void setErrorView(String errMsg) {
        if(mDecorList == null || mDecorList.isEmpty()){
            new EventAnalyticsHelper().logAPICallEvent(getActivity(), Constants.AnaylticsClassName.HomeDecor,
                    null, Constants.ApiName.getDecorList.toString(),Constants.AnalyticsEvents.FAILED,errMsg);
            if(getActivity() != null) {
                showErrorView(false);
            }
        }else{
            if(getActivity() != null) {
                UtilityMethods.showSnackbarOnTop(getActivity(), "Error", errMsg, true, 1500);
            }
        }
    }

    @Override
    public boolean isInternetConnected() {
        if(getActivity() == null) {
            return false;
        }
        if(UtilityMethods.isInternetConnected(getActivity())){
            return true;
        }else{
            isLoading = false;
            if(mDecorList == null || mDecorList.isEmpty()){
                showErrorView(true);
            }
            UtilityMethods.showSnackbarOnTop(getActivity(),"Error",getString(R.string.no_network_connection),true,1500);
            return false;
        }
    }

    @Override
    public void showPagingLoader() {
        isLoading = true;
        if(getActivity() != null) {
            mView.findViewById(R.id.id_next_page_loader_view).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hidePagingLoader() {
        if(getActivity() != null) {
            mView.findViewById(R.id.id_next_page_loader_view).setVisibility(View.GONE);
        }
    }

    @Override
    public void setNextDecorList(DecorListResponse response) {
        String errorMsg = "";
        if(response != null){
            if(response.getStatus()){
                isLoading = false;
                if(response.getDecors() != null && !response.getDecors().isEmpty()) {
                    Log.i(TAG, "setNextDecorList: Total Decor count " + response.getCount());
                    int loadingIndexFrom = mDecorList.size();
                    int loadedIndexTo = response.getDecors().size();
                    if(mDecorList != null){
                        mDecorList.addAll(response.getDecors());
                    }
                    mHomeDecorListAdapter.notifyItemRangeChanged(loadingIndexFrom,loadedIndexTo);
                    if(getActivity() != null) {
                        ((HousingApplication) getActivity().getApplicationContext()).setDecorList(mDecorList);
                    }
                }else{
                    errorMsg = "Home Decor list empty";
                }
            }else if(response.getErrorMessage() != null && !response.getErrorMessage().isEmpty()){
                errorMsg = response.getErrorMessage();
            }else{
                errorMsg = "Error getting home decor list,please retry.";
            }
        }else{
            errorMsg = "Error getting home decor list,please retry.";
        }
        if(!TextUtils.isEmpty(errorMsg)){
            setErrorView(errorMsg);
        }
    }

    @Override
    public void setNextPageErrorView(String errMsg) {
        if(getActivity() != null) {
            UtilityMethods.showSnackbarOnTop(getActivity(), "Error", errMsg, true, 1500);
        }
    }

    private void showErrorView(boolean isInternet){
        mView.findViewById(R.id.id_decor_view_layout).setVisibility(View.GONE);
        LinearLayout linearLayout = mView.findViewById(R.id.id_no_internet_layout);
        linearLayout.setVisibility(View.VISIBLE);
        TextView textView = linearLayout.findViewById(R.id.id_message);
        if(isInternet) {
            textView.setText(getString(R.string.no_internet_message_decor));
        }else{
            textView.setText(getString(R.string.decor_list_error_msg));
        }
        Button tryAgainButton = mView.findViewById(R.id.try_again);
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.getView();
            }
        });
    }

    private void setSuccessView(){
        mView.findViewById(R.id.id_decor_view_layout).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.id_no_internet_layout).setVisibility(View.GONE);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
}
