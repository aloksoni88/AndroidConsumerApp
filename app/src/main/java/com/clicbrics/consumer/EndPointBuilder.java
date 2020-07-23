package com.clicbrics.consumer;

import com.buy.housing.backend.blogEndPoint.BlogEndPoint;
import com.buy.housing.backend.decorEndPoint.DecorEndPoint;
import com.buy.housing.backend.estimateEndPoint.EstimateEndPoint;
import com.buy.housing.backend.loanEndPoint.LoanEndPoint;
import com.buy.housing.backend.newsEndPoint.NewsEndPoint;
import com.buy.housing.backend.propertyEndPoint.PropertyEndPoint;
import com.buy.housing.backend.taskEndPoint.TaskEndPoint;
import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import static com.clicbrics.consumer.utils.BuildConfigConstants.BACKEND_APP_NAME;

/**
 * Created by Alok on 23-07-2018.
 */
public class EndPointBuilder {
    private static UserEndPoint mUserEndPoint;
    private static PropertyEndPoint mPropertyEndPoint;
    private static TaskEndPoint mTaskEndPoint;
    private static BlogEndPoint mBlogEndPoint;
    private static NewsEndPoint mNewsEndPoint;
    private static DecorEndPoint mDecorEndPoint;
    private static EstimateEndPoint mEstimateEndPoint;
    private static LoanEndPoint mLoanEndPoint;

    private EndPointBuilder(){

    }

    public static UserEndPoint getUserEndPoint(){
        if(mUserEndPoint == null) {
            UserEndPoint.Builder builder = new UserEndPoint.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), BuildConfigConstants.authorize())
                    .setRootUrl(BuildConfigConstants.BASE_URL);
            // end of optional local run code
            builder.setApplicationName(BACKEND_APP_NAME);
            mUserEndPoint = builder.build();
        }
        return mUserEndPoint;
    }

    public static PropertyEndPoint getPropertyEndPoint(){
        if (mPropertyEndPoint == null) {
            PropertyEndPoint.Builder builder = new PropertyEndPoint.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), BuildConfigConstants.authorize())
                    .setRootUrl(BuildConfigConstants.BASE_URL);
            builder.setApplicationName(BACKEND_APP_NAME);
            mPropertyEndPoint = builder.build();
        }
        return mPropertyEndPoint;
    }

    public static TaskEndPoint getTaskEndPoint(){
        if (mTaskEndPoint == null) {
            TaskEndPoint.Builder builder = new TaskEndPoint.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), BuildConfigConstants.authorize())
                    .setRootUrl(BuildConfigConstants.BASE_URL);
            builder.setApplicationName(BACKEND_APP_NAME);
            mTaskEndPoint = builder.build();
        }
        return mTaskEndPoint;
    }

    public static BlogEndPoint getBlogEndPoint(){
        if (mBlogEndPoint == null) {
            BlogEndPoint.Builder builder = new BlogEndPoint.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), BuildConfigConstants.authorize())
                    .setRootUrl(BuildConfigConstants.BASE_URL);
            builder.setApplicationName(BACKEND_APP_NAME);
            mBlogEndPoint = builder.build();
        }
        return mBlogEndPoint;
    }


    public static NewsEndPoint getNewsEndPoint(){
        if (mNewsEndPoint == null) {
            NewsEndPoint.Builder builder = new NewsEndPoint.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), BuildConfigConstants.authorize())
                    .setRootUrl(BuildConfigConstants.BASE_URL);
            builder.setApplicationName(BACKEND_APP_NAME);
            mNewsEndPoint = builder.build();
        }
        return mNewsEndPoint;
    }

    public static DecorEndPoint getDecorEndPoint(){
        if (mDecorEndPoint == null) {
            DecorEndPoint.Builder builder = new DecorEndPoint.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), BuildConfigConstants.authorize())
                    .setRootUrl(BuildConfigConstants.BASE_URL);
            builder.setApplicationName(BACKEND_APP_NAME);
            mDecorEndPoint = builder.build();
        }
        return mDecorEndPoint;
    }

    public static EstimateEndPoint getEstimateEndPoint(){
//       String BASE_URL="https://housingtestserver.appspot.com/_ah/api/";
        if (mEstimateEndPoint == null) {
            EstimateEndPoint.Builder builder = new EstimateEndPoint.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), BuildConfigConstants.authorize())
                    .setRootUrl(BuildConfigConstants.BASE_URL);
            builder.setApplicationName(BACKEND_APP_NAME);
            mEstimateEndPoint = builder.build();
        }
        return mEstimateEndPoint;
    }
    public static LoanEndPoint getLoanEndPoint(){
//        String BASE_URL="https://housingtestserver.appspot.com/_ah/api/";
        if (mLoanEndPoint == null) {
            LoanEndPoint.Builder builder = new LoanEndPoint.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), BuildConfigConstants.authorize())
                    .setRootUrl(BuildConfigConstants.BASE_URL);
            builder.setApplicationName(BACKEND_APP_NAME);
            mLoanEndPoint = builder.build();
        }
        return mLoanEndPoint;
    }
}

