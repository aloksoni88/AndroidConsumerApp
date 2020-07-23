package com.clicbrics.consumer.analytics;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.buy.housing.backend.decorEndPoint.model.Decor;
import com.buy.housing.backend.userEndPoint.model.Blog;
import com.buy.housing.backend.userEndPoint.model.City;
import com.buy.housing.backend.userEndPoint.model.News;
import com.clicbrics.consumer.model.Project;
import com.clicbrics.consumer.model.ProjectsByname;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.wrapper.Notificaiton;
import com.clicbrics.consumer.wrapper.RecentSearch;
import com.clicbrics.consumer.wrapper.TopLocalityModel;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Monita on 24-03-2020.
 */
public class EventAnalyticsHelper {
    public void logUsageStatsEvents(Context context, long startTime, long exitTime, String screenName) {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        long durationInMilis = exitTime - startTime;
        long duration = durationInMilis / 1000;
        Bundle params = new Bundle();
        params.putString(Constants.AnalyticsEvents.START_TIME, UtilityMethods.dateFormat(startTime, Constants.DateFormat.COMPLETE_DATE_FORMAT));
        params.putString(Constants.AnalyticsEvents.EXIT_TIME, UtilityMethods.dateFormat(exitTime, Constants.DateFormat.COMPLETE_DATE_FORMAT));
        params.putString(Constants.AnalyticsEvents.SCREEN_NAME, screenName);
        params.putString(Constants.AnalyticsEvents.DURATION, duration + " Seconds");
        params.putLong(Constants.AnalyticsEvents.DURATION_IN_MILIS, durationInMilis);
        firebaseAnalytics.logEvent(Constants.AnalyticsEvents.USAGE_STATS, params);
    }

    public void logAPICallEvent(Context context, String screenName, Object object, String apiName, String status, String errMsg) {
        Bundle params = new Bundle();
        params.putString(Constants.AnalyticsEvents.API_NAME, apiName);
        params.putString(Constants.AnalyticsEvents.SCREEN_NAME, screenName);
        params.putString(Constants.AnalyticsEvents.TIME, UtilityMethods.dateFormat(System.currentTimeMillis(), Constants.DateFormat.COMPLETE_DATE_FORMAT));
        params.putLong(Constants.AnalyticsEvents.TIME_IN_MILIS, System.currentTimeMillis());
        params.putString(Constants.AnalyticsEvents.STATUS, status);
        if (status.equalsIgnoreCase(Constants.AnalyticsEvents.FAILED)) {
            params.putString(Constants.AnalyticsEvents.ERROR_MESSAGE, errMsg);
        }

        if (null != object)
        {

            /*if (object instanceof PropertyBooking) {
                PropertyBooking propertyBooking = (PropertyBooking) object;
                params = setHoldBookingParams(params, propertyBooking);
            }
            if (object instanceof com.buy.housing.backend.userEndPoint.model.PropertyBooking) {
                com.buy.housing.backend.userEndPoint.model.PropertyBooking propertyBooking = (com.buy.housing.backend.userEndPoint.model.PropertyBooking) object;
                params = setPropertyBookingParams(params, propertyBooking);
            }*/


        }
        AppEventAnalytics.trackEvent(context, Constants.AnalyticsEvents.API_CALL, params);
    }


