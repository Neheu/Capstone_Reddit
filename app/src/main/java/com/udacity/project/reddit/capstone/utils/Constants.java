package com.udacity.project.reddit.capstone.utils;

/**
 * Created by Neha on 10-08-2017.
 */

public class Constants {
    public static final String API_ROOT_URL = "https://www.reddit.com";
    public static final String API_SUBREDDIT = "/subreddits";
    public static final String API_SUBREDDIT_ENDPOINT = "/popular.json";
    public static final String CLIENT_ID="HjYqDAM9p_qF6A";
    public static final String AUTH_URL ="https://www.reddit.com/api/v1/authorize.compact?client_id=%s" +
                    "&response_type=code&state=%s&redirect_uri=%s&" +
                    "duration=permanent&scope=identity";
    public static final String REDIRECT_URI =
            "http://www.example.com/redirect";

    public static final String STATE = "RANDON_STATE_STRING";

    public static final String ACCESS_TOKEN_URL =
            "https://www.reddit.com/api/v1/access_token";

    // Prefrences.........
    public static String PREFRENCE_NAME="reddit_pref";
    public static String PREFRENCE_MODHASH="Modhash";
}
