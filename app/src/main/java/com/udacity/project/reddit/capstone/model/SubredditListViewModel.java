package com.udacity.project.reddit.capstone.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Neha on 03-09-2017.
 */

public class SubredditListViewModel implements Parcelable {
    public String id;
    public String subreddit_title;
    public String thumb_url;
    public String name;
    public int up;
    public String subreddit_id;
    public int down;
    public boolean is_subscribed;
    public String subreddit_name;
    public int subCount;
    public int comment_count;
    public String share_url;
    public int likes;

    public SubredditListViewModel(Parcel in) {
        id = in.readString();
        subreddit_title = in.readString();
        thumb_url = in.readString();
        name = in.readString();
        up = in.readInt();
        subreddit_id = in.readString();
        down = in.readInt();
        is_subscribed = in.readByte() != 0;
        subreddit_name = in.readString();
        subCount = in.readInt();
        comment_count = in.readInt();
        share_url = in.readString();
        likes = in.readInt();
    }

    public static final Creator<SubredditListViewModel> CREATOR = new Creator<SubredditListViewModel>() {
        @Override
        public SubredditListViewModel createFromParcel(Parcel in) {
            return new SubredditListViewModel(in);
        }

        @Override
        public SubredditListViewModel[] newArray(int size) {
            return new SubredditListViewModel[size];
        }
    };

    public SubredditListViewModel() {

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(subreddit_title);
        dest.writeString(thumb_url);
        dest.writeString(name);
        dest.writeInt(up);
        dest.writeString(subreddit_id);
        dest.writeInt(down);
        dest.writeByte((byte) (is_subscribed ? 1 : 0));
        dest.writeString(subreddit_name);
        dest.writeInt(subCount);
        dest.writeInt(comment_count);
        dest.writeString(share_url);
        dest.writeInt(likes);
    }
}

