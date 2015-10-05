package com.codepath.apps.mytweets.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import org.json.JSONObject;

public class ComposeTweetActivity extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    private TwitterClient client;
    public static final String CURRENT_USER_PREFERECES = "CurrentUserPreferences";
    public static final String userName = "userName";
    public static final String userScreenName = "userScreenName";
    public static final String userPicUrl = "userPicUrl";
    private final String COMPOSE_TWEET_ACTIVITY_TAG = "COMPOSE_TWEET_ACTIVITY";

    private EditText et_tweetText;
    private TextView tv_userName;
    private TextView tv_userScreenName;
    private ImageView iv_userProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);
        //get user data from shared prefferences
        sharedpreferences = this.getSharedPreferences(CURRENT_USER_PREFERECES, 0);
        final String uName = sharedpreferences.getString(userName, "missing");
        final String uScreenName = sharedpreferences.getString(userScreenName, "missing");
        final String uPicUrl = sharedpreferences.getString(userPicUrl, "missing");


        //set up views
        tv_userName = (TextView) findViewById(R.id.tv_user_name_compose);
        tv_userScreenName = (TextView) findViewById(R.id.tv_user_screen_name_compose);
        iv_userProfilePic = (ImageView) findViewById(R.id.iv_profile_pic_compose);
        et_tweetText = (EditText) findViewById(R.id.tv_tweet_text);

        //setting user name and user screen name
        tv_userName.setText(uName);
        tv_userScreenName.setText(uScreenName);
        //setting user pic
        iv_userProfilePic.setImageResource(0);
        Picasso.with(this).load(uPicUrl).into(iv_userProfilePic);


        //action bar settings
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_compose_action_bar);

        //find char count Text view
        final View customActionBarView = actionBar.getCustomView();
        final TextView tv_chCount = (TextView) customActionBarView.findViewById(R.id.tvCharCount_t);
        tv_chCount.setText("0");

        //find button tweet
        final Button bt_tweet = (Button) customActionBarView.findViewById(R.id.btnTweet_t);


        //set back button listener
        customActionBarView.findViewById(R.id.ivCancel_t).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        //set tweet button listener
        bt_tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkAvailable()) {
                    buildDialog(ComposeTweetActivity.this).show();
                } else {
                    String tweetBody = et_tweetText.getText().toString();
                    client = TwitterApplication.getRestClient();
                    client.postTweet(new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d(COMPOSE_TWEET_ACTIVITY_TAG, response.toString());
                            Tweet tweet = new Tweet();
                            tweet = Tweet.fromJsonObject(response);
                            Intent i = new Intent();
                            i.putExtra("newTweet", tweet);
                            setResult(RESULT_OK, i);
                            finish();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            //add some messaging
                        }
                    }, tweetBody);

                }

            }

        });

        et_tweetText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 140) {
                    tv_chCount.setTextColor(Color.RED);
                    bt_tweet.setBackgroundColor(Color.GRAY);
                    bt_tweet.setEnabled(false);
                } else {
                    tv_chCount.setTextColor(getResources().getColor(R.color.dark_gray));
                    bt_tweet.setEnabled(true);
                    bt_tweet.setBackgroundColor(getResources().getColor(R.color.skyblue));
                }
                tv_chCount.setText(String.valueOf(s.length()));
            }


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

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
            this.finish();
        }

        return super.onOptionsItemSelected(item);
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
        builder.setMessage("Try again later");
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
