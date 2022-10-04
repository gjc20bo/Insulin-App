package com.example.insulinapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
/* This is where our app can interact with our database. */
public class dbProvider extends ContentProvider {
    public final static String DBNAME = "InsulinDB";
    /* First we create a subclass that creates the table and will be what the content provider
    interacts with directly. The database helper is what interacts with our SQLite database.*/
    protected static final class MainDatabaseHelper extends SQLiteOpenHelper {
        MainDatabaseHelper(Context context) {
            super(context, DBNAME, null, 1);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_MAIN);
            db.execSQL(SQL_CREATE_HISTORICAL);
        }
        @Override
        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        }
    }

    /* Construct the database helper. */
    private MainDatabaseHelper mOpenHelper;
    /* Use strings to store the various column names and the create table SQL commands.
    This way we can change these variables and not have to change the rest of our code. */
    public static final String AUTHORITY = "InsulinApp.provider";
    public static final String USERNAME_COLUMN="username";
    public static final String PASSWORD_COLUMN="password";
    public static final String ID_COLUMN ="_ID";
    public static final String DATE_COLUMN ="date";
    public static final String TIME_COLUMN="time";
    public static final String DATA_COLUMN="data";
    public static final String SQL_USER_TABLE="users";
    public static final String SQL_HISTORICAL_TABLE ="historicalData";

    private static final String SQL_CREATE_MAIN="CREATE TABLE "+SQL_USER_TABLE + "("+
            "_ID INTEGER PRIMARY KEY, " +
            USERNAME_COLUMN+" TEXT, " +
            PASSWORD_COLUMN+" TEXT)";
    private static final String SQL_CREATE_HISTORICAL="CREATE TABLE "+SQL_HISTORICAL_TABLE+"("+
            "_ID INTEGER PRIMARY KEY, " +
            USERNAME_COLUMN+" TEXT, " +
            DATA_COLUMN+" TEXT, " +
            TIME_COLUMN+" TEXT, " +
            DATE_COLUMN+" TEXT)";

    public static final Uri USER_URI = Uri.parse("content://" +
            AUTHORITY +"/" + SQL_USER_TABLE);
    public static final Uri HISTORICAL_URI = Uri.parse("content://" + AUTHORITY +"/" +
            SQL_HISTORICAL_TABLE);

    public dbProvider() {
    }
    /* The rest of these are how our content provider will interact with our database through the
    database helper. When we need to do something we will call these functions and give the
    appropriate data for the commands. */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        if(uri.equals(USER_URI)) {
            return mOpenHelper.getWritableDatabase().delete(SQL_USER_TABLE,
                    selection, selectionArgs);
        }
        else {
            return mOpenHelper.getWritableDatabase().delete(SQL_HISTORICAL_TABLE,
                    selection, selectionArgs);
        }
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        long id;
        if(uri.equals(USER_URI)) {
            id = mOpenHelper.getWritableDatabase().insert(SQL_USER_TABLE,
                    null, values);
            return Uri.withAppendedPath(USER_URI, "" + id);
        }
        else {
            id=mOpenHelper.getWritableDatabase().insert(SQL_HISTORICAL_TABLE,
                    null, values);
            return Uri.withAppendedPath(HISTORICAL_URI, "" + id);
        }
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        mOpenHelper = new MainDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        if(uri.equals(USER_URI)) {
            return mOpenHelper.getReadableDatabase().query(SQL_USER_TABLE, projection,
                    selection, selectionArgs,
                    null, null, sortOrder);
        }
        else {
            return mOpenHelper.getReadableDatabase().query(SQL_HISTORICAL_TABLE, projection,
                    selection, selectionArgs,
                    null, null, sortOrder);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        if(uri.equals(USER_URI)) {
            return mOpenHelper.getWritableDatabase().update(SQL_USER_TABLE, values,
                    selection, selectionArgs);
        }
        else {
            return mOpenHelper.getWritableDatabase().update(SQL_HISTORICAL_TABLE, values,
                    selection, selectionArgs);
        }
    }
}