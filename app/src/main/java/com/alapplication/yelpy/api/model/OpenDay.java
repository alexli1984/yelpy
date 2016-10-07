package com.alapplication.yelpy.api.model;

import java.io.Serializable;

public class OpenDay implements Serializable {
    public boolean is_overnight;
    public String end;
    public String start;
    public int day;//start from 0
}
