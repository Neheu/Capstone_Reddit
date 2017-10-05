package com.udacity.project.reddit.capstone.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.adapters.ReplyExpandableAdapter;
import com.udacity.project.reddit.capstone.adapters.SubredditDetailListAdapter;
import com.udacity.project.reddit.capstone.db.ReadyItContract;
import com.udacity.project.reddit.capstone.db.ReadyitProvider;
import com.udacity.project.reddit.capstone.db.RedyItSQLiteOpenHelper;
import com.udacity.project.reddit.capstone.model.GetCommentsModel;
import com.udacity.project.reddit.capstone.model.ReplyViewModel;
import com.udacity.project.reddit.capstone.model.SubscribeRedditsViewModel;
import com.udacity.project.reddit.capstone.server.ApiInterface;
import com.udacity.project.reddit.capstone.server.GetRefreshedToken;
import com.udacity.project.reddit.capstone.utils.Constants;
import com.udacity.project.reddit.capstone.utils.DatabaseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReplyActivity extends AppCompatActivity implements GetRefreshedToken {
    private ApiInterface apiInterface;
    private Intent intent;
    private String subreddit_name, subreddit_id;
    private RedyItSQLiteOpenHelper dbHelper;
    private ArrayList<ReplyViewModel> repTitle = new ArrayList<>();
    private ArrayList<ReplyViewModel> repDataParent = new ArrayList<>();
    private ArrayList<ReplyViewModel> repDataChild = new ArrayList<>();
    HashMap<String, List<ReplyViewModel>> listDataChild = new HashMap<>();
    List<String> listParentHeader = new ArrayList<>();
    @BindView(R.id.lvExp)
    ExpandableListView expListView;
    @BindView(R.id.fab)
    FloatingActionButton postBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHelper = new RedyItSQLiteOpenHelper(this);
        intent = getIntent();
        subreddit_id = intent.getStringExtra("id");
        subreddit_name = intent.getStringExtra("subreddit_name");
        ButterKnife.bind(this);
        new RefreshToken().execute();
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReplyActivity.this, PostReplyActivity.class).putExtra("name", fullParentName));
            }
        });

    }

    @Override
    public void onTokenRefreshed(String token, String tag) {
        getComments(token);
    }

    private class RefreshToken extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

        @Override
        protected Void doInBackground(Void... params) {
            Constants.refreshAccessToken(ReplyActivity.this, ReplyActivity.this, "reply_api");
            return null;
        }

    }

    static Retrofit retrofit = null;

    public Retrofit getClient() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GetCommentsModel.Data_.class, new GetCommentsModel.ReplyDeserializer())
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

    private HashMap<String, List<GetCommentsModel.Child>> getComments(String token) {
        final HashMap<String, List<GetCommentsModel.Child>> commentsList = new HashMap<>();
        apiInterface = getClient().create(ApiInterface.class);
        Call call = apiInterface.doGetComments("bearer " + token, subreddit_name, subreddit_id);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    return;
                }

                if (response.code() == 200) {
                    List<GetCommentsModel> allDataList = (List<GetCommentsModel>) response.body();
                    List<GetCommentsModel.Child> commentsTitle = allDataList.get(0).data.children;
                    for (GetCommentsModel.Child child : commentsTitle) {
                        dbHelper.insertCommentsTitle(child);
                    }
                    List<GetCommentsModel.Child> commentsOnly = allDataList.get(1).data.children;
                    ReplyReccur(commentsOnly);
                    updateList();


                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
            }
        });

        return commentsList;
    }
    List<GetCommentsModel.Child> child = new ArrayList<>();

    private void ReplyReccur(List<GetCommentsModel.Child> commentsOnly) {
        for (GetCommentsModel.Child comm : commentsOnly) {
            // GetCommentsModel.Data_ data = comm.data;
            if (comm != null) {
                GetCommentsModel.Reply reply = comm.data.mReplyData;
                if (reply != null) {
                    dbHelper.insertComments(comm);
                    child = reply.data.children;
                    ReplyReccur(child);
                }
            }
        }
    }


    private void updateList() {
        ReadyitProvider.tableToProcess(DatabaseUtils.TABLE_COMMENTS_TITLE);
        Cursor cursor = getContentResolver().query(ReadyItContract.ReadyitEntry.CONTENT_URI, null, ReadyItContract.ReadyitEntry._ID + " ='" + subreddit_id + "'", null, null);
        populateReplyTitle(cursor);

        // stopping swipe refresh
//        swipeRefreshLayout.setRefreshing(false);
    }

    private void populateReplyTitle(Cursor subredditCursor) {
        if (subredditCursor != null && subredditCursor.moveToFirst())
            do {
                ReplyViewModel holder = new ReplyViewModel();
                holder.id = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry._ID));
                holder.subreddi_id = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.SUBREDDIT_ID));
                holder.subreddit_name = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.TITLE));
                holder.body = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.MAIN_TITLE));
                holder.up = subredditCursor.getInt(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.UP));
                holder.name = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.NAME));
                holder.kind = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.KIND));

                repTitle.add(holder);
            }


            while (subredditCursor.moveToNext());
        populateReplyListParent();

