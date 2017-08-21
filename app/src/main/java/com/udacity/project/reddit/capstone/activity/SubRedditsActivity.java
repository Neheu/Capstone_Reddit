package com.udacity.project.reddit.capstone.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.adapters.SubredditsAdapters;
import com.udacity.project.reddit.capstone.model.GetSubredditsModel;
import com.udacity.project.reddit.capstone.server.ApiClient;
import com.udacity.project.reddit.capstone.server.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.internal.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;

public class SubRedditsActivity extends AppCompatActivity implements SubredditsAdapters.checkChangeListener{
    private ApiInterface apiInterface;
    private RecyclerView recycleViewSubreddit;
    private SubredditsAdapters adapter;
    @BindView(R.id.btn_done)
    FloatingActionButton btnDone;

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_reddits);
//         getSupportActionBar().setTitle(R.string.subreddits);
        ButterKnife.bind(this);
        recycleViewSubreddit = (RecyclerView) findViewById(R.id.rv_subreddit);
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        recycleViewSubreddit.setLayoutManager(gridLayoutManager);
        //Connect to ReteoFit and get subreddits
        connectApiClient();

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(SubRedditsActivity.this, HomePageActivity.class));
            }
        });
    }

    private void connectApiClient() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiInterface.doGetSubredditsListResources("/subreddits/popular.json");
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
                for (GetSubredditsModel.Child data : list) {
                    GetSubredditsModel.subredditDataList.add(data.data);

                }
                recycleViewSubreddit.setAdapter(new SubredditsAdapters(SubRedditsActivity.this, GetSubredditsModel.subredditDataList,SubRedditsActivity.this));
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        onCreateOptionsMenu(menu);

        return super.onCreateOptionsMenu(menu);

    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void checkCount(int count) {
        if(count>0)
            btnDone.setVisibility(View.VISIBLE);
        else
            btnDone.setVisibility(View.GONE);
    }
}
