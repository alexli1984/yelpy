package com.alapplication.yelpy.api;

import com.alapplication.yelpy.api.model.Business;
import com.alapplication.yelpy.api.model.Hours;
import com.alapplication.yelpy.api.model.Review;
import com.alapplication.yelpy.api.model.SearchParam;
import com.alapplication.yelpy.network.ApiFactory;
import com.alapplication.yelpy.network.YelpBaseRequest;
import com.alapplication.yelpy.network.YelpBaseResponse;

import java.util.List;

/**
 * Yelp API Request Repository
 */
public class YelpApi {
    private static YelpApiService apiService = ApiFactory.getInstance().getApiInstance(YelpApiService.class);

    /**
     * Search Request
     */
    public static class Search extends YelpBaseRequest<Search.Response> {
        public Search(SearchParam param) {
            setApiCall(apiService.search(param.getTerm(),
                    param.getLocation().description, param.getLocation().latitude,
                    param.getLocation().longitude, param.getCategories(), param.getLimit(),
                    param.getOffset(), param.getSortBy(), param.getPrice(), param.getOpenNow(), param.getAttributes()));
        }

        public static class Response extends YelpBaseResponse {
            public int total;
            public List<Business> businesses;
        }
    }

    /**
     * Get Business Detail Request
     */
    public static class GetBusinessDetail extends YelpBaseRequest<GetBusinessDetail.Response> {
        public GetBusinessDetail(String id) {
            setApiCall(apiService.getBusinessDetail(id));
        }

        public static class Response extends YelpBaseResponse {
            public String[] photos;
            public List<Hours> hours;
        }
    }

    /**
     * Get Reviews Request
     */
    public static class GetReviews extends YelpBaseRequest<GetReviews.Response> {
        public GetReviews(String id) {
            setApiCall(apiService.getReviews(id));
        }

        public static class Response extends YelpBaseResponse {
            public List<Review> reviews;
            public int total;
        }
    }
}
