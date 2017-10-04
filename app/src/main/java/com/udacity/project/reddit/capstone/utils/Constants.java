package com.udacity.project.reddit.capstone.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.udacity.project.reddit.capstone.activity.LoginActivity;
import com.udacity.project.reddit.capstone.activity.SubRedditsActivity;
import com.udacity.project.reddit.capstone.server.GetRefreshedToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created by Neha on 10-08-2017.
 */

public class Constants {
    public static final String API_BASE_URL = "https://www.reddit.com";
    public static final String API_OAUTH_BASE_URL = "https://oauth.reddit.com";
    public static final String API_SUBREDDIT = "/subreddits";
    public static final String API_VOTE = "/api/vote";
    public static final String API_SUBREDDIT_ENDPOINT = "/popular.json";
    public static final String CLIENT_ID = "HjYqDAM9p_qF6A";
    public static final String AUTH_URL = "https://www.reddit.com/api/v1/authorize.compact?client_id=%s" +
            "&response_type=code&state=%s&redirect_uri=%s&" +
            "duration=permanent&scope=edit, flair, history, modconfig, modflair, modlog, modposts, modwiki, mysubreddits, privatemessages, read, report, save, submit, subscribe, vote, wikiedit, wikiread";
    public static final String REDIRECT_URI =  "http://www.example.com/redirect";
    public static final String STATE = "RANDON_STATE_STRING";
    public static String SCOPE = "identity edit flair";

    public static final String ACCESS_TOKEN_URL = "https://www.reddit.com/api/v1/access_token";
    public static final String PREFRENCE_AFTER = "after";
    public static final String PREFRENCE_BEFORE = "before";
    public static final String INTENT_SUBREDDIT_DETAIL_DATA = "sub_detail_data";
    public static final String PRERENCES_TOKEN_REFRESH_TIME = "refresh_token_time";
    public static final String AUTHORIZATION = "Authorization";
    public static final String USER_AGENT_STRING ="User-Agent";
    // Prefrences.........
    public static String PREFRENCE_NAME = "reddit_pref";
    public static String PREFRENCE_MODHASH = "Modhash";

    public static final String PREFRENCE_TOKEN = "api_access_token";
    public static final String PREFRENCE_REFRESH_TOKEN = "api_refresh_token";

    public static String getToken(Context context) {
        SharedPreferences pref = context.getSharedPreferences(Constants.PREFRENCE_NAME, Context.MODE_PRIVATE);
        return pref.getString(Constants.PREFRENCE_TOKEN, "");
    }


    public static String refreshAccessToken(final Context con, final GetRefreshedToken tokenInf, final String apiTag) {
        OkHttpClient client = new OkHttpClient();
        String authString = Constants.CLIENT_ID + ":";
        String encodedAuthString = Base64.encodeToString(authString.getBytes(),
                Base64.NO_WRAP);
        final SharedPreferences pref = con.getSharedPreferences(Constants.PREFRENCE_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor edit = pref.edit();
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                // .addHeader("Authorization", "bearer " + pref.getString(Constants.PREFRENCE_TOKEN, ""))
                .url(ACCESS_TOKEN_URL)
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                        "grant_type=refresh_token" + "&refresh_token=" + pref.getString(PREFRENCE_REFRESH_TOKEN, "")))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(">> ", "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                try {
                    JSONObject object = new JSONObject(res);
                    edit.putString(PREFRENCE_TOKEN, object.getString("access_token"));
                    Date date = new Date(System.currentTimeMillis());

                    long millis = date.getTime();
                    edit.putLong(PRERENCES_TOKEN_REFRESH_TIME, millis);
                    edit.apply();
                    tokenInf.onTokenRefreshed(getToken(con),apiTag);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });
        return getToken(con);
    }

    public static boolean isTokenExpried(Context context) {
        Date date = new Date(System.currentTimeMillis());
        long currentTime = date.getTime();
        long MAX_DURATION = MILLISECONDS.convert(01, HOURS);
        final SharedPreferences pref = context.getSharedPreferences(Constants.PREFRENCE_NAME, Context.MODE_PRIVATE);
        long savedTime = pref.getLong(PRERENCES_TOKEN_REFRESH_TIME, 0);
        if (savedTime == 0)
            return false;
        else if (currentTime - savedTime > MAX_DURATION)
            return true;
        else
            return false;
    }
}
