package com.udacity.project.reddit.capstone.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.adapters.SubredditsAdapters;
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
import com.udacity.project.reddit.capstone.utils.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.udacity.project.reddit.capstone.utils.DatabaseUtils.TABLE_SUBS_SUBREDDIT;


public class MineSubredditsActivity extends AppCompatActivity implements SubredditsAdapters.checkChangeListener, SubredditsAdapters.onSubredditSelectListener, SwipeRefreshLayout.OnRefreshListener,
        GetRefreshedToken, LoaderManager.LoaderCallbacks<Cursor> {
    private ApiInterface apiInterface;
    private RecyclerView recycleViewSubreddit;
    @BindView(R.id.btn_subs_now)
    Button btnSubsNow;
    @BindView(R.id.toolbar)
    Toolbar toolBar;
    @BindView(R.id.no_subs_subreddit_layout)
    LinearLayout noSubscribeLayout;
    private Cursor cursor;
    private RedyItSQLiteOpenHelper dbHelper;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private String before = "", after = "";
    private ArrayList<SubscribeRedditsViewModel> favList = new ArrayList<>();
    private SubredditsAdapters adapters;
    private Snackbar snackbar;
    @BindView(R.id.parent_layout)
    View parentLayout;
    private final int LOADER_ID = 101;
    private List<GetSubredditsModel.Child> list;
    @BindView(R.id.adView)
    AdView addView;

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_reddits);
        ButterKnife.bind(this);

        setSupportActionBar(toolBar);
        preferences = getSharedPreferences(Constants.PREFRENCE_NAME, MODE_PRIVATE);
        editor = preferences.edit();

        recycleViewSubreddit = (RecyclerView) findViewById(R.id.rv_subreddit);
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        recycleViewSubreddit.setLayoutManager(gridLayoutManager);
        dbHelper = new RedyItSQLiteOpenHelper(this);
        adapters = new SubredditsAdapters(false, MineSubredditsActivity.this, recycleViewSubreddit, favList, MineSubredditsActivity.this, MineSubredditsActivity.this);
        recycleViewSubreddit.setAdapter(adapters);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        addView.loadAd(adRequest);
        snackbar = Snackbar
                .make(parentLayout, R.string.unsubscribe_subreddits, Snackbar.LENGTH_INDEFINITE);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (savedInstanceState != null) {
                                            favList = savedInstanceState.getParcelableArrayList(Constants.INTENT_MINE_SUBREDDITS);
                                            adapters = new SubredditsAdapters(false, MineSubredditsActivity.this, recycleViewSubreddit, favList, MineSubredditsActivity.this, MineSubredditsActivity.this);

                                            recycleViewSubreddit.setAdapter(adapters);

                                            adapters.notifyDataSetChanged();
                                        } else {
                                            swipeRefreshLayout.setRefreshing(true);
                                            if (NetworkUtils.isOnline(MineSubredditsActivity.this))
                                                new GetSubredditsList().execute();
                                            else
                                                getSupportLoaderManager().initLoader(1, null, MineSubredditsActivity.this);

                                        }
                                    }
                                }
        );


