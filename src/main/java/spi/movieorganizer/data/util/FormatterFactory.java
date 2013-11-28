/**
 * 23 févr. 2010
 *
 * Created by Tribondeau Brian.
 * Copyright 2010 Exane All rights reserved.
 */
package spi.movieorganizer.data.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import spi.movieorganizer.data.UnknownValue;

/**
 * @author Tribondeau Brian
 * 
 */
public class FormatterFactory {

    /**
     * Returns a decimal format object corresponding to the given precision+1(in order to avoid round values).
     */
    public static DecimalFormat getDecimalFormatForPrecision(int precision) {
        String pattern = "0.";
        if (precision == UnknownValue.UNKNOWN_INTEGER)
            precision = 2;
        else
            // Add on number to the precision in order to avoid round values
            precision++;
        for (int i = 0; i < precision; i++)
            pattern += "0";
        return new DecimalFormat(pattern, FormatterFactory.getDecimalFormatSymbols());
    }

    /**
     * Returns the default decimal format symbols used in the application.
     */
    public static DecimalFormatSymbols getDecimalFormatSymbols() {
        final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        decimalFormatSymbols.setGroupingSeparator(' ');
        return decimalFormatSymbols;
    }

    public static DecimalFormat getPercentFormat(final int precision) {
        String percStr = "0";
        if (precision > 0)
            percStr += ".";
        for (int precisionIndex = 0; precisionIndex < precision; precisionIndex++)
            percStr += "0";
        percStr += " %";
        return new DecimalFormat(percStr, FormatterFactory.getDecimalFormatSymbols());
    }

    // GL_SIGNIFICATIVE_NUMBER should be 5 and is always < 10 !
    private final static int           GL_SIGNIFICATIVE_NUMBER = 5;
    private final static int           GL_SIGNIFICATIVE_VALUE  = (int) Math.pow(10, FormatterFactory.GL_SIGNIFICATIVE_NUMBER);
    private final static DecimalFormat GL_DECIMAL_FORMAT       = new DecimalFormat("000");

    private final static long          TERA                    = 1000000000000L;
    private final static long          GIGA                    = 1000000000;
    private final static long          MEGA                    = 1000000;
    private final static long          KILO                    = 1000;

    public static String getGLFormula(final long value) {
        final long absValue = Math.abs(value);

        if (absValue < FormatterFactory.GL_SIGNIFICATIVE_VALUE)
            return String.valueOf(value);

        final long tValue = absValue / FormatterFactory.TERA;
        final long gValue = absValue / FormatterFactory.GIGA % 1000;
        final long mValue = absValue / FormatterFactory.MEGA % 1000;
        final long kValue = absValue / FormatterFactory.KILO % 1000;
        final long uValue = absValue % 1000;

        final StringBuilder sb = new StringBuilder(15);

        if (tValue > 0)
            sb.append(tValue + "t");
        if (gValue > 0)
            if (tValue > 0)
                sb.append(FormatterFactory.GL_DECIMAL_FORMAT.format(gValue) + "g");
            else
                sb.append(gValue + "g");
        if (mValue > 0)
            if (gValue > 0)
                sb.append(FormatterFactory.GL_DECIMAL_FORMAT.format(mValue) + "m");
            else
                sb.append(mValue + "m");
        if (kValue > 0)
            if (gValue + mValue > 0)
                sb.append(FormatterFactory.GL_DECIMAL_FORMAT.format(kValue) + "k");
            else
                sb.append(kValue + "k");

        if (tValue + kValue + gValue + mValue > 0)
            sb.append(FormatterFactory.GL_DECIMAL_FORMAT.format(uValue));
        else
            sb.append(uValue);

        String subStr = sb.substring(0, Math.min(sb.length(), FormatterFactory.GL_SIGNIFICATIVE_NUMBER));
        if (value < 0)
            subStr = "-" + subStr;

        return subStr;
    }

    public static String humanReadableByteCount(final long bytes, final boolean si) {
        final int unit = si ? 1000 : 1024;
        if (bytes < unit)
            return bytes + " B";
        final int exp = (int) (Math.log(bytes) / Math.log(unit));
        final String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String humanReadableSecondCount(final long nanos) {
        final int unit = 1000;
        if (nanos < unit)
            return nanos + " ns";
        final int exp = (int) (Math.log(nanos) / Math.log(unit));
        final String pre = String.valueOf("\u00B5m kMGTPE".charAt(exp - 1));
        return String.format("%.1f %ss", nanos / Math.pow(unit, exp), pre.trim());
    }
}
