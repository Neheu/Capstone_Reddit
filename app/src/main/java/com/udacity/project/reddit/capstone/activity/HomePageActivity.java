package com.udacity.project.reddit.capstone.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.adapters.SubredditDetailListAdapter;
import com.udacity.project.reddit.capstone.db.ReadyItContract;
import com.udacity.project.reddit.capstone.db.ReadyitProvider;
import com.udacity.project.reddit.capstone.db.RedyItSQLiteOpenHelper;
import com.udacity.project.reddit.capstone.model.GetCommentsModel;
import com.udacity.project.reddit.capstone.model.GetDetailedSubredditListModel;
import com.udacity.project.reddit.capstone.model.SubredditListViewModel;
import com.udacity.project.reddit.capstone.model.SubscribeRedditsViewModel;
import com.udacity.project.reddit.capstone.server.ApiInterface;
import com.udacity.project.reddit.capstone.server.GetRefreshedToken;
import com.udacity.project.reddit.capstone.utils.Constants;
import com.udacity.project.reddit.capstone.utils.DatabaseUtils;
import com.udacity.project.reddit.capstone.utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomePageActivity extends AppCompatActivity implements SubredditDetailListAdapter.onSubredditSelectListener, GetRefreshedToken, LoaderManager.LoaderCallbacks<Cursor> {
    private ApiInterface apiInterface;
    private Intent intent;
    private String subredditUrl, detailName, subredditId;
    private int subscriberCount;
    @BindView(R.id.tv_subreddit_url)
    TextView tvSubredditUrl;
    @BindView(R.id.tv_subs_count)
    TextView tvSubsCount;
    @BindView(R.id.tv_detail_name)
    TextView tvDetailName;
    private RedyItSQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;
    @BindView(R.id.list_view)
    RecyclerView rvList;
    SubredditDetailListAdapter adapter;
    private ArrayList<SubredditListViewModel> subredditList = new ArrayList<>();
    private SubscribeRedditsViewModel intentData;
    private int LOADER_ID = 201;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        dbHelper = new RedyItSQLiteOpenHelper(this);
        intent = getIntent();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(layoutManager);

        intentData = intent.getExtras().getParcelable(getString(R.string.data));
        subredditUrl = intentData.url;
        subscriberCount = intentData.subCount;
        detailName = intentData.display_name;
        tvSubredditUrl.setText(subredditUrl);
        tvDetailName.setText(detailName);
        tvSubsCount.setText(String.valueOf(subscriberCount));
        subredditId = intentData.subreddit_id;

        db = dbHelper.getWritableDatabase();
        ReadyitProvider.tableToProcess(DatabaseUtils.TABLE_DETAIL_SUBREDDIT);
        adapter = new SubredditDetailListAdapter(this, this, this, rvList, subredditList);

        if (savedInstanceState != null) {
            subredditList = savedInstanceState.getParcelableArrayList(Constants.INTENT_HOME_DATA);
            adapter = new SubredditDetailListAdapter(this, this, this, rvList, subredditList);

            rvList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {

//        connectApiClient();
            new GetSubreddits().execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOADER_ID, null, HomePageActivity.this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = ReadyItContract.ReadyitEntry.CONTENT_URI;
        ReadyitProvider.tableToProcess(DatabaseUtils.TABLE_DETAIL_SUBREDDIT);

        return new CursorLoader(this, uri, null, ReadyItContract.ReadyitEntry.SUBREDDIT_ID + " ='" + subredditId + "'", null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        subredditList.clear();
        ReadyitProvider.tableToProcess(DatabaseUtils.TABLE_DETAIL_SUBREDDIT);

        if (loader.getId() == LOADER_ID)
            populateSubscribedRedditList(data);
        else getSupportLoaderManager().restartLoader(LOADER_ID, null, HomePageActivity.this);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        loader.deliverResult(getContentResolver().query(ReadyItContract.ReadyitEntry.CONTENT_URI, null, ReadyItContract.ReadyitEntry._ID + " ='" + subredditId + "'", null, null));

    }

    private class GetSubreddits extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if (new NetworkUtils().isOnline(HomePageActivity.this))
                Constants.refreshAccessToken(HomePageActivity.this, HomePageActivity.this, "detail_subreddit");
            return null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.INTENT_HOME_DATA, subredditList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getSupportLoaderManager().destroyLoader(LOADER_ID);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    static Retrofit retrofit = null;

    public static Retrofit getClient() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GetDetailedSubredditListModel.Data_.class, new GetDetailedSubredditListModel.LikesDeserializer())
                .setLenient()
                .create();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.API_OAUTH_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    private void connectApiClient(String token) {
        apiInterface = getClient().create(ApiInterface.class);
        Call call = apiInterface.doGetSubredditList(getString(R.string.bearer)+" " + token, subredditUrl + ".json");

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    return;
                }
                //Get the response from api
                GetDetailedSubredditListModel model = (GetDetailedSubredditListModel) response.body();
                List<GetDetailedSubredditListModel.Child> list = model.data.children;
                if (list != null && list.size() > 0) {
                    for (GetDetailedSubredditListModel.Child data : list) {
                        data.data.subs_count = subscriberCount;
//                        if(data.data.likes==null)
//                        {
//                            data.data.likes = -1;
//                        }
                        dbHelper.insertSubSubredditsDetail(data.data);
                    }
                }
                getSupportLoaderManager().initLoader(LOADER_ID, null, HomePageActivity.this);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
            }
        });


    }


//    private void updateList() {
//        ReadyitProvider.tableToProcess(DatabaseUtils.TABLE_DETAIL_SUBREDDIT);
//        Cursor cursor = getContentResolver().query(ReadyItContract.ReadyitEntry.CONTENT_URI, null, ReadyItContract.ReadyitEntry.SUBREDDIT_ID + " ='" + subredditId + "'", null, null);
//        populateSubscribedRedditList(cursor);
//    }

    private void populateSubscribedRedditList(Cursor subredditCursor) {


        if (subredditCursor != null && subredditCursor.moveToFirst())
            do {
                SubredditListViewModel holder = new SubredditListViewModel();
                holder.id = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry._ID));
                holder.up = subredditCursor.getInt(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.UP));
                holder.down = subredditCursor.getInt(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.DOWN));
                holder.is_subscribed = Boolean.valueOf(subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.USER_IS_SUBSCRIBER)));
                holder.subreddit_name = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.SUBREDDIT_NAME));
                holder.thumb_url = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.URL));
                holder.name = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.NAME));
                holder.subreddit_title = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.SUBREDDIT_TITLE));
                holder.comment_count = subredditCursor.getInt(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.COMMENTS_COUNT));
                holder.subreddit_id = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.SUBREDDIT_ID));
                holder.share_url = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.URL));
                holder.likes = subredditCursor.getInt(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.LIKES));
                subredditList.add(holder);
            }


            while (subredditCursor.moveToNext());
        rvList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(SubredditListViewModel dataHolder) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getSupportLoaderManager().destroyLoader(LOADER_ID);
        finish();
    }

    @Override
    public void onTokenRefreshed(String token, String tag) {
        connectApiClient(token);
    }
}