    public void ItemClickEvent(Activity context, String simpleName, Object object, String eventype, String action_name) {
        Bundle params = new Bundle();
        params.putString(Constants.AnalyticsEvents.ACTION_NAME, action_name);
        params.putString(Constants.AnalyticsEvents.SCREEN_NAME, simpleName);
        params.putString(Constants.AnalyticsEvents.TIME, UtilityMethods.dateFormat(System.currentTimeMillis(), Constants.DateFormat.COMPLETE_DATE_FORMAT));
        params.putLong(Constants.AnalyticsEvents.TIME_IN_MILIS, System.currentTimeMillis());

        if (object != null) {
            if (object instanceof City)
            {
                City city = (City) object;
                params = setCityParameter(params, city);

            }
            else if (object instanceof ProjectsByname)
            {
                ProjectsByname projectsByname = (ProjectsByname) object;
                params = setProjectByName(params, projectsByname);

            }
            else if (object instanceof RecentSearch)
            {
                RecentSearch recentSearch = (RecentSearch) object;
                params = setRecentSearchParameters(params, recentSearch);

            } else if (object instanceof TopLocalityModel)
            {
                TopLocalityModel topLocalityModel = (TopLocalityModel) object;
                params = setTopLocalitySearch(params, topLocalityModel);

            }
            else if (object instanceof Project)
            {
                Project project = (Project) object;
                params = setProjectParameters(params, project);

            }
            else if (object instanceof Decor)
            {
                Decor decor = (Decor) object;
                params = setDecorParameters(params, decor);

            }
            else if (object instanceof News)
            {
                News news = (News) object;
                params = setNewsParameters(params, news);

            }
            else if (object instanceof Blog)
            {
                Blog blog = (Blog) object;
                params = setBlogParameters(params, blog);

            }
            else if (object instanceof Notificaiton)
            {
                Notificaiton notificaiton = (Notificaiton) object;
                params = setNotificaitonParameters(params, notificaiton);

            }
        }
        AppEventAnalytics.trackEvent(context, eventype, params);
    }

