package spi.movieorganizer.data.movie;

public class UserMovieSettings {
    public static enum MovieResolution {
        HD("720p"),
        FULL_HD("1080p"),
        UNKNOWN("Unknown");

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
        UNKNOWN("Unknown");

        private final String label;

        private MovieFormat(final String label) {
            this.label = label;
        }

        public String getLabel() {
            return this.label;
        }

    }

    private boolean         seen            = false;
    private MovieFormat     movieFormat     = MovieFormat.UNKNOWN;
    private MovieResolution movieResolution = MovieResolution.UNKNOWN;

    public UserMovieSettings(final boolean seen, final MovieFormat movieFormat, final MovieResolution movieResolution) {
        this.seen = seen;
        this.movieFormat = movieFormat;
        this.movieResolution = movieResolution;
    }

    public static UserMovieSettings createUnknownSettings() {
        return new UserMovieSettings(false, MovieFormat.UNKNOWN, MovieResolution.UNKNOWN);
    }

    public boolean isSeen() {
        return this.seen;
    }

    public void setSeen(final boolean seen) {
        this.seen = seen;
    }

    public MovieFormat getMovieFormat() {
        return this.movieFormat;
    }

    public void setMovieFormat(final MovieFormat movieFormat) {
        this.movieFormat = movieFormat;
    }

    public MovieResolution getMovieResolution() {
        return this.movieResolution;
    }

    public void setMovieResolution(final MovieResolution movieResolution) {
        this.movieResolution = movieResolution;
    }

}
