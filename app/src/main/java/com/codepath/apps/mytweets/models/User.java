package com.codepath.apps.mytweets.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dbykovskyy on 9/29/15.
 */

public class User {


    private String userName;
    private long uid;
    private String screenName;
    private String profileImageUrl;


    public  static User fromJsonObject(JSONObject json){

        User u= new User();

        try {
            u.userName = json.getString("name");
            u.uid = json.getLong("id");
            u.screenName = json.getString("screen_name");
            u.profileImageUrl= json.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    return u;
    }


    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
