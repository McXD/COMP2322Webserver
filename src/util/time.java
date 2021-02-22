package util;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class time {
    public static String convertLongTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("EEE, dd MM yyyy HH:mm:ss 'GMT'");
        return format.format(date);
    }

    public static LocalDateTime convertLocalTime(String time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MM yyyy HH:mm:ss 'GMT'");
        LocalDateTime ldt = LocalDateTime.parse(time, formatter);
        return ldt;
    }

    public static String convertString(LocalDateTime time){
        return time.format(DateTimeFormatter.ofPattern("EEE, dd MM yyyy HH:mm:ss 'GMT'"));
    }

    public static void main(String[] args){
        System.out.println(convertString(LocalDateTime.now()));
    }
}
