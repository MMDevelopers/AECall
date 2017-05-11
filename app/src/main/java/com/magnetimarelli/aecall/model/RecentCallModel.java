package com.magnetimarelli.aecall.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.util.Date;

/**
 * Created by F49558B on 5/10/2017.
 */

public class RecentCallModel {
    private String Name;
    private String Contact;
    private String DateTimes;
    private String Duration;
    private int CallType;
    private String ImageUrl;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getDateTimes() {
        return DateTimes;
    }

    public void setDateTimes(String dateTimes) {
        DateTimes = dateTimes;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public int getCallType() {
        return CallType;
    }

    public void setCallType(int callType) {
        CallType = callType;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
