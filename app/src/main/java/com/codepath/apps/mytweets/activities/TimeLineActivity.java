package com.codepath.apps.mytweets.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
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

public class TimeLineActivity extends AppCompatActivity {
    private final int REQUEST_CODE = 777;
    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter adapterTweets;
    private ListView lv_tweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);
        lv_tweets = (ListView)findViewById(R.id.lv_tweets);
        tweets = new ArrayList<>();
        adapterTweets = new TweetsArrayAdapter(this, tweets);
        lv_tweets.setAdapter(adapterTweets);
        client = TwitterApplication.getRestClient(); //singleton
        //Action Bar
        // ActionBar configuration
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_white_twitter_bird);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setElevation(0);
        populateTimeline(1);
        Log.d("My response onSucess", "This is on create");


        //on scroll listener
        lv_tweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (page <= 10) {
                    populateTimeline(page);
                    return true;
                }else {
                    return false;
                }
            }
        });

    }

    //Send API req to get timeline
    //Fill out listview
    private void populateTimeline(int page){

        client.getHomeTimeline(page, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("My response onSucess", response.toString());
                Toast.makeText(getApplicationContext(), "onSuccess response worked", Toast.LENGTH_SHORT).show();

                //adapterTweets.clear();
                adapterTweets.addAll(Tweet.fromJsonArray(response));
                Log.d("Debug", adapterTweets.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if(errorResponse!=null){
                    Log.d("My response onFailure", errorResponse.toString());
                }
                Log.d("My response onFailure", throwable.toString());
                Toast.makeText(getApplicationContext(), "onFailure response failed", Toast.LENGTH_SHORT).show();
            }

        });
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
            Toast.makeText(getApplicationContext(), "I'm composing tweet", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TimeLineActivity.this, ComposeTweetActivity.class);
            startActivityForResult(intent,777);
        }



    }
}
