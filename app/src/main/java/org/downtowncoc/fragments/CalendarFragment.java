package org.downtowncoc.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;

import org.downtowncoc.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CalendarFragment extends Fragment {
    private static final String LOG_TAG = CalendarFragment.class.getSimpleName();
    //private dayOfMonth
    @BindView(R.id.btn_add_event)
    Button btn_add_event;
    @BindView(R.id.lv_events)
    ListView lv_events;

    CalendarView cal;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        ButterKnife.bind(this, view);

        Log.d(LOG_TAG, "onCreateView");
        cal = (CalendarView) view.findViewById(R.id.calendarView);

        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, dayOfMonth);

                viewEvents(cal.getTime());
            }
        });
        return view;
    }

    @OnClick(R.id.btn_add_event)
    public void addEvent(View view) {
        Intent calIntent = new Intent(Intent.ACTION_INSERT);

        calIntent.setData(CalendarContract.Events.CONTENT_URI);
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getDate());
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.getDate() + 60 * 60 * 1000);
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
        calIntent.putExtra(CalendarContract.Events.TITLE, "Event add test");
        calIntent.putExtra(CalendarContract.Events.DESCRIPTION, "This is a sample description");
        calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "My Guest House");
       // startActivity(calIntent);
    }

    private void viewEvents(Date calDate) {
        ArrayList<String> eventsList = new ArrayList<>();

        Cursor mCursor;
        final String[] projection = new String[]{CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND};
        ContentResolver cr = getContext().getContentResolver();

        if (ActivityCompat.checkSelfPermission(super.getContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, PackageManager.PERMISSION_GRANTED);
            return;
        }
        mCursor = cr.query(CalendarContract.Events.CONTENT_URI, projection, null, null, null);

        if(mCursor != null)
        {
            while(mCursor.moveToNext())
            {
                if(processDate(mCursor.getLong(1), calDate))
                {
                    GregorianCalendar gCalendar = new GregorianCalendar();
                    gCalendar.setTime(new Date(mCursor.getLong(1)));
                    eventsList.add(mCursor.getString(0) + "\n" + gCalendar.get(Calendar.DATE) + "-" +  (gCalendar.get(Calendar.MONTH)+1) + "-" +  gCalendar.get(Calendar.YEAR));
                }
            }
        }
        mCursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(super.getContext(), android.R.layout.simple_list_item_1, eventsList);
        lv_events.setAdapter(adapter);
    }

    private boolean processDate(long dateTime, Date calDate)
    {
        Date date = new Date(dateTime);
        GregorianCalendar gCalender = new GregorianCalendar();
        gCalender.setTime(date);

        int eventMonth = gCalender.get(Calendar.MONTH);
        int eventYear = gCalender.get(Calendar.YEAR);

        GregorianCalendar gCalenderView = new GregorianCalendar();
        gCalenderView.setTime(calDate);
        int calendarMonth = gCalenderView.get(Calendar.MONTH);
        int calendarYear = gCalenderView.get(Calendar.YEAR);

        return ((eventMonth == calendarMonth) && (eventYear == calendarYear));
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
