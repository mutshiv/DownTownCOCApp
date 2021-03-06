package org.downtowncoc.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.downtowncoc.R;
import org.downtowncoc.app.DownTownCoCApp;
import org.downtowncoc.prefs.Constants;

public class PreferencesActivity extends AppCompatActivity {

    private static final String LOG_TAG = PreferencesActivity.class.getSimpleName();
    private static SharedPreferences mSharedPreferences;
    private ConnectivityManager connManager;
    private NetworkInfo activeNetworkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        mSharedPreferences = (getApplication()).getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);
        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connManager.getActiveNetworkInfo();

        getFragmentManager().beginTransaction().replace(R.id.prefs_content, new PrefsFragment()).commit();

        //getSupportActionBar().setTitle("Settings");
    }

    public static class PrefsFragment extends PreferenceFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);


            final SwitchPreference useWifi = ((SwitchPreference) findPreference("over_wifi"));

            useWifi.setSwitchTextOn("Playback over Wi-fi");
            useWifi.setSwitchTextOn("Playback over Data");

            useWifi.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (preference.isEnabled() && newValue.equals(true)) {
                        preference.setTitle("Stream over Wi-fi");
                        mSharedPreferences.edit().putBoolean(Constants.WIFI_SYNC, true)
                                                .putBoolean(Constants.DATA_USAGE, false).apply();

                        Log.d(LOG_TAG, mSharedPreferences.getBoolean(Constants.WIFI_SYNC, preference.isEnabled()) + " Use wi-fi data *" +
                                mSharedPreferences.getBoolean(Constants.WIFI_CONNECTED, (Boolean) newValue) +
                                "* wi-fi data on? " + mSharedPreferences.getBoolean(Constants.DATA_USAGE, preference.isEnabled()));
                        return true;
                    }
                    else if (preference.isEnabled() && newValue.equals(false)) {
                        preference.setTitle("Stream using Mobile Data");
                        mSharedPreferences.edit().putBoolean(Constants.WIFI_SYNC, false)
                                                 .putBoolean(Constants.DATA_USAGE, true)
                                                 .putBoolean(Constants.WIFI_CONNECTED, false).apply();
                        Log.d(LOG_TAG, mSharedPreferences.getBoolean(Constants.WIFI_SYNC, preference.isEnabled()) + " Use Mobile data *" +
                                mSharedPreferences.getBoolean(Constants.WIFI_CONNECTED, (Boolean) newValue) +
                                "* mobile data on? " + mSharedPreferences.getBoolean(Constants.DATA_USAGE, preference.isEnabled()));
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}