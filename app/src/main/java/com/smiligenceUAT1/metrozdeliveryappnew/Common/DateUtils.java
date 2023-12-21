package com.smiligenceUAT1.metrozdeliveryappnew.Common;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.smiligenceUAT1.metrozdeliveryappnew.Common.Constant.*;


public class DateUtils {
    static Calendar calendar = Calendar.getInstance ();

    public static String fetchCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat ( DATE_FORMAT );
        String currentDateAndTime = dateFormat.format ( new Date () );
        return currentDateAndTime;
    }

    public static String fetchCurrentDateAndTime() {
        DateFormat dateFormat = new SimpleDateFormat ( DATE_TIME_FORMAT );
        String currentDateAndTime = dateFormat.format ( new Date () );
        return currentDateAndTime;
    }

    public static String fetchCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat ( TIME_FORMAT );
        String currentDateAndTime = dateFormat.format ( new Date () );
        return currentDateAndTime;
    }


    public static String fetchDayOfTheWeek() {
        SimpleDateFormat dateFormat = new SimpleDateFormat ( "EEEE" );
        String dayOfTheWeek = dateFormat.format ( new Date () );
        return dayOfTheWeek;
    }

    public static String fetchYesterdayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat ( "yyyy-MM-dd" );
        calendar.add ( Calendar.DAY_OF_MONTH, -1 );
        String yesterdayDate = sdf.format ( calendar.getTime () );
        return yesterdayDate;
    }

    public static String fetchTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat ( "hh:mm a" );
        String time = timeFormat.format ( calendar.getTime () );
        return time;
    }

    public static String fetchDateAndTimeInMilliSecond() {


        DateFormat sdf = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss.SSS" );
        String stringDate = sdf.format ( new Date () );
        return stringDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String addDatesToDate(int noOfDatesTobeAdded) {

        LocalDate date = LocalDate.now ().plusDays ( noOfDatesTobeAdded );


        return date.toString ();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getPastDate(int noOfDatesTobeMinused) {

        LocalDate date = LocalDate.now ().plusDays ( noOfDatesTobeMinused );
        return date.toString ();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean pastDate(String inputDate) {
        Date date = null;
        try {
            date = new SimpleDateFormat ( "dd/MM/yyyy" ).parse ( inputDate );
        } catch (ParseException e) {
            e.printStackTrace ();
        }
        return new Date ().before ( date );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean ValidatePastDate(String inputDate) {

        Date date = null;
        try {
            date = new SimpleDateFormat ( "dd/MM/yyyy" ).parse ( inputDate );
        } catch (ParseException e) {
            e.printStackTrace ();
        }

        return new Date ().after ( date );
    }

    public static ArrayList<String> fetchTimeInterval() {

        int begin = 0;
        int end = 1439;
        int interval = 120;

        ArrayList<String> timeArrayList = new ArrayList<> ();

        for ( int time = begin; time <= end; time += interval ) {
            timeArrayList.add ( String.format ( "%02d:%02d", time / 60, time % 60 ) );
        }
        return timeArrayList;
    }

    public static boolean isHourInInterval(String target, String start, String end) {
        return ((target.compareTo ( start ) >= 0)
                && (target.compareTo ( end ) <= 0));
    }
}