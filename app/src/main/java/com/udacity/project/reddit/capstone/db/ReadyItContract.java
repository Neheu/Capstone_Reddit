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
    public static final String PATH_READYIT = "reddit";

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
//        public static final String IS_SUBSCRIBED = "is_subscribed";
        public static final String CREATED_UTC = "created_utc";
        public static final String SUBS_COUNT = "subs_count";
        public static final String SUBREDDIT_ID = "subreddit_id";
        public static final String KIND = "kind";
        public static final String DOWN = "down_vote";
        public static final String UP = "up_vote";
        public static final String COMMENTS_COUNT = "comment_count";
        public static final String SUBREDDIT_NAME = "subreddit_name";
        public static final String SUBREDDIT_TITLE = "subreddit_title";


        public static final String THUMB_URL = "thumb_url";


//id

        public static final String DEPTH = "depth";


        //"subreddit_id";

        public static final String LIKES = "likes";


        public static final String AUTHOR = "author";

        public static final String PARENT_ID = "parentId";

        public static final String SCORE = "score";

        public static final String BODY = "body";

        //DOWNS = "downs";

        //SUBREDDIT = "subreddit";

        //NAME = "name";

        //UPS =  "ups";

        public static final String LINK_ID = "link_id";

        public static final String MAIN_TITLE = "selftext";
        public static final String USER_IS_SUBSCRIBER = "user_is_subscriber";
    }
}
