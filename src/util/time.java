package util;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class time {
    public static String convertLongTime(long time){
        Date date = new Date(time);
        date.setHours(date.getHours()-8);
        Format format = new SimpleDateFormat("EEE, dd MM yyyy HH:mm:ss 'GMT'");
        return format.format(date);
    }

    public static LocalDateTime convertLocalTime(String time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MM yyyy HH:mm:ss 'GMT'");
        LocalDateTime ldt = LocalDateTime.parse(time, formatter);
        return ldt.minusHours(8);
    }

    public static String convertString(LocalDateTime time){
        time = time.minusHours(8);
        return time.format(DateTimeFormatter.ofPattern("EEE, dd MM yyyy HH:mm:ss 'GMT'"));
    }

    public static void main(String[] args){
        System.out.println(convertString(LocalDateTime.now()));
    }
}
