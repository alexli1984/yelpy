package com.alapplication.yelpy.api;

import com.alapplication.yelpy.BuildConfig;
import com.alapplication.yelpy.api.model.Business;
import com.alapplication.yelpy.api.model.Hours;
import com.alapplication.yelpy.api.model.Review;
import com.alapplication.yelpy.api.model.SearchParam;
import com.alapplication.yelpy.api.model.Session;
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
     * Authentication Request
     */
    public static class GetToken extends YelpBaseRequest<GetToken.Response> {
        public GetToken() {
            setApiCall(apiService.getToken("client_credentials", BuildConfig.YELP_CLIENT_ID, BuildConfig.YELP_CLIENT_SECRET));
        }

        public static class Response extends YelpBaseResponse {
            public String access_token;
            public String token_type;
            public long expires_in;
        }

        @Override
        public void onSuccess(retrofit2.Response response) {
            super.onSuccess(response);
            Session.getInstance().token = this.response.access_token;
            Session.getInstance().expires = System.currentTimeMillis() + this.response.expires_in * 1000;
        }
    }

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
