package org.downtowncoc.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DownloadService extends IntentService
{
    private static final String LOG_TAG = DownloadService.class.getSimpleName();

    int total = 0;
    int count;
    int totalSize;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try
        {
            NotificationManager mNotifyManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(DownloadService.this);

            mBuilder.setSmallIcon(android.R.drawable.ic_popup_sync)
                    .setContentTitle("Media Download")
                    .setContentText("Download in progress...");

            File cacheDir = new File(getCacheDir().getAbsolutePath(), "Sermons");

            if (!cacheDir.exists()){
                cacheDir.mkdir();}

            File f = new File(getCacheDir().getAbsolutePath(), intent.getStringExtra("filename"));
            URL url = new URL(intent.getStringExtra("download_url"));
            // URLConnection connection = url.openConnection();

            final HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
            c.setRequestMethod("GET");//Set Request Method to "GET" since we are getting data
            c.connect();//connect the URL Connection
            Log.d(LOG_TAG, getCacheDir().getAbsolutePath() + " connection content");
            Log.d(LOG_TAG, "connection length " + c.getContentLength());

            if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(LOG_TAG, "Server returned HTTP " + c.getResponseCode()
                        + " " + c.getResponseMessage());
            }

            //buffer size , 8192
            InputStream input = new BufferedInputStream(c.getInputStream());
            OutputStream output = new FileOutputStream(f);

            byte data[] = new byte[1024];
            totalSize = c.getContentLength();
            float per;

            while ((count = input.read(data)) != -1)
            {
                total+= count;
                output.write(data, 0, count);
                per = ((float)total/totalSize) * 100;
                Log.d(LOG_TAG, "Downloaded " + total + "KB / " + totalSize + "KB (" + (int)per + "%)");

                mBuilder.setProgress(100, (int)per, false);
                mNotifyManager.notify(1, mBuilder.build());
            }

            mBuilder.setContentText("Download complete")
                    .setProgress(0,0,false);
            mNotifyManager.notify(1, mBuilder.build());

            output.flush();
            output.close();
            input.close();
            Log.d(LOG_TAG, "Downloaded " + totalSize + "Kb");

        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage() + " Error");
        }
    }
}
