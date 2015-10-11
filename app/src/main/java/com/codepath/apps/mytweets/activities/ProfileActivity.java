package com.codepath.apps.mytweets.activities;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.mytweets.R;
import com.codepath.apps.mytweets.connection.TwitterApplication;
import com.codepath.apps.mytweets.connection.TwitterClient;
import com.codepath.apps.mytweets.fragments.UserTimelineFragment;
import com.codepath.apps.mytweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.apache.http.Header;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private TwitterClient client;
    private User user;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        client = TwitterApplication.getRestClient();
        //Get users info


        user = (User) getIntent().getSerializableExtra("user");
        client = TwitterApplication.getRestClient();

        if (user != null && savedInstanceState == null) {
            getSupportActionBar().setTitle("@" + user.getScreenName());
            populateProfileHeader(user);
            setupFragment(user);

        }else {
            client.getCurrentUserInfo(new JsonHttpResponseHandler(){
                                          @Override
                                          public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                              User currentUser = User.fromJsonObject(response);
                                              //my current user account info
                                              if (savedInstanceState == null) {
                                                  setupFragment(currentUser);
                                              }

                                              getSupportActionBar().setTitle("@"+currentUser.getScreenName());
                                              populateProfileHeader(currentUser);
                                          }

                                          @Override
                                          public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                                          }
                                      });
        }

    }

    public void populateProfileHeader(User user){
        TextView tv_name_profile = (TextView) findViewById(R.id.tv_user_name_profile);
        TextView tv_tag_line = (TextView) findViewById(R.id.tv_tag_line_profile);
        ImageView iv_profile_picture = (ImageView) findViewById(R.id.iv_user_image_profile);
        TextView tv_followers = (TextView) findViewById(R.id.tv_followers_profile);
        TextView tv_following = (TextView) findViewById(R.id.tv_following_profile);

        tv_name_profile.setText(user.getUserName());
        tv_tag_line.setText(user.getDescription());
        tv_followers.setText(String.valueOf(user.getFollowersCount()) + " Followers ");
        tv_following.setText(String.valueOf(user.getFollowingsCount()) + " Following");




        Picasso.with(this).load(user.getProfileImageUrl()).into(iv_profile_picture);

        //making user profile photo oval
        iv_profile_picture.setImageResource(0);
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(this.getResources().getColor(R.color.white))
                .borderWidthDp(2)
                .cornerRadiusDp(60)
                .oval(true)
                .build();

        //setting user profile photo
        Picasso.with(this)
                .load(user.getProfileImageUrl()).resize(90, 90)
                .transform(transformation)
                .into(iv_profile_picture);

    }

    private void setupFragment(User user) {
        // Create the user timeline fragment
        UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(user.getScreenName());
        // Display user timeline fragment within this activity (dynamically... so needs a container)
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_containe, fragmentUserTimeline); // insert fragment dynamically into FrameLayout
        ft.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            client.clearAccessToken();
            Toast.makeText(this, "You have been logged out successfully", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
