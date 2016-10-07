package com.alapplication.yelpy.api.model;

/**
 * Stores the currrent Yelp Session
 */
public class Session {
    public String token;
    public long expires;

    private static Session instance = new Session();

    private Session() {
    }

    public static Session getInstance() {
        return instance;
    }

    public String getTokenHeader() {
        return "Bearer " + token;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expires;
    }
}
