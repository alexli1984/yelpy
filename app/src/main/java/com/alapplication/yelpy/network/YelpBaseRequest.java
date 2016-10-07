package com.alapplication.yelpy.network;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Base Request class that represents a single API call
 *
 * @param <T> response object type
 */
public abstract class YelpBaseRequest<T extends YelpBaseResponse> {

    protected transient Call<T> call;
    protected transient T response;

    public YelpBaseRequest() {
        //recreate a default response object
        try {
            Class<T> responseClazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            response = responseClazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setApiCall(Call<T> call) {
        this.call = call;
    }

    public Response execute() throws IOException {
        if (call == null) throw new IllegalArgumentException("call not initialized!");
        return call.execute();
    }

    public void postRequestAsync() {
        EventBus.getDefault().post(this);
    }

    public void postRequestSync() {
        try {
            //Blocking call
            Response retroResponse = execute();

            if (retroResponse.isSuccessful()) {
                onSuccess(retroResponse);
            } else {
                onFailure(new ApiStatus(ApiStatus.STATUS_ERROR, retroResponse.errorBody().string()));
            }

        } catch (Exception e) {
            // Simplified error handling
            onFailure(new ApiStatus(ApiStatus.STATUS_ERROR, e.getMessage()));
        }

        // post the event to notify UI
        postResponse(response);
    }


    public void postResponse(Object response) {
        EventBus.getDefault().post(response);
    }

    public void onSuccess(Response response) {
        this.response = (T) response.body();
        this.response.status = new ApiStatus(ApiStatus.STATUS_SUCCESS, "success");
    }

    public void onFailure(ApiStatus errorStatus) {
        response.status = errorStatus;
    }
}
