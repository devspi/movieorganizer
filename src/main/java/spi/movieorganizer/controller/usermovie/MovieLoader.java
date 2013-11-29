package spi.movieorganizer.controller.usermovie;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import spi.movieorganizer.data.movie.LoadMovie;
import spi.movieorganizer.data.movie.UserMovieSettings.MovieFormat;

public class MovieLoader {

    private static String qualityRegexp;
    private static String yearRegexp;
    private static String collectionRegexp = "(saga|the complete|complete|dualogy|trilogy|quadrilogy|pentalogy|collection?)";

    static {
        StringBuilder sb = new StringBuilder("(");
        final int actualYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = 1970; year <= actualYear; year++)
            sb.append(String.valueOf(year) + (year + 1 <= actualYear ? "|" : ""));
        sb.append("?)");
        MovieLoader.yearRegexp = sb.toString();

        sb = new StringBuilder("(");
        for (final MovieFormat movieQuality : MovieFormat.values())
            if (MovieFormat.UNKNOWN.equals(movieQuality) == false)
                sb.append(movieQuality.getLabel() + (movieQuality.ordinal() + 1 <= MovieFormat.values().length - 2 ? "|" : "]"));
        sb.append("?)");
        MovieLoader.qualityRegexp = sb.toString();

    }

    public static LoadMovie getLoadMovie(final String fileName) {
        final String clearFileName = fileName.replaceAll("[\\-\\_\\.]+", " ").replaceAll("[\\(\\)\\[\\]]+", "");

        String name = null;
        String quality = null;
        String year = null;
        boolean collection = false;

        int lowerIndex = -1;
        Pattern pattern = Pattern.compile(MovieLoader.yearRegexp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(clearFileName);
        if (matcher.find())
            if (matcher.group() != null) {
                year = matcher.group().trim();
                lowerIndex = matcher.start();
            }
        pattern = Pattern.compile(MovieLoader.qualityRegexp, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(clearFileName);
        if (matcher.find())
            if (matcher.group() != null) {
                quality = matcher.group().trim();
                if (lowerIndex == -1 || matcher.start() < lowerIndex)
                    lowerIndex = matcher.start();
            }

        pattern = Pattern.compile(MovieLoader.collectionRegexp, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(clearFileName);
        if (matcher.find())
            if (matcher.group() != null) {
                collection = true;
                if (lowerIndex == -1 || matcher.start() < lowerIndex)
                    lowerIndex = matcher.start();
            }

        if (lowerIndex != -1)
            name = clearFileName.substring(0, lowerIndex).trim();
        else
            name = clearFileName;

        // System.out.println("movieName=" + clearFileName);
        // System.out.println("name=" + name);
        // System.out.println("year=" + year);
        // System.out.println("quality=" + quality);
        // System.out.println("collection=" + collection);

        return new LoadMovie(fileName, name, quality, year, collection);
    }
}
