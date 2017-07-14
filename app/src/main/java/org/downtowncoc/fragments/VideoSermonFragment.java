package org.downtowncoc.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.VideoView;

import org.downtowncoc.R;
import org.downtowncoc.database.DataBaseHelper;
import org.downtowncoc.prefs.Constants;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoSermonFragment extends Fragment implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener
{
    private static final String LOG_TAG = VideoSermonFragment.class.getSimpleName();
    private SQLiteDatabase mDatabase;
    private DataBaseHelper mDatabaaseHelper;
    private Cursor mCursor;
    private SimpleCursorAdapter mAdapter;
    private String[] from = {Constants.Columns.FILE_NAME, Constants.Columns.FILE_NAME_FULL, Constants.Columns.MEDIA_TYPE, Constants.Columns.MEDIA_ID};
    private String where = Constants.Columns.MEDIA_TYPE + "=?";
    private String[] whereArgs = {"video"};
    @BindView(R.id.lv_sermons)
    ListView lv_sermons;
    @BindView(R.id.vSermon)
    VideoView vSermon;

    private MediaPlayer mediaPlayer;
    private SurfaceHolder vidHolder;
    private int[] to = {android.R.id.text1};
   /* @BindView(R.id.svSermon)
    SurfaceView vidSurface;*/

    public VideoSermonFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mDatabaaseHelper = new DataBaseHelper(super.getContext(), Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        mDatabase = mDatabaaseHelper.getReadableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_video_sermon, container, false);
        ButterKnife.bind(this, view);

        String videoUri = Constants.VIDEO_URI + "Rebecca%20Malope%20ft%20Sechaba%20Lona%20Baratang.mp4";
        Uri vidUri = Uri.parse(videoUri);

       new Thread(new Runnable() {
           @Override
           public void run()
           {
               mCursor = mDatabase.query(Constants.TABLE_NAME, from, where, whereArgs, null, null, null);
               mAdapter = new SimpleCursorAdapter(VideoSermonFragment.super.getContext(), android.R.layout.simple_expandable_list_item_1, mCursor, from, to, 0);
               lv_sermons.setAdapter(mAdapter);

               mCursor.moveToFirst();
               while(!mCursor.isAfterLast())
               {
                   Log.d(LOG_TAG, ""+ mCursor.getString(mCursor.getColumnIndex(Constants.Columns.FILE_NAME_FULL)));
                   mCursor.moveToNext();
               }
           }
       }).start();


        Log.d(LOG_TAG, videoUri + " URL");
        MediaController vidControl = new MediaController(super.getContext());
        vidControl.setAnchorView(vSermon);

        vSermon.setMediaController(vidControl);
        vSermon.setVideoURI(vidUri);

 /*       try
        {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDisplay(vSermon.getHolder());
            mediaPlayer.setDataSource(Constants.VIDEO_URI);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(this);

            vSermon.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }*/

       /* vidHolder = vidSurface.getHolder();
        vidHolder.addCallback(this);*/

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mediaPlayer != null) mediaPlayer.release();
    }

    @Override
    public void onPrepared(MediaPlayer mp)
    {
       // mediaPlayer.start();
        vSermon.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        try
        {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDisplay(vSermon.getHolder());
            mediaPlayer.setDataSource(Constants.VIDEO_URI);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(this);
            //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
