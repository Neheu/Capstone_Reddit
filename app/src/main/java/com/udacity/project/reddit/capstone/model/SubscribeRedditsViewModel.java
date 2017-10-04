package com.udacity.project.reddit.capstone.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Neha on 23-08-2017.
 */
public class SubscribeRedditsViewModel implements Parcelable {
    public String id;
    public String title;
    public String url;
    public String name;
    public String display_name;
    public String after;
    public String before;
    public boolean isSubscribed;
    public String subreddit_id;
    public String kind;
    public int subCount;


    public SubscribeRedditsViewModel(Parcel in) {
        id = in.readString();
        title = in.readString();
        url = in.readString();
        name = in.readString();
        display_name = in.readString();
        after = in.readString();
        before = in.readString();
        isSubscribed = in.readByte() != 0;
        subreddit_id = in.readString();
        kind = in.readString();
        subCount = in.readInt();
    }
    public SubscribeRedditsViewModel()
    {

    }

    public static final Creator<SubscribeRedditsViewModel> CREATOR = new Creator<SubscribeRedditsViewModel>() {
        @Override
        public SubscribeRedditsViewModel createFromParcel(Parcel in) {
            return new SubscribeRedditsViewModel(in);
        }

        @Override
        public SubscribeRedditsViewModel[] newArray(int size) {
            return new SubscribeRedditsViewModel[size];
        }
    };

    public void isSelected(boolean bol) {
        isSubscribed = bol;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(name);
        dest.writeString(display_name);
        dest.writeString(after);
        dest.writeString(before);
        dest.writeByte((byte) (isSubscribed ? 1 : 0));
        dest.writeString(subreddit_id);
        dest.writeString(kind);
        dest.writeInt(subCount);
    }
}
