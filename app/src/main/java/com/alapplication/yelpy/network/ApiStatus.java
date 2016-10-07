package com.alapplication.yelpy.network;

/**
 * A custom status object to record API Request status
 */
public class ApiStatus {
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_ERROR = 2;

    public int statusCode;
    public String errorMsg;

    public ApiStatus(int code, String msg) {
        statusCode = code;
        errorMsg = msg;
    }
}
