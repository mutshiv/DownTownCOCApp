package org.downtowncoc.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkSettingsReceiver extends BroadcastReceiver
{
    private static final String LOG_TAG = NetworkSettingsReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connManager.getActiveNetworkInfo();

        Log.d(LOG_TAG, "Connection test " + activeNetworkInfo.isConnected());

        if(activeNetworkInfo.isConnected() && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            Log.d(LOG_TAG, "Wi-fi connection");
        }
        else if(activeNetworkInfo.isConnected() && activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
        {
            Log.d(LOG_TAG, "Mobile network connection");
        }
    }
}
