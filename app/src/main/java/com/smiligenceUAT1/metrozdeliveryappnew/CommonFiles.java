package com.smiligenceUAT1.metrozdeliveryappnew;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.util.Date;

public class CommonFiles
{
    public static final String KEY_REQUESTING_LOCATION_UODATES = "LocationUpdateEnable";

    public static String getLocationText(Location mLocation) {

        return mLocation == null ? "Unknown Location" : new StringBuilder().append(mLocation.getLatitude()).append("/")
                .append(mLocation.getLongitude()).toString();
    }
    public static Double latitudeStringFromLocation(final Location location) {
        return Location.convert(String.valueOf(location.getLatitude()));
    }
    public static Double longtitudeStringFromLocation(final Location location) {
        return Location.convert(String.valueOf(location.getLongitude()));
    }

    public static CharSequence getLocationTitle(MyBackgroundService myBackgroundService) {
        return String.format("Location Updated", DateFormat.getDateInstance().format(new Date()));
    }

    public static void setRequestingLocationUpdates(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(KEY_REQUESTING_LOCATION_UODATES, value).apply();
    }

    public static boolean requestingLocationUpdated(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_REQUESTING_LOCATION_UODATES,false);
    }
}
