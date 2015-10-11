package com.codepath.apps.mytweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.codepath.apps.mytweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mytweets.connection.TwitterApplication;
import com.codepath.apps.mytweets.connection.TwitterClient;
import com.codepath.apps.mytweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by dbykovskyy on 10/6/15.
 */
public class UserTimelineFragment extends TweetsListFragment {
    private TwitterClient client;
    private TweetsArrayAdapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = this.adapterTweets;
        client = TwitterApplication.getRestClient();
        populateTimeline(0,false);
    }

    // Creates a new fragment given an int and title
    // DemoFragment.newInstance(5, "Hello");
    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment fragmentUserTimeline = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screenName", screenName);
        fragmentUserTimeline.setArguments(args);
        return fragmentUserTimeline;
    }



        //Send API req to get timeline
    //Fill out listview
    private void populateTimeline(final int page, final boolean isClean) {

        String screenName = getArguments().getString("screenName");

        client.getUserTimeline(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //clean up db when start up first time
               /* if (page == 0 && !isClean) {
                    deleteAllTweetsFromDb();
                    /*//****************** remove it later

                }*/

                ArrayList<Tweet> tweets = Tweet.fromJsonArray(response, "usertimeline");
                //get tweets form DB and write to the array
                //addAll(Tweet.fromJsonArray(response));
                //getAdapter().addAll(tweets);
                adapter.addAll(tweets);



                //refetch data on pull refresh
  /*              if (isClean) {
                    *//*    adapterTweets.clear();
                        deleteAllTweetsFromDb();
                        adapterTweets.addAll(tweets);*//*
                    //swipeContainer.setRefreshing(false);
                }*/

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) {
                    //   Log.d(TIMELINE_ACTIVITY_TAG, errorResponse.toString());
                }
                // Log.d(TIMELINE_ACTIVITY_TAG, throwable.toString());
            }

        });



    }
}
