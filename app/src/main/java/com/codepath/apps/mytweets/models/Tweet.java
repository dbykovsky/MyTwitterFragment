package com.codepath.apps.mytweets.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by dbykovskyy on 9/29/15.
 */
public class Tweet {


    private String body;
    private long uid; //tweet id
    private  String createdAt;
    private User user;
    private String timestamp;


    public static Tweet fromJsonObject(JSONObject jsonObject){
        Tweet tweet = new Tweet();
        try {
            tweet.body=jsonObject.getString("text");
            tweet.uid=jsonObject.getLong("id");
            tweet.createdAt=jsonObject.getString("created_at");
            tweet.user=User.fromJsonObject(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

     return tweet;
    }

    public static ArrayList<Tweet> fromJsonArray(JSONArray response){
        ArrayList<Tweet> tweetArray = new ArrayList<>();

        for(int i=0; i<response.length(); i++){

            try {
                JSONObject jsonObject = response.getJSONObject(i);
                Tweet tweet = fromJsonObject(jsonObject);
                if(tweet!=null){
                    tweetArray.add(tweet);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

        }
        return tweetArray;

    }


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
