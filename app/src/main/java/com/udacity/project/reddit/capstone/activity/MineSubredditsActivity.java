package com.udacity.project.reddit.capstone.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
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
        GetRefreshedToken {
    private ApiInterface apiInterface;
    private RecyclerView recycleViewSubreddit;
    @BindView(R.id.btn_subs_now)
    Button btnSubsNow;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.no_subs_subreddit_layout)
    LinearLayout no_subscribe_layout;
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
    private Snackbar snackbar;
    @BindView(R.id.parent_layout)
    View parentLayout;

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_reddits);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        preferences = getSharedPreferences(Constants.PREFRENCE_NAME, MODE_PRIVATE);
        editor = preferences.edit();
        networkUtils = new NetworkUtils();

        recycleViewSubreddit = (RecyclerView) findViewById(R.id.rv_subreddit);
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        recycleViewSubreddit.setLayoutManager(gridLayoutManager);
        dbHelper = new RedyItSQLiteOpenHelper(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        snackbar = Snackbar
                .make(parentLayout, "UnSubscribe Subreddits!", Snackbar.LENGTH_INDEFINITE);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        if (networkUtils.isOnline(MineSubredditsActivity.this))
                                            new GetSubredditsList().execute();
                                        else
                                            updateList();
                                    }
                                }
        );

        adapters = new SubredditsAdapters(false, MineSubredditsActivity.this, recycleViewSubreddit, favList, MineSubredditsActivity.this, MineSubredditsActivity.this);


        adapters.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (networkUtils.isOnline(MineSubredditsActivity.this))
                    connectApiClient("/subreddits/mine/subscriber.json");
                else
                    updateList();
            }
        });
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
        connectApiClient("/subreddits/mine/subscriber.json");

    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void checkCount(int count, HashMap<String, SubscribeRedditsViewModel> data) {
        if (count > 0) {
            showSnackbar(data);
        } else
            snackbar.dismiss();
    }

    private class GetSubredditsList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Constants.refreshAccessToken(MineSubredditsActivity.this, MineSubredditsActivity.this, "subreddits_list");
            return null;
        }
    }

    private void connectApiClient(String url) {
        Map<String, Object> map = new HashMap<>();
        map.put("after", after);
        map.put("limit", "10");
        map.put("count", "0");
        map.put("before", before);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiInterface.doGetMineSubreddits("bearer " + token, url, map);
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
        if(cursor.getCount()<=0){
                no_subscribe_layout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }
        else if (cursor != null && cursor.moveToFirst())
            do {
                SubscribeRedditsViewModel holder = new SubscribeRedditsViewModel();
                holder.id = cursor.getString(cursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry._ID));
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


            while (cursor.moveToNext());

        recycleViewSubreddit.setAdapter(adapters);
        adapters.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
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
                return true;


        }
        return true;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        if (networkUtils.isOnline(MineSubredditsActivity.this))
            updateList();
        else
            connectApiClient("/subreddits/mine/subscriber.json");


    }

    private void updateList() {
        ReadyitProvider.tableToProcess(DatabaseUtils.TABLE_SUBS_SUBREDDIT);
        cursor = getContentResolver().query(ReadyItContract.ReadyitEntry.CONTENT_URI, null, ReadyItContract.ReadyitEntry.USER_IS_SUBSCRIBER+" ='1'", null, null);
        populateSubscribedRedditList(cursor);
        // stopping swipe refresh
        swipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onClick(SubscribeRedditsViewModel dataHolder) {
        startActivity(new Intent(MineSubredditsActivity.this, HomePageActivity.class).putExtra("data", dataHolder));

    }

    public void showSnackbar(final HashMap<String, SubscribeRedditsViewModel> data) {
        snackbar.setAction("Done", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String key : data.keySet()) {
                    String name = data.get(key).name;
                    updateSubscription("unsub", MineSubredditsActivity.this, name, data.get(key).id);

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
        String token = "bearer " + Constants.getToken(context);
        mApiInterface.doSubUnsubSubreddit(token, subVal, false, subredditFullName)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d(">>", "Success responseCode:" + response.code());
                        if (response.code() == 200) {
                            ReadyitProvider.tableToProcess(TABLE_SUBS_SUBREDDIT);
                            dbHelper.updateSubscribeReddits(id,"0");
                            updateList();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("<<", "fail:" + t.getMessage());
                    }
                });
    }
}
