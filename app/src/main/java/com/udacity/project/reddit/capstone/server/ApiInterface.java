package com.udacity.project.reddit.capstone.server;

import com.udacity.project.reddit.capstone.model.GetCommentsModel;
import com.udacity.project.reddit.capstone.model.GetDetailedSubredditListModel;
import com.udacity.project.reddit.capstone.model.GetSubredditByTypeModel;
import com.udacity.project.reddit.capstone.model.GetSubredditsModel;
import com.udacity.project.reddit.capstone.model.PostVoteModel;
import com.udacity.project.reddit.capstone.utils.Constants;
import com.udacity.project.reddit.capstone.utils.NetworkUtils;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by Neha on 10-08-2017.
 */

public interface ApiInterface {
    @GET
    Call<GetSubredditsModel> doGetSubredditsListResources(@Url String url);

    @GET
    Call<GetSubredditByTypeModel> doGetSubredditByType(@Url String url);

    @GET
    Call<GetDetailedSubredditListModel> doGetDetailSubredditByType(@Url String url);

    @GET
    Call<List<GetCommentsModel>> doGetComments(@Url String url);

    @Headers(Constants.USER_AGENT_STRING+": Sample App")
    @GET("{subbreddit_name}")
    Call<GetDetailedSubredditListModel> getSubredditList(@Header(Constants.AUTHORIZATION) String authorization,
                                                         @Path(value = "subbreddit_name", encoded = true) String subbreddit_name);
}
