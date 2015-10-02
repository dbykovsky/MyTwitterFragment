package com.codepath.apps.mytweets.activities;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mytweets.R;
import com.codepath.apps.mytweets.connection.TwitterClient;
import com.codepath.apps.mytweets.models.User;
import com.squareup.picasso.Picasso;

public class ComposeTweetActivity extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    public static final String CURRENT_USER_PREFERECES = "CurrentUserPreferences";
    public static final String userName = "userName";
    public static final String userScreenName = "userScreenName";
    public static final String userPicUrl = "userPicUrl";

    private TextView tv_userName;
    private TextView tv_userScreenName;
    private ImageView iv_userProfilePic;
    private Button bt_submitButton;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);

        //get user data from shared prefferences
        SharedPreferences mSettings = this.getSharedPreferences(CURRENT_USER_PREFERECES, 0);
        String uName = mSettings.getString(userName, "missing");
        String uScreenName= mSettings.getString(userScreenName, "missing");
        String uPicUrl= mSettings.getString(userPicUrl, "missing");


        //set up views
        tv_userName=(TextView)findViewById(R.id.tv_user_name_compose);
        tv_userScreenName =(TextView)findViewById(R.id.tv_user_screen_name_compose);
        iv_userProfilePic = (ImageView)findViewById(R.id.iv_profile_pic_compose);
        bt_submitButton = (Button)findViewById(R.id.bt_button_compose);


        //setting user name and user screen name
        tv_userName.setText(uName);
        tv_userScreenName.setText(uScreenName);
        //setting user pic
        iv_userProfilePic.setImageResource(0);
        Picasso.with(this).load(uPicUrl).into(iv_userProfilePic);


        //action bar settings
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_white_twitter_bird);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setElevation(0);

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
