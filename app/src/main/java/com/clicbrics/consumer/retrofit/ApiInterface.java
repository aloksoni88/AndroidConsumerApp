package com.clicbrics.consumer.retrofit;

import android.content.Intent;

import com.clicbrics.consumer.clicworth.GetEStimateModel;
import com.clicbrics.consumer.model.ProjectListResponse;
import com.clicbrics.consumer.retrofit.pojoclass.SearchByName;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Alok on 15-01-2019.
 */
public interface ApiInterface {
    /**
     * @param cityId        - city id of which city we need to get project list
     * @param propertyType  - Property type filter - ex - apartment, villa ,plot etc
     * @param bedType       - Room type filter - 1 bhk, 2 bhk etc
     * @param projectStatus - project status, ex- upcoming, readytomove etc
     * @param minPrice      - minimum price value
     * @param maxPrice      - max price value
     * @param minArea       - min area value
     * @param maxArea       - max area value
     * @param orderBy       - order by ex- price or distance
     * @param offset        - offset value(starting index from where we have to get the result)
     * @param limit         - limit value(end index till we have to get the result
     *                      ex - if we send offset -0 and limit 20 then it will send result from 0th index to 20th index(20 result)
     * @return - list of project
     */
    @GET("api/getProjectByCity/{city}")
    Call<ProjectListResponse> getProjectListByCity(@Path("city") String cityId, @Query("offset") Integer offset,
                                                   @Query("limit") Integer limit, @Query("builderId") Long builderId,
                                                   @Query("type") String propertyType,
                                                   @Query("bed") String bedType, @Query("status") String projectStatus,
                                                   @Query("minPrice") Integer minPrice, @Query("maxPrice") Integer maxPrice,
                                                   @Query("minArea") Integer minArea, @Query("maxArea") Integer maxArea,
                                                   @Query("orderby") String orderBy
            , @Query("requestId") Long requestId
            , @Query("appVersion") String appVersion, @Query("userId") Long userId, @Query("userEmail") String userEmail
            , @Query("userPhone") String userPhone, @Query("userName") String userName, @Query("virtualId") Long virtualId
            , @Query("source") String source, @Query("browser") String browser, @Query("browserVersion") String browserVersion
            , @Query("browserType") String browserType, @Query("getSummary") Boolean getSummary);

    @GET("api/getProjectByDistance")
    Call<ProjectListResponse> getProjectListByDistance(@Query("lat") double latitude, @Query("lng") double longitude, @Query("cityId") String cityId,
                                                       @Query("offset") Integer offset, @Query("limit") Integer limit,
                                                       @Query("type") String propertyType, @Query("bed") String bedType,
                                                       @Query("status") String projectStatus, @Query("minPrice") Integer minPrice, @Query("maxPrice") Integer maxPrice,
                                                       @Query("minArea") Integer minArea, @Query("maxArea") Integer maxArea,
                                                       @Query("orderby") String orderBy, @Query("requestId") Long requestId
            , @Query("appVersion") String appVersion, @Query("userId") Long userId, @Query("userEmail") String userEmail
            , @Query("userPhone") String userPhone, @Query("userName") String userName, @Query("virtualId") Long virtualId
            , @Query("source") String source, @Query("browser") String browser, @Query("browserVersion") String browserVersion
            , @Query("browserType") String browserType, @Query("city") String city,@Query("searchText") String searchText);

    @GET("api/getProjectByIds")
    Call<ProjectListResponse> getFavoriteProjectList(@Query("idList") String[] projectIdList);


    @GET("locality/searchByName/{searchtext}")
    Call<SearchByName> getSearchByName(@Path("searchtext") String searchtext, @Query("city") Long cityId,
                                       @Query("prefix") String prefix, @Query("seq") Long timestamp);

//    https://backend.clickbricks.in/clicworth/api/calculateEstimation/<LATITUDE>/<LONGITUDE>/<SIZE>/<TYPE>?address=<Address>&bed=<Bed>&age=<Age>&floor=<Floor>


    @GET("clicworth/api/calculateEstimation/{latitude}/{longitude}/{propertySize}/{propertyType}")
    Call<GetEStimateModel> getEstimateClicworth(@Path("latitude") String latitude, @Path("longitude") String longitude,
                                                @Path("propertySize") String propertySize,
                                                @Path("propertyType") String propertyType, @Query("address") String propertyAddress, @Query("bed") int bed,
                                                @Query("floor") int floor);
}
