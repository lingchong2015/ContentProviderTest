package com.curry.stephen.contentprovidertest;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;

/**
 * Created by Administrator on 2016/3/4 0004.
 */
public class MyPeopleListActivity extends ListActivity implements AdapterView.OnItemSelectedListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a cursor for specific content uri.
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME},
                null, null, null);

        // Allow activity to take care cursor's lifecycle based on activity's lifecycle, and we don't call close() method for cursor.
        startManagingCursor(cursor);

        // Initialize a adapter.
        ListAdapter listAdapter = new SimpleCursorAdapter(this, // context.
                android.R.layout.simple_list_item_1, // row layout.
                cursor, // cursor and we want to use its pointing data.
                new String[]{ContactsContract.Contacts.DISPLAY_NAME}, // 'from' object.
                new int[]{android.R.id.text1} // 'to' object.
        );

        setListAdapter(listAdapter);// Provide adapter for view, and use the adapter to communicate data to which the cursor points with the view.

        getListView().setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(this, MyPhoneListActivity.class));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
