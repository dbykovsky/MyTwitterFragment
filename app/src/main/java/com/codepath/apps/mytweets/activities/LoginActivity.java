package com.codepath.apps.mytweets.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.mytweets.R;
import com.codepath.apps.mytweets.connection.TwitterApplication;
import com.codepath.apps.mytweets.connection.TwitterClient;
import com.codepath.apps.mytweets.models.User;
import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {
	SharedPreferences sharedpreferences;
	public static final String CURRENt_USER_PREFERECES = "CurrentUserPreferences";
	private TwitterClient client;
	private User currentUser;
	public static final String userName = "userName";
	public static final String userScreenName = "userScreenName";
	public static final String userPicUrl = "userPicUrl";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		client = TwitterApplication.getRestClient();

	}


	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
		//get current User object and write it to shared preferences
		currentUser = new User();
		getCurrentUser();

		//specify preference file
		sharedpreferences = this.getSharedPreferences(CURRENt_USER_PREFERECES, Context.MODE_PRIVATE);
		//edit shared preferences
		SharedPreferences.Editor editor = sharedpreferences.edit();
		editor.putString(userName, currentUser.getUserName());
		editor.putString(userScreenName, currentUser.getScreenName());
		editor.putString(userPicUrl, currentUser.getProfileImageUrl());
		editor.commit();

		Toast.makeText(getApplicationContext(), "user name "+currentUser.getUserName(), Toast.LENGTH_SHORT).show();
		 //send intent
		 Intent i = new Intent(this, TimeLineActivity.class);
		 startActivity(i);
	}

	private void getCurrentUser(){
		//Send API req to get user

		client.getCurrentUserInfo(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				Toast.makeText(getApplicationContext(), "onSuccess login worked", Toast.LENGTH_SHORT).show();
				currentUser = User.fromJsonObject(response);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				if (errorResponse != null) {
					Log.d("My user onFailure", errorResponse.toString());
				}
				Log.d("My user onFailure", throwable.toString());
				Toast.makeText(getApplicationContext(), "Couldn't retrieve user's info", Toast.LENGTH_SHORT).show();
			}

		});
	}


	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to login
	public void loginToRest(View view) {
		getClient().connect();
	}

}
