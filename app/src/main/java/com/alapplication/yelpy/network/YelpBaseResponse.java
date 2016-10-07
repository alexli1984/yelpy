package com.alapplication.yelpy.network;

/**
 * Base class for a Request response
 */
public abstract class YelpBaseResponse {
    public ApiStatus status;

    public boolean isSuccess() {
        return status != null && status.statusCode == ApiStatus.STATUS_SUCCESS;
    }
}
