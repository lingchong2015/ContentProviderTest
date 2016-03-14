package com.curry.stephen.contentprovidertest;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/3/4 0004.
 */
public class MyPhoneListActivity extends ListActivity implements AdapterView.OnItemClickListener {
    private static final String[] PHONE_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.TYPE,
            ContactsContract.CommonDataKinds.Phone.LABEL,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
    };

    private static final String SELECTION = ContactsContract.CommonDataKinds.Phone.NUMBER + " NOT NULL";

    private static final String ORDER = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_phone_list);

        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PHONE_PROJECTION,
                SELECTION,
                null,
                ORDER);

        startManagingCursor(cursor);

        ListAdapter listAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                cursor,
                new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                new int[]{android.R.id.text1});

        setListAdapter(listAdapter);

        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position > 0) {
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);// Get the data(Cursor Object) associated with the specified position in the list.

            int typeIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
            int type = cursor.getInt(typeIndex);
            int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String number = cursor.getString(numberIndex);
            String label = null;
            if (type == ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM) {
                label = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
            }

            String numberType = (String) ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(), type, label);
            String text = String.format("%s: %s", numberType, number);
            ((TextView) findViewById(R.id.text_view_phone)).setText(text);
        }
    }
}
