package com.codepath.apps.mytweets.connection;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import com.codepath.apps.mytweets.activities.TimeLineActivity;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "pfUbe4uUg2ZLPD5Ko5TXRp3jD";       // Change this
	public static final String REST_CONSUMER_SECRET = "2Vz26rQPbtqmSeuF1QnEjPWaf0Yl1esf4FXzbm8PncekDN04L2"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://codepathtweets"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}


	//get timeline data
	public void getHomeTimeline(AsyncHttpResponseHandler handler){
		String apiUrl= getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since_id", 1);
		client.get(apiUrl, params, handler);
		Log.d("My response2", "GET is fine");
		Toast.makeText(context, "Api GET call was sucessfull", Toast.LENGTH_SHORT).show();
	}

	//composing tweet
	public void postTweetOnTimeline(AsyncHttpResponseHandler handler){

		String apiUrl= getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since", 1);
		getClient().get(apiUrl, params, handler);

	}



	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}