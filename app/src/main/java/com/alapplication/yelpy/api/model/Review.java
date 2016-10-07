package com.alapplication.yelpy.api.model;

import java.io.Serializable;

public class Review implements Serializable {
    public int rating;
    public String text;
    public String time_created;
    public String url;
    public User user;
}
