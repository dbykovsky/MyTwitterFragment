package com.codepath.apps.mytweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dbykovskyy on 9/29/15.
 */

@Table(name = "Tweets")
public class Tweet extends Model implements Serializable {


    @Column (name = "body")
    private String body;

    @Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uid; //tweet id


    @Column(name = "created_at")
    private  String createdAt;


    @Column(name = "retweet_count")
    private  int retweetCount;


    @Column(name = "favorite_count")
    private int favouritesCount;


    @Column(name ="media_url")
    private  String mediaUrl;

    @Column(name = "user", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User user;


    @Column(name = "time_stamp")
    private String timestamp;

    public Tweet(){
        super();
    }




    public static Tweet fromJsonObject(JSONObject jsonObject){
        Tweet tweet = new Tweet();

        try {
            Long currentUid = jsonObject.getLong("id");
            Tweet existingTweet = new Select().from(Tweet.class)
                    .where("uid = ?", currentUid)
                    .executeSingle();

            if (existingTweet != null) {
                tweet = existingTweet;
            }

            tweet.body=jsonObject.getString("text");
            tweet.uid=jsonObject.getLong("id");
            tweet.createdAt=jsonObject.getString("created_at");
            tweet.favouritesCount=jsonObject.getInt("favorite_count");
            tweet.retweetCount= jsonObject.getInt("retweet_count");
/*
            try{
                JSONObject media = jsonObject.getJSONObject("entities");
                if(media.getJSONArray("media")!=null){
                    tweet.mediaUrl = media.getJSONArray("media").getJSONObject(0).getString("media_url");
                }else {
                    tweet.mediaUrl = "empty";
                }
            }catch (JSONException a){
                a.printStackTrace();
            }finally {
                if (tweet.mediaUrl==null){
                    tweet.mediaUrl="empty";
                }
            }*/


            tweet.user=User.fromJsonObject(jsonObject.getJSONObject("user"));
            tweet.save();
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
            }catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return tweetArray;

    }


    public static ArrayList<Tweet> getAllTweetsFromDb() {
        ArrayList<Tweet> alTweets = new ArrayList<>();
        List<Tweet> lTweets = new Select()
                .from(Tweet.class)
                .execute();
        alTweets.addAll(lTweets);
        return alTweets;
    }


    // Used to return items from another table based on the foreign key
    public List<Tweet> getAllTweetsFromDbTwo() {

        return getMany(Tweet.class, "user");
    }


    public static void deleteAllTweetsFromDb() {
        new Delete().from(Tweet.class).execute();
        new Delete().from(User.class).execute();
        //reset internal id
        SQLiteUtils.execSql("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'Users'");
        SQLiteUtils.execSql("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'Tweets'");
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

    public Integer getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(Integer retweetCount) {
        this.retweetCount = retweetCount;
    }

    public int getFavouritesCount() {
        return favouritesCount;
    }

    public void setFavouritesCount(int favouritesCount) {
        this.favouritesCount = favouritesCount;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }


}
