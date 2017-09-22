package com.udacity.project.reddit.capstone.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.udacity.project.reddit.capstone.server.ApiClient;
import com.udacity.project.reddit.capstone.server.ApiInterface;
import com.udacity.project.reddit.capstone.server.GetRefreshedToken;
import com.udacity.project.reddit.capstone.utils.Constants;
import com.udacity.project.reddit.capstone.utils.DatabaseUtils;

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

public class HomePageActivity extends AppCompatActivity implements SubredditDetailListAdapter.onSubredditSelectListener, GetRefreshedToken {
    private ApiInterface apiInterface;
    private Intent intent;
    private String subreddit_url, detail_name, subreddit_id;
    private int subscriber_count;
    @BindView(R.id.tv_subreddit_url)
    TextView tv_subreddit_url;
    @BindView(R.id.tv_subs_count)
    TextView tv_subs_count;
    @BindView(R.id.tv_detail_name)
    TextView tv_detail_name;
    @BindView(R.id.btn_sub)
    Button btn_sub;
    private RedyItSQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;
    @BindView(R.id.list_view)
    RecyclerView rvList;
    SubredditDetailListAdapter adapter;
    private ArrayList<SubredditListViewModel> subredditList = new ArrayList<>();
    private SubscribeRedditsViewModel intentData;

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

        intentData = intent.getExtras().getParcelable("data");
        subreddit_url = intentData.url;
        subscriber_count = intentData.subCount;
        detail_name = intentData.display_name;
        tv_subreddit_url.setText(subreddit_url);
        tv_detail_name.setText(detail_name);
        tv_subs_count.setText(String.valueOf(subscriber_count));
        subreddit_id = intentData.subreddit_id;

        db = dbHelper.getWritableDatabase();
        ReadyitProvider.tableToProcess(DatabaseUtils.TABLE_DETAIL_SUBREDDIT);
        adapter = new SubredditDetailListAdapter(this,this, this, rvList, subredditList);

//        connectApiClient();
        new GetSubreddits().execute();
    }

    class GetSubreddits extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Constants.refreshAccessToken(HomePageActivity.this, HomePageActivity.this);
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getSubredditByname(String token) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .addHeader("Authorization", "bearer " + token)
                .url(Constants.API_OAUTH_BASE_URL + subreddit_url + "/.json")
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
                    List<GetCommentsModel> commentsModel = (List<GetCommentsModel>) response.body();
                }

            }
        });
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
        Call call = apiInterface.getSubredditList("bearer " + token, subreddit_url+".json");

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
                        data.data.subs_count = subscriber_count;
//                        if(data.data.likes==null)
//                        {
//                            data.data.likes = -1;
//                        }
                        dbHelper.insertSubSubredditsDetail(data.data);
                    }
                }
                updateList();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
            }
        });


    }


    private void updateList() {
        ReadyitProvider.tableToProcess(DatabaseUtils.TABLE_DETAIL_SUBREDDIT);
        Cursor cursor = getContentResolver().query(ReadyItContract.ReadyitEntry.CONTENT_URI, null, ReadyItContract.ReadyitEntry.SUBREDDIT_ID + " ='" + subreddit_id + "'", null, null);
        populateSubscribedRedditList(cursor);
    }

    private void populateSubscribedRedditList(Cursor subredditCursor) {


        if (subredditCursor != null && subredditCursor.moveToFirst())
            do {
                SubredditListViewModel holder = new SubredditListViewModel();
                holder.id = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry._ID));
                holder.up = subredditCursor.getInt(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.UP));
                holder.down = subredditCursor.getInt(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.DOWN));
                holder.is_subscribed = Boolean.valueOf(subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.IS_SUBSCRIBED)));
                holder.subreddit_name = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.SUBREDDIT_NAME));
                holder.thumb_url = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.URL));
                holder.name = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.NAME));
                holder.subreddit_title = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.SUBREDDIT_TITLE));
                holder.comment_count = subredditCursor.getInt(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.COMMENTS_COUNT));
                holder.subreddit_id = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.SUBREDDIT_ID));
                holder.share_url = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.URL));
                holder.likes= subredditCursor.getInt(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.LIKES));
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
        finish();
    }

    @Override
    public void onTokenRefreshed(String token) {
        connectApiClient(token);
    }
}
