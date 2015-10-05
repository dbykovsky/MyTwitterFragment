package com.codepath.apps.mytweets.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
	private final String COMPOSE_ACTIVITY_TAG = "LOGIN_ACTIVITY";
	public static final String CURRENT_USER_PREFERECES = "CurrentUserPreferences";
	private User currentUser;
	public static final String userName = "userName";
	public static final String userScreenName = "userScreenName";
	public static final String userPicUrl = "userPicUrl";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
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
		getCurrentUser();
	}


	private void getCurrentUser(){
		//Send API req to get user

		if(!isNetworkAvailable()){
			buildDialog(this).show();
		}else{

			getClient().getCurrentUserInfo(new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					currentUser = new User();
					currentUser = User.fromJsonObject(response);
					SharedPreferences sharedpreferences = LoginActivity.this.getSharedPreferences(CURRENT_USER_PREFERECES, Context.MODE_PRIVATE);
					//edit shared preferences
					SharedPreferences.Editor editor = sharedpreferences.edit();
					editor.putString(userName, currentUser.getUserName());
					editor.putString(userScreenName, currentUser.getScreenName());
					editor.putString(userPicUrl, currentUser.getProfileImageUrl());
					editor.apply();

					Intent i = new Intent(LoginActivity.this, TimeLineActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
					if (errorResponse != null) {
						Log.d(COMPOSE_ACTIVITY_TAG , errorResponse.toString());
					}
					Log.d(COMPOSE_ACTIVITY_TAG , throwable.toString());
				}

			});

		}
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


	protected Boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager
				= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}


	//Show alert dialog if network is not awailable
	public AlertDialog.Builder buildDialog(Context c) {

		AlertDialog.Builder builder = new AlertDialog.Builder(c, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
		builder.setTitle("No Internet connection.");
		builder.setMessage("Only offline content is available");
		builder.setIcon(R.drawable.ic_twitter_bird);

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent i = new Intent(LoginActivity.this, TimeLineActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}
		});

		return builder;
	}


}
