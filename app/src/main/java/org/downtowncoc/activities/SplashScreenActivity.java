package org.downtowncoc.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.downtowncoc.R;
import org.downtowncoc.app.DownTownCoCApp;
import org.downtowncoc.prefs.Constants;
import org.downtowncoc.services.PDTService;

import java.util.UUID;

public class SplashScreenActivity extends AppCompatActivity
{
    private static final String LOG_TAG = SplashScreenActivity.class.getSimpleName();
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mSharedPreferences = (getApplication()).getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);
        String deviceGUID = UUID.randomUUID().toString();

        if(mSharedPreferences.getString(Constants.DEVICE_ID, "new").equals("new"))
        {
            mSharedPreferences.edit().putString(Constants.DEVICE_ID, deviceGUID).putBoolean(Constants.WIFI_SYNC, false).apply();
//            mSharedPreferences.edit().putBoolean(Constants.WIFI_SYNC, false).apply();
        }

        new Thread(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch(InterruptedException ie)
                {
                    ie.printStackTrace();
                }

                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connManager.getActiveNetworkInfo();

                mSharedPreferences = getApplicationContext().getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);

                if(activeNetworkInfo != null)
                {
                    if (activeNetworkInfo.isConnected() && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI)
                    {
                        if(mSharedPreferences.getBoolean(Constants.WIFI_SYNC, false))
                        {
                            Log.d(LOG_TAG, mSharedPreferences.getBoolean(Constants.WIFI_SYNC, false) + " false mean true");
                            mSharedPreferences.edit().putBoolean(Constants.WIFI_CONNECTED, true)
                                    .putBoolean(Constants.CONNECTIVITY, false).apply();
                        }
                        else
                        {
                            Log.d(LOG_TAG, mSharedPreferences.getBoolean(Constants.WIFI_SYNC, true) + " true is false");
                            mSharedPreferences.edit().putBoolean(Constants.DATA_USAGE, true)
                                    .putBoolean(Constants.CONNECTIVITY, false).apply();
                        }
                    }
                    else if (activeNetworkInfo.isConnected() && activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
                    {
                        mSharedPreferences.edit().putBoolean(Constants.DATA_USAGE, true)
                                .putBoolean(Constants.CONNECTIVITY, false).apply();
                    }
                }

                Intent intent = new Intent(SplashScreenActivity.this, HomeNewActivity.class);
                startActivity(intent);
                startService(new Intent(SplashScreenActivity.this, PDTService.class));
                finish();
            }
        }).start();
        Log.d(LOG_TAG, "Splash screen loaded "+ mSharedPreferences.getBoolean(Constants.DATA_USAGE, true));
    }
}
