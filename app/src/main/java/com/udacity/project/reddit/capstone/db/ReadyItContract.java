package com.udacity.project.reddit.capstone.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Neha on 22-08-2017.
 */

public class ReadyItContract {
    /* Add content provider constants to the Contract
     Clients need to know how to access the task data*/

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.capstone.readyit.contentprovider";
    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "readyit" directory
    public static final String PATH_READYIT = "movies";

    public static final class ReadyitEntry implements BaseColumns {
        // PopularMoviesEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_READYIT).build();

        //Subs Subreddit table columns --
        public static final String _ID = "id";
        public static final String DISPLAY_NAME = "display_name";
        public static final String TITLE = "title";
        public static final String NAME = "name";
        public static final String URL = "url";
        public static final String IS_SUBSCRIBED = "is_subscribed";
        public static final String CREATED_UTC = "created_utc";
    }
}
