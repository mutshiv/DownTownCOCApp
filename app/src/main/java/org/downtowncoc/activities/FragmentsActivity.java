package org.downtowncoc.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;

import org.downtowncoc.R;
import org.downtowncoc.fragments.AnnouncementsFragment;
import org.downtowncoc.fragments.AudioSermonFragment;
import org.downtowncoc.fragments.CalendarFragment;
import org.downtowncoc.fragments.NoticeViewFragment;
import org.downtowncoc.fragments.VideoSermonFragment;
import org.downtowncoc.prefs.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentsActivity extends AppCompatActivity
{
    private static final String LOG_TAG = FragmentsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);
        ButterKnife.bind(this);

        Log.d(LOG_TAG, "OnCreate fragment activity");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if(findViewById(R.id.fragments_container) != null)
        {
            if(savedInstanceState != null)
            {
                return;
            }
        }

        int activeFragment = getIntent().getIntExtra(Constants.ACTIVE_FRAGMENT, 1);
        Fragment currentFragment = null;

        switch (activeFragment)
        {
            case 1 :
                currentFragment = new CalendarFragment();
                actionBar.setTitle("Events");
                break;
            case 2 :
                currentFragment = new VideoSermonFragment();
                actionBar.setTitle("Videos");
                break;
            case 3 :
                currentFragment = new AudioSermonFragment();
                actionBar.setTitle("Audio");
                break;
            case 4 :
                currentFragment = new AnnouncementsFragment();
                actionBar.setTitle("Announcements");
                break;
            case 5 :
                currentFragment = new NoticeViewFragment();
                actionBar.setTitle("View Notification");
                Log.d(LOG_TAG, "inside fragment activity");
                break;
            default :
                Log.d(LOG_TAG, "inside fragment activity");
        }

        FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragments_container, currentFragment);
        fragmentTransaction.commit();
    }
}
