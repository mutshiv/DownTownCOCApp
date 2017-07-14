package org.downtowncoc.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import org.downtowncoc.R;
import org.downtowncoc.prefs.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VisionMissionActivity extends AppCompatActivity
{
    @BindView(R.id.tvVision)
    TextView tv_vision;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_vision_mission);
        ButterKnife.bind(this);

        tv_vision.setText(Constants.VISION + "\n\n" + Constants.MISSION);
        tv_vision.setMovementMethod(new ScrollingMovementMethod());
        tv_vision.setTextSize(15);

        //ActionBar actionBar = getSupportActionBar();

       // actionBar.setIcon(R.drawable.about_us);
        //actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
