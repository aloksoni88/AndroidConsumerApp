package com.clicbrics.consumer.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.databinding.Observable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.buy.housing.backend.blogEndPoint.model.Blog;
import com.buy.housing.backend.blogEndPoint.model.BlogListResponse;
import com.buy.housing.backend.blogEndPoint.model.BlogTag;
import com.buy.housing.backend.blogEndPoint.model.TimeLineStats;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.helper.ArticleResultCallback;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;

import java.net.SocketTimeoutException;
import java.util.List;

/**
 * Created by Alok on 02-08-2018.
 */
public class ArticleViewModel extends ViewModel{

    Blog blog;
    ArticleResultCallback mResultCallback;
    public ArticleViewModel(Blog blog) {
        this.blog = blog;
    }

    public ArticleViewModel(ArticleResultCallback resultCallback,Blog blog) {
        this.mResultCallback = resultCallback;
        this.blog = blog;
    }


    private List<BlogTag> blogTags;

    public List<BlogTag> getBlogTags() {
        return blog.getBlogTags();
    }

    public void setBlogTags(List<BlogTag> blogTags) {
        blog.setBlogTags(blogTags);
    }

    public String getCategoryId() {
        return blog.getCategoryId();
    }

    public void setBlogTags(String categoryId) {
        blog.setCategoryId(categoryId);
    }

    public String getCategoryName() {
        return blog.getCategoryName();
    }

    public void setCategoryName(String categoryName) {
        blog.setCategoryName(categoryName);
    }

    public String getContent() {
        return blog.getContent();
    }

    public void setContent(String content) {
        blog.setContent(content);
    }

    public long getCreateById() {
        return blog.getCreateById();
    }

    public void setCreateById(long createById) {
        blog.setCreateById(createById);
    }

    public String getCreateByName() {
        return blog.getCreateByName();
    }

    public void setCreateByName(String createByName) {
        blog.setCreateByName(createByName);
    }

    public long getCreateTime() {
        return blog.getCreateTime();
    }

    public void setCreateTime(long createTime) {
        blog.setCreateTime(createTime);
    }

    public String getExcerpt() {
        return blog.getExcerpt();
    }

    public void setExcerpt(String excerpt) {
        blog.setExcerpt(excerpt);
    }

    public String getFbPostId() {
        return blog.getFbPostId();
    }

    public void setFbPostId(String fbPostId) {
        blog.setFbPostId(fbPostId);
    }


    public String getMetaDescription() {
        return blog.getMetaDescription();
    }

    public void setMetaDescription(String metaDescription) {
        blog.setMetaDescription(metaDescription);
    }

    public String getMetaKeyword() {
        return blog.getMetaKeyword();
    }

    public void setMetaKeyword(String metaKeyword) {
        blog.setMetaKeyword(metaKeyword);
    }

    public int getPopularScore() {
        return blog.getPopularScore();
    }

    public void setPopularScore(int popularScore) {
        blog.setPopularScore(popularScore);
    }

    public String getSTitleImage() {
        return blog.getSTitleImage();
    }

    public void setSTitleImage(String sTitleImage) {
        blog.setSTitleImage(sTitleImage);
    }

    public String getSearchTitle() {
        return blog.getSearchTitle();
    }

    public void setSearchTitle(String searchTitle) {
        blog.setSearchTitle(searchTitle);
    }

    public String getSeourl() {
        return blog.getSeourl();
    }

    public void setSeourl(String seourl) {
        blog.setSeourl(seourl);
    }

    public String getStatus() {
        return blog.getStatus();
    }

    public void setStatus(String status) {
        blog.setStatus(status);
    }

    public String getSubCategoryId() {
        return blog.getSubCategoryId();
    }

    public void setSubCategoryId(String subCategoryId) {
        blog.setSubCategoryId(subCategoryId);
    }

    public String getSubCategoryName() {
        return blog.getSubCategoryName();
    }

    public void setSubCategoryName(String subCategoryName) {
        blog.setSubCategoryName(subCategoryName);
    }

    public List<TimeLineStats> getTimeLineStates() {
        return blog.getTimeLineStates();
    }

    public void setTimeLineStates(List<TimeLineStats> timeLineStates) {
        blog.setTimeLineStates(timeLineStates);
    }

    public String getTitle() {
        return blog.getTitle();
    }

    public void setTitle(String title) {
        blog.setTitle(title);
    }

    public String getTitleImage() {
        return blog.getTitleImage();
    }

    public void setTitleImage(String titleImage) {
        blog.setTitleImage(titleImage);
    }

    public final void getBlogList(){
        if(!mResultCallback.isInternetConnected()){
            return;
        }
        mResultCallback.showLoader();
        new Thread(new Runnable() {
            String errMsg = "";
            @Override
            public void run() {
                try {
                    final BlogListResponse blogListResponse = EndPointBuilder.getBlogEndPoint().getBlogs(1l,"1",0,15)
                                                    .setBlogStatus(Constants.BlogStatus.Publish.toString())
                                                    .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mResultCallback.onSuccess(blogListResponse);
                            mResultCallback.hideLoader();
                        }
                    });
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    errMsg = "Internet connecivity lost,please retry";
                } catch (Exception e) {
                    e.printStackTrace();
                    errMsg = "Error getting articles,please retry.";;
                }
                if(!TextUtils.isEmpty(errMsg)){
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mResultCallback.hideLoader();
                            mResultCallback.onError(errMsg);
                        }
                    });
                }
            }
        }).start();
    }


    public final void getNextBlogList(final int offset, final int limit){
        if(!mResultCallback.isInternetConnected()){
            return;
        }
        mResultCallback.showPagingLoader();
        new Thread(new Runnable() {
            String errMsg = "";
            @Override
            public void run() {
                try {
                    final BlogListResponse blogListResponse = EndPointBuilder.getBlogEndPoint().getBlogs(1l,"1",offset,limit)
                            .setBlogStatus(Constants.BlogStatus.Publish.toString())
                            .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mResultCallback.onNextPageLoadSuccess(blogListResponse);
                            mResultCallback.hidePagingLoader();
                        }
                    });
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    errMsg = "Internet connecivity lost,please retry";
                } catch (Exception e) {
                    e.printStackTrace();
                    errMsg = "Error getting articles,please retry.";;
                }
                if(!TextUtils.isEmpty(errMsg)){
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mResultCallback.hidePagingLoader();
                            mResultCallback.onNextPageLoadError(errMsg);
                        }
                    });
                }
            }
        }).start();
    }
}
