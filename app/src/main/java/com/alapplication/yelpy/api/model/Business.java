package com.alapplication.yelpy.api.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.telephony.PhoneNumberUtils;

import com.alapplication.yelpy.R;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class Business implements Serializable {
    public float rating;
    public String price;
    public String phone;
    public String id;
    public int review_count;
    public String name;
    public String url;
    public String image_url;
    public Location location;
    public Cooridates coordinates;
    public List<Category> categories;
    public String[] photos;
    public List<Hours> hours;

    public String getCategorySummary() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < categories.size(); i++) {
            if (i != 0) sb.append(", ");
            sb.append(categories.get(i).title);
        }

        return sb.toString();
    }

    public
    @NonNull
    String getIndexString(int groupBy) {
        switch (groupBy) {
            case SearchParam.GROUP_BY_NONE:
                return "";
            case SearchParam.GROUP_BY_PRICE:
                return price == null ? "Unspecified" : price;
            case SearchParam.GROUP_BY_CATEGORY:
            default:
                return categories == null || categories.isEmpty() ? "Unspecified" : categories.get(0).title;
        }
    }

    public String getFormattedPhone() {
        if (phone == null) return "";
        return PhoneNumberUtils.formatNumber(phone, "US");
    }

    public String getHoursSummary(Context context, int dayOfWeek) {
        if (hours != null) {
            Hours hour = hours.get(0);
            if (hour.open != null && dayOfWeek < hour.open.size()) {
                OpenDay openDay = hour.open.get(dayOfWeek);
                return openDay.start.substring(0, 2) + ":" + openDay.start.substring(2, 4) +
                        " - " + openDay.end.substring(0, 2) + ":" + openDay.end.substring(2, 4);
            }
        }
        return context.getString(R.string.hours_unavailable);
    }

    public boolean isOpenNow() {
        if (hours != null) {
            Hours hour = hours.get(0);
            return hour.is_open_now;
        }
        return false;
    }

    /**
     * A simple category comparator that compares first entry in category
     */
    public static class CategorySorter implements Comparator<Business> {
        @Override
        public int compare(Business o1, Business o2) {
            if ((o1.categories == null || o1.categories.isEmpty()) &&
                    (o2.categories == null || o2.categories.isEmpty())) {
                //both empty
                return 0;
            }

            Category c1 = null;
            Category c2 = null;
            if (o1.categories != null && !o1.categories.isEmpty()) {
                c1 = o1.categories.get(0);
            }
            if (o2.categories != null && !o2.categories.isEmpty()) {
                c2 = o2.categories.get(0);
            }

            return c1 == null || c1.title == null ? -1 : c2 == null || c2.title == null ? 1 : c1.title.compareTo(c2.title);
        }
    }

    /**
     * A simple sorter compares price level of two business
     */
    public static class PriceSorter implements Comparator<Business> {

        @Override
        public int compare(Business o1, Business o2) {
            if (o1.price == null && o2.price == null) {
                return 0;
            }
            return o1.price == null ? 1 : o2.price == null ? -1 : o1.price.compareTo(o2.price);
        }
    }

}
