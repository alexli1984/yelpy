package com.alapplication.yelpy.api.model;

import android.text.TextUtils;

import java.io.Serializable;

public class Location implements Serializable {
    public String city;
    public String country;
    public String address1;
    public String address2;
    public String address3;
    public String state;
    public String zip_code;

    public String getShortForm() {
        String s = TextUtils.isEmpty(address1) ? "" : address1 +
                (TextUtils.isEmpty(address2) ? "" : ", " + address2) +
                (TextUtils.isEmpty(address3) ? "" : ", " + address3) +
                (TextUtils.isEmpty(city) ? "" : ", " + city);
        if (s.startsWith(", ")) return s.substring(2);
        return s;
    }

    public String getLongForm() {
        String addr = getShortForm();
        addr += (TextUtils.isEmpty(state) ? "" : ", " + state) +
                (TextUtils.isEmpty(zip_code) ? "" : ", " + zip_code);
        if (addr.startsWith(", ")) return addr.substring(2);
        return addr;
    }
}
