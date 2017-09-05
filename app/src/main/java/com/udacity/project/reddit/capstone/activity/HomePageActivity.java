package com.udacity.project.reddit.capstone.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.adapters.SubredditDetailListAdapter;
import com.udacity.project.reddit.capstone.db.ReadyItContract;
import com.udacity.project.reddit.capstone.db.ReadyitProvider;
import com.udacity.project.reddit.capstone.db.RedyItSQLiteOpenHelper;
import com.udacity.project.reddit.capstone.model.GetDetailedSubredditListModel;
import com.udacity.project.reddit.capstone.model.SubredditListViewModel;
import com.udacity.project.reddit.capstone.model.SubscribeRedditsViewModel;
import com.udacity.project.reddit.capstone.server.ApiClient;
import com.udacity.project.reddit.capstone.server.ApiInterface;
import com.udacity.project.reddit.capstone.utils.Constants;
import com.udacity.project.reddit.capstone.utils.DatabaseUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePageActivity extends AppCompatActivity implements SubredditDetailListAdapter.onSubredditSelectListener {
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
        adapter = new SubredditDetailListAdapter(this, this, rvList, subredditList);

        connectApiClient();

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

    private void connectApiClient() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call call = apiInterface.doGetDetailSubredditByType( subreddit_url + ".json");
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
}
