package org.downtowncoc.customClasses;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.downtowncoc.R;
import org.downtowncoc.prefs.Constants;

/**
 * Created by vmutshinya on 7/18/2017.
 *
 */

public class CustomCursorAdapter extends SimpleCursorAdapter
{
    private int layout;
    private Context context;

    public CustomCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.layout = layout;
        this.context = context;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        int filename = cursor.getColumnIndex(Constants.Columns.FILE_NAME);
        String name = cursor.getString(filename);

        TextView tvFilename = (TextView)view.findViewById(R.id.tvListItemName);
        tvFilename.setText(name);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(layout, parent, false);

        int filename = cursor.getColumnIndex(Constants.Columns.FILE_NAME);
        String name = cursor.getString(filename);

        TextView tvFilename = (TextView)v.findViewById(R.id.tvListItemName);
        tvFilename.setText(name);

        return v;
    }
}
