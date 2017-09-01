package com.udacity.project.reddit.capstone.model;

/**
 * Created by Neha on 23-08-2017.
 */

public class SubscribeRedditsViewModel {
    public String id;
    public String title;
    public String url;
    public String name;
    public String display_name;
    public String after;
    public String before;
    public boolean hasChecked;
    public void isSelected(boolean bol) {
        hasChecked = bol;
    }

}
