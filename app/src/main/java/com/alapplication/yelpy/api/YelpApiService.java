package com.alapplication.yelpy.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Yelp API Interface
 */
public interface YelpApiService {

    /**
     * @param term       Optional. Search term (e.g. "food", "restaurants")
     * @param location   required if lat/long is missing
     * @param latitude   required if location is missing
     * @param longitude  required if location is missing
     * @param categories Optional. Categories to filter the search results with. For example "Bar, French"
     * @param limit      Optional. Number of business results to return. By default, it will return 20. Maximum is 50.
     * @param offset     Optional. Offset the list of returned business results by this amount.
     * @param sortBy     Optional. Sort the results by one of the these modes: best_match, rating, review_count or distance. default is best_match
     * @param priceLevel Optional. Pricing levels to filter the search result with: 1 = $, 2 = $$, 3 = $$$, 4 = $$$$.
     * @param openNow    Optional. Default to false
     * @return up to 1000 businesses based on the provided search criteria
     */
    @GET("v3/businesses/search")
    Call<YelpApi.Search.Response> search(@Query("term") String term, @Query("location") String location, @Query("latitude") Double latitude,
                                         @Query("longitude") Double longitude, @Query("categories") String categories, @Query("limit") Integer limit,
                                         @Query("offset") Integer offset, @Query("sort_by") String sortBy, @Query("price") String priceLevel,
                                         @Query("open_now") Boolean openNow, @Query("attributes") String attributes);

    /**
     * @param id id of business
     * @return the detail information of a business
     */
    @GET("v3/businesses/{id}")
    Call<YelpApi.GetBusinessDetail.Response> getBusinessDetail(@Path("id") String id);

    /**
     * This endpoint returns the up to three reviews of a business.
     *
     * @param id business id
     * @return up to three reviews of a business.
     */
    @GET("v3/businesses/{id}/reviews")
    Call<YelpApi.GetReviews.Response> getReviews(@Path("id") String id);
}
