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
import butterknife.OnItemClick;
import butterknife.OnItemSelected;

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
    private Uri videoUrl;
    private MediaController mMediaController;

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
                   videoUrl = Uri.parse(Constants.VIDEO_URI + mCursor.getString(mCursor.getColumnIndex(Constants.Columns.FILE_NAME_FULL)));
                   mCursor.moveToNext();
               }
           }
       }).start();

        mMediaController = new MediaController(super.getContext());
        mMediaController.setAnchorView(vSermon);

        try
        {
            Log.d(LOG_TAG, "videoUrl " + videoUrl + " context: " + VideoSermonFragment.this.getContext());
//            surfaceCreated(vidHolder);
            mediaPlayer.setDataSource(VideoSermonFragment.this.getContext(), videoUrl);
            mediaPlayer.prepare();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnPreparedListener(VideoSermonFragment.this);
        vSermon.setMediaController(mMediaController);
        vSermon.setVideoURI(videoUrl);

        return view;
    }


    @OnItemClick(R.id.lv_sermons)
    public void playSelected(final int position)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    if(!mediaPlayer.isPlaying())
                    {
                        mCursor.move(position);

                        videoUrl = Uri.parse(Constants.VIDEO_URI + mCursor.getString(mCursor.getColumnIndex(Constants.Columns.FILE_NAME_FULL)));
                       /* mediaPlayer.setDataSource(VideoSermonFragment.super.getContext(), videoUrl);
                        mediaPlayer.prepare();
                        mediaPlayer.setOnPreparedListener(VideoSermonFragment.this);*/
                        Log.d(LOG_TAG, "" + mediaPlayer.getDuration());
                        mediaPlayer.reset();
                        surfaceCreated(vidHolder);
                        mediaPlayer.start();
                        Log.d(LOG_TAG, "Auto click!!! " + mCursor.getString(mCursor.getColumnIndex(Constants.Columns.FILE_NAME_FULL)));
                    }
                    else
                    {
                        Log.d(LOG_TAG, "Media player on!!! " + mCursor.getString(mCursor.getColumnIndex(Constants.Columns.FILE_NAME_FULL)));
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mediaPlayer != null) mediaPlayer.release();
    }

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    public void onPrepared(MediaPlayer mp)
    {
        vSermon.setMediaController(mMediaController);
        vSermon.setVideoURI(videoUrl);
        try
        {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDisplay(vSermon.getHolder());
            mediaPlayer.setDataSource(VideoSermonFragment.this.getContext(), videoUrl);
//            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(this);
            //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        try
        {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDisplay(vSermon.getHolder());
            mediaPlayer.setDataSource(VideoSermonFragment.this.getContext(), videoUrl);
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
        mediaPlayer.release();
    }
}
