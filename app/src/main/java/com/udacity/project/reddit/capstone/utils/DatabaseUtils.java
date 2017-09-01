package com.udacity.project.reddit.capstone.utils;

import com.udacity.project.reddit.capstone.db.ReadyItContract.ReadyitEntry;

/**
 * Created by Neha on 22-08-2017.
 */

public class DatabaseUtils {
    public static final String DATABASE_NAME = "redditDB.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_SUBS_SUBREDDIT = "sub_subreddit";
    //Create table query to insert Subscribe subreddit data
    public static final String CREATE_TABLE_Sub_Subreddit =
            "CREATE TABLE " + TABLE_SUBS_SUBREDDIT
                    + " ("
                    + ReadyitEntry._ID + " VARCHAR  PRIMARY KEY, "
                    + ReadyitEntry.DISPLAY_NAME + " TEXT NOT NULL, "
                    + ReadyitEntry.NAME + " TEXT NOT NULL, "
                    + ReadyitEntry.TITLE + " TEXT NOT NULL, "
                    + ReadyitEntry.URL + " TEXT NOT NULL,"
                    + ReadyitEntry.IS_SUBSCRIBED
                    + " BOOLEAN NOT NULL DEFAULT 1,"
                    +ReadyitEntry.CREATED_UTC+" DATETIME NOT NULL,"
                    + "UNIQUE (" + ReadyitEntry._ID + ") ON CONFLICT REPLACE"
                    + " )";
    public static final String DROP_TABLE_SUB_SUBREDDIT = "DROP TABLE IF EXISTS " + TABLE_SUBS_SUBREDDIT;
}
