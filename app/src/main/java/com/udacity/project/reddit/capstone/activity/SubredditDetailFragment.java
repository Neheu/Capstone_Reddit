package com.udacity.project.reddit.capstone.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.db.RedyItSQLiteOpenHelper;
import com.udacity.project.reddit.capstone.model.GetCommentsModel;
import com.udacity.project.reddit.capstone.model.SubredditListViewModel;
import com.udacity.project.reddit.capstone.server.ApiClient;
import com.udacity.project.reddit.capstone.server.ApiInterface;
import com.udacity.project.reddit.capstone.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Neha on 04-09-2017.
 */

public class SubredditDetailFragment extends Fragment {
    @SuppressWarnings("unused")
    public static final String TAG = SubredditDetailFragment.class.getSimpleName();
    // private PopularMovies mMovie;
    @BindView(R.id.rv_replylist)
    RecyclerView rvList;
    private SubredditListViewModel data;
    private RedyItSQLiteOpenHelper dbHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Get data from intents set by parent activity*/
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(Constants.INTENT_SUBREDDIT_DETAIL_DATA)) {
            data = arguments.getParcelable(Constants.INTENT_SUBREDDIT_DETAIL_DATA);
        }
        connectApiClient();
        // new GetCommentsFromServer().execute();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout)
                activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null && activity instanceof SubredditDetailActivity) {
            appBarLayout.setTitle(data.subreddit_name);
        }

        ImageView movieBackdrop = ((ImageView) activity.findViewById(R.id.backdrop));
        if (movieBackdrop != null) {
            Picasso.with(activity)
                    .load(data.thumb_url)
                    .config(Bitmap.Config.RGB_565)
                    .into(movieBackdrop);
        }
        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putString("id",
                    data.id);
            Fragment fragment = new Fragment();
            fragment.setArguments(arguments);
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.rv_replylist, fragment)
                    .commit();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.subreddit_detail_fragment, container, false);
        ButterKnife.bind(this, rootView);
        dbHelper = new RedyItSQLiteOpenHelper(getActivity());


        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    private void connectApiClient() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<GetCommentsModel.RootData>> call = apiInterface.doGetComments("/r/" + data.subreddit_name + "/comments/"
                + data.id + "/.json");

        call.enqueue(new Callback<List<GetCommentsModel.RootData>>() {
            @Override
            public void onResponse(Call<List<GetCommentsModel.RootData>> call, Response<List<GetCommentsModel.RootData>> response) {
                response.isSuccessful();
                List<GetCommentsModel.RootData> commentsModel = response.body();
            }

            @Override
            public void onFailure(Call<List<GetCommentsModel.RootData>> call, Throwable t) {

            }
        });
    }
   }