package com.alapplication.yelpy.network;

import com.alapplication.yelpy.BuildConfig;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Provides factory methods to obtain Retrofit instance
 */
public class ApiFactory {
    private static final String YELP_BASE_URL = "https://api.yelp.com/";
    private static ApiFactory sInstance = new ApiFactory();

    private Map<Class, Object> apiMap = new HashMap<>();
    private Retrofit retrofit;

    private ApiFactory() {
    }

    public static ApiFactory getInstance() {
        return sInstance;
    }


    private synchronized Retrofit getRetrofitInstance() {
        if (retrofit != null) {
            return retrofit;
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // using interceptor to create the header
        builder.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request.Builder requestBuilder = request.newBuilder();
                requestBuilder.addHeader("Authorization", "Bearer " + BuildConfig.YELP_API_KEY);
                return chain.proceed(requestBuilder.build());
            }
        });
        Retrofit.Builder retroBuilder = new Retrofit.Builder()
                .baseUrl(YELP_BASE_URL)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()));

        retrofit = retroBuilder.build();

        return retrofit;
    }

    @SuppressWarnings("unchecked")
    public <T> T getApiInstance(Class<T> clazz) {
        if (!apiMap.containsKey(clazz)) {
            synchronized (clazz) {
                if (!apiMap.containsKey(clazz)) {
                    apiMap.put(clazz, getRetrofitInstance().create(clazz));
                }
            }
        }

        return (T) apiMap.get(clazz);
    }
}
