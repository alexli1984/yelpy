package com.alapplication.yelpy.api.model;

import java.io.Serializable;

/**
 * Location Param in search
 */
public class LocationParam implements Serializable {
    public String description;
    public double longitude;
    public double latitude;

    public LocationParam(String description) {
        this.description = description;
    }

    public LocationParam(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
