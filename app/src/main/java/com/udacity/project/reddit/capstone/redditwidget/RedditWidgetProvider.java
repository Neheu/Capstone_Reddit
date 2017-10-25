package com.udacity.project.reddit.capstone.redditwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.activity.MineSubredditsActivity;
import com.udacity.project.reddit.capstone.activity.ReadyItWidgetActivity;


/**
 * Created by Neha on 27-04-2017.
 */

public class RedditWidgetProvider extends AppWidgetProvider {
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.layout_widget);

        views.setRemoteAdapter(R.id.rv_subreddit,
                new Intent(context, RedditWidgetService.class));

        Intent appIntent = new Intent(context, MineSubredditsActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, 0);

        views.setOnClickPendingIntent(R.id.txt_widget, appPendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        //  when the last widget is disabled
    }
}
