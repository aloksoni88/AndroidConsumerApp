package com.clicbrics.consumer.view.fragment;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buy.housing.backend.newsEndPoint.model.News;
import com.buy.housing.backend.newsEndPoint.model.NewsListResponse;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.Presenter.NewsPresenterContract;
import com.clicbrics.consumer.Presenter.NewsPresenterImpl;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.DecorDetailViewActivity;
import com.clicbrics.consumer.analytics.EventAnalyticsHelper;
import com.clicbrics.consumer.databinding.FragmentNewsBinding;
import com.clicbrics.consumer.fragment.BaseFragment;
import com.clicbrics.consumer.model.NewsModel;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.adapter.NewsListAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends BaseFragment implements NewsPresenterContract.View{

    private static final String TAG = "NewsFragment";
    private NewsPresenterContract.Presenter presenter;
    private int mTotalNewsCount = 0;
    private final int OFFSET_DIFF = 15;
    private boolean isLoading = false;
    private NewsListAdapter mNewsListAdapter;
    private List<News> mNewsList = new ArrayList<>();
    private View mView;
    private FragmentNewsBinding binding;
    private ShimmerFrameLayout shimmerFrameLayout;
    long onStartTime;
    public NewsFragment() {
        // Required empty public constructor
    }

    public static NewsFragment newInstance(){
        return new NewsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_news,container,false);
        onStartTime = System.currentTimeMillis();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        shimmerFrameLayout = view.findViewById(R.id.id_shimmer_layout);
        presenter = new NewsPresenterImpl(this, new NewsModel());
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

    private void setRecyclerView(List<News> newsList){
        mNewsList = newsList;
        if(getActivity() == null) {
            return;
        }
        RecyclerView recyclerView = mView.findViewById(R.id.news_recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mNewsListAdapter = new NewsListAdapter(getActivity(),mNewsList,mTotalNewsCount);
        ((HousingApplication) getActivity().getApplicationContext()).setNewsList(mNewsList);
        recyclerView.setAdapter(mNewsListAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0){
                    Log.i(TAG, "onScrolled: Scrolling UP");
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && totalItemCount < mTotalNewsCount) {
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
                if(totalItemCount < mTotalNewsCount){
                    if(!isLoading && (lastVisibleItemPos+1) == totalItemCount){
                        isLoading = true;
                        presenter.loadNextPage(totalItemCount,OFFSET_DIFF);
                    }
                }*/
            }
        });
    }


    @Override
    public void showProgress() {
        isLoading = true;
        //binding.idProgressLayout.setVisibility(View.VISIBLE);
        //showProgressBar();
        if(getActivity() != null) {

            shimmerFrameLayout.setVisibility(View.VISIBLE);
            shimmerFrameLayout.startShimmer();
        }
    }

    @Override
    public void hideProgress() {
        //binding.idProgressLayout.setVisibility(View.GONE);
        //dismissProgressBar();
        if(getActivity() != null) {
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void setNewsList(NewsListResponse response) {
        String errorMsg = "";
        if(response != null){
            if(response.getStatus()){
                isLoading = false;
                if(response.getNews() != null && !response.getNews().isEmpty()) {
                    new EventAnalyticsHelper().logAPICallEvent(getActivity(), Constants.AnaylticsClassName.HomeDecorDetailScreen,
                            null, Constants.ApiName.getNewsList.toString(),Constants.AnalyticsEvents.SUCCESS,null);
                    mTotalNewsCount = response.getCount() != null ? response.getCount() : response.getNews().size();
                    if(getActivity() != null) {
                        setSuccessView();
                        setRecyclerView(response.getNews());
                    }
                }else{
                    errorMsg = "News list empty";
                }
            }else if(response.getErrorMessage() != null && !response.getErrorMessage().isEmpty()){
                errorMsg = response.getErrorMessage();
            }else{
                errorMsg = "Error getting news list,please retry.";
            }
        }else{
            errorMsg = "Error getting news list,please retry.";
        }
        if(!TextUtils.isEmpty(errorMsg)){

            setErrorView(errorMsg);
        }
    }

    @Override
    public void showPagingLoader() {
        isLoading = true;
        if(getActivity() != null)
        mView.findViewById(R.id.id_next_page_loader_view).setVisibility(View.VISIBLE);
    }

    @Override
    public void hidePagingLoader() {
        if(getActivity() != null)
        mView.findViewById(R.id.id_next_page_loader_view).setVisibility(View.GONE);
    }

    @Override
    public void setErrorView(String errMsg) {
        if(mNewsList == null || mNewsList.isEmpty()){
            new EventAnalyticsHelper().logAPICallEvent(getActivity(), Constants.AnaylticsClassName.HomeDecorDetailScreen,
                    null, Constants.ApiName.getNewsList.toString(),Constants.AnalyticsEvents.FAILED,errMsg);
            if(getActivity() != null)
            {
                showErrorView(false);
            }

        }else{
            if(getActivity() != null) {
                UtilityMethods.showSnackbarOnTop(getActivity(), "Error", errMsg, true, 1500);
            }
        }
    }


    @Override
    public void setNextNewsList(NewsListResponse response) {
        String errorMsg = "";
        if(response != null){
            if(response.getStatus()){
                isLoading = false;
                if(response.getNews() != null && !response.getNews().isEmpty()) {
                    new EventAnalyticsHelper().logAPICallEvent(getActivity(), Constants.AnaylticsClassName.HomeDecorDetailScreen,
                            null, Constants.ApiName.getNewsList.toString(),Constants.AnalyticsEvents.SUCCESS,null);
                    int loadingIndexFrom = mNewsList.size();
                    int loadedIndexTo = response.getNews().size();
                    if(mNewsList != null){
                        mNewsList.addAll(response.getNews());
                    }
                    mNewsListAdapter.notifyItemRangeChanged(loadingIndexFrom,loadedIndexTo);
                    if(getActivity() != null) {
                        ((HousingApplication) getActivity().getApplicationContext()).setNewsList(mNewsList);
                    }
                }else{
                    errorMsg = "News list empty";
                }
            }else if(response.getErrorMessage() != null && !response.getErrorMessage().isEmpty()){
                errorMsg = response.getErrorMessage();
            }else{
                errorMsg = "Error getting news list,please retry.";
            }
        }else{
            errorMsg = "Error getting news list,please retry.";
        }
        if(!TextUtils.isEmpty(errorMsg)){
            setErrorView(errorMsg);
        }
    }

    @Override
    public void setOnNextPageLoadErrorView(String errMsg) {
        if(getActivity() != null) {
            new EventAnalyticsHelper().logAPICallEvent(getActivity(), Constants.AnaylticsClassName.HomeDecorDetailScreen,
                    null, Constants.ApiName.getNewsList.toString(),Constants.AnalyticsEvents.FAILED,errMsg);
            UtilityMethods.showSnackbarOnTop(getActivity(), "Error", errMsg, true, 1500);
        }
    }

    @Override
    public boolean isInternetConnect() {
        if(getActivity() == null) {
            return false;
        }
        if(UtilityMethods.isInternetConnected(getActivity())){
            return true;
        }else{
            isLoading = false;
            if(mNewsList == null || mNewsList.isEmpty()){
                if(getActivity() != null)
                {
                    showErrorView(true);
                }

            }
            UtilityMethods.showSnackbarOnTop(getActivity(),"Error",getString(R.string.no_network_connection),true,1500);
            return false;
        }
    }

    private void showErrorView(boolean isInternet){
        mView.findViewById(R.id.id_news_view_layout).setVisibility(View.GONE);
        LinearLayout linearLayout = mView.findViewById(R.id.id_no_internet_layout);
        linearLayout.setVisibility(View.VISIBLE);
        TextView textView = linearLayout.findViewById(R.id.id_message);

            if(isInternet) {
                textView.setText(getString(R.string.no_internet_message_news));
            }
            else
                {
                textView.setText(getString(R.string.news_list_error_msg));
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
        mView.findViewById(R.id.id_news_view_layout).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.id_no_internet_layout).setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        new EventAnalyticsHelper().logUsageStatsEvents(getActivity(),onStartTime,System.currentTimeMillis(), Constants.AnaylticsClassName.NewsScreen);
    }
}
