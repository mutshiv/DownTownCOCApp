package org.downtowncoc.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.downtowncoc.R;
import org.downtowncoc.activities.FragmentsActivity;
import org.downtowncoc.activities.HomeActivity;
import org.downtowncoc.activities.HomeNewActivity;
import org.downtowncoc.database.DataBaseHelper;
import org.downtowncoc.prefs.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class PDTService extends Service
{
    private static final String LOG_TAG = PDTService.class.getSimpleName();
    private IntentFilter mIntentFilter = new IntentFilter(Constants.ACTION_BROADCAST_INCOMING_NOTIFICATION);
    private SQLiteDatabase mDatabase;
    private DataBaseHelper mDatabaaseHelper;
    private ContentValues dbInsertValues = new ContentValues();
    private String full_filename;
    private String filename;
    private Socket socket;
    private OutgoingNotification mOutgoingNotification;
    private SharedPreferences mSharedPreferences;

    public PDTService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public  void onCreate()
    {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate service");

        mDatabaaseHelper = new DataBaseHelper(this, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        mDatabase = mDatabaaseHelper.getWritableDatabase();
        mOutgoingNotification = new OutgoingNotification();
        mSharedPreferences = (getApplication()).getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);
        final String deviceID = mSharedPreferences.getString(Constants.DEVICE_ID, "T");
        Log.d(LOG_TAG, "generated IDs: " + deviceID);

       new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    Thread.sleep(1000);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mDatabase.delete(Constants.TABLE_NAME, null, null);
                mDatabase.execSQL("vacuum");

                getDatabaseValuesFromURL(Constants.VIDEO_URI, "video");
                getDatabaseValuesFromURL(Constants.AUDIO_URI, "audio");
            }
        }).start();

        try
        {
            socket = IO.socket(Constants.NOTICES_URL);
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(LOG_TAG, "Service Connected " + socket.id());
                LocalBroadcastManager.getInstance(PDTService.this).registerReceiver(mOutgoingNotification, mIntentFilter);
            }
        }).on(Socket.EVENT_CONNECTING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(LOG_TAG, "connecting");
            }
        }).on(Constants.NOTICE_MSG, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(args != null && args.length > 0)
                {
                    try
                    {
                        JSONObject jsonObject = new JSONObject(args[0].toString());

                        Log.d(LOG_TAG, "Event Title: -- " + jsonObject.getString(Constants.NOTICE_TYPE));

                        String device_id = jsonObject.getString(Constants.DEVICE_ID);

                       if(!deviceID.equals(device_id))
                       {
                           Log.d(LOG_TAG, "Device ID " + device_id + " Device ID: " + deviceID + " bool answer " + deviceID.equals(device_id));
                           NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(PDTService.this);

                           mBuilder.setSmallIcon(R.drawable.android_icon_36x36);
                           mBuilder.setContentTitle(jsonObject.getString("eventTitle"));
                           mBuilder.setContentText(jsonObject.getString("noticeMsg"));
                           mBuilder.setAutoCancel(true);

                           if(jsonObject.getString("noticeType").equals("Event Notice"))
                           {
                               Calendar cal = Calendar.getInstance();

                               Intent calIntent = new Intent(Intent.ACTION_INSERT);
                               calIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                               cal.setTime(new Date(jsonObject.getString(Constants.EVENT_DATE)));

                               calIntent.setData(CalendarContract.Events.CONTENT_URI);
                               calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.get(Calendar.DATE));
                               calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.get(Calendar.DATE) + 60 * 60 * 1000);
                               calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
                               calIntent.putExtra(CalendarContract.Events.TITLE, jsonObject.getString(Constants.EVENT_TITLE));
                               calIntent.putExtra(CalendarContract.Events.DESCRIPTION, jsonObject.getString(Constants.EVENT_MESSAGE));
                               calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, jsonObject.getString(Constants.EVENT_LOCATION));

                               Log.d(LOG_TAG, "Notice type event notice type " + mSharedPreferences.getString(Constants.DEVICE_ID, "-default-"));
                               mBuilder.addAction(R.drawable.okay, "Save Event", PendingIntent.getActivity(PDTService.this, (int)System.currentTimeMillis(), calIntent, PendingIntent.FLAG_CANCEL_CURRENT));
                           }
                           else
                           {
                               Log.d(LOG_TAG, "Notice type else other " + jsonObject.getString(Constants.NOTICE_TYPE));

                               Intent intentNotice = new Intent(PDTService.this, FragmentsActivity.class);

                               intentNotice.putExtra(Constants.ACTIVE_FRAGMENT, Constants.NOTICE_VIEW_FRAGMENT);
                               intentNotice.putExtra(Constants.EVENT_TITLE, jsonObject.getString(Constants.EVENT_TITLE));
                               intentNotice.putExtra(Constants.EVENT_MESSAGE, jsonObject.getString(Constants.EVENT_MESSAGE));
                               intentNotice.putExtra(Constants.EVENT_DATE, jsonObject.getString(Constants.EVENT_DATE));
                               intentNotice.putExtra(Constants.EVENT_LOCATION, jsonObject.getString(Constants.EVENT_LOCATION));

                               Log.d(LOG_TAG, "Intent print " + intentNotice.getIntExtra(Constants.ACTIVE_FRAGMENT, 0));
                               mBuilder.addAction(R.drawable.okay, "View", PendingIntent.getActivity(PDTService.this, (int)System.currentTimeMillis(), intentNotice, PendingIntent.FLAG_ONE_SHOT));
                           }

                           NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                           mNotificationManager.notify(1, mBuilder.build());
                       }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener(){
            @Override
            public void call(Object... args) {
                Log.d(LOG_TAG, "Service disconnected " + socket.id());
                LocalBroadcastManager.getInstance(PDTService.this).unregisterReceiver(mOutgoingNotification);
            }
        }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(LOG_TAG, "Service error " + args[0]);
            }
        });
        socket.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void getDatabaseValuesFromURL(String mediaURL, String type)
    {
        URL url;
        try
        {
            url = new URL(mediaURL);
            URLConnection conn = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;

            while ((inputLine = br.readLine()) != null)
            {
                if(type.equalsIgnoreCase("video"))
                {
                    if(inputLine.contains("href=") && inputLine.contains(".mp4"))
                    {
                        inputLine = inputLine.substring(inputLine.indexOf("href"));
                        inputLine = inputLine.substring(0, inputLine.indexOf(">"));
                        inputLine = inputLine.substring((inputLine.indexOf('"')+1), inputLine.length()-1);

                        full_filename = inputLine;
                        filename = full_filename.replace("%20", " ");

                        dbInsertValues.put(Constants.Columns.FILE_NAME, filename);
                        dbInsertValues.put(Constants.Columns.FILE_NAME_FULL, full_filename);
                        dbInsertValues.put(Constants.Columns.MEDIA_TYPE, type);

                        long id = mDatabase.insert(Constants.TABLE_NAME, null, dbInsertValues);

                        Log.d(LOG_TAG, id + " new video item inserted");
                    }
                }
                else if(type.equalsIgnoreCase("audio"))
                {
                    if(inputLine.contains("href=") && inputLine.contains(".mp3"))
                    {
                        inputLine = inputLine.substring(inputLine.indexOf("href"));
                        inputLine = inputLine.substring(0, inputLine.indexOf(">"));
                        inputLine = inputLine.substring((inputLine.indexOf('"')+1), inputLine.length()-1);

                        full_filename = inputLine;
                        filename = full_filename.replace("%20", " ");

                        dbInsertValues.put(Constants.Columns.FILE_NAME, filename);
                        dbInsertValues.put(Constants.Columns.FILE_NAME_FULL, full_filename);
                        dbInsertValues.put(Constants.Columns.MEDIA_TYPE, type);

                        long id = mDatabase.insert(Constants.TABLE_NAME, null, dbInsertValues);

                        Log.d(LOG_TAG, id + " new Audio item inserted");
                    }
                }
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private class OutgoingNotification extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            JSONObject data = new JSONObject();
            try
            {
                data.put(Constants.EVENT_TITLE, intent.getStringExtra(Constants.EVENT_TITLE));
                data.put(Constants.EVENT_MESSAGE, intent.getStringExtra(Constants.EVENT_MESSAGE));
                data.put(Constants.NOTICE_TYPE, intent.getStringExtra(Constants.NOTICE_TYPE));
                data.put(Constants.EVENT_DATE, intent.getStringExtra(Constants.EVENT_DATE));
                data.put(Constants.EVENT_LOCATION, intent.getStringExtra(Constants.EVENT_LOCATION));
                data.put(Constants.DEVICE_ID, intent.getStringExtra(Constants.DEVICE_ID));

                socket.emit(Constants.NOTICE_MSG, data);

                Log.d(LOG_TAG, "After emit " + socket.id());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(LOG_TAG, "Broadcast received " + intent.getStringExtra("noticeType"));
        }
    }
}
