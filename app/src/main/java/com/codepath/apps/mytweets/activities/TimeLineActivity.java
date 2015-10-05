package com.codepath.apps.mytweets.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.mytweets.R;
import com.codepath.apps.mytweets.Utils.EndlessScrollListener;
import com.codepath.apps.mytweets.adapters.TweetsArrayAdapter;
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

public class TimeLineActivity extends AppCompatActivity {
    private final String TIMELINE_ACTIVITY_TAG = "TIMELINE_ACTIVITY";
    private final int REQUEST_CODE = 777;
    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter adapterTweets;
    private ListView lv_tweets;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        lv_tweets = (ListView)findViewById(R.id.lv_tweets);
        tweets = new ArrayList<>();
        adapterTweets = new TweetsArrayAdapter(this, tweets);
        lv_tweets.setAdapter(adapterTweets);
        client = TwitterApplication.getRestClient(); //singleton


        // ActionBar configuration
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_white_twitter_bird);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setElevation(0);

        populateTimeline(0, false);


        //on scroll listener
        lv_tweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                //added some page limit to avoid 15min block from Twitter
                if (page <= 15) {
                    populateTimeline(page, false);
                    return true;
                } else {
                    return false;
                }
            }
        });



        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override

            public void onRefresh() {

                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.

                populateTimeline(0, true);

            }

        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }


    //Send API req to get timeline
    //Fill out listview
    private void populateTimeline(final int page, final boolean isClean){

        if(!isConnected(TimeLineActivity.this)){
            buildDialog(this).show();
            //this is only when launch app in offline mode
            if(adapterTweets.isEmpty()){
                adapterTweets.addAll(getAllTweetsFromDb());
            }
            //in case user pull for updates
            swipeContainer.setRefreshing(false);

        }else{

            client.getHomeTimeline(page, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    //clean up db when start up first time
                    if (page == 0 && !isClean) {
                        deleteAllTweetsFromDb();
                    }

                    //get tweets form DB and write to the array
                    tweets = Tweet.fromJsonArray(response);
                    adapterTweets.addAll(tweets);


                    //refetch data on pull refresh
                    if(isClean){
                        adapterTweets.clear();
                        deleteAllTweetsFromDb();
                        adapterTweets.addAll(tweets);
                        swipeContainer.setRefreshing(false);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_line, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_compose) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void composeTweet(MenuItem menuItem){
        if (menuItem.getItemId() == R.id.action_compose) {
            Intent intent = new Intent(TimeLineActivity.this, ComposeTweetActivity.class);
            startActivityForResult(intent, 777);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Tweet newTweet = (Tweet) i.getSerializableExtra("newTweet");
            adapterTweets.insert(newTweet,0);
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
