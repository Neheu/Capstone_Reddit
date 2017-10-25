package com.udacity.project.reddit.capstone.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Get data from intents set by parent activity*/
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(Constants.INTENT_SUBREDDIT_DETAIL_DATA)) {
            data = arguments.getParcelable(Constants.INTENT_SUBREDDIT_DETAIL_DATA);
        }
       // connectApiClient();
        // new GetCommentsFromServer().execute();
        getReplies();
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
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

//    private void connectApiClient() {
//        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//        Call<List<GetCommentsModel>> call = apiInterface.doGetComments("/r/" + data.subreddit_name + "/comments/"
//                + data.id + "/.json");
//
//        call.enqueue(new Callback<List<GetCommentsModel>>() {
//            @Override
//            public void onResponse(Call<List<GetCommentsModel>> call, Response<List<GetCommentsModel>> response) {
//                response.isSuccessful();
//                List<GetCommentsModel> commentsModel = response.body();
//
//            }
//
//            @Override
//            public void onFailure(Call<List<GetCommentsModel>> call, Throwable t) {
//
//            }
//        });
//    }

    private void getReplies() {
        OkHttpClient client = new OkHttpClient();
        String token = Constants.getToken(getContext());

        Request request = new Request.Builder()
                .addHeader("Authorization", "bearer " + token)
                .url(Constants.API_OAUTH_BASE_URL + Constants.API_SUBREDDIT +"/r/" + data.subreddit_name + "/comments/"
                        + data.id + "/.json")
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

}