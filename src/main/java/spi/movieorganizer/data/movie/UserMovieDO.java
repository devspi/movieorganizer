package spi.movieorganizer.data.movie;

import java.util.Date;

import spi.movieorganizer.data.MovieOrganizerType;
import exane.osgi.jexlib.data.object.AbstractDataObject;
import exane.osgi.jexlib.data.object.ExaneDataType;

public class UserMovieDO extends AbstractDataObject<Integer> {

    public static enum MovieResolution {
        HD("720p"),
        FULL_HD("1080p"),
        UNKNOWN(null);

        private final String label;

        private MovieResolution(final String label) {
            this.label = label;
        }

        public String getLabel() {
            return this.label;
        }
    }

    public static enum MovieFormat {
        BLURAY("Bluray"),
        BRRIP("BRRip"),
        DVDRIP("DVDRip"),
        SCREENER("SCR"),
        UNKNOWN(null);

        private final String label;

        private MovieFormat(final String label) {
            this.label = label;
        }

        public String getLabel() {
            return this.label;
        }

    }

    private final MovieDO   movie;
    private final Date      addingDate;

    private boolean         seen            = false;
    private MovieFormat     movieFormat     = MovieFormat.UNKNOWN;
    private MovieResolution movieResolution = MovieResolution.UNKNOWN;

    public UserMovieDO(final Integer key, final MovieDO movie) {
        super(key);
        this.movie = movie;
        this.addingDate = new Date();
    }

    public UserMovieDO(final Integer key, final MovieDO movie, final Date addingDate, final boolean seen, final MovieFormat movieFormat, final MovieResolution movieResolution) {
        super(key);
        this.movie = movie;
        this.addingDate = addingDate;
        this.seen = seen;
        this.movieFormat = movieFormat;
        this.movieResolution = movieResolution;
    }

    public void setMovieResolution(final MovieResolution movieResolution) {
        this.movieResolution = movieResolution;
    }

    public MovieResolution getMovieResolution() {
        return this.movieResolution;
    }

    public void setMovieQuality(final MovieFormat movieQuality) {
        this.movieFormat = movieQuality;
    }

    public MovieFormat getMovieFormat() {
        return this.movieFormat;
    }

    public MovieDO getMovie() {
        return this.movie;
    }

    public Date getAddingDate() {
        return this.addingDate;
    }

    public boolean isSeen() {
        return this.seen;
    }

    public void setSeen(final boolean seen) {
        this.seen = seen;
    }

    @Override
    public ExaneDataType getDataType() {
        return MovieOrganizerType.USER_MOVIE;
    }
}
