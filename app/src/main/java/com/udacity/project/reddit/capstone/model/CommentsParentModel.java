package com.udacity.project.reddit.capstone.model;


import com.oissela.software.multilevelexpindlistview.MultiLevelExpIndListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neha on 11-10-2017.
 */

public class CommentsParentModel implements MultiLevelExpIndListAdapter.ExpIndData {
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
}