//        adapters.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//                if (networkUtils.isOnline(MineSubredditsActivity.this))
//                    connectApiClient("/subreddits/mine/subscriber.json");
//                else
//                    updateList();
//            }
//        });
        btnSubsNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MineSubredditsActivity.this, SubRedditsActivity.class));
            }
        });
    }

    private String token = "";

    @Override
    public void onTokenRefreshed(String token, String tag) {
        this.token = token;
        connectApiClient(Constants.MINE_SUBS_URL);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.INTENT_MINE_SUBREDDITS, favList);
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void checkCount(int count, HashMap<String, SubscribeRedditsViewModel> data) {
        if (count > 0) {
            showSnackbar(data);
        } else
            snackbar.dismiss();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = ReadyItContract.ReadyitEntry.CONTENT_URI;
        ReadyitProvider.tableToProcess(DatabaseUtils.TABLE_SUBS_SUBREDDIT);

        return new CursorLoader(this, uri, null, ReadyItContract.ReadyitEntry.USER_IS_SUBSCRIBER + " ='1'", null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        swipeRefreshLayout.setRefreshing(false);
        ReadyitProvider.tableToProcess(DatabaseUtils.TABLE_SUBS_SUBREDDIT);
        if (loader.getId() == LOADER_ID)
            populateSubscribedRedditList(data);
        else
            getSupportLoaderManager().restartLoader(LOADER_ID, null, MineSubredditsActivity.this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        loader.deliverResult(getContentResolver().query(ReadyItContract.ReadyitEntry.CONTENT_URI, null, ReadyItContract.ReadyitEntry.USER_IS_SUBSCRIBER + " ='1'", null, null));

    }

    private class GetSubredditsList extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            Constants.refreshAccessToken(MineSubredditsActivity.this, MineSubredditsActivity.this, "subreddits_list");
            return null;
        }
    }

    private List<GetSubredditsModel.Child> connectApiClient(String url) {
        Map<String, Object> map = new HashMap<>();
        map.put(getString(R.string.after), after);
        map.put(getString(R.string.limit), "10");
        map.put(getString(R.string.count), "0");
        map.put(getString(R.string.before), before);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiInterface.doGetMineSubreddits(getString(R.string.bearer) + token, url, map);
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
                        ReadyitProvider.tableToProcess(TABLE_SUBS_SUBREDDIT);
                        if (!dbHelper.isAlreadyInserted(data.data.id)) {
                            dbHelper.insertSubSubreddits(data);
                        }
                    }
                    before = list.get(0).data.name;
                    after = list.get(list.size() - 1).data.name;
                    editor.putString(Constants.PREFRENCE_AFTER, after);
                    editor.putString(Constants.PREFRENCE_BEFORE, before);
                    editor.apply();

                    getSupportLoaderManager().initLoader(LOADER_ID, null, MineSubredditsActivity.this);

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
        return list;
    }

    private void populateSubscribedRedditList(Cursor subredditCursor) {
        favList.clear();
        swipeRefreshLayout.setRefreshing(false);

        noSubscribeLayout.setVisibility(View.GONE);
        if (subredditCursor.getCount() <= 0) {
            noSubscribeLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        } else if (subredditCursor != null && subredditCursor.moveToFirst())
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
                favList.add(holder);
            }


            while (subredditCursor.moveToNext());

        adapters.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOADER_ID, null, MineSubredditsActivity.this);

//        if (dbHelper.isTableNotEmpty(DatabaseUtils.TABLE_SUBS_SUBREDDIT, dbHelper.getWritableDatabase()) && NetworkUtils.isOnline(MineSubredditsActivity.this)) {
//            swipeRefreshLayout.setRefreshing(true);
//            new GetSubredditsList().execute();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_subreddit, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_subreddits:
                startActivity(new Intent(MineSubredditsActivity.this, SubRedditsActivity.class));
                break;
            case android.R.id.home:
                getSupportLoaderManager().destroyLoader(LOADER_ID);
                finish();

                break;


        }
        return true;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        if (!NetworkUtils.isOnline(MineSubredditsActivity.this))
            connectApiClient(Constants.MINE_SUBS_URL);
        else

            getSupportLoaderManager().initLoader(1, null, MineSubredditsActivity.this);
    }


//    private void updateList() {
//        cursor = getContentResolver().query(ReadyItContract.ReadyitEntry.CONTENT_URI, null, ReadyItContract.ReadyitEntry.USER_IS_SUBSCRIBER + " ='1'", null, null);
//        populateSubscribedRedditList(cursor);
//        // stopping swipe refresh
//        swipeRefreshLayout.setRefreshing(false);
//
//        noSubscribeLayout.setVisibility(View.GONE);
//
//    }


    @Override
    public void onClick(SubscribeRedditsViewModel dataHolder) {
        startActivity(new Intent(MineSubredditsActivity.this, HomePageActivity.class).putExtra(getString(R.string.data), dataHolder));

    }

    public void showSnackbar(final HashMap<String, SubscribeRedditsViewModel> data) {
        snackbar.setAction(R.string.done, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String key : data.keySet()) {
                    String name = data.get(key).name;
                    updateSubscription(getString(R.string.unsub), MineSubredditsActivity.this, name, data.get(key).id);

                }
            }
        });
        snackbar.setActionTextColor(Color.RED);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.DKGRAY);
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    private void updateSubscription(final String subVal, Context context, String subredditFullName, final String id) {
        ApiInterface mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        String token = getString(R.string.bearer) + Constants.getToken(context);
        mApiInterface.doSubUnsubSubreddit(token, subVal, false, subredditFullName)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            ReadyitProvider.tableToProcess(TABLE_SUBS_SUBREDDIT);
                            dbHelper.updateSubscribeReddits(id, "0");
                            getSupportLoaderManager().initLoader(1, null, MineSubredditsActivity.this);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                    }
                });
    }
}
