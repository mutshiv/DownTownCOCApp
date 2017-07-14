package org.downtowncoc.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.downtowncoc.prefs.Constants;

/**
 * Created by vmutshinya on 6/3/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper
{
    private static final String LOG_TAG = DataBaseHelper.class.getSimpleName();

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.d(LOG_TAG, "OnCreate DB");

        String sql = String.format("CREATE TABLE %s(%s integer primary key autoincrement, %s text, %s text, %s text)",
                                    Constants.TABLE_NAME,
                                    Constants.Columns.MEDIA_ID,
                                    Constants.Columns.FILE_NAME,
                                    Constants.Columns.FILE_NAME_FULL,
                                    Constants.Columns.MEDIA_TYPE);

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.d(LOG_TAG, "upgrade");
        String upgradeSQL = "DROP TABLE IF EXISTS " + Constants.TABLE_NAME;
        db.execSQL(upgradeSQL);
        onCreate(db);
    }
}
