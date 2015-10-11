package com.codepath.apps.mytweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.apps.mytweets.R;
import com.codepath.apps.mytweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mytweets.models.Tweet;

import java.util.ArrayList;
import java.util.List;

import static com.codepath.apps.mytweets.models.Tweet.deleteAllTweetsFromDb;

/**
 * Created by dbykovskyy on 10/5/15.
 */
public class TweetsListFragment extends Fragment{

    private ArrayList<Tweet> tweets;
    protected TweetsArrayAdapter adapterTweets;
    protected ListView lv_tweets;
    protected SwipeRefreshLayout swipeContainer;



    //inflation logic
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets, container, false);
        lv_tweets = (ListView) v.findViewById(R.id.lv_tweets);
        lv_tweets.setAdapter(adapterTweets);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        return v;
    }

    //creation lifecycle event

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tweets = new ArrayList<>();
        adapterTweets = new TweetsArrayAdapter(getActivity(), tweets);
    }


    public boolean isAdapterEmpty(){
        return adapterTweets.isEmpty();
    }

    public void addAll(ArrayList<Tweet> tweets) {
        adapterTweets.addAll(tweets);
    }


    public void appendTweet(Tweet newTweet) {
        this.adapterTweets.insert(newTweet, 0);
    }

    public void clear() {
        this.adapterTweets.clear();
    }


    public TweetsArrayAdapter getAdapter(){
        return this.adapterTweets;
    }


    protected void deleteAllFromDb() {
       deleteAllTweetsFromDb();
    }

}
