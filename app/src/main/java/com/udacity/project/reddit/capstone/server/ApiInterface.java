package com.udacity.project.reddit.capstone.server;

import com.udacity.project.reddit.capstone.model.GetCommentsModel;
import com.udacity.project.reddit.capstone.model.GetDetailedSubredditListModel;
import com.udacity.project.reddit.capstone.model.GetSubredditByTypeModel;
import com.udacity.project.reddit.capstone.model.GetSubredditsModel;
import com.udacity.project.reddit.capstone.utils.Constants;

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
    @GET("{subreddit_url}")
    @Headers(Constants.USER_AGENT_STRING + ": Sample App")
    Call<GetSubredditsModel> doGetSubredditsListResources(@Header(Constants.AUTHORIZATION) String authorization, @Path(value = "subreddit_url", encoded = true)
            String subreddit_url, @QueryMap Map<String, Object> map);

    @GET
    Call<GetSubredditByTypeModel> doGetSubredditByType(@Url String url);

    @GET("{subreddit_url}")
    @Headers(Constants.USER_AGENT_STRING + ": Sample App")
    Call<GetSubredditsModel> doGetMineSubreddits(@Header(Constants.AUTHORIZATION) String authorization, @Path(value = "subreddit_url", encoded = true)
            String subreddit_url, @QueryMap Map<String, Object> map);

    @GET
    Call<GetDetailedSubredditListModel> doGetDetailSubredditByType(@Url String url);

    @GET("/r/{subbreddit_url}/comments/{postId}.json")
    @Headers(Constants.USER_AGENT_STRING + ": Sample App")
    Call<List<GetCommentsModel>> doGetComments(@Header(Constants.AUTHORIZATION) String authorization,
                                               @Path(value = "subbreddit_url", encoded = true) String subbreddit_url,
                                               @Path(value = "postId", encoded = true) String postId);


    @GET("{subbreddit_url}")
    @Headers(Constants.USER_AGENT_STRING + ": Sample App")
    Call<GetDetailedSubredditListModel> doGetSubredditList(@Header(Constants.AUTHORIZATION) String authorization,
                                                           @Path(value = "subbreddit_url", encoded = true) String subbreddit_url);
    @FormUrlEncoded
    @POST("/api/subscribe")
    @Headers("User-Agent: Sample App")
    Call<ResponseBody> doSubUnsubSubreddit(@Header(Constants.AUTHORIZATION) String authorization,
                                              @Field("action") String action,
                                              @Field("skip_initial_defaults") boolean skip_initial_defaults,
                                              @Field("sr") String fullName
    );

}
