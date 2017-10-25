package com.udacity.project.reddit.capstone.redditwidget;

/**
 * Created by Neha on 27-04-2017.
 */

import android.content.Intent;
import android.widget.RemoteViewsService;


public class RedditWidgetService extends RemoteViewsService {
//This will be used to get remote views for the stocks..
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RedditWidgetFactory(getApplicationContext(),intent);

    }
}
