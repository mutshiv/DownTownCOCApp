package org.downtowncoc.prefs;

import android.provider.BaseColumns;

/**
 * Created by vmutshinya on 5/27/2017.
 * ${PACKAGE_NAME}
 */

public class Constants
{
    public static final String VISION = "VISION\n\nA Vibrant Community Of Worshipers\n" +
            " The vision of the church has been summarized using key and common words from a long list of vision statements that were suggested. The following are some of the supporting views to our vision:\n" +
            "A fortress center that meets the physical and spiritual needs of the community. " +
            "A quality driven church that promotes discipleship, unity, saves souls, diversity and purity. " +
            "A spiritual vibrant church that supports all. " +
            "A place where vibrancy, sense of spirituality and unity is a key to success. " +
            "A vibrant family orientated church and a fountain of hope for all. " +
            "Growing spiritually as a unified church and become a better home for all in Tshwane";

    public static final String MISSION = "\nMISSION \n\nIn order to guide the church towards the vision, the following guiding principles will be adopted and will be incorporated in the mission statement:\n" +
            "Winning the souls through empowering, hospitality and upliftment for Christ. " +
            "To embark on intense biblical teaching that leads to true spiritual freedom for all " +
            "To provide physical and spiritual needs for backsliders and people in the church. " +
            "Effectively implementing programs and ministries for the glory of Christ";

    public static final String[] dayOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    public static final String NOTICE_SENDING_CODE = "PDTR0m16_16M@t28";

    public static final int CALENDAR_FRAGMENT = 1;
    public static final int SERMON_FRAGMENT = 2;
    public static final int AUDIO_SERMON_FRAGMENT = 3;
    public static final int ANNOUNCEMENTS = 4;
    public static final int NOTICE_VIEW_FRAGMENT = 5;

    public static final String ACTIVE_FRAGMENT = "activeFragment";
    public static final String VIDEO_URI = "http://www.pdtcoc.co.za/joomla30/sermon_video/";
    public static final String AUDIO_URI = "http://www.pdtcoc.co.za/joomla30/sermon_audio/";

    public static final String DATABASE_NAME = "pdtcoc.db";
    public static final String TABLE_NAME = "MediaDB";
    //public static final String NOTICES_URL = "http://10.2.0.17:3000/";
    //public static final String NOTICES_URL = "http://192.168.1.103:3000/";
    public static final String NOTICES_URL = "https://guarded-retreat-35646.herokuapp.com";
    public static final String NOTICE_MSG = "noticemsg";
    public static final String ACTION_BROADCAST_INCOMING_NOTIFICATION = "org.downtowncoc.ACTION_BROADCAST_INCOMING_NOTIFICATION";
    public static final String DEVICE_ID = "deviceID";
    public static final String MY_PREF = "MyPrefs";
    public static final String EVENT_TITLE = "eventTitle";
    public static final String EVENT_MESSAGE = "noticeMsg";
    public static final String NOTICE_TYPE = "noticeType";
    public static final String EVENT_DATE = "eventDate";
    public static final String EVENT_LOCATION = "eventlocation";

    public static final String WIFI_SYNC = "use_wifi";
    public static final String DATA_USAGE = "data_usage";
    public static final String WIFI_CONNECTED = "wifi_connected";

    public static int DATABASE_VERSION = 1;

    public static class Columns
    {
        public static final String MEDIA_ID = BaseColumns._ID;
        public static final String FILE_NAME = "filename";
        public static final String FILE_NAME_FULL = "full_filename";
        public static final String MEDIA_TYPE = "media_type";

    }
}
