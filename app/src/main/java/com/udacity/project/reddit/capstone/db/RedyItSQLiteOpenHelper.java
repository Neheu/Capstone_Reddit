package com.udacity.project.reddit.capstone.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.udacity.project.reddit.capstone.model.GetCommentsModel;
import com.udacity.project.reddit.capstone.model.GetDetailedSubredditListModel;
import com.udacity.project.reddit.capstone.model.GetSubredditsModel;
import com.udacity.project.reddit.capstone.model.SubscribeRedditsViewModel;
import com.udacity.project.reddit.capstone.utils.DatabaseUtils;

import static com.udacity.project.reddit.capstone.utils.DatabaseUtils.TABLE_COMMENTS;
import static com.udacity.project.reddit.capstone.utils.DatabaseUtils.TABLE_SUBS_SUBREDDIT;
import static com.udacity.project.reddit.capstone.utils.DatabaseUtils.TABLE_DETAIL_SUBREDDIT;

/**
 * Created by Neha on 22-08-2017.
 */

public class RedyItSQLiteOpenHelper extends SQLiteOpenHelper {
    private Context context;

    public RedyItSQLiteOpenHelper(Context context) {
        super(context, DatabaseUtils.DATABASE_NAME, null, DatabaseUtils.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create table to store movies data
        db.execSQL(DatabaseUtils.CREATE_TABLE_Sub_Subreddit);
        db.execSQL(DatabaseUtils.CREATE_TABLE_DETAIL_Subreddit);
        db.execSQL(DatabaseUtils.CREATE_TABLE_COMMENTS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseUtils.DROP_TABLE_SUB_SUBREDDIT);
        db.execSQL(DatabaseUtils.DROP_TABLE_DETAIL_SUBREDDIT);
        db.execSQL(DatabaseUtils.DROP_TABLE_COMMENTS);
    }

    public void insertSubSubreddits(GetSubredditsModel.Child dataHolder) {

        // Create new empty ContentValues object
        ContentValues contentValues = new ContentValues();
        // Put the movie data into the ContentValues
        contentValues.put(ReadyItContract.ReadyitEntry._ID, dataHolder.data.id);
        contentValues.put(ReadyItContract.ReadyitEntry.DISPLAY_NAME, dataHolder.data.displayName);
        contentValues.put(ReadyItContract.ReadyitEntry.NAME, dataHolder.data.name);
        contentValues.put(ReadyItContract.ReadyitEntry.TITLE, dataHolder.data.title);
        contentValues.put(ReadyItContract.ReadyitEntry.URL, dataHolder.data.url);
        contentValues.put(ReadyItContract.ReadyitEntry.IS_SUBSCRIBED, dataHolder.data.isSubscribed);
        contentValues.put(ReadyItContract.ReadyitEntry.CREATED_UTC, dataHolder.data.createdUtc);
        contentValues.put(ReadyItContract.ReadyitEntry.SUBS_COUNT, dataHolder.data.subscribers);
        contentValues.put(ReadyItContract.ReadyitEntry.KIND, dataHolder.kind);

        // Insert the content values via a ContentResolver
        ReadyitProvider.tableToProcess(TABLE_SUBS_SUBREDDIT);
        context.getContentResolver().insert(ReadyItContract.ReadyitEntry.CONTENT_URI, contentValues);
    }

    public void insertSubSubredditsDetail(GetDetailedSubredditListModel.Data_ dataHolder) {

        // Create new empty ContentValues object
        ContentValues contentValues = new ContentValues();
        // Put the movie data into the ContentValues
        contentValues.put(ReadyItContract.ReadyitEntry._ID, dataHolder.id);
        contentValues.put(ReadyItContract.ReadyitEntry.SUBREDDIT_NAME, dataHolder.subreddit);
        contentValues.put(ReadyItContract.ReadyitEntry.NAME, dataHolder.name);
        contentValues.put(ReadyItContract.ReadyitEntry.SUBREDDIT_TITLE, dataHolder.title);
        contentValues.put(ReadyItContract.ReadyitEntry.URL, dataHolder.url);
        contentValues.put(ReadyItContract.ReadyitEntry.THUMB_URL, dataHolder.thumbnail);

        contentValues.put(ReadyItContract.ReadyitEntry.IS_SUBSCRIBED, true);
        contentValues.put(ReadyItContract.ReadyitEntry.SUBREDDIT_ID, dataHolder.subredditId);
        contentValues.put(ReadyItContract.ReadyitEntry.UP, dataHolder.ups);
        contentValues.put(ReadyItContract.ReadyitEntry.DOWN, dataHolder.downs);
        contentValues.put(ReadyItContract.ReadyitEntry.COMMENTS_COUNT, dataHolder.numComments);
        contentValues.put(ReadyItContract.ReadyitEntry.CREATED_UTC, dataHolder.createdUtc);
        // Insert the content values via a ContentResolver
        ReadyitProvider.tableToProcess(TABLE_DETAIL_SUBREDDIT);
        context.getContentResolver().insert(ReadyItContract.ReadyitEntry.CONTENT_URI, contentValues);
    }

//    public void insertComments(GetCommentsModel.Child dataHolder) {
//
//        // Create new empty ContentValues object
//        ContentValues contentValues = new ContentValues();
//        // Put the movie data into the ContentValues
//        contentValues.put(ReadyItContract.ReadyitEntry._ID, dataHolder.data.id);
//        contentValues.put(ReadyItContract.ReadyitEntry.DEPTH, dataHolder.data.depth);
//        contentValues.put(ReadyItContract.ReadyitEntry.SUBREDDIT_ID, dataHolder.data.subredditId);
//        contentValues.put(ReadyItContract.ReadyitEntry.LIKES, (int)dataHolder.data.likes);
//        contentValues.put(ReadyItContract.ReadyitEntry.AUTHOR, dataHolder.data.author);
//        contentValues.put(ReadyItContract.ReadyitEntry.PARENT_ID, dataHolder.data.parentId);
//        contentValues.put(ReadyItContract.ReadyitEntry.SCORE, dataHolder.data.score);
//        contentValues.put(ReadyItContract.ReadyitEntry.BODY, dataHolder.data.body);
//        contentValues.put(ReadyItContract.ReadyitEntry.DOWN, dataHolder.data.downs);
//        contentValues.put(ReadyItContract.ReadyitEntry.UP, dataHolder.data.ups);
//        contentValues.put(ReadyItContract.ReadyitEntry.SUBREDDIT_NAME, dataHolder.data.subreddit);
//        contentValues.put(ReadyItContract.ReadyitEntry.NAME, dataHolder.data.name);
//        contentValues.put(ReadyItContract.ReadyitEntry.LINK_ID, dataHolder.data.linkId);
//
//        // Insert the content values via a ContentResolver
//        ReadyitProvider.tableToProcess(TABLE_COMMENTS);
//        context.getContentResolver().insert(ReadyItContract.ReadyitEntry.CONTENT_URI, contentValues);
//    }

    /**
     * UPDATE
     */
    public int updateSubscribeReddits(SubscribeRedditsViewModel data) {
        ContentValues conValues = new ContentValues();
        String selectionClause = ReadyItContract.ReadyitEntry._ID + " ='" + data.id + "'";
        conValues.put(ReadyItContract.ReadyitEntry.IS_SUBSCRIBED, data.hasChecked);
        int rowsUpdated = context.getContentResolver().update(ReadyItContract.ReadyitEntry.CONTENT_URI, conValues, selectionClause, null);
        return rowsUpdated;
    }

    public boolean isAlreadySubscribed(int id) {
        boolean isMarked = false;
        ContentResolver mContentResolver = context.getContentResolver();
        String selection = ReadyItContract.ReadyitEntry._ID + " = " + id + " AND " + ReadyItContract.ReadyitEntry.IS_SUBSCRIBED + " =0";
        ReadyitProvider.tableToProcess(TABLE_SUBS_SUBREDDIT);
        Cursor cursor = mContentResolver.query(ReadyItContract.ReadyitEntry.CONTENT_URI,
                new String[]{ReadyItContract.ReadyitEntry.IS_SUBSCRIBED, ReadyItContract.ReadyitEntry.IS_SUBSCRIBED}, selection, null,
                null);

        if (cursor.getCount() > 0) {
            isMarked = true;
            cursor.close();
        } else {
            isMarked = false;
        }

        return isMarked;
    }

    public boolean isAlreadyInserted(String id) {
        boolean isMarked = false;
        ContentResolver mContentResolver = context.getContentResolver();
        String selection = ReadyItContract.ReadyitEntry._ID + " = '" + id + "' AND " + ReadyItContract.ReadyitEntry.IS_SUBSCRIBED + " =0";
        ReadyitProvider.tableToProcess(TABLE_SUBS_SUBREDDIT);
        Cursor cursor = mContentResolver.query(ReadyItContract.ReadyitEntry.CONTENT_URI,
                new String[]{ReadyItContract.ReadyitEntry.IS_SUBSCRIBED, ReadyItContract.ReadyitEntry.IS_SUBSCRIBED}, selection, null,
                null);

        if (cursor.getCount() > 0) {
            isMarked = true;
            cursor.close();
        } else {
            isMarked = false;
        }

        return isMarked;
    }

    public boolean isTableNotEmpty(String tableName, SQLiteDatabase db) {
        String count = "SELECT count(*) FROM " + tableName;
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount > 0)
            return true;
        else
            return false;
    }
}

