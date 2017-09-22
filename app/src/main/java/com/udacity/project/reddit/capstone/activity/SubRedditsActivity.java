package com.udacity.project.reddit.capstone.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.adapters.SubredditsAdapters;
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
import com.udacity.project.reddit.capstone.utils.OnLoadMoreListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.udacity.project.reddit.capstone.utils.Constants.ACCESS_TOKEN_URL;
import static com.udacity.project.reddit.capstone.utils.Constants.REDIRECT_URI;

public class SubRedditsActivity extends AppCompatActivity implements SubredditsAdapters.checkChangeListener, SubredditsAdapters.onSubredditSelectListener, SwipeRefreshLayout.OnRefreshListener {
    private ApiInterface apiInterface;
    private RecyclerView recycleViewSubreddit;
    @BindView(R.id.btn_done)
    FloatingActionButton btnDone;
    private Cursor cursor;
    private RedyItSQLiteOpenHelper dbHelper;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private String before = "", after = "";
    private ArrayList<SubscribeRedditsViewModel> favList = new ArrayList<>();
    private SubredditsAdapters adapters;
    private NetworkUtils networkUtils;
    private List<String> sub = new ArrayList<>(), unsub = new ArrayList<>();
    Intent intent;

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_reddits);
//         getSupportActionBar().setTitle(R.string.subreddits);
        ButterKnife.bind(this);
        preferences = getSharedPreferences(Constants.PREFRENCE_NAME, MODE_PRIVATE);
        editor = preferences.edit();
        networkUtils = new NetworkUtils();

        recycleViewSubreddit = (RecyclerView) findViewById(R.id.rv_subreddit);
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        recycleViewSubreddit.setLayoutManager(gridLayoutManager);
        dbHelper = new RedyItSQLiteOpenHelper(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        intent = getIntent();
        intent.getBooleanExtra("for_my_subreddits", true);


        //Connect to ReteoFit and get subreddits
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        if (preferences.contains(Constants.PREFRENCE_AFTER)) {
            after = preferences.getString(Constants.PREFRENCE_AFTER, "");
            before = preferences.getString(Constants.PREFRENCE_BEFORE, "");
        }
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        if (networkUtils.isOnline(SubRedditsActivity.this))
                                            connectApiClient("/subreddits/popular.json?limit=10&before=" + before);
                                        else
                                            updateList();
                                    }
                                }
        );

        adapters = new SubredditsAdapters(SubRedditsActivity.this, recycleViewSubreddit, favList, SubRedditsActivity.this, SubRedditsActivity.this);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Map.Entry<String, SubscribeRedditsViewModel> mapEntry : SubredditsAdapters.checkedList.entrySet()) {
                    String key = mapEntry.getKey();
                    SubscribeRedditsViewModel value = mapEntry.getValue();
                    dbHelper.updateSubscribeReddits(value);
                    if (value.hasChecked)
                        sub.add(value.display_name);
                    else
                        unsub.add(value.display_name);

                }
//                if (sub.size() > 0)
//                    requestForSubredditSubscription("sub", android.text.TextUtils.join(",", sub));
//                if (unsub.size() > 0)
//                    requestForSubredditSubscription("unsub", android.text.TextUtils.join(",", unsub));
                getMySubscribedSubreddits();

            }
        });
        adapters.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (networkUtils.isOnline(SubRedditsActivity.this))
                    connectApiClient("/subreddits/popular.json?limit=10&after=" + after);
                else
                    updateList();
            }
        });
    }

    private void connectApiClient(String url) {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiInterface.doGetSubredditsListResources(url);
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
                        if (!dbHelper.isAlreadyInserted(data.data.id))
                            dbHelper.insertSubSubreddits(data);
                    }
                    before = list.get(0).data.name;
                    after = list.get(list.size() - 1).data.name;
                    editor.putString(Constants.PREFRENCE_AFTER, after);
                    editor.putString(Constants.PREFRENCE_BEFORE, before);
                    editor.apply();

                    updateList();
                    // stopping swipe refresh
                    swipeRefreshLayout.setRefreshing(false);
                } else if (favList.size() == 0) {
                    updateList();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    private void populateSubscribedRedditList(Cursor subredditCursor) {
        favList.clear();
        if (cursor != null && cursor.moveToFirst())
            do {
                SubscribeRedditsViewModel holder = new SubscribeRedditsViewModel();
                holder.id = cursor.getString(cursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry._ID));
                holder.hasChecked = Boolean.valueOf(subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.IS_SUBSCRIBED)));
                holder.display_name = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.DISPLAY_NAME));
                holder.url = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.URL));
                holder.name = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.NAME));
                holder.title = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.TITLE));
                holder.kind = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.KIND));
                holder.subCount = subredditCursor.getInt(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.SUBS_COUNT));
                holder.subreddit_id = holder.kind + "_" + holder.id;
                favList.add(holder);
            }


            while (cursor.moveToNext());
        recycleViewSubreddit.setAdapter(adapters);
        adapters.notifyDataSetChanged();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        onCreateOptionsMenu(menu);

        return super.onCreateOptionsMenu(menu);

    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void checkCount(int count) {
//        if (count > 0)
//            btnDone.setVisibility(View.VISIBLE);
//        else
//            btnDone.setVisibility(View.GONE);
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        if (networkUtils.isOnline(SubRedditsActivity.this))
            updateList();
        else
            connectApiClient("/subreddits/popular.json?limit=10&before=" + before + "&after=" + after);


    }

    private void updateList() {
        ReadyitProvider.tableToProcess(DatabaseUtils.TABLE_SUBS_SUBREDDIT);
        cursor = getContentResolver().query(ReadyItContract.ReadyitEntry.CONTENT_URI, null, null, null, null);
        populateSubscribedRedditList(cursor);
        // stopping swipe refresh
        swipeRefreshLayout.setRefreshing(false);
    }


    private void getMySubscribedSubreddits() {
        OkHttpClient client = new OkHttpClient();
        String token = Constants.getToken(this);


        Request request = new Request.Builder()
                .addHeader("Authorization", "bearer " + token)
                .url(Constants.API_OAUTH_BASE_URL + Constants.API_SUBREDDIT + "/mine/subscriber")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e(">> ", "ERROR: " + e);
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String json = response.body().string();
                int code = response.code();

                if (code == 200) {

                }

            }
        });
    }


    @Override
    public void onClick(SubscribeRedditsViewModel dataHolder) {
        startActivity(new Intent(SubRedditsActivity.this, HomePageActivity.class).putExtra("data", dataHolder));

    }
}
