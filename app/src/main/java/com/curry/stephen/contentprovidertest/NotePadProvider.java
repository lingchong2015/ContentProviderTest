package com.curry.stephen.contentprovidertest;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.LiveFolders;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/3/15 0015.
 */
public class NotePadProvider extends ContentProvider implements ContentProvider.PipeDataWriter<Cursor> {

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
    private static final String[] READ_NOTE_PROJECTION = new String[] {
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
        // Add a pattern that routes URIs terminated with "notes" plus an integer to a NOTE ID operation.
        sUrimatcher.addURI(NotePadContract.AUTHORITY, "notes/#", NOTE_ID);
        // Add a pattern that routes URIs terminated with "live_folder/notes" to a live folder operation.
        sUrimatcher.addURI(NotePadContract.AUTHORITY, "live_folder/notes", LIVE_FOLDER_NOTES);

        // Create a new projection map instance. The map returns a column name by given a string. The two are usually equal.
        sMapNotesProjection = new HashMap<>();
        // Maps the string "_ID" to the column name "_ID".
        sMapNotesProjection.put(NotePadContract.Notes._ID, NotePadContract.Notes._ID);
        // Maps "title" to "title".
        sMapNotesProjection.put(NotePadContract.Notes.COLUMN_NAME_TITLE, NotePadContract.Notes.COLUMN_NAME_TITLE);
        // Maps "note" to "note".
        sMapNotesProjection.put(NotePadContract.Notes.COLUMN_NAME_NOTE, NotePadContract.Notes.COLUMN_NAME_NOTE);
        // Maps "created" to "created"
        sMapNotesProjection.put(NotePadContract.Notes.COLUMN_NAME_CREATE_DATE, NotePadContract.Notes.COLUMN_NAME_CREATE_DATE);
        sMapNotesProjection.put(NotePadContract.Notes.COLUMN_NAME_MODIFICATION_DATE,
                NotePadContract.Notes.COLUMN_NAME_MODIFICATION_DATE);

        sMapLiveFolderProjection.put(LiveFolders._ID, NotePadContract.Notes._ID + " AS " + LiveFolders._ID);
        sMapLiveFolderProjection.put(LiveFolders.NAME, NotePadContract.Notes.COLUMN_NAME_TITLE + " AS " + LiveFolders.NAME);
    }

    /**
     * This class helps open, create and update the database file. Set to package visibility for test purposes.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        /**
         * Create the underlying database with table name and column names taken from the NotePadContract Class.
         * @param db
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
         * Demonstrates that the provider must consider what happens when the underlying database is changed.
         * In this sample, the database is upgraded by destroying the existing data.
         * In really application, the database is supposed to be by upgrade the existing datum or schemas in place.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, String.format("Upgrading database from version %d to %d, which will destroy all old data.",
                    oldVersion, newVersion));

            db.execSQL(String.format("DROP TABLE IF EXISTS %s", NotePadContract.Notes.TABLE_NAME));

            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public void writeDataToPipe(ParcelFileDescriptor output, Uri uri, String mimeType, Bundle opts, Cursor args) {

    }
}
