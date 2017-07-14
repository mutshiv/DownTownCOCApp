package org.downtowncoc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.downtowncoc.R;
import org.downtowncoc.prefs.Constants;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity
{
    private static final String LOG_TAG = HomeActivity.class.getSimpleName();
    private String[] menuItems = {"Vision & Mission", "Bible", "About"};

    @BindView(R.id.btn_Bible)
    ImageButton ibtnBible;
    @BindView(R.id.imageButton3)
    ImageButton ibtnCalendar;
    @BindView(R.id.rlCalDesign)
    RelativeLayout rlCalenderOpen;
    @BindView(R.id.tvDayOfMonth)
    TextView tvDayOfMonth;
    @BindView(R.id.tvDayOfWeek)
    TextView tvDayOfWeek;
    @BindView(R.id.btnBroadcast)
    Button btnBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        Calendar today = new GregorianCalendar();

        String dayOfMonth = String.valueOf(today.get(Calendar.DAY_OF_MONTH));
        String dayOfWeek = String.valueOf(Constants.dayOfWeek[today.get(Calendar.DAY_OF_WEEK)-1]);

        int dayofMonth = Integer.parseInt(dayOfMonth);
        dayOfMonth = (dayofMonth < 10) ? "0" + dayofMonth : dayofMonth + "";

        tvDayOfMonth.setText(dayOfMonth);
        tvDayOfWeek.setText(dayOfWeek);

        Log.d(LOG_TAG, dayOfMonth + " Day of month");
        Log.d(LOG_TAG, dayOfWeek + " Day of week");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        Intent intent;

        switch (id)
        {
            case R.id.bible_app :
               /* intent = new Intent(this, BibleActivity.class);
                startActivity(intent);*/
                Log.d(LOG_TAG, "Bible view menu item clicked");
                //startService(new Intent(this, PDTService.class));
                return true;
            case R.id.vision :
                intent = new Intent(this, VisionMissionActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_Bible)
    public void openBible(View view)
    {
        Intent intent = new Intent(this, BibleActivity.class);
        startActivity(intent);
        Log.d(LOG_TAG, "Bible button clicked");
    }

    @OnClick(R.id.btnBroadcast)
    public void sendBroadcast(View view)
    {
       /* Intent intent = new Intent(Constants.ACTION_BROADCAST_INCOMING_NOTIFICATION);
        intent.putExtra("eventTitle", "Youth outing");
        intent.putExtra("noticeMsg", "Sports and fun day");
        intent.putExtra("noticeType", "Fellowship");
        intent.putExtra("eventDate", "02-06-2017:" + new Date().getTime());
        intent.putExtra("eventlocation", "Pretoria");

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);*/

        Intent intent = new Intent(HomeActivity.this, FragmentsActivity.class);
        intent.putExtra("activeFragment", Constants.ANNOUNCEMENTS);
        startActivity(intent);
    }

    @OnClick(R.id.imageButton3)
    public void openCalendar(View view)
    {
        Log.d(LOG_TAG, "Calender button clicked");
        Intent intent = new Intent(HomeActivity.this, FragmentsActivity.class);
        intent.putExtra("activeFragment", Constants.SERMON_FRAGMENT);
        startActivity(intent);
    }

    @OnClick(R.id.rlCalDesign)
    public void calenderView(View view)
    {
        Log.d(LOG_TAG, "Calender Layout clicked");
        Intent intent = new Intent(HomeActivity.this, FragmentsActivity.class);
        intent.putExtra("activeFragment", Constants.CALENDAR_FRAGMENT);
        startActivity(intent);
    }

    @OnClick(R.id.rAudioSermon)
    public void audioSermonView(View view)
    {
        Log.d(LOG_TAG, "Audio Sermon Layout clicked");
        Intent intent = new Intent(HomeActivity.this, FragmentsActivity.class);
        intent.putExtra("activeFragment", Constants.AUDIO_SERMON_FRAGMENT);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}