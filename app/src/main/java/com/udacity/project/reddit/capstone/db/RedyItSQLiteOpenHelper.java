package com.udacity.project.reddit.capstone.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.udacity.project.reddit.capstone.model.GetSubredditsModel;
import com.udacity.project.reddit.capstone.model.SubscribeRedditsViewModel;
import com.udacity.project.reddit.capstone.utils.DatabaseUtils;

import static com.udacity.project.reddit.capstone.utils.DatabaseUtils.TABLE_SUBS_SUBREDDIT;

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseUtils.DROP_TABLE_SUB_SUBREDDIT);
    }

    public void insertSubSubreddits(GetSubredditsModel.Data_ dataHolder) {

        // Create new empty ContentValues object
        ContentValues contentValues = new ContentValues();
        // Put the movie data into the ContentValues
        contentValues.put(ReadyItContract.ReadyitEntry._ID, dataHolder.id);
        contentValues.put(ReadyItContract.ReadyitEntry.DISPLAY_NAME, dataHolder.displayName);
        contentValues.put(ReadyItContract.ReadyitEntry.NAME, dataHolder.name);
        contentValues.put(ReadyItContract.ReadyitEntry.TITLE, dataHolder.title);
        contentValues.put(ReadyItContract.ReadyitEntry.URL, dataHolder.url);
        contentValues.put(ReadyItContract.ReadyitEntry.IS_SUBSCRIBED, dataHolder.isSubscribed);
        contentValues.put(ReadyItContract.ReadyitEntry.CREATED_UTC,dataHolder.createdUtc);

        // Insert the content values via a ContentResolver
        ReadyitProvider.tableToProcess(TABLE_SUBS_SUBREDDIT);
        context.getContentResolver().insert(ReadyItContract.ReadyitEntry.CONTENT_URI, contentValues);
    }
    /** UPDATE	 */
    public int updateSubscribeReddits(SubscribeRedditsViewModel data) {
        ContentValues conValues = new ContentValues();
        String selectionClause = ReadyItContract.ReadyitEntry._ID +  " ='"+data.id+"'";
        conValues.put(ReadyItContract.ReadyitEntry.IS_SUBSCRIBED, data.hasChecked);
        int rowsUpdated = context.getContentResolver().update(ReadyItContract.ReadyitEntry.CONTENT_URI, conValues, selectionClause, null);
        return rowsUpdated;
    }
    public boolean isAlreadySubscribed(int id) {
        boolean isMarked=false;
        ContentResolver mContentResolver = context.getContentResolver();
        String selection = ReadyItContract.ReadyitEntry._ID + " = " + id +" AND "+ ReadyItContract.ReadyitEntry.IS_SUBSCRIBED+" =0";
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
        boolean isMarked=false;
        ContentResolver mContentResolver = context.getContentResolver();
        String selection = ReadyItContract.ReadyitEntry._ID + " = '" + id +"' AND "+ ReadyItContract.ReadyitEntry.IS_SUBSCRIBED+" =0";
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
}

