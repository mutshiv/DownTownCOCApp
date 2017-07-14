package org.downtowncoc.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.downtowncoc.R;
import org.downtowncoc.activities.HomeNewActivity;
import org.downtowncoc.prefs.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vmutshinya
 */
public class NoticeViewFragment extends Fragment
{
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_message)
    TextView tv_message;
    @BindView(R.id.tv_date)
    TextView tv_date;
    @BindView(R.id.tv_location)
    TextView tv_location;
    @BindView(R.id.btnDone)
    Button btnDone;

    public NoticeViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice_view, container, false);
        ButterKnife.bind(this, view);


        Intent intent = super.getActivity().getIntent();

        tv_title.setText(intent.getStringExtra(Constants.EVENT_TITLE));
        tv_date.setText(intent.getStringExtra(Constants.EVENT_DATE));
        tv_location.setText(intent.getStringExtra(Constants.EVENT_LOCATION));
        tv_message.setText(intent.getStringExtra(Constants.EVENT_MESSAGE));

        return view;
    }

    @OnClick(R.id.btnDone)
    public void closeNoticeView(View view)
    {
        onDestroy();
       // startActivity(new Intent(super.getActivity(), HomeNewActivity.class));
    }
}
