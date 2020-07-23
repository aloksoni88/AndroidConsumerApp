package com.clicbrics.consumer.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.buy.housing.backend.blogEndPoint.model.Blog;
import com.buy.housing.backend.blogEndPoint.model.BlogListResponse;
import com.clicbrics.consumer.HousingApplication;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.databinding.ActivityBlogBinding;
import com.clicbrics.consumer.framework.activity.BaseActivity;
import com.clicbrics.consumer.helper.ArticleResultCallback;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.adapter.ArticleListAdapter;
import com.clicbrics.consumer.viewmodel.ArticleViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alok on 17-09-2019.
 */
public class BlogActivity extends BaseActivity implements ArticleResultCallback {

    private static final String TAG = BlogActivity.class.getSimpleName();
    private ActivityBlogBinding binding;
    private ArticleViewModel model = null;
    private List<Blog> mBlogList = new ArrayList<>();
    private ArticleListAdapter articleListAdapter = null;
    private int mTotalBlogCount = 0;
    private final int OFFSET_DIFF = 15;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_blog);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        addBackButton();

        model = new ArticleViewModel(this,new Blog());
        binding.setArticleModel(model);
        model.getBlogList();
    }

    private void setRecyclerView(List<Blog> blogList){
        mBlogList = blogList;
        articleListAdapter = new ArticleListAdapter(mBlogList,mTotalBlogCount);
        ((HousingApplication)getApplicationContext()).setBlogList(mBlogList);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.articleRecyclerView.setLayoutManager(layoutManager);
        binding.articleRecyclerView.setAdapter(articleListAdapter);
        binding.articleRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0){
                    Log.i(TAG, "onScrolled: Scrolling UP");
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && totalItemCount < mTotalBlogCount) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0) {
                            model.getNextBlogList(totalItemCount,OFFSET_DIFF);
                        }
                    }
                }else{
                    Log.i(TAG, "onScrolled: Scrolling DOWN");
                }
                /*int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPos = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPos = layoutManager.findLastVisibleItemPosition();


                if(totalItemCount < 0 || firstVisibleItemPos < 0 || lastVisibleItemPos < 0){
                    return;
                }
                if(totalItemCount < mTotalBlogCount){
                    if(!isLoading && (lastVisibleItemPos+1) == totalItemCount){
                        isLoading = true;
                        model.getNextBlogList(totalItemCount,OFFSET_DIFF);
                    }
                }*/
            }
        });
    }

    @Override
    public boolean isInternetConnected() {
        if(UtilityMethods.isInternetConnected(this)){
            return true;
        }else{
            isLoading = false;
            if(mBlogList == null || mBlogList.isEmpty()){
                showErrorView(true);
            }
            UtilityMethods.showSnackbarOnTop(this,"Error",getString(R.string.no_network_connection),true,1500);
            return false;
        }
    }

    @Override
    public void showLoader() {
        isLoading = true;
        //binding.idProgressLayout.setVisibility(View.VISIBLE);
        binding.idShimmerLayout.setVisibility(View.VISIBLE);
        binding.idShimmerLayout.startShimmer();
        //showProgressBar();
    }

    @Override
    public void hideLoader() {
        //binding.idProgressLayout.setVisibility(View.GONE);
        //dismissProgressBar();
        binding.idShimmerLayout.stopShimmer();
        binding.idShimmerLayout.setVisibility(View.GONE);
    }

    @Override
    public void onSuccess(BlogListResponse response) {
        String errorMsg = "";
        if(response != null){
            if(response.getStatus()){
                isLoading = false;
                if(response.getBlogs() != null && !response.getBlogs().isEmpty()) {
                    mTotalBlogCount = response.getCount() != null ? response.getCount() : response.getBlogs().size();
                    Log.i(TAG, "Total Article count "  + mTotalBlogCount);
                    setSuccessView();
                    setRecyclerView(response.getBlogs());
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
            if(mBlogList == null || mBlogList.isEmpty()){
                showErrorView(false);
            }else{
                UtilityMethods.showSnackbarOnTop(this, "Error", errorMsg, true, 1500);
            }
        }
    }

    @Override
    public void onError(String errMsg) {
        if(mBlogList == null || mBlogList.isEmpty()){
            showErrorView(false);
        }else{
            UtilityMethods.showSnackbarOnTop(this, "Error", errMsg, true, 1500);
        }
    }

    @Override
    public void showPagingLoader() {
        isLoading = true;
        binding.idNextPageLoaderView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidePagingLoader() {
        binding.idNextPageLoaderView.setVisibility(View.GONE);
    }

    @Override
    public void onNextPageLoadSuccess(BlogListResponse response) {
        String errorMsg = "";
        if(response != null){
            if(response.getStatus()){
                isLoading = false;
                if(response.getBlogs() != null && !response.getBlogs().isEmpty()) {
                    int loadingIndexFrom = mBlogList.size();
                    int loadedIndexTo = response.getBlogs().size();
                    if(mBlogList != null){
                        mBlogList.addAll(response.getBlogs());
                    }
                    articleListAdapter.notifyItemRangeChanged(loadingIndexFrom,loadedIndexTo);
                    ((HousingApplication)getApplicationContext()).setBlogList(mBlogList);
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
            if(mBlogList == null || mBlogList.isEmpty()){
                showErrorView(false);
            }else{
                UtilityMethods.showSnackbarOnTop(this, "Error", errorMsg, true, 1500);
            }
        }
    }

    @Override
    public void onNextPageLoadError(String errMsg) {
        UtilityMethods.showSnackbarOnTop(this, "Error", errMsg, true, 1500);
    }

    private void showErrorView(boolean isInternet){
        binding.idArticleViewLayout.setVisibility(View.GONE);
        binding.idNoInternetLayout.setVisibility(View.VISIBLE);
        TextView textView = binding.idNoInternetLayout.findViewById(R.id.id_message);
        if(isInternet) {
            textView.setText(getString(R.string.no_internet_message_article));
        }else{
            textView.setText(getString(R.string.article_list_error_msg));
        }
        Button tryAgainButton = binding.idNoInternetLayout.findViewById(R.id.try_again);
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.getBlogList();
            }
        });
    }

    private void setSuccessView(){
        binding.idArticleViewLayout.setVisibility(View.VISIBLE);
        binding.idNoInternetLayout.setVisibility(View.GONE);
    }
}
