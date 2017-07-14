package org.downtowncoc.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private static final String LOG_TAG = DownTownCoCApp.class.getSimpleName();
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mSharedPreferences = (getApplication()).getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);
        String deviceGUID = UUID.randomUUID().toString();

        Log.d(LOG_TAG, deviceGUID + " hello!!!");

        if(mSharedPreferences.getString(Constants.DEVICE_ID, "new").equals("new"))
        {
            mSharedPreferences.edit().putString(Constants.DEVICE_ID, deviceGUID).commit();
            mSharedPreferences.edit().putBoolean(Constants.WIFI_SYNC, false).commit();
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

                Intent intent = new Intent(SplashScreenActivity.this, HomeNewActivity.class);
                startActivity(intent);
                startService(new Intent(SplashScreenActivity.this, PDTService.class));
                finish();
            }
        }).start();
        Log.d(LOG_TAG, "Splash screen loaded");
    }
}
