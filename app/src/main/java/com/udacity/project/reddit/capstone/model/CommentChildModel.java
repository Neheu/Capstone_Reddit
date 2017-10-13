package com.udacity.project.reddit.capstone.model;


import com.oissela.software.multilevelexpindlistview.MultiLevelExpIndListAdapter;

import java.util.List;

/**
 * Created by Neha on 11-10-2017.
 */

public class CommentChildModel implements MultiLevelExpIndListAdapter.ExpIndData {
    public String content;

    public CommentChildModel(String content) {
        this.content = content;
    }

    @Override
    public List<? extends MultiLevelExpIndListAdapter.ExpIndData> getChildren() {
        return null;
    }

    @Override
    public boolean isGroup() {
        return false;
    }

    @Override
    public void setIsGroup(boolean value) {
    }

    @Override
    public void setGroupSize(int groupSize) {

    }
}
