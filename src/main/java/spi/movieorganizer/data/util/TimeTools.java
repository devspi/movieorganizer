package spi.movieorganizer.data.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TimeTools {

    public static final String             yyyy_PATTERN                 = "yyyy";
    public static final String             dd_MM_yyyy_PATTERN           = "dd/MM/yyyy";
    public static final String             yyyy_MM_dd_PATTERN           = "yyyy/MM/dd";
    public static final String             yyyyMMdd_PATTERN             = "yyyyMMdd";
    public static final String             ddMMyyyy_PATTERN             = "ddMMyyyy";
    public static final String             HHmmss_PATTERN               = "HHmmss";
    public static final String             HH_mm_ss_PATTERN             = "HH:mm:ss";
    public static final String             HH_mm_ss_SSS_PATTERN         = "HH:mm:ss.SSS";
    public static final String             yyyyMMddHHmmss_PATTERN       = TimeTools.yyyyMMdd_PATTERN + TimeTools.HHmmss_PATTERN;
    public static final String             yyyyMMddHH_mm_ss_SSS_PATTERN = TimeTools.yyyyMMdd_PATTERN + TimeTools.HH_mm_ss_SSS_PATTERN;
    public static final String             dd_MM_yyyy_HH_mm_ss_PATTERN  = TimeTools.dd_MM_yyyy_PATTERN + " " + TimeTools.HH_mm_ss_PATTERN;

    static private Map<String, DateFormat> dateFormatsMap               = new HashMap<>();

    static {
        TimeTools.dateFormatsMap.put(TimeTools.dd_MM_yyyy_PATTERN, new SimpleDateFormat(TimeTools.dd_MM_yyyy_PATTERN));
        TimeTools.dateFormatsMap.put(TimeTools.yyyy_MM_dd_PATTERN, new SimpleDateFormat(TimeTools.yyyy_MM_dd_PATTERN));
        TimeTools.dateFormatsMap.put(TimeTools.yyyyMMdd_PATTERN, new SimpleDateFormat(TimeTools.yyyyMMdd_PATTERN));
        TimeTools.dateFormatsMap.put(TimeTools.ddMMyyyy_PATTERN, new SimpleDateFormat(TimeTools.ddMMyyyy_PATTERN));
        TimeTools.dateFormatsMap.put(TimeTools.HHmmss_PATTERN, new SimpleDateFormat(TimeTools.HHmmss_PATTERN));
        TimeTools.dateFormatsMap.put(TimeTools.HH_mm_ss_PATTERN, new SimpleDateFormat(TimeTools.HH_mm_ss_PATTERN));
        TimeTools.dateFormatsMap.put(TimeTools.HH_mm_ss_SSS_PATTERN, new SimpleDateFormat(TimeTools.HH_mm_ss_SSS_PATTERN));
        TimeTools.dateFormatsMap.put(TimeTools.yyyyMMddHHmmss_PATTERN, new SimpleDateFormat(TimeTools.yyyyMMddHHmmss_PATTERN));
        TimeTools.dateFormatsMap.put(TimeTools.yyyyMMddHH_mm_ss_SSS_PATTERN, new SimpleDateFormat(TimeTools.yyyyMMddHH_mm_ss_SSS_PATTERN));
        TimeTools.dateFormatsMap.put(TimeTools.dd_MM_yyyy_HH_mm_ss_PATTERN, new SimpleDateFormat(TimeTools.dd_MM_yyyy_HH_mm_ss_PATTERN));
        TimeTools.dateFormatsMap.put(TimeTools.yyyy_PATTERN, new SimpleDateFormat(TimeTools.yyyy_PATTERN));
    }

    static public DateFormat getDateFormat(final String pattern) {
        return TimeTools.dateFormatsMap.get(pattern);
    }

    static public String format(final String pattern, final Date date) {
        if (date == null)
            return null;
        DateFormat format = TimeTools.dateFormatsMap.get(pattern);
        if (format == null)
            format = new SimpleDateFormat(pattern);
        String result = null;
        synchronized (format) {
            result = format.format(date);
        }
        return result;
    }

    static public Date parse(final String pattern, final String source) {
        DateFormat format = TimeTools.dateFormatsMap.get(pattern);
        if (format == null)
            format = new SimpleDateFormat(pattern);
        Date result = null;
        synchronized (format) {
            try {
                result = format.parse(source);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public static Calendar rollToMonthAndClearTime(final int rollValue, final boolean up) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, up ? rollValue : -rollValue);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }
}
