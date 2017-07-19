package org.downtowncoc.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;

import org.downtowncoc.R;
import org.downtowncoc.customClasses.CustomCursorAdapter;
import org.downtowncoc.database.DataBaseHelper;
import org.downtowncoc.prefs.Constants;
import org.downtowncoc.services.DownloadService;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;

public class AudioSermonFragment extends Fragment implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaController.MediaPlayerControl, SurfaceHolder.Callback
{
    private static final String LOG_TAG = AudioSermonFragment.class.getSimpleName();
    private SQLiteDatabase mDatabase;
    private DataBaseHelper mDatabaaseHelper;
    private Cursor mCursor;
    private SimpleCursorAdapter mAdapter;
    private String[] from = {Constants.Columns.FILE_NAME, Constants.Columns.FILE_NAME_FULL, Constants.Columns.MEDIA_TYPE, Constants.Columns.MEDIA_ID};
    private String where = Constants.Columns.MEDIA_TYPE + "=?";
    private String[] whereArgs = {"audio"};
    private MediaPlayer mediaPlayer;
    private MediaController mediaController;
    private Uri soundUri;
    private String downloadUrl;
    //private int[] to = {android.R.id.text1};
    private int[] to = {R.id.tvListItemName};

    @BindView(R.id.lv_audio_sermon)
    ListView lv_audio_sermon;
    @BindView(R.id.lv_custom)
    View lv_custom;
    @BindView(R.id.sv_audio_sermon)
    SurfaceView sv_audio_sermon;
    @BindView(R.id.btnDownloadItem)
    Button btnDownloadItem;
    IncludedLayout includedLayout;

    public AudioSermonFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaaseHelper = new DataBaseHelper(super.getContext(), Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        mDatabase = mDatabaaseHelper.getReadableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_audio_sermon, container, false);
        View listView = inflater.inflate(R.layout.list_items_display_layout, container, false);
        includedLayout = new IncludedLayout();

        ButterKnife.bind(this, view);
        ButterKnife.bind(includedLayout, lv_custom);

        new Thread(new Runnable() {
            @Override
            public void run()
            {
                mCursor = mDatabase.query(Constants.TABLE_NAME, from, where, whereArgs, null, null, null);
                //mAdapter = new SimpleCursorAdapter(AudioSermonFragment.super.getContext(), android.R.layout.simple_expandable_list_item_1, mCursor, from, to, 0);
                mAdapter = new CustomCursorAdapter(AudioSermonFragment.super.getContext(), R.layout.list_items_display_layout, mCursor, from, to, 0);
                lv_audio_sermon.setAdapter(mAdapter);

                mediaPlayer = new MediaPlayer();
                sv_audio_sermon.setEnabled(true);

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setWakeMode(AudioSermonFragment.super.getContext(), PowerManager.PARTIAL_WAKE_LOCK);

                Button btn = includedLayout.btnDownload;
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(LOG_TAG, "Button clicked");
                    }
                });
            }
        }).start();


       /* btnDownloadItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Button clicked Exclusive!");
            }
        });
*/
        return view;
    }

    public class IncludedLayout {
        @BindView( R.id.btnDownloadItem )
        Button btnDownload;
        @BindView(R.id.tvListItemName)
        TextView tvListItem;
    }

    @OnClick(R.id.btnDownloadItem)
    public void onDownloadClick()
    {
        Log.d(LOG_TAG, "Download button clicked");
    }

    @OnItemLongClick(R.id.lv_audio_sermon)
    public boolean downloadSelectedSermon(final int position)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(isStoragePermissionGranted())
                {
                    mCursor.move(position);

                    Intent downloadIntent = new Intent(getActivity(), DownloadService.class);
                    downloadIntent.putExtra(Constants.DOWNLOAD_URL, (Constants.AUDIO_URI + mCursor.getString(mCursor.getColumnIndex(Constants.Columns.FILE_NAME_FULL))));
                    downloadIntent.putExtra(Constants.DOWNLOAD_FILENAME, mCursor.getString(mCursor.getColumnIndex(Constants.Columns.FILE_NAME_FULL)));
                    getActivity().startService(downloadIntent);
                    Log.d(LOG_TAG, "Download " + position + " - " + (Constants.AUDIO_URI + mCursor.getString(mCursor.getColumnIndex(Constants.Columns.FILE_NAME_FULL))));
                }
            }
        }).start();
        return true;
    }

    @OnItemClick(R.id.lv_audio_sermon)
    public void playSelectedSermon(final int position)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    if(!mediaPlayer.isPlaying())
                    {
                        mCursor.move(position);

                        soundUri = Uri.parse(Constants.AUDIO_URI + mCursor.getString(mCursor.getColumnIndex(Constants.Columns.FILE_NAME_FULL)));
                        mediaPlayer.setDataSource(AudioSermonFragment.super.getContext(), soundUri);
                        mediaPlayer.setOnPreparedListener(AudioSermonFragment.this);
                        mediaPlayer.prepare();
                        mediaPlayer.start();

                        Log.d(LOG_TAG, "Auto click!!! " + mCursor.getString(mCursor.getColumnIndex(Constants.Columns.FILE_NAME_FULL)));
                        downloadUrl =  mCursor.getString(mCursor.getColumnIndex(Constants.Columns.FILE_NAME_FULL));
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23)
        {
           if(ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                   == PackageManager.PERMISSION_GRANTED)
            {
                return true;
            }
            else
           {
               ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
               return true;
           }
        }
        else
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mDatabase.close();
        mDatabaaseHelper.close();
        super.onDetach();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaController = new MediaController(AudioSermonFragment.this.getContext());
        mediaController.setAnchorView(sv_audio_sermon);
        mediaController.setMediaPlayer(this);
//        mediaController.show(0);
    }

    @Override
    public void start() {
        mediaPlayer.start();
        mediaController.show(0);
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 1;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaPlayer.setDisplay(sv_audio_sermon.getHolder());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mediaPlayer.release();
        mediaPlayer.reset();
    }
}
