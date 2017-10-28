package com.udacity.project.reddit.capstone.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.udacity.project.reddit.capstone.utils.DatabaseUtils.DATABASE_NAME;

/**
 * Created by Neha on 22-08-2017.
 */

public class ReadyitProvider extends ContentProvider {
    public static final int REDDIT = 100;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static String TABLE_NAME;
    private RedyItSQLiteOpenHelper dBHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dBHelper = new RedyItSQLiteOpenHelper(context);
        return false;
    }

    /**
     * Initialize a new matcher object without any matches,
     * then use .addURI(String authority, String path, int match) to add matches
     */
    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
          The two calls below add matches for the task directory and a single item by ID.
         */
        uriMatcher.addURI(ReadyItContract.AUTHORITY,ReadyItContract.PAT_MINE_READYIT,REDDIT);
        return uriMatcher;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        final SQLiteDatabase db = dBHelper.getReadableDatabase();
        final String table = projection[0];

        switch (sUriMatcher.match(uri)) {
            case REDDIT: {
                cursor = db.query(table
                        ,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        ReadyItContract.ReadyitEntry.CREATED_UTC+" DESC"
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        //  Get access to the task database (to write new data to)
        final SQLiteDatabase db = dBHelper.getWritableDatabase();

        //  Write URI matching code to identify the match for the tasks directory
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case REDDIT:
                //  Insert new values into the database
                // Inserting values into tasks table
                long id = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(ReadyItContract.ReadyitEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            //Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = dBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;

        switch (match) {
            case REDDIT:
                rowsUpdated = db.delete(TABLE_NAME, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        //  Get access to the task database (to write new data to)
        final SQLiteDatabase db = dBHelper.getWritableDatabase();

        //  Write URI matching code to identify the match for the tasks directory
        int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;

        switch (match) {
            case REDDIT:
                rowsUpdated = db.update(TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;

    }
    public static void tableToProcess(String table)
    {
        TABLE_NAME =  table;
    }
}
