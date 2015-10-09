package com.codepath.apps.mytweets.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.codepath.apps.mytweets.R;
import com.codepath.apps.mytweets.connection.TwitterApplication;
import com.codepath.apps.mytweets.connection.TwitterClient;
import com.codepath.apps.mytweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.codepath.apps.mytweets.models.Tweet.deleteAllTweetsFromDb;
import static com.codepath.apps.mytweets.models.Tweet.getAllTweetsFromDb;

/**
 * Created by dbykovskyy on 10/6/15.
 */
public class HomeTimelineFragment extends TweetsListFragment {

    private TwitterClient client;
    private static String TIMELINE_ACTIVITY_TAG = "HOME_TIMELINE_FRAGMENT";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        populateHomeTimeline(0, false);
    }


    //Send API req to get timeline
    //Fill out listview
    public void populateHomeTimeline(final int page, final boolean isClean){

        if(!isConnected(getContext())){
            buildDialog(getContext()).show();
            //this is only when launch app in offline mode
            if(isAdapterEmpty()){
                addAll(getAllTweetsFromDb());
            }
            //in case user pull for updates
            //swipeContainer.setRefreshing(false);

        }else{

            client.getHomeTimeline(page, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    //clean up db when start up first time
                    if (page == 0 && !isClean) {
                        deleteAllTweetsFromDb();
                    }
                    //get tweets form API and write to the array and db
                    ArrayList<Tweet> tweets = Tweet.fromJsonArray(response);
                    //add all tweets to the adapter
                   // addAll(tweets);

                    getAdapter().addAll(tweets);

                    //refetch data on pull refresh
                    if(isClean){
                        //clear adapter
                        clear();
                        //clear db
                        deleteAllTweetsFromDb();
                        //add new tweets to the array and db
                        addAll(tweets);
                        //swipeContainer.setRefreshing(false);
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    if (errorResponse != null) {
                        Log.d(TIMELINE_ACTIVITY_TAG, errorResponse.toString());
                    }
                    Log.d(TIMELINE_ACTIVITY_TAG, throwable.toString());
                }

            });


        }

    }


    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else return false;
    }


    public AlertDialog.Builder buildDialog(Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c,android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        builder.setTitle("No Internet connection.");
        builder.setMessage("Only offline content available");
        builder.setIcon(R.drawable.ic_twitter_bird);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        return builder;
    }


}
