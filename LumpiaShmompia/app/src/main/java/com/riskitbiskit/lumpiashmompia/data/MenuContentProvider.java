package com.riskitbiskit.lumpiashmompia.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.riskitbiskit.lumpiashmompia.data.MenuContract.MenuEntry;


public class MenuContentProvider extends ContentProvider {

    //Debug
    public static final String LOG_TAG = MenuContentProvider.class.getSimpleName();

    //Constants
    public static final int ITEMS = 100;
    public static final int ITEMS_WITH_ID = 101;

    //Fields
    private MenuDBHelper mDBHelper;

    //sUriMatcher setup
    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(MenuContract.CONTENT_AUTHORITY, MenuContract.PATH_MENU, ITEMS);
        sUriMatcher.addURI(MenuContract.CONTENT_AUTHORITY, MenuContract.PATH_MENU + "/#", ITEMS_WITH_ID);
    }

    //Upon creation of provider...
    @Override
    public boolean onCreate() {
        mDBHelper = new MenuDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        //Get an readable instance of database
        SQLiteDatabase database = mDBHelper.getReadableDatabase();

        //Returnable cursor instance
        Cursor returnCursor;

        //Cross check uri
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                //TODO: Query for all menu items
                returnCursor = database.query(MenuEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ITEMS_WITH_ID:
                //TODO: Query for a specific item
                selection = MenuEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                returnCursor = database.query(MenuEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        //TODO: change to returnCursor once all cases have been made
        return returnCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        //Get an writable instance of database
        SQLiteDatabase database = mDBHelper.getReadableDatabase();

        //Cross check uri
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                long id = database.insert(MenuEntry.TABLE_NAME, null, contentValues);

                if (id == -1) {
                    Log.e(LOG_TAG, "Failed in insert row(s) " + uri);
                    return null;
                }
                return ContentUris.withAppendedId(uri, id);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        //Get an readable instance of database
        SQLiteDatabase database = mDBHelper.getReadableDatabase();

        //Returnable int (count of deleted rows)
        int rowsDeleted;

        //Cross check uri
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(MenuEntry.TABLE_NAME, selection, selectionArgs);

                //Check to see if any rows were successfully deleted
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                //return the number of rowsDeleted
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    //Feature not supported
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {

        //Get an writable instance of database
        SQLiteDatabase database = mDBHelper.getReadableDatabase();

        int rowsUpdated = 0;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                rowsUpdated = database.update(MenuEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case ITEMS_WITH_ID:
                selection = MenuEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsUpdated = database.update(MenuEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                Log.e(LOG_TAG, rowsUpdated + "");
                break;
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    //Feature not supported
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
