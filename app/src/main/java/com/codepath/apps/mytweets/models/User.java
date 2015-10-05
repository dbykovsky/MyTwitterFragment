package com.codepath.apps.mytweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by dbykovskyy on 9/29/15.
 */

@Table(name="Users")
public class User extends Model implements Serializable{


    //private String userName;
   // private long uid;
    //private String screenName;
    //private String profileImageUrl;

    @Column(name = "name")
    private String userName;

    @Column(name = "uid", index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private Long uid;

    @Column(name = "screen_name")
    private String screenName;

    @Column(name = "profile_image_url")
    private String profileImageUrl;


    // Make sure to have a default constructor for every ActiveAndroid model
    public User (){
        super();
    }




    public static User fromJsonObject(JSONObject json){

        User u= new User();

        try {
            Long currentUid = json.getLong("id");
            User existingUser = new Select().from(User.class)
                    .where("uid = ?", currentUid).executeSingle();

            if (existingUser != null) {
                u = existingUser;
            }
            u.userName = json.getString("name");
            u.uid = json.getLong("id");
            u.screenName = json.getString("screen_name");
            u.profileImageUrl= json.getString("profile_image_url");
            u.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    return u;
    }


    public static void deleteAllUsers() {
        new Delete().from(User.class).execute();
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
