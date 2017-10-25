package com.udacity.project.reddit.capstone.redditwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.activity.ReadyItWidgetActivity;
import com.udacity.project.reddit.capstone.db.ReadyItContract;
import com.udacity.project.reddit.capstone.db.ReadyitProvider;
import com.udacity.project.reddit.capstone.db.RedyItSQLiteOpenHelper;
import com.udacity.project.reddit.capstone.model.GetSubredditsModel;
import com.udacity.project.reddit.capstone.model.SubscribeRedditsViewModel;
import com.udacity.project.reddit.capstone.server.ApiClient;
import com.udacity.project.reddit.capstone.server.ApiInterface;
import com.udacity.project.reddit.capstone.utils.Constants;
import com.udacity.project.reddit.capstone.utils.DatabaseUtils;
import com.udacity.project.reddit.capstone.utils.NetworkUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.udacity.project.reddit.capstone.utils.DatabaseUtils.TABLE_SUBS_SUBREDDIT;


/**
 * Created by Neha on 27-04-2017.
 */
public class RedditWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private Cursor _cursor;
    private Context _context;
    int _widgetId;

    public RedditWidgetFactory(Context applicationContext, Intent intent) {
        this._context = applicationContext;
        _widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        SharedPreferences pref = _context.getSharedPreferences(Constants.PREFRENCE_NAME, Context.MODE_PRIVATE);
        if (NetworkUtils.isOnline(_context) && pref.getString(Constants.PREFRENCE_TOKEN, "") != null) {
            connectApiClient("subreddits/popular.json", pref.getString(Constants.PREFRENCE_TOKEN, ""));
        }
        else
            _cursor = _context.getContentResolver().query(ReadyItContract.ReadyitEntry.CONTENT_URI,
                    null,
                    ReadyItContract.ReadyitEntry.USER_IS_SUBSCRIBER + " ='0'",
                    null,
                    null);
    }

    @Override
    public void onDataSetChanged() {
        if (_cursor != null) {
            _cursor.close();
        }
        ReadyitProvider.tableToProcess(DatabaseUtils.TABLE_SUBS_SUBREDDIT);
        //get the data from database and to show on widget
        _cursor = _context.getContentResolver().query(ReadyItContract.ReadyitEntry.CONTENT_URI,
                null,
                ReadyItContract.ReadyitEntry.USER_IS_SUBSCRIBER + " ='0'",
                null,
                null);
    }


    private ApiInterface apiInterface;
    private RedyItSQLiteOpenHelper dbHelper;

    private void connectApiClient(String url, String token) {
        dbHelper = new RedyItSQLiteOpenHelper(_context);
        Map<String, Object> map = new HashMap<>();
        map.put("limit", "10");
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiInterface.doGetSubredditsListResources("bearer " + token, url, map);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {

                    return;
                }
                //Get the response from api
                GetSubredditsModel model = (GetSubredditsModel) response.body();

                //get and set subreddits into a list
                List<GetSubredditsModel.Child> list = model.data.children;
                if (list != null && list.size() > 0) {
                    for (GetSubredditsModel.Child data : list) {
                        if (!dbHelper.isAlreadyInserted(data.data.id)) {
                            ReadyitProvider.tableToProcess(TABLE_SUBS_SUBREDDIT);
                            dbHelper.insertSubSubreddits(data);
                        }
                    }
                    _cursor = _context.getContentResolver().query(ReadyItContract.ReadyitEntry.CONTENT_URI,
                            null,
                            ReadyItContract.ReadyitEntry.USER_IS_SUBSCRIBER + " ='0'",
                            null,
                            null);

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    @Override
    public void onDestroy() {
        if (_cursor != null) {
            _cursor.close();
        }
    }

    @Override
    public int getCount() {
        return _cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        //This will create the view remotely for the items to be showm on list of stocks
        RemoteViews rv = new RemoteViews(_context.getPackageName(), R.layout.layout_widget_item);
        if (_cursor.moveToPosition(position)) {
            rv.setTextViewText(R.id.txt_subreddit_title,
                    _cursor.getString(_cursor.getColumnIndex(ReadyItContract.ReadyitEntry.TITLE)));

        }
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
