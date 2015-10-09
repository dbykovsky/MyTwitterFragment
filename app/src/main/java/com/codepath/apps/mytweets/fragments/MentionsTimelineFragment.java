package com.codepath.apps.mytweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.mytweets.connection.TwitterApplication;
import com.codepath.apps.mytweets.connection.TwitterClient;
import com.codepath.apps.mytweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.codepath.apps.mytweets.models.Tweet.deleteAllTweetsFromDb;

/**
 * Created by dbykovskyy on 10/6/15.
 */
public class MentionsTimelineFragment extends TweetsListFragment{

    private TwitterClient client;
    private static String MENTIONS_ACTIVITY_TAG = "MENTIONS_TIMELINE_FRAGMENT";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        populateMentionsTimeline(0, false);
    }



    //Send API req to get timeline
    //Fill out listview
    private void populateMentionsTimeline(final int page, final boolean isClean){

/*        if(!isConnected(TimeLineActivity.this)){
            buildDialog(this).show();
            //this is only when launch app in offline mode
     *//*       if(adapterTweets.isEmpty()){
                adapterTweets.addAll(getAllTweetsFromDb());
            }*//*
            //in case user pull for updates
            //swipeContainer.setRefreshing(false);

        }else{*/

        client.getMentionsTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //clean up db when start up first time
                if (page == 0 && !isClean) {
                    deleteAllTweetsFromDb();
                    //****************** remove it later

                }

                ArrayList<Tweet> tweets = Tweet.fromJsonArray(response);
                //get tweets form DB and write to the array
                //addAll(tweets);
                getAdapter().addAll(tweets);

                //refetch data on pull refresh
                if (isClean) {
                    /*    adapterTweets.clear();
                        deleteAllTweetsFromDb();
                        adapterTweets.addAll(tweets);*/
                    //swipeContainer.setRefreshing(false);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) {
                    Log.d(MENTIONS_ACTIVITY_TAG, errorResponse.toString());
                }
                Log.d(MENTIONS_ACTIVITY_TAG, throwable.toString());
            }

        });


        //}

    }



}
