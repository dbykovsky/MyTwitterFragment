package com.codepath.apps.mytweets.adapters;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mytweets.R;
import com.codepath.apps.mytweets.models.Tweet;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by dbykovskyy on 9/29/15.
 */

public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    static class ViewHolder {

        TextView tvUserName;
        TextView tvTweetText;
        TextView tvTimeStamp;
        ImageView ivProfileImage;


    }


    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_expandable_list_item_1, tweets);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Tweet tweet = getItem(position);
        final ViewHolder viewHolder;


        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_tweet, parent, false);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_profile_name);
            viewHolder.tvTweetText = (TextView) convertView.findViewById(R.id.tv_tweet_text);
            viewHolder.ivProfileImage = (ImageView) convertView.findViewById(R.id.iv_userPic);
            viewHolder.tvTimeStamp = (TextView) convertView.findViewById(R.id.tv_timestamp);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Spannable userScreenName  = new SpannableString(tweet.getUser().getUserName()+" @" +tweet.getUser().getScreenName());
        int spanStart = tweet.getUser().getUserName().length()+1;
        int spanEnds = spanStart+tweet.getUser().getScreenName().length()+1;
        userScreenName.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.gull_gray)), spanStart,spanEnds, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        viewHolder.tvUserName.setText(userScreenName);
        viewHolder.tvTweetText.setText(tweet.getBody());
        viewHolder.tvTimeStamp.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
        viewHolder.ivProfileImage.setImageResource(0);
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(viewHolder.ivProfileImage);

        return convertView;
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
