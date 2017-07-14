package org.downtowncoc.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by vmutshinya on 5/26/2017.
 */

public class DownTownCoCApp extends Application
{
    private static final String TAG = DownTownCoCApp.class.getSimpleName();
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Down Town CoC App started");
    }

    public SharedPreferences getSharedPreferences()
    {
        if(mSharedPreferences != null)
        {
            return mSharedPreferences;
        }
        else
        {
            return mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        }
    }
}
