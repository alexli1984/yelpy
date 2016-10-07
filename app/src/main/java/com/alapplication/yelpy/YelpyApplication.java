package com.alapplication.yelpy;

import android.app.Application;

import com.alapplication.yelpy.network.YelpBaseRequest;

import de.greenrobot.event.EventBus;

public class YelpyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    public void onEventAsync(YelpBaseRequest request) {
        request.postRequestSync();
    }
}
