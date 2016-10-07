package com.alapplication.yelpy.api.model;

import java.io.Serializable;
import java.util.List;

public class Hours implements Serializable {
    public String hours_type;
    public List<OpenDay> open;
    public boolean is_open_now;
}
