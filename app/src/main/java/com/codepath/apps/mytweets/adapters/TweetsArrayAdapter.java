package com.codepath.apps.mytweets.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.mytweets.R;
import com.codepath.apps.mytweets.activities.TimeLineActivity;
import com.codepath.apps.mytweets.connection.TwitterApplication;
import com.codepath.apps.mytweets.connection.TwitterClient;
import com.codepath.apps.mytweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by dbykovskyy on 9/29/15.
 */

public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {
    private TwitterClient client;
    private ForegroundColorSpan span;

    private class ViewHolder {
        TextView tvUserName;
        TextView tvTweetText;
        TextView tvTimeStamp;
        TextView tvRetweetCount;
        TextView tvFavouritesCount;
        ImageView ivTwetImage;
        ImageView ivProfileImage;
        ImageView ivReplyToTweet;

        public void bind(final Tweet tweet) {
            Spannable userScreenName  = new SpannableString(tweet.getUser().getUserName()+" @" +tweet.getUser().getScreenName());
            int spanStart = tweet.getUser().getUserName().length()+1;
            int spanEnds = spanStart+tweet.getUser().getScreenName().length()+1;
            userScreenName.setSpan(span, spanStart, spanEnds, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            this.tvUserName.setText(userScreenName);
            this.tvTweetText.setText(tweet.getBody());
            this.tvTimeStamp.setText(getRelativeTimeAgo(tweet.getCreatedAt()));

            if(tweet.getRetweetCount()>0){
               this.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
            }
            if(tweet.getRetweetCount()>0) {
                this.tvFavouritesCount.setText(String.valueOf(tweet.getFavouritesCount()));
            }


            this.ivTwetImage.setImageResource(0);
            if(tweet.getMediaUrl()!=null){
                Picasso.with(getContext()).load(tweet.getMediaUrl()).into(this.ivTwetImage);
            }


            this.ivProfileImage.setImageResource(0);
            Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(this.ivProfileImage);


            //listener to reply to a tweet
            this.ivReplyToTweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buildDialog(getContext(), tweet).show();

                }
            });
        }

    }


    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, R.layout.tweet_item, tweets);
        span = new ForegroundColorSpan(getContext().getResources().getColor(R.color.twitter_gray));
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Tweet tweet = getItem(position);
        final ViewHolder viewHolder;


        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.tweet_item, parent, false);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_profile_name);
            viewHolder.tvTweetText = (TextView) convertView.findViewById(R.id.tv_tweet_text);
            viewHolder.ivProfileImage = (ImageView) convertView.findViewById(R.id.iv_userPic);
            viewHolder.tvTimeStamp = (TextView) convertView.findViewById(R.id.tv_timestamp);
            viewHolder.tvRetweetCount = (TextView) convertView.findViewById(R.id.tvRetweets);
            viewHolder.tvFavouritesCount = (TextView) convertView.findViewById(R.id.tvFavoritest);
            viewHolder.ivTwetImage = (ImageView) convertView.findViewById(R.id.iv_tweetImage);
            viewHolder.ivReplyToTweet = (ImageView) convertView.findViewById(R.id.ivReply);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.bind(tweet);



        return convertView;
    }



    public AlertDialog.Builder buildDialog(Context c, final Tweet tweet) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c, android.R.style.Theme_Material_Light_Dialog_NoActionBar);

        final EditText replyText = new EditText(c);
        replyText.setText("@" + tweet.getUser().getScreenName() + " ");
        replyText.setSelection(replyText.getText().length());
        builder.setTitle("In reply to "+tweet.getUser().getUserName());
        builder.setView(replyText);
        builder.setIcon(R.drawable.ic_twitter_bird);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                client = TwitterApplication.getRestClient();
                client.replyToTweetTweet(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getContext(),"Sorry, couldn't reply to this tweet", Toast.LENGTH_LONG).show();
                    }
                },replyText.getText().toString(), tweet.getId());


                dialog.dismiss();
            }
        });

        return builder;
    }


    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();

            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(relativeDate.contains("second")){
            return relativeDate.replaceAll("[^0-9]+", "s");
        }else if(relativeDate.contains("minute")) {
            return relativeDate.replaceAll("[^0-9]+", "m");
        }else if(relativeDate.contains("hour")){
            return relativeDate.replaceAll("[^0-9]+", "h");
        }else if(relativeDate.contains("day")){
            return relativeDate.replaceAll("[^0-9]+", "d");
        }else return relativeDate.replaceAll("[^0-9]+", "m");


    }
}
