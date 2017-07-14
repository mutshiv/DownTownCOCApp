package org.downtowncoc.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import org.downtowncoc.R;
import org.downtowncoc.app.DownTownCoCApp;
import org.downtowncoc.prefs.Constants;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

public class AnnouncementsFragment extends Fragment
{
    private static final String LOG_TAG = AnnouncementsFragment.class.getSimpleName();
    private SharedPreferences mSharedPreferences;

    @BindView(R.id.etDate)
    EditText etDate;
    @BindView(R.id.etNoticeTitle)
    EditText etNoticeTitle;
    @BindView(R.id.etMessage)
    EditText etMessage;
    @BindView(R.id.spinner)
    Spinner spNoticeType;
    @BindView(R.id.btnPush)
    Button btnPush;
    @BindView(R.id.etLocation)
    EditText etLocation;

    public AnnouncementsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = super.getActivity().getPreferences(Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_announcements, container, false);
        ButterKnife.bind(this, view);

        Log.d(LOG_TAG, "onCreateView fragment");

        return view;
    }

    private DatePickerDialog.OnDateSetListener eventDateListener = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            etDate.setText(dayOfMonth + "/" + month + "/" + year);
            Log.d(LOG_TAG, "dialog event fired");
        }
    };

    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            Calendar cal = Calendar.getInstance();
            return new DatePickerDialog(super.getContext(), eventDateListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }

    @OnClick(R.id.etDate)
    public void onSetDate(View view)
    {
        Log.d(LOG_TAG, "On set et clicked");
        onCreateDialog(999).show();
    }

    @OnFocusChange(R.id.etMessage)
    public void enableButton(View view)
    {
        if(view == etMessage && etMessage.getText().length() > 10)
        {
            btnPush.setVisibility(Button.VISIBLE);
        }
    }

    @OnClick(R.id.btnPush)
    public void pushNotifications(View view)
    {
        Intent intent = new Intent(Constants.ACTION_BROADCAST_INCOMING_NOTIFICATION);
        intent.putExtra("eventTitle", etNoticeTitle.getText().toString());
        intent.putExtra("noticeMsg", etMessage.getText().toString());
        intent.putExtra("noticeType", spNoticeType.getItemAtPosition(spNoticeType.getSelectedItemPosition()).toString());
        intent.putExtra("eventDate", etDate.getText().toString());
        intent.putExtra("eventlocation", etLocation.getText().toString());
        intent.putExtra(Constants.DEVICE_ID, UUID.randomUUID().toString());

        Log.d(LOG_TAG, etMessage.getText().toString() + " ***** " +  mSharedPreferences.getString(Constants.DEVICE_ID, ""));
        LocalBroadcastManager.getInstance(super.getContext()).sendBroadcast(intent);
    }
}
