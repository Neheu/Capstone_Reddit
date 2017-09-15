package com.udacity.project.reddit.capstone.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.udacity.project.reddit.capstone.model.GetCommentsModel;
import com.udacity.project.reddit.capstone.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Neha on 10-08-2017.
 */

public class ApiClient {
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GetCommentsModel.Data_.class, new GetCommentsModel.ReplyDeserializer())
                .setLenient()
                .create();
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
