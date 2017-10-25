package com.udacity.project.reddit.capstone.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.oissela.software.multilevelexpindlistview.MultiLevelExpIndListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neha on 11-10-2017.
 */

public class CommentsParentModel implements Parcelable, MultiLevelExpIndListAdapter.ExpIndData {
    private int mIndentation;
    private List<CommentsParentModel> mChildren;
    private boolean mIsGroup;
    private int mGroupSize;

    public String author;
    public String comment;

    public CommentsParentModel(String author, String comment) {
        this.author = author;
        this.comment = comment;
        mChildren = new ArrayList<>();

        setIndentation(0);
    }

    protected CommentsParentModel(Parcel in) {
        mIndentation = in.readInt();
        mChildren = in.createTypedArrayList(CommentsParentModel.CREATOR);
        mIsGroup = in.readByte() != 0;
        mGroupSize = in.readInt();
        author = in.readString();
        comment = in.readString();
    }

    public static final Creator<CommentsParentModel> CREATOR = new Creator<CommentsParentModel>() {
        @Override
        public CommentsParentModel createFromParcel(Parcel in) {
            return new CommentsParentModel(in);
        }

        @Override
        public CommentsParentModel[] newArray(int size) {
            return new CommentsParentModel[size];
        }
    };

    @Override
    public List<? extends MultiLevelExpIndListAdapter.ExpIndData> getChildren() {
        return mChildren;
    }

    @Override
    public boolean isGroup() {
        return mIsGroup;
    }

    @Override
    public void setIsGroup(boolean value) {
        mIsGroup = value;
    }

    @Override
    public void setGroupSize(int groupSize) {
        mGroupSize = groupSize;
    }

    public int getmGroupSize() {
        return mGroupSize;
    }
    public void addChild(CommentsParentModel child) {
        mChildren.add(child);
        child.setIndentation(getIndentation() + 1);
    }
    public int getIndentation() {
        return mIndentation;
    }

    private void setIndentation(int indentation) {
        mIndentation = indentation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mIndentation);
        dest.writeTypedList(mChildren);
        dest.writeByte((byte) (mIsGroup ? 1 : 0));
        dest.writeInt(mGroupSize);
        dest.writeString(author);
        dest.writeString(comment);
    }
}
