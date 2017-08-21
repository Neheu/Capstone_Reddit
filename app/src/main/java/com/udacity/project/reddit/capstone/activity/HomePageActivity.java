package com.udacity.project.reddit.capstone.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.adapters.SubredditsAdapters;
import com.udacity.project.reddit.capstone.model.GetSubredditByType;
import com.udacity.project.reddit.capstone.model.GetSubredditsModel;
import com.udacity.project.reddit.capstone.server.ApiClient;
import com.udacity.project.reddit.capstone.server.ApiInterface;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePageActivity extends AppCompatActivity {
    private ApiInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        for (int i=0;i<SubredditsAdapters.checkedList.size();i++ ) {
            connectApiClient(SubredditsAdapters.checkedList.get(i)+".json");
        }

    }
    private void connectApiClient(String url) {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

            Call call = apiInterface.doGetSubredditByType(url);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (!response.isSuccessful()) {
                        return;
                    }
                    //Get the response from api
                    GetSubredditByType model = (GetSubredditByType) response.body();

                }

                @Override
                public void onFailure(Call call, Throwable t) {

                }
            });


    }

}
