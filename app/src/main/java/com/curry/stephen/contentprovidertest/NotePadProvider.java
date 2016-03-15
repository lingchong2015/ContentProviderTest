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
import android.support.annotation.Nullable;

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

    static {
        // Create a new UriMatcher instance.
        sUrimatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Add a pattern that routes URIs terminated with "notes" to a NOTES operation.
        sUrimatcher.addURI(NotePadContract.AUTHORITY, "notes", NOTES);
        // Add a pattern that routes URIs terminated with "notes" plus an integer to a NOTE ID operation.
        sUrimatcher.addURI(NotePadContract.AUTHORITY, "notes/#", NOTE_ID);
        // Add a pattern that routes URIs terminated with "live_folder/notes" to a live folder operation.
        sUrimatcher.addURI(NotePadContract.AUTHORITY, "live_folder/notes", LIVE_FOLDER_NOTES);

        // Create a new projection map instance.
        sMapNotesProjection = new HashMap<>();
        sMapNotesProjection.put(NotePadContract.Notes._ID, NotePadContract.Notes._ID);
        sMapNotesProjection.put(NotePadContract.Notes.COLUMN_NAME_TITLE, NotePadContract.Notes.COLUMN_NAME_TITLE);
        sMapNotesProjection.put(NotePadContract.Notes.COLUMN_NAME_NOTE, NotePadContract.Notes.COLUMN_NAME_NOTE);
        sMapNotesProjection.put(NotePadContract.Notes.COLUMN_NAME_CREATE_DATE, NotePadContract.Notes.COLUMN_NAME_CREATE_DATE);
        sMapNotesProjection.put(NotePadContract.Notes.COLUMN_NAME_MODIFICATION_DATE, NotePadContract.Notes.COLUMN_NAME_MODIFICATION_DATE);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    @Override
    public boolean onCreate() {
        return false;
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
