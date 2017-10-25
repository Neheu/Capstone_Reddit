package com.udacity.project.reddit.capstone.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.adapters.SubredditDetailListAdapter;
import com.udacity.project.reddit.capstone.model.SubredditListViewModel;
import com.udacity.project.reddit.capstone.model.SubscribeRedditsViewModel;
import com.udacity.project.reddit.capstone.utils.Constants;

import butterknife.BindView;

public class SubredditDetailActivity extends AppCompatActivity {

    @BindView(R.id.detail_toolbar)
    @Nullable
    Toolbar mToolbar;
    private SubredditDetailFragment fragment;
    private static final String FRAGMENT_DETAILS_TAG = "fragment_detail";
    @BindView(R.id.reply_fab)
    FloatingActionButton fabReply;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subreddit_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putParcelable(Constants.INTENT_SUBREDDIT_DETAIL_DATA,
                    getIntent().getParcelableExtra(Constants.INTENT_SUBREDDIT_DETAIL_DATA));
            fragment = new SubredditDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, fragment, FRAGMENT_DETAILS_TAG).commit();
        } else {
            //  isMarkedFav = savedInstanceState.getBoolean(Constants.INTENT_SUBREDDIT_DETAIL_DATA);
        }

    }

}
