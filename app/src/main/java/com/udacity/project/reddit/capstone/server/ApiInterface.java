package com.udacity.project.reddit.capstone.server;

import com.udacity.project.reddit.capstone.model.GetCommentsModel;
import com.udacity.project.reddit.capstone.model.GetDetailedSubredditListModel;
import com.udacity.project.reddit.capstone.model.GetSubredditByTypeModel;
import com.udacity.project.reddit.capstone.model.GetSubredditsModel;
import com.udacity.project.reddit.capstone.utils.NetworkUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
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
    Call<List<NetworkUtils.CommentsEndpointResponse>> doGetComments(@Url String url);

}
