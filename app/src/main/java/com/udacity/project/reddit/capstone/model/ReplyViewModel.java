package com.udacity.project.reddit.capstone.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Neha on 04-10-2017.
 */

public class ReplyViewModel implements Parcelable {
    public String kind;
    public String id;
    public String depth;
    public String subreddi_id;
    public String author;
    public int score;
    public String body;
    public int down;
    public int up;
    public String subreddit_name;
    public String name;
    public String link_id;
    public String parent_id;

    public ReplyViewModel(Parcel in) {
        id = in.readString();
        depth = in.readString();
        subreddi_id = in.readString();
        name = in.readString();
        up = in.readInt();
        author = in.readString();
        down = in.readInt();
        subreddit_name = in.readString();
        body = in.readString();
        score = in.readInt();
        link_id = in.readString();
        parent_id = in.readString();
        kind = in.readString();
    }

    public static final Creator<ReplyViewModel> CREATOR = new Creator<ReplyViewModel>() {
        @Override
        public ReplyViewModel createFromParcel(Parcel in) {
            return new ReplyViewModel(in);
        }

        @Override
        public ReplyViewModel[] newArray(int size) {
            return new ReplyViewModel[size];
        }
    };

    public ReplyViewModel() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(depth);
        dest.writeString(subreddi_id);
        dest.writeString(name);
        dest.writeInt(up);
        dest.writeString(author);
        dest.writeInt(down);
        dest.writeString(subreddit_name);
        dest.writeInt(score);
        dest.writeString(link_id);
        dest.writeString(body);
        dest.writeString(parent_id);
        dest.writeString(kind);
    }
}
