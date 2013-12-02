package spi.movieorganizer.controller.usermovie;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import spi.movieorganizer.data.movie.LoadedMovieData;
import spi.movieorganizer.data.movie.UserMovieSettings.MovieFormat;
import spi.movieorganizer.data.movie.UserMovieSettings.MovieResolution;

public class MovieLoader {

    private static String resolutionRegexp;
    private static String formatRegexp;
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
        for (final MovieFormat movieFormat : MovieFormat.values())
            if (MovieFormat.UNKNOWN.equals(movieFormat) == false)
                sb.append(movieFormat.getLabel() + (movieFormat.ordinal() + 1 <= MovieFormat.values().length - 2 ? "|" : "]"));
        sb.append("?)");
        MovieLoader.formatRegexp = sb.toString();

        sb = new StringBuilder("(");
        for (final MovieResolution movieResolution : MovieResolution.values())
            if (MovieResolution.UNKNOWN.equals(movieResolution) == false)
                sb.append(movieResolution.getLabel() + (movieResolution.ordinal() + 1 <= MovieResolution.values().length - 2 ? "|" : "]"));
        sb.append("?)");
        MovieLoader.resolutionRegexp = sb.toString();
    }

    public static LoadedMovieData getLoadMovie(final String fileName) {
        final String clearFileName = fileName.replaceAll("[\\-\\_\\.]+", " ").replaceAll("[\\(\\)\\[\\]]+", "");

        String name = null;
        String format = null;
        String resolution = null;
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

        pattern = Pattern.compile(MovieLoader.formatRegexp, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(clearFileName);
        if (matcher.find())
            if (matcher.group() != null) {
                format = matcher.group().trim();
                if (lowerIndex == -1 || matcher.start() < lowerIndex)
                    lowerIndex = matcher.start();
            }

        pattern = Pattern.compile(MovieLoader.resolutionRegexp, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(clearFileName);
        if (matcher.find())
            if (matcher.group() != null) {
                resolution = matcher.group().trim();
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

        MovieFormat movieFormat = MovieFormat.UNKNOWN;
        if (format != null)
            movieFormat = MovieFormat.getMovieFormat(format);
        MovieResolution movieResolution = MovieResolution.UNKNOWN;
        if (format != null)
            movieResolution = MovieResolution.getMovieResolution(resolution);

        return new LoadedMovieData(fileName, name, movieFormat, movieResolution, year, collection);
    }
}