    private Bundle setNotificaitonParameters(Bundle params, Notificaiton notificaiton) {
        if(notificaiton.getId()!=null)
        {
            params.putLong(Constants.AnalyticsEvents.NotifiCationId,notificaiton.getId());
        }
        if(notificaiton.getClickUrl()!=null)
        {
            params.putString(Constants.AnalyticsEvents.ClickUrl,notificaiton.getClickUrl());
        }
        if(notificaiton.getEndTime()!=null)
        {
            params.putLong(Constants.AnalyticsEvents.EndTime,notificaiton.getEndTime());
        }
        if(notificaiton.getStartTime()!=null)
        {
            params.putLong(Constants.AnalyticsEvents.StartTime,notificaiton.getStartTime());
        }
        if(notificaiton.getText()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Text,notificaiton.getText());
        }
        if(notificaiton.getTime()!=null)
        {
            params.putLong(Constants.AnalyticsEvents.Time,notificaiton.getTime());
        }
        if(notificaiton.getType()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Type,notificaiton.getType());
        }
        if(notificaiton.getUid()!=null)
        {
            params.putLong(Constants.AnalyticsEvents.Uid,notificaiton.getUid());
        }
        if(notificaiton.getUrl()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Url,notificaiton.getUrl());
        } if(notificaiton.getWelcomeUrl()!=null)
        {
            params.putString(Constants.AnalyticsEvents.WelcomeUrl,notificaiton.getWelcomeUrl());
        }
        return params;
    }

    private Bundle setBlogParameters(Bundle params, Blog blog) {
        if(blog.getCategoryId()!=null)
        {
            params.putString(Constants.AnalyticsEvents.CategoryId,blog.getCategoryId());
        }
        if(blog.getCategoryName()!=null)
        {
            params.putString(Constants.AnalyticsEvents.CategoryName,blog.getCategoryName());
        }
        if(blog.getSearchTitle()!=null)
        {
            params.putString(Constants.AnalyticsEvents.SearchTitle,blog.getSearchTitle());
        }
        if(blog.getContent()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Content,blog.getContent());
        }
        if(blog.getCreateById()!=null)
        {
            params.putLong(Constants.AnalyticsEvents.CreateById,blog.getCreateById());
        }
        if(blog.getCreateTime()!=null)
        {
            params.putLong(Constants.AnalyticsEvents.CreateTime,blog.getCreateTime());
        }
        if(blog.getCreateByName()!=null)
        {
            params.putString(Constants.AnalyticsEvents.CreateByName,blog.getCreateByName());
        }
        if(blog.getExcerpt()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Excerpt,blog.getExcerpt());
        }
        if(blog.getFbPostId()!=null)
        {
            params.putString(Constants.AnalyticsEvents.FbPostId,blog.getFbPostId());
        }
        if(blog.getMetaDescription()!=null)
        {
            params.putString(Constants.AnalyticsEvents.MetaDescription,blog.getMetaDescription());
        }
        if(blog.getMetaKeyword()!=null)
        {
            params.putString(Constants.AnalyticsEvents.MetaKeyword,blog.getMetaKeyword());
        }
        if(blog.getSeourl()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Seourl,blog.getSeourl());
        }
        if(blog.getPopularScore()!=null)
        {
            params.putInt(Constants.AnalyticsEvents.PopularScore,blog.getPopularScore());
        }
        if(blog.getTitle()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Title,blog.getTitle());
        }
        if(blog.getTitleImage()!=null)
        {
            params.putString(Constants.AnalyticsEvents.TitleImage,blog.getTitleImage());
        }
        if(blog.getStatus()!=null)
        {
            params.putString(Constants.AnalyticsEvents.STATUS,blog.getStatus());
        }
        if(blog.getSTitleImage()!=null)
        {
            params.putString(Constants.AnalyticsEvents.STitleImage,blog.getSTitleImage());
        }
        if(blog.getSubCategoryId()!=null)
        {
            params.putString(Constants.AnalyticsEvents.SubCategoryId,blog.getSubCategoryId());
        }
        if(blog.getSubCategoryName()!=null)
        {
            params.putString(Constants.AnalyticsEvents.STitleImage,blog.getSubCategoryName());
        }
        return params;
    }

    private Bundle setNewsParameters(Bundle params, News news) {
        if(news.getClickURL()!=null)
        {
            params.putString(Constants.AnalyticsEvents.ClickURL,news.getClickURL());
        }
        if(news.getCreateTime()!=null)
        {
            params.putLong(Constants.AnalyticsEvents.CreateTime,news.getCreateTime());
        }
        if(news.getDescription()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Description,news.getDescription());
        }
        if(news.getImage()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Image,news.getImage());
        }
        if(news.getNewsTime()!=null)
        {
            params.putLong(Constants.AnalyticsEvents.Image,news.getNewsTime());
        }
        if(news.getTitle()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Title,news.getTitle());
        }
        if(news.getSearchTitle()!=null)
        {
            params.putString(Constants.AnalyticsEvents.SearchTitle,news.getSearchTitle());
        }
        if(news.getSource()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Source,news.getSource());
        }


        return params;
    }

    private Bundle setDecorParameters(Bundle params, Decor decor) {
        if(decor.getCreateTime()!=null)
        {
            params.putLong(Constants.AnalyticsEvents.CreateTime,decor.getCreateTime());
        }

        if(decor.getMetaDescription()!=null)
        {
            params.putString(Constants.AnalyticsEvents.MetaDescription,decor.getMetaDescription());
        }
        if(decor.getMetaKeyword()!=null)
        {
            params.putString(Constants.AnalyticsEvents.MetaDescription,decor.getMetaKeyword());
        }
        if(decor.getMetaDescription()!=null)
        {
            params.putString(Constants.AnalyticsEvents.MetaKeyword,decor.getMetaDescription());
        }
        if(decor.getSearchTitle()!=null)
        {
            params.putString(Constants.AnalyticsEvents.SearchTitle,decor.getSearchTitle());
        }
        if(decor.getSeourl()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Seourl,decor.getSeourl());
        }
        if(decor.getSource()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Source,decor.getSource());
        }
        if(decor.getSource()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Source,decor.getSource());
        }

        if(decor.getSourceUrl()!=null)
        {
            params.putString(Constants.AnalyticsEvents.SourceUrl,decor.getSourceUrl());
        }
        if(decor.getStatus()!=null)
        {
            params.putString(Constants.AnalyticsEvents.STATUS,decor.getStatus());
        }


        return params;
    }

    private Bundle setProjectParameters(Bundle params, Project project) {
        if(project.getId()!=null)
        {
            params.putLong(Constants.AnalyticsEvents.Project_Id,project.getId());
        } if(project.getName()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Project_Name,project.getName());
        }
        if(project.getAddress()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Address,project.getAddress());
        }
        if(project.getBuilderName()!=null)
        {
            params.putString(Constants.AnalyticsEvents.BuilderName,project.getBuilderName());
        }
        if(project.getBuilderId()!=0)
        {
            params.putLong(Constants.AnalyticsEvents.BuilderId,project.getBuilderId());
        }

        if(project.getWalkThroughVideoURL()!=null)
        {
            params.putString(Constants.AnalyticsEvents.WalkThroughVideoURL,project.getWalkThroughVideoURL());
        }
        if(project.getOffer()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Offer,project.getOffer());
        }if(project.getAccName()!=null)
        {
            params.putString(Constants.AnalyticsEvents.AccName,project.getAccName());
        }if(project.getAccNumber()!=null)
        {
            params.putString(Constants.AnalyticsEvents.AccNumber,project.getAccNumber());
        }if(project.getBankName()!=null)
        {
            params.putString(Constants.AnalyticsEvents.BankName,project.getBankName());
        }if(project.getBranchName()!=null)
        {
            params.putString(Constants.AnalyticsEvents.BranchName,project.getBranchName());
        }if(project.getBspRange()!=null)
        {
            params.putString(Constants.AnalyticsEvents.BspRange,project.getBspRange());
        }if(project.getCity()!=null)
        {
            params.putString(Constants.AnalyticsEvents.CITY,project.getCity());
        }if(project.getCityId()!=0)
        {
            params.putLong(Constants.AnalyticsEvents.CITY_ID,project.getCityId());
        }if(project.getBuilderNameInUp()!=null)
        {
            params.putString(Constants.AnalyticsEvents.BuilderNameInUp,project.getBuilderNameInUp());
        }if(project.getCommercial()!=null)
        {
            params.putBoolean(Constants.AnalyticsEvents.Commercial,project.getCommercial());
        }if(project.getCommisionRemarks()!=null)
        {
            params.putString(Constants.AnalyticsEvents.CommisionRemarks,project.getCommisionRemarks());
        }if(project.getConnectivityScore()!=null)
        {
            params.putFloat(Constants.AnalyticsEvents.ConnectivityScore,project.getConnectivityScore());
        }if(project.getCountry()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Country,project.getCountry());
        }
        if(project.getCoverImage()!=null)
        {
            params.putString(Constants.AnalyticsEvents.CoverImage,project.getCoverImage());
        } if(project.getDeveloperIncentive()!=null)
        {
            params.putString(Constants.AnalyticsEvents.DeveloperIncentive,project.getDeveloperIncentive());
        }
        if(project.getEndDate()!=null)
        {
            params.putLong(Constants.AnalyticsEvents.EndDate,project.getEndDate());
        }if(project.getEndOfferDate()!=null)
        {
            params.putLong(Constants.AnalyticsEvents.EndOfferDate,project.getEndOfferDate());
        }
        if(project.getExclusive()!=null)
        {
            params.putBoolean(Constants.AnalyticsEvents.Exclusive,project.getExclusive());
        }if(project.getFDI()!=null)
        {
            params.putString(Constants.AnalyticsEvents.FDI,project.getFDI());
        }
        if(project.getGreenArea()!=null)
        {
            params.putFloat(Constants.AnalyticsEvents.GreenArea,project.getGreenArea());
        }
        if(project.getHallipad()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Hallipad,project.getHallipad());
        }
        if(project.getIfsc()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Ifsc,project.getIfsc());
        } if(project.getInternalIncentive()!=null)
        {
            params.putString(Constants.AnalyticsEvents.InternalIncentive,project.getInternalIncentive());
        } if(project.getLat()!=0.0)
        {
            params.putDouble(Constants.AnalyticsEvents.LATITUDE,project.getLat());
        }if(project.getLng()!=0.0)
        {
            params.putDouble(Constants.AnalyticsEvents.LONGITUDE,project.getLng());
        }
        if(project.getLaunchDate()!=0)
        {
            params.putLong(Constants.AnalyticsEvents.LaunchDate,project.getLaunchDate());
        }if(project.getLifeStyleScore()!=null)
        {
            params.putFloat(Constants.AnalyticsEvents.LifeStyleScore,project.getLifeStyleScore());
        }if(project.getLivabilityScore()!=null)
        {
            params.putFloat(Constants.AnalyticsEvents.LivabilityScore,project.getLivabilityScore());
        }if(project.getLocality()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Locality,project.getLocality());
        }if(project.getLogoImageURL()!=null)
        {
            params.putString(Constants.AnalyticsEvents.LogoImageURL,project.getLogoImageURL());
        }if(project.getMetaDescription()!=null)
        {
            params.putString(Constants.AnalyticsEvents.MetaDescription,project.getMetaDescription());
        }if(project.getMetaKey()!=null)
        {
            params.putString(Constants.AnalyticsEvents.MetaKeyword,project.getMetaKey());
        }if(project.getMetaTitle()!=null)
        {
            params.putString(Constants.AnalyticsEvents.MetaTitle,project.getMetaTitle());
        }if(project.getMouURL()!=null)
        {
            params.putString(Constants.AnalyticsEvents.MouURL,project.getMouURL());
        }if(project.getNewsUpdate()!=null)
        {
            params.putString(Constants.AnalyticsEvents.NewsUpdate,project.getNewsUpdate());
        }if(project.getNumberOfBuilderFloor()!=0)
        {
            params.putInt(Constants.AnalyticsEvents.NumberOfBuilderFloor,project.getNumberOfBuilderFloor());
        }if(project.getNumberofMultiStoryTowers()!=0)
        {
            params.putInt(Constants.AnalyticsEvents.NumberofMultiStoryTowers,project.getNumberofMultiStoryTowers());
        }if(project.getNumberOfPlots()!=0)
        {
            params.putInt(Constants.AnalyticsEvents.NumberOfPlots,project.getNumberOfPlots());
        }if(project.getNumberOfShops()!=0)
        {
            params.putInt(Constants.AnalyticsEvents.NumberOfShops,project.getNumberOfShops());
        }if(project.getNumberOfUnits()!=0)
        {
            params.putInt(Constants.AnalyticsEvents.NumberOfUnits,project.getNumberOfUnits());
        }if(project.getNumberOfVillas()!=0)
        {
            params.putInt(Constants.AnalyticsEvents.NumberOfVillas,project.getNumberOfVillas());
        }if(project.getOfferAvailable()!=null)
        {
            params.putBoolean(Constants.AnalyticsEvents.OfferAvailable,project.getOfferAvailable());
        }if(project.getOfferLink()!=null)
        {
            params.putString(Constants.AnalyticsEvents.OfferLink,project.getOfferLink());
        }if(project.getOnBoardingDate()!=null)
        {
            params.putLong(Constants.AnalyticsEvents.OnBoardingDate,project.getOnBoardingDate());
        }if(project.getPhone()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Phone,project.getPhone());
        }if(project.getPinCode()!=null)
        {
            params.putString(Constants.AnalyticsEvents.PinCode,project.getPinCode());
        }if(project.getPossessionDate()!=0)
        {
            params.putLong(Constants.AnalyticsEvents.PossessionDate,project.getPossessionDate());
        }if(project.getPreLaunchDate()!=0)
        {
            params.putLong(Constants.AnalyticsEvents.PreLaunchDate,project.getPreLaunchDate());
        }if(project.getPriceRange()!=null)
        {
            params.putString(Constants.AnalyticsEvents.PriceRange,project.getPriceRange());
        }if(project.getOtherImages()!=null)
        {
            params.putString(Constants.AnalyticsEvents.OtherImages,project.getOtherImages());
        }if(project.getProjectArchitect()!=null)
        {
            params.putString(Constants.AnalyticsEvents.ProjectArchitect,project.getProjectArchitect());
        }if(project.getProjectbrowcherPdfURL()!=null)
        {
            params.putString(Constants.AnalyticsEvents.ProjectbrowcherPdfURL,project.getProjectbrowcherPdfURL());
        }if(project.getProjectSummary()!=null)
        {
            params.putString(Constants.AnalyticsEvents.ProjectSummary,project.getProjectSummary());
        }if(project.getPropertyTypeJson()!=null)
        {
            params.putString(Constants.AnalyticsEvents.PropertyTypeJson,project.getPropertyTypeJson());
        }if(project.getPropertyTypeRange()!=null)
        {
            params.putString(Constants.AnalyticsEvents.PropertyTypeRange,project.getPropertyTypeRange());
        }if(project.getReraId()!=null)
        {
            params.putString(Constants.AnalyticsEvents.ReraId,project.getReraId());
        }if(project.getRMAgentId()!=null)
        {
            params.putLong(Constants.AnalyticsEvents.RMAgentId,project.getRMAgentId());
        }if(project.getRMAgentName()!=null)
        {
            params.putString(Constants.AnalyticsEvents.RMAgentName,project.getRMAgentName());
        }if(project.getRMAgentPhone()!=null)
        {
            params.putString(Constants.AnalyticsEvents.RMAgentPhone,project.getRMAgentPhone());
        }if(project.getSafetyScore()!=null)
        {
            params.putFloat(Constants.AnalyticsEvents.SafetyScore,project.getSafetyScore());
        }if(project.getSceoURL()!=null)
        {
            params.putString(Constants.AnalyticsEvents.SceoURL,project.getSceoURL());
        }if(project.getStartDate()!=null)
        {
            params.putLong(Constants.AnalyticsEvents.StartDate,project.getStartDate());
        }
        if(project.getsCoverImage()!=null)
        {
            params.putString(Constants.AnalyticsEvents.sCoverImage,project.getsCoverImage());
        } if(project.getSearchNameInUp()!=null)
        {
            params.putString(Constants.AnalyticsEvents.SearchNameInUp,project.getSearchNameInUp());
        }if(project.getSizeRange()!=null)
        {
            params.putString(Constants.AnalyticsEvents.SizeRange,project.getSizeRange());
        }if(project.getSold()!=null)
        {
            params.putBoolean(Constants.AnalyticsEvents.Sold,project.getSold());
        }if(project.getStartOfferDate()!=null)
        {
            params.putLong(Constants.AnalyticsEvents.StartOfferDate,project.getStartOfferDate());
        }if(project.getState()!=null)
        {
            params.putString(Constants.AnalyticsEvents.STATE,project.getState());
        }if(project.getTargetAmount()!=null)
        {
            params.putLong(Constants.AnalyticsEvents.TargetAmount,project.getTargetAmount());
        }
        if(project.getTentative()!=null)
        {
            params.putBoolean(Constants.AnalyticsEvents.Tentative,project.getTentative());
        }
        if(project.getTimeStamp()!=null)
        {
            params.putLong(Constants.AnalyticsEvents.TimeStamp,project.getTimeStamp());
        }if(project.getTotalArea()!=0.0)
        {
            params.putFloat(Constants.AnalyticsEvents.TotalArea,project.getTotalArea());
        }if(project.getTotalNumberBlock()!=0)
        {
            params.putInt(Constants.AnalyticsEvents.TotalNumberBlock,project.getTotalNumberBlock());
        }if(project.getValueForMoneyScore()!=0.0)
        {
            params.putFloat(Constants.AnalyticsEvents.ValueForMoneyScore,project.getValueForMoneyScore());
        }if(project.getVirtualTour()!=null)
        {
            params.putBoolean(Constants.AnalyticsEvents.VirtualTour,project.getVirtualTour());
        }
        /*if(project.getMouDocumentList()!=null&&project.getMouDocumentList().size()>0)
        {
            params.putSerializable(Constants.AnalyticsEvents.ID,project.getMouDocumentList());
        }*/

      /*  if(project.getBlockList()!=null&&project.getBlockList().size()>0)
        {
            params.putStringArrayList(Constants.AnalyticsEvents.AgentList,project.getBlockList());
        }*/

        return params;
    }

    private Bundle setTopLocalitySearch(Bundle params, TopLocalityModel topLocalityModel) {
        if(topLocalityModel.cityName!=null)
        {
            params.putString(Constants.AnalyticsEvents.CITY,topLocalityModel.cityName);
        } if(topLocalityModel.cityId!=0)
        {
            params.putLong(Constants.AnalyticsEvents.CITY_ID,topLocalityModel.cityId);
        }if(topLocalityModel.id!=0)
        {
            params.putLong(Constants.AnalyticsEvents.Locality_Id,topLocalityModel.id);
        }
        if(topLocalityModel.name!=null)
        {
            params.putString(Constants.AnalyticsEvents.LocalityName,topLocalityModel.name);
        }
        if(topLocalityModel.rank!=0)
        {
            params.putInt(Constants.AnalyticsEvents.Rank,topLocalityModel.rank);
        }


        return params;
    }

    private Bundle setRecentSearchParameters(Bundle params, RecentSearch recentSearch) {

        if(recentSearch.latLng!=null)
        {

        }
        if(recentSearch.projectId!=0)
        {
            params.putLong(Constants.AnalyticsEvents.Project_Id,recentSearch.projectId);
        }
        if(recentSearch.builderId!=0)
        {
            params.putLong(Constants.AnalyticsEvents.BuilderId,recentSearch.builderId);
        }

        if(recentSearch.cityId!=0)
        {
            params.putLong(Constants.AnalyticsEvents.CITY_ID,recentSearch.cityId);
        }
        if(recentSearch.cityName!=null)
        {
            params.putString(Constants.AnalyticsEvents.CITY,recentSearch.cityName);
        }
        if(recentSearch.builderName !=null)
        {
            params.putString(Constants.AnalyticsEvents.BuilderName,recentSearch.builderName);
        }
         if(recentSearch.state !=null)
        {
            params.putString(Constants.AnalyticsEvents.STATE,recentSearch.state);
        }


        return params;
    }

    private Bundle setProjectByName(Bundle params, ProjectsByname projectsByname) {
        if(projectsByname.getName()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Project_Name,projectsByname.getName());
        }
        if(projectsByname.getType()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Type,projectsByname.getType());
        }
        if(projectsByname.getId()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Project_Id,projectsByname.getId());
        }
        if(projectsByname.getAddresss()!=null)
        {
            params.putString(Constants.AnalyticsEvents.Address,projectsByname.getAddresss());
        }
        if(projectsByname.getId()!=null)
        {
            params.putString(Constants.AnalyticsEvents.ID,projectsByname.getId());
        }

        return params;
    }

    private Bundle setCityParameter(Bundle params, City city) {
        if(city.getName()!=null)
        {
            params.putString(Constants.AnalyticsEvents.CITY,city.getName());
        }
        if(city.getId()!=null)
        {
            params.putLong(Constants.AnalyticsEvents.CITY_ID,city.getId());
        }
        if(city.getStateId()!=null)
        {
            params.putLong(Constants.AnalyticsEvents.STATE_Id,city.getStateId());
        }
         if(city.getState()!=null)
        {
            params.putString(Constants.AnalyticsEvents.STATE,city.getState());
        }
        if(city.getLat()!=null)
        {
            params.putDouble(Constants.AnalyticsEvents.LATITUDE,city.getLat());
        }
        if(city.getLng()!=null)
        {
            params.putDouble(Constants.AnalyticsEvents.LONGITUDE,city.getLng());
        }


        return params;
    }







}
