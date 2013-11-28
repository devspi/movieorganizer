package spi.movieorganizer.data;

import java.util.Date;

/**
 * The definition of
 * 
 * @author zigah_d
 * 
 */
public class UnknownValue {
    private UnknownValue() {
        // Contains only constants
    }

    public final static int    UNKNOWN_INTEGER   = 0x7FFFFFFF;
    public final static int    UNKNOWN_SHORT     = 0x7FFF;
    public final static int    UNKNOWN_USHORT    = 0xFFFF;
    public final static char   UNKNOWN_CHAR      = 0x7F;
    public final static short  UNKNOWN_UCHAR     = 0xFF;
    public final static double UNKNOWN_DOUBLE    = 9.999999999E9;
    public final static double UNKNOWN_PERCENT   = 9.999999999E7;
    public final static Date   UNKNOWN_TIME      = new Date(UnknownValue.UNKNOWN_INTEGER * 1000L);
    public final static String UNKNOWN_DATE      = "19700101";
    public final static String UNKNOWN_STRING    = "{";

    public final static String NA_STRING         = "N/A";

    public final static int    UNCHANGED_INTEGER = (UnknownValue.UNKNOWN_INTEGER - 1);
    public final static int    UNCHANGED_SHORT   = (UnknownValue.UNKNOWN_SHORT - 1);
    public final static int    UNCHANGED_USHORT  = (UnknownValue.UNKNOWN_USHORT - 1);
    public final static char   UNCHANGED_CHAR    = (UnknownValue.UNKNOWN_CHAR - 1);
    public final static short  UNCHANGED_UCHAR   = (UnknownValue.UNKNOWN_UCHAR - 1);
    public final static double UNCHANGED_DOUBLE  = (UnknownValue.UNKNOWN_DOUBLE - 1);
    public final static String UNCHANGED_STRING  = "}";

    public final static int    RESET_INT         = 2147483646;
    public final static double RESET_DOUBLE      = 9999999990.0;

    public static final boolean isUnknown(final String value) {
        return UnknownValue.UNKNOWN_STRING.equals(value);
    }

    public static final boolean isUnchanged(final String value) {
        return UnknownValue.UNCHANGED_STRING.equals(value);
    }

    /**
     * This functions converts a double into a string, if value is equal to <code>UNKNOWN_DOUBLE</code> the returned string is empty.
     */
    public static String doubleToString(final double value) {
        return (value == UnknownValue.UNKNOWN_DOUBLE) ? "" : Double.toString(value);
    }

    /**
     * This functions converts a int into a string, if value is equal to <code>UNKNOWN_INTEGER</code> the returned string is empty.
     */
    public static String intToString(final int value) {
        return (value == UnknownValue.UNKNOWN_INTEGER) ? "" : Integer.toString(value);
    }

    /**
     * This functions returns "N/A" for unknown strings.
     */
    public static String stringToString(final String value) {
        return (UnknownValue.UNKNOWN_STRING.equals(value) ? UnknownValue.NA_STRING : value);
    }

    /**
     * This functions returns <code>null</code> for unknown strings.
     */
    public static String stringToNullString(final String value) {
        return (UnknownValue.UNKNOWN_STRING.equals(value) ? null : value);
    }

    /**
     * This functions returns an empty string for unknown strings.
     */
    public static String stringToEmptyString(final String value) {
        return (UnknownValue.UNKNOWN_STRING.equals(value) ? "" : value);
    }
}
