package spi.movieorganizer.data.movie;

import spi.movieorganizer.data.movie.UserMovieSettings.MovieFormat;
import spi.movieorganizer.data.movie.UserMovieSettings.MovieResolution;

public class LoadedMovieData {

    private final String          fileName;
    private final String          name;
    private final MovieFormat     format;
    private final MovieResolution resolution;
    private final String          year;
    private final boolean         collection;

    public LoadedMovieData(final String fileName, final String name, final MovieFormat format, final MovieResolution resolution, final String year, final boolean collection) {
        super();
        this.fileName = fileName;
        this.name = name;
        this.format = format;
        this.resolution = resolution;
        this.year = year;
        this.collection = collection;
    }

    public String getFileName() {
        return this.fileName;
    }

    public MovieResolution getResolution() {
        return this.resolution;
    }

    public boolean isCollection() {
        return this.collection;
    }

    public String getName() {
        return this.name;
    }

    public MovieFormat getFormat() {
        return this.format;
    }

    public String getYear() {
        return this.year;
    }
}
