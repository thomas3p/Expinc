package com.basic.project;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Time;

public class TimeConversionUtil {

    public static Time convertStringToTime(String timeString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        java.util.Date date = sdf.parse(timeString);
        long timeInMillis = date.getTime();
        return new Time(timeInMillis);
    }

}
