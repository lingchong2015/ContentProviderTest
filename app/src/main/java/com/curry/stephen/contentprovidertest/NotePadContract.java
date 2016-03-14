package com.curry.stephen.contentprovidertest;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Administrator on 2016/3/5 0005.
 * Notice: a well-written client depends only on the constants in the contract class.
 */
public final class NotePadContract {
    /**
     * The authority of NotePad.
     */
    public static final String AUTHORITY = "com.curry.stephen.provider.NotePad";

    private NotePadContract() {
    }

    public static final class Notes implements BaseColumns {
        private Notes() {
        }

        /**
         * The table name offered by this provider.
         */
        public static final String TABLE_NAME = "notes";

        /**
         * The schema part for this provider's URI.
         */
        private static final String SCHEME = "content://";

        /**
         * Path part for the Notes URI.
         */
        private static final String PATH_NOTES = "/notes";

        /**
         * Path part for the Notes ID URI.
         */
        private static final String PATH_NOTES_ID = "/notes/";

        /**
         * 0-relative position of a note ID segment in the path part of a note ID URI.
         */
        public static final int NOTE_ID_PATH_POSITION = 1;

        /**
         * Path part for the Live Folder URI.
         */
        private static final String PATH_LIVE_FOLDER = "/live_folders/notes";

        /**
         * The content URI for Notes Table.
         */
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_NOTES);

        /**
         * The content URI base for match a single note item. Caller must append a numeric note id to this Uri to retrieve a note.
         */
        public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + PATH_NOTES_ID);

        /**
         * The content URI match pattern for a single note item. Use this to match incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME + AUTHORITY + PATH_NOTES_ID + "/#");

        /**
         * The content URI for live folders's notes.
         */
        public static final Uri LIVE_FOLDER_URI = Uri.parse(SCHEME + AUTHORITY + PATH_LIVE_FOLDER);

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.curry.stephen.contentprovidertest.notes";

        /**
         * The MIME type of {@link #CONTENT_URI} providing a sub-directory of a single item of notes.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.curry.stephen.contentprovidertest.notes";

        /**
         * The default sort order for this table.
         */
        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        /**
         * Column name for the title of the note.
         * <P>Type: TEXT.</P>
         */
        public static final String COLUMN_NAME_TITLE = "title";

        /**
         * Column name for the note content.
         * <P>Type: Text.</P>
         */
        public static final String COLUMN_NAME_NOTE = "note";

        /**
         * Column name for the creation timestamp.
         * <P>Type: INTEGER (long from System.currentTimeMillis()).</P>
         */
        public static final String COLUMN_NAME_CREATE_DATE = "created";

        /**
         * Column name for the modification timestamp.
         * <P>Type: INTEGER (long from System.currentTimeMillis()).</P>
         */
        public static final String COLUMN_NAME_MODIFICATION_DATE = "modified";
    }
}
