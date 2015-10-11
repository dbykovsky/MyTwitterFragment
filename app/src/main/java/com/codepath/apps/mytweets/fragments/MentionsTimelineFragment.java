package com.codepath.apps.mytweets.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.mytweets.R;
import com.codepath.apps.mytweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mytweets.connection.TwitterApplication;
import com.codepath.apps.mytweets.connection.TwitterClient;
import com.codepath.apps.mytweets.models.Tweet;
import com.codepath.apps.mytweets.utils.EndlessScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.codepath.apps.mytweets.models.Tweet.deleteAllTweetsFromDb;
import static com.codepath.apps.mytweets.models.Tweet.getAllTweetsFromDbWithKey;

/**
 * Created by dbykovskyy on 10/6/15.
 */
public class MentionsTimelineFragment extends TweetsListFragment{

    ArrayList<Tweet> tweets;
    private TwitterClient client;
    private TweetsArrayAdapter adapter;
    private static String MENTIONS_ACTIVITY_TAG = "MENTIONS_TIMELINE_FRAGMENT";



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        adapter = this.adapterTweets;
        populateMentionsTimeline(0, false);
    }

    //in oreder to use on scroll we need onCreateViewMetod to be able to access this lv_tweets
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets, container, false);

        this.lv_tweets=(ListView) v.findViewById(R.id.lv_tweets);
        this.lv_tweets.setAdapter(adapter);

        // endless scroll
        this.lv_tweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Restricting this by 15 pages to avoid lock from twitter
                if (page <= 15) {
                    //get last mention tweet id
                    long maxId= tweets.get(tweets.size()-1).getUid()-1;
                    populateMentionsTimeline(maxId, false);
                    return true;

                } else
                    return false;
            }
        });

        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.

                populateMentionsTimeline(0, true);

            }

        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return v;
    }


    //Send API req to get timeline
    //Fill out listview
    private void populateMentionsTimeline(final long maxId, final boolean isClean){

        if(!isConnected(getContext())){
            buildDialog(getContext()).show();
            //this is only when launch app in offline mode
            adapter.clear();
            adapter.addAll(getAllTweetsFromDbWithKey("mentions"));
            //in case user pull for updates
            //swipeContainer.setRefreshing(false);

        }else{

        client.getMentionsTimeline(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {


                //get tweets form API and write to the array and db
                if(isClean){
                    deleteAllTweetsFromDb();
                    adapter.clear();
                    tweets = Tweet.fromJsonArray(response, "home");
                    adapter.addAll(tweets);
                    Log.d("My response mentFresh", response.toString());
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(getContext(), "Mentions DATA on refresh", Toast.LENGTH_SHORT).show();
                }else {
                    tweets = Tweet.fromJsonArray(response, "home");
                    adapter.addAll(tweets);
                    Log.d("My response mentions", response.toString());
                    Toast.makeText(getContext(), "Mentions DATA loaded", Toast.LENGTH_SHORT).show();
                }

/*


                //clean up db when start up first time
                if (maxId == 0 && !isClean) {
                    //deleteAllTweetsFromDb();
                    Toast.makeText(getContext(), "Mentions DB is Loaded", Toast.LENGTH_SHORT).show();
                }
                Log.d("My response mentions", response.toString());

                tweets = Tweet.fromJsonArray(response, "mentions");
                adapter.addAll(tweets);
                Toast.makeText(getContext(), "Mentions DATA is loaded", Toast.LENGTH_SHORT).show();
*/


/*

                adapter.addAll(tweets);

                //getAllTweetsFromDbWithKey()

/*                //refetch data on pull refresh
                if (isClean) {
                    *//*    adapterTweets.clear();
                        deleteAllTweetsFromDb();
                        adapterTweets.addAll(tweets);*//*
                    //swipeContainer.setRefreshing(false);
                }*/

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) {
                    Log.d(MENTIONS_ACTIVITY_TAG, errorResponse.toString());
                }
                Log.d(MENTIONS_ACTIVITY_TAG, throwable.toString());
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
