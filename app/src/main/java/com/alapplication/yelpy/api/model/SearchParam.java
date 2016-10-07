package com.alapplication.yelpy.api.model;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class SearchParam implements Serializable {
    public static final String ATTR_HOT_NEW = "hot_and_new";
    public static final String ATTR_DEALS = "deals";
    public static final int GROUP_BY_NONE = 0;
    public static final int GROUP_BY_CATEGORY = 1;
    public static final int GROUP_BY_PRICE = 2;

    private LocationParam location;
    private String term;
    private Integer radius;
    private String categories;
    private Integer limit;
    private Integer offset;
    private String sortBy;
    private String price;
    private Boolean openNow;
    private String attributes;//only two supporrted
    private boolean useCurrentLocation = true;
    private int groupBy = GROUP_BY_NONE;

    public SearchParam(@NonNull LocationParam location) {
        this.location = location;
    }

    public LocationParam getLocation() {
        return location;
    }

    public String getTerm() {
        return term;
    }

    public Integer getRadius() {
        return radius;
    }

    public String getCategories() {
        return categories;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public String getSortBy() {
        return sortBy;
    }

    public String getPrice() {
        return price;
    }

    public Boolean getOpenNow() {
        return openNow;
    }

    public String getAttributes() {
        return attributes;
    }

    public boolean useCurrentLocation() {
        return useCurrentLocation;
    }

    public void setUseCurrentLocation(boolean b) {
        useCurrentLocation = b;
    }

    public int getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(int groupBy) {
        this.groupBy = groupBy;
    }

    public static class Builder {
        private SearchParam param;

        public Builder(@NonNull LocationParam location) {
            param = new SearchParam(location);
        }

        public Builder(SearchParam param) {
            this.param = param;
        }

        public Builder term(String term) {
            param.term = term;
            return this;
        }

        public Builder location(String location) {
            param.location = location == null ? null : new LocationParam(location);
            return this;
        }

        public Builder location(double latitude, double longitude) {
            param.location = new LocationParam(latitude, longitude);
            return this;
        }

        public Builder radius(int radiusInMeters) {
            param.radius = radiusInMeters;
            return this;
        }

        public Builder categories(String categories) {
            param.categories = categories;
            return this;
        }

        public Builder sortBy(String sort) {
            param.sortBy = sort;
            return this;
        }

        public Builder price(String priceLevel) {
            param.price = priceLevel;
            return this;
        }

        public Builder openNow(boolean openNow) {
            param.openNow = openNow;
            return this;
        }

        public Builder limit(int limit) {
            param.limit = limit;
            return this;
        }

        public Builder offset(int offset) {
            param.offset = offset;
            return this;
        }

        public Builder attributes(String attributes) {
            param.attributes = attributes;
            return this;
        }

        public SearchParam build() {
            return param;
        }
    }
}
