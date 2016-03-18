package com.curry.stephen.contentprovidertest;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.LiveFolders;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.test.mock.MockPackageManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/3/15 0015.
 */
public class NotePadProvider extends ContentProvider
        implements ContentProvider.PipeDataWriter<Cursor> {

    /**
     * Used for debugging and logging.
     */
    private static final String TAG = NotePadProvider.class.getSimpleName();
    /**
     * The database that the provider uses as its underlying data store.
     */
    private static final String DATABASE_NAME = "note_pad.db";
    /**
     * The database version.
     */
    private static final int DATABASE_VERSION = 2;
    /**
     * A projection map used to select columns from the database;
     */
    private static HashMap<String, String> sMapNotesProjection;
    /**
     * A projection map used to select columns from the database;
     */
    private static HashMap<String, String> sMapLiveFolderProjection;
    /**
     * Standard projection for the interesting columns of a normal note.
     */
    private static final String[] READ_NOTE_PROJECTION = new String[]{
            NotePadContract.Notes._ID,
            NotePadContract.Notes.COLUMN_NAME_NOTE,
            NotePadContract.Notes.COLUMN_NAME_TITLE
    };
    private static final int READ_NOTE_NOTE_INDEX = 1;
    private static final int READ_NOTE_TITLE_INDEX = 2;

    // Constants used by the UriMatcher to choose an action based on the pattern.
    /**
     * The incoming URI matches the Notes URI pattern.
     */
    private static final int NOTES = 1;
    /**
     * The incoming URI matches the Note ID URI pattern.
     */
    private static final int NOTE_ID = 2;
    /**
     * The incoming URI matches the Live Folder URI pattern.
     */
    private static final int LIVE_FOLDER_NOTES = 3;

    /**
     * A UriMather instance.
     */
    private static final UriMatcher sUrimatcher;

    /**
     * A DatabaseHelper instance.
     */
    private DatabaseHelper mDatabaseHelper;

    static {
        // Create a new UriMatcher instance.
        sUrimatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Add a pattern that routes URIs terminated with "notes" to a NOTES operation.
        sUrimatcher.addURI(NotePadContract.AUTHORITY, "notes", NOTES);
        // Add a pattern that routes URIs terminated with "notes" plus an integer to a NOTE ID
        // operation.
        sUrimatcher.addURI(NotePadContract.AUTHORITY, "notes/#", NOTE_ID);
        // Add a pattern that routes URIs terminated with "live_folder/notes" to a live folder
        // operation.
        sUrimatcher.addURI(NotePadContract.AUTHORITY, "live_folder/notes", LIVE_FOLDER_NOTES);

        // Create a new projection map instance. The map returns a column name by given a string.
        // The two are usually equal.
        sMapNotesProjection = new HashMap<>();
        // Maps the string "_ID" to the column name "_ID".
        sMapNotesProjection.put(NotePadContract.Notes._ID, NotePadContract.Notes._ID);
        // Maps "title" to "title".
        sMapNotesProjection.put(NotePadContract.Notes.COLUMN_NAME_TITLE,
                NotePadContract.Notes.COLUMN_NAME_TITLE);
        // Maps "note" to "note".
        sMapNotesProjection.put(NotePadContract.Notes.COLUMN_NAME_NOTE,
                NotePadContract.Notes.COLUMN_NAME_NOTE);
        // Maps "created" to "created"
        sMapNotesProjection.put(NotePadContract.Notes.COLUMN_NAME_CREATE_DATE,
                NotePadContract.Notes.COLUMN_NAME_CREATE_DATE);
        sMapNotesProjection.put(NotePadContract.Notes.COLUMN_NAME_MODIFICATION_DATE,
                NotePadContract.Notes.COLUMN_NAME_MODIFICATION_DATE);

        sMapLiveFolderProjection.put(LiveFolders._ID,
                NotePadContract.Notes._ID + " AS " + LiveFolders._ID);
        sMapLiveFolderProjection.put(LiveFolders.NAME,
                NotePadContract.Notes.COLUMN_NAME_TITLE + " AS " + LiveFolders.NAME);
    }

    /**
     * This class helps open, create and update the database file. Set to package visibility
     * for test purposes.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        /**
         * Create the underlying database with table name and column names taken from the
         * NotePadContract Class.
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + NotePadContract.Notes.TABLE_NAME + " (" +
                    NotePadContract.Notes._ID + " INTEGER PRIMARY EKY," +
                    NotePadContract.Notes.COLUMN_NAME_TITLE + " TEXT," +
                    NotePadContract.Notes.COLUMN_NAME_NOTE + " TEXT," +
                    NotePadContract.Notes.COLUMN_NAME_CREATE_DATE + " INTEGER," +
                    NotePadContract.Notes.COLUMN_NAME_MODIFICATION_DATE + " INTEGER" +
                    ");");
        }

        /**
         * Demonstrates that the provider must consider what happens when the underlying database
         * is changed.
         * In this sample, the database is upgraded by destroying the existing data.
         * In really application, the database is supposed to be by upgrade the existing datum or
         * schemas in place.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, String.format("Upgrading database from version %d to %d, which will destroy all old data.",
                    oldVersion, newVersion));

            db.execSQL(String.format("DROP TABLE IF EXISTS %s", NotePadContract.Notes.TABLE_NAME));

            onCreate(db);
        }
    }

    /**
     * Initializes the provider by creating a new DatabaseHelper instance. onCreate() is called
     * automatically
     * when Android creates the provider in response to a resolver request from a client.
     *
     * @return True if provider loads successfully, otherwise return false.
     */
    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext());

        return true;
    }

    /**
     * This method is called when a client calls
     * {@link android.content.ContentResolver#query(Uri, String[], String, String[], String)}.
     * Queries the database and returns a cursor containing the results.
     *
     * @return {@link IllegalArgumentException} if the incoming {@link Uri} pattern is invalid.
     */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();

        // Set the table name for SQLiteQueryBuilder (convience query class).
        sqLiteQueryBuilder.setTables(NotePadContract.Notes.TABLE_NAME);

        // Set the projection map and append the condition clause for SQLiteQueryBuilder.
        switch (sUrimatcher.match(uri)) {
            case NOTES:
                sqLiteQueryBuilder.setProjectionMap(sMapNotesProjection);
                break;
            case NOTE_ID:
                sqLiteQueryBuilder.setProjectionMap(sMapNotesProjection);
                sqLiteQueryBuilder.appendWhere(
                        NotePadContract.Notes._ID +
                                "=" +
                                uri.getPathSegments().get(NotePadContract.Notes.NOTE_ID_PATH_POSITION)
                );
                break;
            case LIVE_FOLDER_NOTES:
                sqLiteQueryBuilder.setProjectionMap(sMapLiveFolderProjection);
                break;
            default:
                throw new
                        IllegalArgumentException(String.format("Unknown URI %s.", uri.toString()));
        }

        // Set result set arrange order for SQLiteQueryBuilder.
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = NotePadContract.Notes.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Create if necessary and open the database.
        SQLiteDatabase sqLiteDatabase = mDatabaseHelper.getReadableDatabase();

        Cursor cursor = sqLiteQueryBuilder.query(
                sqLiteDatabase,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                orderBy
        );

        // Register to watch a content URI for changes. If data on URI is changed,
        // the listener attached to the content resolver will be notified.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUrimatcher.match(uri)) {
            case NOTES:
            case LIVE_FOLDER_NOTES:
                return NotePadContract.Notes.CONTENT_TYPE;
            case NOTE_ID:
                return NotePadContract.Notes.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException(String.format("Unknown URI %s", uri.toString()));
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (sUrimatcher.match(uri) != NOTES) {
            throw new IllegalArgumentException(String.format("Unknown URI %s", uri.toString()));
        }

        ContentValues contentValues;

        if (values == null) {
            contentValues = new ContentValues();
        } else {
            contentValues = new ContentValues(values);
        }

        long now = System.currentTimeMillis();

        if (contentValues.containsKey(NotePadContract.Notes.COLUMN_NAME_CREATE_DATE)) {
            contentValues.put(NotePadContract.Notes.COLUMN_NAME_CREATE_DATE, now);
        }

        if (contentValues.containsKey(NotePadContract.Notes.COLUMN_NAME_MODIFICATION_DATE)) {
            contentValues.put(NotePadContract.Notes.COLUMN_NAME_MODIFICATION_DATE, now);
        }

        if (contentValues.containsKey(NotePadContract.Notes.COLUMN_NAME_TITLE)) {
            contentValues.put(NotePadContract.Notes.COLUMN_NAME_TITLE, getContext().getString(R.string.untitled));
        }

        if (contentValues.containsKey(NotePadContract.Notes.COLUMN_NAME_NOTE)) {
            contentValues.put(NotePadContract.Notes.COLUMN_NAME_NOTE, "");
        }

        SQLiteDatabase sqLiteDatabase = mDatabaseHelper.getWritableDatabase();

        long rowID = sqLiteDatabase.insert(
                NotePadContract.Notes.TABLE_NAME,
                NotePadContract.Notes.COLUMN_NAME_NOTE, // A hack for inserting a row which
                                                        // nullHackColumn's value is explicitly
                                                        // set NULL value when the
                                                        // contentValues is empty.
                contentValues
        );

        if (rowID > 0) {
            Uri rowUri = ContentUris.withAppendedId(NotePadContract.Notes.CONTENT_ID_URI_BASE, rowID);

            // Notify all observers which registered to the ContentResolver of some change being
            // happened on the URI.
            getContext().getContentResolver().notifyChange(rowUri, null);

            return rowUri;
        } else {
            throw new SQLException(String.format("Failed to insert new row into %s", uri.toString()));
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;
        SQLiteDatabase sqLiteDatabase = mDatabaseHelper.getWritableDatabase();

        switch (sUrimatcher.match(uri)) {
            case NOTES:
                count = sqLiteDatabase.delete(NotePadContract.Notes.TABLE_NAME, selection, selectionArgs);
                break;
            case NOTE_ID:
                String finalWhere = NotePadContract.Notes._ID + " = " +
                        uri.getPathSegments().get(NotePadContract.Notes.NOTE_ID_PATH_POSITION);
                finalWhere = selection != null ? finalWhere + " AND " + selection: finalWhere;
                count = sqLiteDatabase.delete(NotePadContract.Notes.TABLE_NAME, finalWhere, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown URI %s", uri.toString()));
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        SQLiteDatabase sqLiteDatabase = mDatabaseHelper.getWritableDatabase();

        switch (sUrimatcher.match(uri)) {
            case NOTES:
                count = sqLiteDatabase.update(NotePadContract.Notes.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case NOTE_ID:
                String finalWhere = NotePadContract.Notes._ID + " = " +
                        uri.getPathSegments().get(NotePadContract.Notes.NOTE_ID_PATH_POSITION);
                finalWhere = selection != null ? finalWhere + " AND " + selection : finalWhere;
                count = sqLiteDatabase.update(NotePadContract.Notes.TABLE_NAME, values, finalWhere,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown URI %s", uri.toString()));
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    DatabaseHelper getOpenHelperForTest() {
        return mDatabaseHelper;
    }

    @Override
    public void writeDataToPipe(ParcelFileDescriptor output, Uri uri, String mimeType, Bundle opts,
                                Cursor args) {

    }
}
