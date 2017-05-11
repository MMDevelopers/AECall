package com.magnetimarelli.aecall.model;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by F49558B on 5/10/2017.
 */

public class ContactModel {
    private String contact_id ;
    private String display_name ;
    private ArrayList<String> phone ;
    private ArrayList<String> emailContact ;
    private String emailType ;
    private String image_uri ;

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getEmailType() {
        return emailType;
    }

    public void setEmailType(String emailType) {
        this.emailType = emailType;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }

    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }


    public ArrayList<String> getPhone() {
        return phone;
    }

    public void setPhone(ArrayList<String> phone) {
        this.phone = phone;
    }

    public ArrayList<String> getEmailContact() {
        return emailContact;
    }

    public void setEmailContact(ArrayList<String> emailContact) {
        this.emailContact = emailContact;
    }
}
