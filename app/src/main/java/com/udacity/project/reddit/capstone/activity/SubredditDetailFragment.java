package com.udacity.project.reddit.capstone.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.udacity.project.reddit.capstone.R;
import com.udacity.project.reddit.capstone.db.RedyItSQLiteOpenHelper;
import com.udacity.project.reddit.capstone.model.GetCommentsModel;
import com.udacity.project.reddit.capstone.model.GetDetailedSubredditListModel;
import com.udacity.project.reddit.capstone.model.SubredditListViewModel;
import com.udacity.project.reddit.capstone.server.ApiClient;
import com.udacity.project.reddit.capstone.server.ApiInterface;
import com.udacity.project.reddit.capstone.utils.Constants;
import com.udacity.project.reddit.capstone.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
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
        Call<List<NetworkUtils.CommentsEndpointResponse>> call = apiInterface.doGetComments("/r/" + data.subreddit_name + "/comments/"
                + data.id + "/.json");

        call.enqueue(new Callback<List<NetworkUtils.CommentsEndpointResponse>>() {
            @Override
            public void onResponse(Call<List<NetworkUtils.CommentsEndpointResponse>> call, Response<List<NetworkUtils.CommentsEndpointResponse>> response) {
                if (response.isSuccessful()) {

                }
            }

            @Override
            public void onFailure(Call<List<NetworkUtils.CommentsEndpointResponse>> call, Throwable t) {

            }
        });


    }

    private class GetCommentsFromServer extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Void doInBackground(Void... arg0) {

            // Making a request to url and getting response
            String jsonStr = makeServiceCall("/r/" + data.subreddit_name + "/comments/"
                    + data.id + "/.json");


            Gson gson = new Gson();

            Type collectionType = new TypeToken<Collection<GetCommentsModel>>() {
            }.getType();
            Collection<GetCommentsModel> enums = gson.fromJson(jsonStr, collectionType);
            if (jsonStr != null) {
                try {
                    JSONArray jsonArray = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        JSONArray jsonArray1 = jsonObject1.getJSONArray("children");

                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject obj = jsonArray1.getJSONObject(j);
                            GetCommentsModel model = new GetCommentsModel();
                            JSONObject object = obj.getJSONObject("data");
                            JSONObject repObj = object.getJSONObject("replies");

                        }
                    }

                    // Getting JSON Array node


                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());


                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");


            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


        }

    }

    public String makeServiceCall(String reqUrl) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


}