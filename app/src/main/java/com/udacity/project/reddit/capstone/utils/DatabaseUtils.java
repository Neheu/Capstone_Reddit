package com.udacity.project.reddit.capstone.utils;

import com.udacity.project.reddit.capstone.db.ReadyItContract.ReadyitEntry;

/**
 * Created by Neha on 22-08-2017.
 */

public class DatabaseUtils {
    public static final String DATABASE_NAME = "redditDB.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_SUBS_SUBREDDIT = "sub_subreddit";
    public static final String TABLE_DETAIL_SUBREDDIT = "detail_subreddit";
    public static final String TABLE_COMMENTS = "comments";

    //Create table query to insert Subscribe subreddit data
    public static final String CREATE_TABLE_Sub_Subreddit =
            "CREATE TABLE " + TABLE_SUBS_SUBREDDIT
                    + " ("
                    + ReadyitEntry._ID + " VARCHAR  PRIMARY KEY, "
                    + ReadyitEntry.DISPLAY_NAME + " TEXT NOT NULL, "
                    + ReadyitEntry.NAME + " TEXT NOT NULL, "
                    + ReadyitEntry.TITLE + " TEXT NOT NULL, "
                    + ReadyitEntry.URL + " TEXT NOT NULL,"
                    + ReadyitEntry.KIND + " VARCHAR NOT NULL,"
                    + ReadyitEntry.SUBS_COUNT + " INTEGER NOT NULL DEFAULT 0,"
                    + ReadyitEntry.IS_SUBSCRIBED
                    + " BOOLEAN NOT NULL DEFAULT 1,"
                    + ReadyitEntry.CREATED_UTC + " DATETIME NOT NULL,"
                    + "UNIQUE (" + ReadyitEntry._ID + ") ON CONFLICT REPLACE"
                    + " )";

    public static final String CREATE_TABLE_COMMENTS =
            "CREATE TABLE " + TABLE_DETAIL_SUBREDDIT
                    + " ("
                    + ReadyitEntry._ID + " VARCHAR  PRIMARY KEY, "
                    + ReadyitEntry.UP + "  INTEGER NOT NULL DEFAULT 0, "
                    + ReadyitEntry.NAME + " TEXT NOT NULL, "
                    + ReadyitEntry.SUBREDDIT_TITLE + " TEXT NOT NULL, "
                    + ReadyitEntry.THUMB_URL + " TEXT NOT NULL,"
                    + ReadyitEntry.URL + " TEXT NOT NULL,"
                    + ReadyitEntry.SUBREDDIT_ID + " VARCHAR NOT NULL,"
                    + ReadyitEntry.DOWN + " INTEGER NOT NULL DEFAULT 0,"
                    + ReadyitEntry.IS_SUBSCRIBED + " BOOLEAN NOT NULL DEFAULT 1,"
                    + ReadyitEntry.SUBREDDIT_NAME + " TEXT NOT NULL,"
                    + ReadyitEntry.CREATED_UTC + " DATETIME NOT NULL,"
                    + ReadyitEntry.COMMENTS_COUNT + " INTEGER NOT NULL DEFAULT 0,"
                    + "UNIQUE (" + ReadyitEntry._ID + ") ON CONFLICT REPLACE"
                    + " )";
    public static final String CREATE_TABLE_DETAIL_Subreddit =
            "CREATE TABLE " + TABLE_COMMENTS
                    + " ("
                    + ReadyitEntry._ID + " VARCHAR  PRIMARY KEY, "
                    + ReadyitEntry.DEPTH + "  INTEGER NOT NULL DEFAULT 0, "
                    + ReadyitEntry.SUBREDDIT_ID + " VARCHAR NOT NULL,"
                    + ReadyitEntry.LIKES + " INTEGER NOT NULL DEFAULT 0,"

                    + ReadyitEntry.AUTHOR + " TEXT NOT NULL, "
                    + ReadyitEntry.PARENT_ID + " TEXT NOT NULL, "
                    + ReadyitEntry.SCORE + " INTEGER NOT NULL DEFAULT 0,"
                    + ReadyitEntry.BODY + " TEXT NOT NULL,"
                    + ReadyitEntry.DOWN + " INTEGER NOT NULL DEFAULT 0,"
                    + ReadyitEntry.UP + " INTEGER NOT NULL DEFAULT 0,"

                    + ReadyitEntry.SUBREDDIT_NAME + " TEXT NOT NULL,"
                    + ReadyitEntry.NAME + " TEXT NOT NULL,"
                    + ReadyitEntry.LINK_ID + " TEXT NOT NULL,"
                    + "UNIQUE (" + ReadyitEntry._ID + ") ON CONFLICT REPLACE"
                    + " )";

    public static final String DROP_TABLE_SUB_SUBREDDIT = "DROP TABLE IF EXISTS " + TABLE_SUBS_SUBREDDIT;
    public static final String DROP_TABLE_DETAIL_SUBREDDIT = "DROP TABLE IF EXISTS " + TABLE_DETAIL_SUBREDDIT;
    public static final String DROP_TABLE_COMMENTS = "DROP TABLE IF EXISTS " + TABLE_COMMENTS;


}