//        recycleViewSubreddit.setAdapter(adapters);
//        adapters.notifyDataSetChanged();

    }

    private String fullParentName;

    private void populateReplyListParent() {
        listDataChild.clear();
        listParentHeader.clear();
        Cursor subredditCursor = null;
        for (ReplyViewModel rep : repTitle) {
            ReadyitProvider.tableToProcess(DatabaseUtils.TABLE_COMMENTS);
            fullParentName = rep.kind + "_" + rep.id;
            subredditCursor = getContentResolver().query(ReadyItContract.ReadyitEntry.CONTENT_URI, null, ReadyItContract.ReadyitEntry.PARENT_ID + " ='" + fullParentName + "'", null, null);

            if (subredditCursor != null && subredditCursor.moveToFirst())
                do {
                    ReplyViewModel holder = new ReplyViewModel();
                    holder.id = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry._ID));
                    holder.depth = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.DEPTH));
                    holder.subreddi_id = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.SUBREDDIT_ID));
                    holder.author = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.AUTHOR));
                    holder.name = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.NAME));
                    holder.parent_id = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.PARENT_ID));
                    holder.score = subredditCursor.getInt(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.SCORE));
                    holder.body = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.BODY));
                    holder.down = subredditCursor.getInt(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.DOWN));
                    holder.up = subredditCursor.getInt(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.UP));
                    holder.subreddit_name = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.SUBREDDIT_NAME));
                    holder.link_id = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.LINK_ID));
                    holder.kind = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.KIND));
                    repDataParent.add(holder);
                   // listDataChild.put(holder.body, repDataParent);
                    listParentHeader.add(holder.body);


                }
                while (subredditCursor.moveToNext());
            populateReplyListChild();
        }

//        recycleViewSubreddit.setAdapter(adapters);
//        adapters.notifyDataSetChanged();

    }

    private void populateReplyListChild() {
        Cursor subredditCursor = null;
        for (ReplyViewModel rep : repDataParent) {
            ReadyitProvider.tableToProcess(DatabaseUtils.TABLE_COMMENTS);
            String parent_id = rep.kind + "_" + rep.id;
            subredditCursor = getContentResolver().query(ReadyItContract.ReadyitEntry.CONTENT_URI, null, ReadyItContract.ReadyitEntry.PARENT_ID + " ='" + parent_id + "'", null, null);

            if (subredditCursor != null && subredditCursor.moveToFirst())
                do {
                    ReplyViewModel holder = new ReplyViewModel();
                    holder.id = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry._ID));
                    holder.subreddi_id = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.SUBREDDIT_ID));
                    holder.author = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.AUTHOR));
                    holder.name = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.NAME));
                    holder.body = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.BODY));
                    holder.down = subredditCursor.getInt(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.DOWN));
                    holder.up = subredditCursor.getInt(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.UP));
                    holder.subreddit_name = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.SUBREDDIT_NAME));

                    repDataChild.add(holder);
                    listDataChild.put(holder.body, repDataChild);
                  //  listParentHeader.add(holder.body);

                }

                while (subredditCursor.moveToNext());
        }


        expListView.setAdapter(new ReplyExpandableAdapter(this, listParentHeader, listDataChild));


    }

}
