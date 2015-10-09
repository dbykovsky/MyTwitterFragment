package com.codepath.apps.mytweets.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mytweets.R;
import com.codepath.apps.mytweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mytweets.fragments.MentionsTimelineFragment;
import com.codepath.apps.mytweets.fragments.TweetsListFragment;

import com.codepath.apps.mytweets.models.Tweet;


public class TimeLineActivity extends AppCompatActivity {
    private final String TIMELINE_ACTIVITY_TAG = "TIMELINE_ACTIVITY";
    private final int REQUEST_CODE = 777;
    private TweetsListFragment fragmentTweetList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);

        // ActionBar configuration
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_white_twitter_bird);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setElevation(0);

        //Get viewpager
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);

        //Set the viewpager adapter
        vpPager.setAdapter(new TweetPagerAdapter(getSupportFragmentManager()));
        //Find the pager sliding tabs
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        //Attach the tabstrip to the viewpager
        tabStrip.setViewPager(vpPager);



        //on scroll listener
/*        lv_tweets.setOnScrollListener(new EndlessScrollListener() {
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
        });*/
    }



/*        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override

            public void onRefresh() {

                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.

                populateTimeline(0, true);

            }

        });*/

/*        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }*/


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

    public void onProfileView(MenuItem menuItem){



        Intent i = new Intent(this, ProfileActivity.class);

        i.putExtra("user_name", "@dbykovskyy");
        startActivity(i);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Tweet newTweet = (Tweet) i.getSerializableExtra("newTweet");
            HomeTimelineFragment fragmentTweetList = (HomeTimelineFragment) getSupportFragmentManager()
                    .findFragmentByTag(getFragmentName(R.id.viewpager, 0));
            fragmentTweetList.appendTweet(newTweet);
            fragmentTweetList.populateHomeTimeline(0, false);
        }
    }


    public class TweetPagerAdapter extends FragmentPagerAdapter{
        final int PAGE_COUNT= 2;
        private String tabTitles[]= {"Home","Mentions"};

        public TweetPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
           if(position==0){
               return new HomeTimelineFragment();
           }else if(position==1){
               return new MentionsTimelineFragment();
           }else {
               return null;
           }
        }
        // returns tab title at top
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        // how many fragments to swipe between
        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }

    private static String getFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }



}
