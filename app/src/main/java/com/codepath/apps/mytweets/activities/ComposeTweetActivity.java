package com.codepath.apps.mytweets.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.mytweets.R;
import com.codepath.apps.mytweets.connection.TwitterApplication;
import com.codepath.apps.mytweets.connection.TwitterClient;
import com.codepath.apps.mytweets.models.Tweet;
import com.codepath.apps.mytweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class ComposeTweetActivity extends AppCompatActivity {
    private TextView tv_userName;
    private TextView tv_userScreenName;
    private ImageView iv_userProfilePic;
    private Button bt_submitButton;

    private TwitterClient client;
    private User currentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);

        //get user data
        currentUser = new User();
        client = TwitterApplication.getRestClient();
        getCurrentUser();

        //set up views
        tv_userName=(TextView)findViewById(R.id.tv_user_name_compose);
        tv_userScreenName =(TextView)findViewById(R.id.tv_user_screen_name_compose);
        iv_userProfilePic = (ImageView)findViewById(R.id.iv_profile_pic_compose);
        bt_submitButton = (Button)findViewById(R.id.bt_button_compose);


        //setting user name and user screen name
        tv_userName.setText(currentUser.getUserName());
        tv_userScreenName.setText(currentUser.getScreenName());
        //setting user pic
        iv_userProfilePic.setImageResource(0);
        Picasso.with(this).load(currentUser.getProfileImageUrl()).into(iv_userProfilePic);

        //test current user name
        Toast.makeText(getApplicationContext(), currentUser.getUserName(), Toast.LENGTH_SHORT).show();


        //action bar settings
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_white_twitter_bird);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setElevation(0);

    }

    private void getCurrentUser(){
        //Send API req to get user

            client.getCurrentUserInfo(new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Toast.makeText(getApplicationContext(), "onSuccess response worked", Toast.LENGTH_SHORT).show();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose_tweet, menu);

        MenuItem dots = menu.findItem(R.id.action_settings);
        dots.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
