package org.downtowncoc.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.downtowncoc.prefs.Constants;

public class NetworkSettingsReceiver extends BroadcastReceiver
{
    private static final String LOG_TAG = NetworkSettingsReceiver.class.getSimpleName();
    private SharedPreferences mSharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connManager.getActiveNetworkInfo();

        mSharedPreferences = context.getApplicationContext().getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);

        if(activeNetworkInfo != null)
        {
            if (activeNetworkInfo.isConnected() && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI)
            {
                if(mSharedPreferences.getBoolean(Constants.WIFI_SYNC, true))
                {
                    Log.d(LOG_TAG, "Wi-fi connection " + mSharedPreferences.getBoolean(Constants.WIFI_CONNECTED, true));
                    mSharedPreferences.edit().putBoolean(Constants.WIFI_CONNECTED, true)
                                             .putBoolean(Constants.CONNECTIVITY, true).apply();
                }
                else
                {

                    mSharedPreferences.edit().putBoolean(Constants.DATA_USAGE, true)
                                         .putBoolean(Constants.CONNECTIVITY, true)
                                         .putBoolean(Constants.WIFI_CONNECTED, false).apply();
                    Log.d(LOG_TAG, "Else wi-fi sync off " + mSharedPreferences.getBoolean(Constants.DATA_USAGE, false));
                }
            }
            else if (activeNetworkInfo.isConnected() && activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                mSharedPreferences.edit().putBoolean(Constants.DATA_USAGE, true)
                                         .putBoolean(Constants.CONNECTIVITY, true)
                                         .putBoolean(Constants.WIFI_CONNECTED, false).apply();
                Log.d(LOG_TAG, "Mobile connection " + mSharedPreferences.getBoolean(Constants.DATA_USAGE, false) + " connectivity " + mSharedPreferences.getBoolean(Constants.CONNECTIVITY, false));
            }

        }
        else
        {
            /*if(!mSharedPreferences.getBoolean(Constants.WIFI_CONNECTED, true)) {
                mSharedPreferences.edit().putBoolean(Constants.DATA_USAGE, true).apply();
                Log.d(LOG_TAG, "Date usage wi-fi off " + mSharedPreferences.getBoolean(Constants.DATA_USAGE, false));
            }*/
            mSharedPreferences.edit().putBoolean(Constants.WIFI_SYNC, false)
                                     .putBoolean(Constants.DATA_USAGE, false)
                                     .putBoolean(Constants.CONNECTIVITY, false)
                                     .putBoolean(Constants.WIFI_CONNECTED, false).apply();
            Log.d(LOG_TAG, "Data usage wi-fi off " + mSharedPreferences.getBoolean(Constants.DATA_USAGE, false) + " connectivity " + mSharedPreferences.getBoolean(Constants.CONNECTIVITY, false));
        }
    }
}
