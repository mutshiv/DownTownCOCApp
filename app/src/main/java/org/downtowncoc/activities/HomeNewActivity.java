package org.downtowncoc.activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.downtowncoc.R;
import org.downtowncoc.prefs.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemLongClick;
import butterknife.OnLongClick;

public class HomeNewActivity extends AppCompatActivity {

    private static final String LOG_TAG = HomeNewActivity.class.getSimpleName();

    @BindView(R.id.aboutUs)
    LinearLayout aboutUs;
    @BindView(R.id.videos)
    LinearLayout videos;
    @BindView(R.id.audio)
    LinearLayout audio;
    @BindView(R.id.events)
    LinearLayout events;
    @BindView(R.id.bible)
    LinearLayout bible;
    @BindView(R.id.lv_coming_event)
    ListView lv_coming_events;
    @BindView(R.id.imgHome)
    ImageView imgHome;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        ButterKnife.bind(this);

        viewEvents(new CalendarView(this));
    }

    @OnClick(R.id.aboutUs)
    public void aboutUsPage(View view)
    {
        intent = new Intent(this, VisionMissionActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.videos)
    public void videosPage(View view)
    {
        intent = new Intent(this, FragmentsActivity.class);
        intent.putExtra(Constants.ACTIVE_FRAGMENT, Constants.SERMON_FRAGMENT);
        startActivity(intent);
    }

    @OnClick(R.id.audio)
    public void audioPage(View view)
    {
        intent = new Intent(this, FragmentsActivity.class);
        intent.putExtra(Constants.ACTIVE_FRAGMENT, Constants.AUDIO_SERMON_FRAGMENT);
        startActivity(intent);
    }

    @OnClick(R.id.events)
    public void eventsPage(View view)
    {
        intent = new Intent(this, FragmentsActivity.class);
        intent.putExtra(Constants.ACTIVE_FRAGMENT, Constants.CALENDAR_FRAGMENT);
        startActivity(intent);
    }

    @OnClick(R.id.bible)
    public void biblePage(View view)
    {
        intent = new Intent(this, BibleActivity.class);
        startActivity(intent);
    }

    @OnLongClick(R.id.imgHome)
    public boolean pushNotification(View view)
    {
        Intent intent = new Intent(this, FragmentsActivity.class);
        intent.putExtra(Constants.ACTIVE_FRAGMENT, Constants.ANNOUNCEMENTS);
        startActivity(intent);
        return true;
    }

    private void viewEvents(CalendarView view) {
        ArrayList<String> eventsList = new ArrayList<>();

        Cursor mCursor = null;
        final String[] projection = new String[]{CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND};
        ContentResolver cr = this.getContentResolver();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, PackageManager.PERMISSION_GRANTED);
            return;
        }
        mCursor = cr.query(CalendarContract.Events.CONTENT_URI, projection, null, null, null);

        while(mCursor.moveToNext())
        {
            if(processDate(mCursor.getLong(1), view))
            {
                GregorianCalendar gCalendar = new GregorianCalendar();
                gCalendar.setTime(new Date(mCursor.getLong(1)));
                eventsList.add(mCursor.getString(0) + "\n" + gCalendar.get(Calendar.DATE) + "-" +  (gCalendar.get(Calendar.MONTH)+1) + "-" +  gCalendar.get(Calendar.YEAR));
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventsList);
        lv_coming_events.setAdapter(adapter);
    }

    private boolean processDate(long dateTime, CalendarView calView)
    {
        Date date = new Date(dateTime);
        GregorianCalendar gCalender = new GregorianCalendar();
        gCalender.setTime(date);

        int eventMonth = gCalender.get(Calendar.MONTH);
        int eventYear = gCalender.get(Calendar.YEAR);

        gCalender.setTime(new Date(calView.getDate()));
        int calendarMonth = gCalender.get(Calendar.MONTH);
        int calendarYear = gCalender.get(Calendar.YEAR);

        if(eventMonth == calendarMonth && eventYear == calendarYear)
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_preferences, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        Intent intent;

        switch (id)
        {
            case R.id.action_settings :
                intent = new Intent(this, PreferencesActivity.class);
                startActivity(intent);
                Log.d(LOG_TAG, "Bible view menu item clicked");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
