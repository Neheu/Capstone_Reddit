package com.udacity.project.reddit.capstone.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;

import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.adapters.SubredditsAdapters;
import com.udacity.project.reddit.capstone.adapters.WidgetListAdapter;
import com.udacity.project.reddit.capstone.db.ReadyItContract;
import com.udacity.project.reddit.capstone.db.ReadyitProvider;
import com.udacity.project.reddit.capstone.db.RedyItSQLiteOpenHelper;
import com.udacity.project.reddit.capstone.model.GetSubredditsModel;
import com.udacity.project.reddit.capstone.model.SubscribeRedditsViewModel;
import com.udacity.project.reddit.capstone.server.ApiClient;
import com.udacity.project.reddit.capstone.server.ApiInterface;
import com.udacity.project.reddit.capstone.server.GetRefreshedToken;
import com.udacity.project.reddit.capstone.utils.Constants;
import com.udacity.project.reddit.capstone.utils.DatabaseUtils;
import com.udacity.project.reddit.capstone.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.udacity.project.reddit.capstone.utils.DatabaseUtils.TABLE_SUBS_SUBREDDIT;

/**
 * Created by Neha on 23-10-2017.
 */

public class ReadyItWidgetActivity extends AppCompatActivity implements GetRefreshedToken, LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.rv_subreddit)
    ListView listView;
    private WidgetListAdapter adapters;
    private ArrayList<SubscribeRedditsViewModel> subredditDataList = new ArrayList<>();

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_widget);
        ButterKnife.bind(this);

        adapters = new WidgetListAdapter(ReadyItWidgetActivity.this, subredditDataList);


        if (!NetworkUtils.isOnline(ReadyItWidgetActivity.this))
            new GetSubredditsList().execute();
        else
            getSupportLoaderManager().initLoader(333, null, ReadyItWidgetActivity.this);

    }

    private class GetSubredditsList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Constants.refreshAccessToken(ReadyItWidgetActivity.this, ReadyItWidgetActivity.this, "subredditswidget_list");
            return null;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        ReadyitProvider.tableToProcess(DatabaseUtils.TABLE_SUBS_SUBREDDIT);

        return new CursorLoader(this, ReadyItContract.ReadyitEntry.CONTENT_URI, null, ReadyItContract.ReadyitEntry.USER_IS_SUBSCRIBER + " ='0'", null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        populateSubscribedRedditList(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void populateSubscribedRedditList(Cursor subredditCursor) {
        subredditDataList.clear();
        if (subredditCursor != null && subredditCursor.moveToFirst())
            do {
                SubscribeRedditsViewModel holder = new SubscribeRedditsViewModel();
                holder.id = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry._ID));
                holder.isSubscribed = Boolean.valueOf(subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.USER_IS_SUBSCRIBER)));
                holder.display_name = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.DISPLAY_NAME));
                holder.url = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.URL));
                holder.name = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.NAME));
                holder.title = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.TITLE));
                holder.kind = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.KIND));
                holder.subCount = subredditCursor.getInt(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.SUBS_COUNT));
                holder.subreddit_id = holder.kind + "_" + holder.id;
                subredditDataList.add(holder);
            }


            while (subredditCursor.moveToNext());
        listView.setAdapter(adapters);
        adapters.notifyDataSetChanged();

    }

    @Override
    public void onTokenRefreshed(String token, String tag) {
        connectApiClient("/subreddits/popular.json", token);
    }

    private ApiInterface apiInterface;
    private RedyItSQLiteOpenHelper dbHelper;

    private void connectApiClient(String url, String token) {
        dbHelper = new RedyItSQLiteOpenHelper(this);
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
                        if (!dbHelper.isAlreadyInserted(data.data.id,TABLE_SUBS_SUBREDDIT)) {
                           // ReadyitProvider.tableToProcess(TABLE_SUBS_SUBREDDIT);
                            dbHelper.insertSubSubreddits(data);
                        }
                    }

                    getSupportLoaderManager().initLoader(333, null, ReadyItWidgetActivity.this);

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }
}
