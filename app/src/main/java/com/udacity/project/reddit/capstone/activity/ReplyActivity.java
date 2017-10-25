package com.udacity.project.reddit.capstone.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.adapters.CommentsAdapter;
import com.udacity.project.reddit.capstone.db.ReadyItContract;
import com.udacity.project.reddit.capstone.db.ReadyitProvider;
import com.udacity.project.reddit.capstone.db.RedyItSQLiteOpenHelper;
import com.udacity.project.reddit.capstone.model.CommentChildModel;
import com.udacity.project.reddit.capstone.model.CommentsParentModel;
import com.udacity.project.reddit.capstone.model.GetCommentsModel;
import com.udacity.project.reddit.capstone.model.ReplyViewModel;
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

public class ReplyActivity extends AppCompatActivity implements GetRefreshedToken, LoaderManager.LoaderCallbacks<Cursor> {
    private ApiInterface apiInterface;
    private Intent intent;
    private String subredditName, subredditId;
    private RedyItSQLiteOpenHelper dbHelper;
    private ArrayList<ReplyViewModel> repTitle = new ArrayList<>();
    private ArrayList<ReplyViewModel> repDataParent = new ArrayList<>();
    private ArrayList<ReplyViewModel> repDataChild = new ArrayList<>();
    HashMap<String, List<ReplyViewModel>> listDataChild = new HashMap<>();
    HashMap<String, List<ReplyViewModel>> listParentData = new HashMap<>();
    List<String> listParentHeader = new ArrayList<>();
    List<String> listChildHeader = new ArrayList<>();
    //    @BindView(R.id.lvExp)
//    ExpandableListView expListView;
    @BindView(R.id.fab)
    FloatingActionButton postBtn;
    @BindView(R.id.rv_comment)
    RecyclerView rvComments;
    private CommentsAdapter commentsAdapter;
    @BindView(R.id.tv_post)
    TextView txtPost;
    @BindView(R.id.tv_subreddit_url)
    TextView subredditTitle;
    private LinearLayoutManager mLayoutManager;
    private int LOADER_ID = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        rvComments.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(mLayoutManager);
        dbHelper = new RedyItSQLiteOpenHelper(this);
        intent = getIntent();
        subredditId = intent.getStringExtra(Constants.SUB_ID);
        subredditName = intent.getStringExtra(Constants.SUB_NAME);
        subredditTitle.setText(subredditName);
        if (savedInstanceState != null) {
            subredditId = savedInstanceState.getString(Constants.INTENT_REPLY);
            getSupportLoaderManager().initLoader(LOADER_ID, null, ReplyActivity.this);
        } else
            new RefreshToken().execute();
        commentsAdapter = new CommentsAdapter(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = rvComments.getChildPosition(v);
                commentsAdapter.toggleGroup(position);
            }
        });


        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReplyActivity.this, PostReplyActivity.class).putExtra(Constants.SUB_NAME, fullParentName));
            }
        });


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.INTENT_REPLY, subredditId);
    }

    @Override
    public void onTokenRefreshed(String token, String tag) {
        getComments(token);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getSupportLoaderManager().destroyLoader(LOADER_ID);
                finish();

                break;
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = ReadyItContract.ReadyitEntry.CONTENT_URI;
        ReadyitProvider.tableToProcess(DatabaseUtils.TABLE_COMMENTS_TITLE);
        return new CursorLoader(this, uri, null, ReadyItContract.ReadyitEntry._ID + " ='" + subredditId + "'", null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        populateReplyTitle(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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
        Call call = apiInterface.doGetComments("bearer " + token, subredditName, subredditId);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    return;
                }

                if (response.code() == 200) {
                    List<GetCommentsModel> allDataList = (List<GetCommentsModel>) response.body();
                    List<GetCommentsModel.Child> commentsTitle = allDataList.get(0).data.children;
                    txtPost.setText(commentsTitle.get(0).data.selftext);
                    for (GetCommentsModel.Child child : commentsTitle) {
                        dbHelper.insertCommentsTitle(child);
                    }
                    List<GetCommentsModel.Child> commentsOnly = allDataList.get(1).data.children;
                    ReplyReccur(commentsOnly);
                    // updateList();
                    getSupportLoaderManager().initLoader(LOADER_ID, null, ReplyActivity.this);

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
            }
        });

        return commentsList;
    }

    List<GetCommentsModel.Child> child = new ArrayList<>();
    CommentChildModel content;
    List<CommentsParentModel> comments = new ArrayList<>();

    private void ReplyReccur(List<GetCommentsModel.Child> commentsOnly) {
        for (GetCommentsModel.Child comm : commentsOnly) {
            // GetCommentsModel.Data_ data = comm.data;
            if (comm != null) {
                GetCommentsModel.Reply reply = comm.data.mReplyData;
                if (comm.data.subreddit != null) {
                    dbHelper.insertComments(comm);
                    if (reply != null) {
                        child = reply.data.children;
                        ReplyReccur(child);
                    }
                }
            }
        }
    }


//    private void updateList() {
//        ReadyitProvider.tableToProcess(DatabaseUtils.TABLE_COMMENTS_TITLE);
//        Cursor cursor = getContentResolver().query(ReadyItContract.ReadyitEntry.CONTENT_URI, null, ReadyItContract.ReadyitEntry._ID + " ='" + subredditId + "'", null, null);
//        populateReplyTitle(cursor);
//
//        // stopping swipe refresh
////        swipeRefreshLayout.setRefreshing(false);
//    }

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
                //holder.author = subredditCursor.getString(subredditCursor.getColumnIndexOrThrow(ReadyItContract.ReadyitEntry.AUTHOR));
                repTitle.add(holder);
                content = new CommentChildModel(holder.body);
                commentsAdapter.add(content);

            }


            while (subredditCursor.moveToNext());
        populateReplyListParent();

//        recycleViewSubreddit.setAdapter(adapters);
//        adapters.notifyDataSetChanged();

    }

    private String fullParentName;
    private int childCount = 0;

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
                    listParentData.put(holder.body, repDataParent);
                    listParentHeader.add(holder.body);
                    if (comments.size() == 0) {
                        childCount = 0;
                    } else childCount = comments.size() - 1;

                    comments.add(new CommentsParentModel(holder.author, holder.body));
                    populateReplyListChild(holder, childCount);

                    childCount++;
                }
                while (subredditCursor.moveToNext());

        }
        rvComments.setAdapter(commentsAdapter);

        commentsAdapter.addAll(comments);
        commentsAdapter.notifyDataSetChanged();
//        recycleViewSubreddit.setAdapter(adapters);
//        adapters.notifyDataSetChanged();

    }

    private void populateReplyListChild(ReplyViewModel rep, int position) {
        Cursor subredditCursor = null;
//        for (ReplyViewModel rep : repDataParent) {
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
                listChildHeader.add(holder.body);
                comments.get(position).addChild(new CommentsParentModel(holder.author, holder.body));
            }

            while (subredditCursor.moveToNext());

    }


//        expListView.setAdapter(new ReplyExpandableAdapter(this, listParentHeader, listDataChild));


//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getSupportLoaderManager().destroyLoader(LOADER_ID);
        finish();
    }
}