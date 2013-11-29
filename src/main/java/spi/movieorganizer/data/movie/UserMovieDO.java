package spi.movieorganizer.data.movie;

import java.util.Date;

import spi.movieorganizer.data.MovieOrganizerType;
import exane.osgi.jexlib.data.object.AbstractDataObject;
import exane.osgi.jexlib.data.object.ExaneDataType;

public class UserMovieDO extends AbstractDataObject<Integer> {

    private final MovieDO     movie;
    private final Date        addingDate;

    private UserMovieSettings settings;

    public UserMovieDO(final Integer key, final MovieDO movie) {
        super(key);
        this.movie = movie;
        this.addingDate = new Date();
    }

    public UserMovieDO(final Integer key, final MovieDO movie, final Date addingDate, final UserMovieSettings settings) {
        super(key);
        this.movie = movie;
        this.addingDate = addingDate;
        this.settings = settings;
    }

    public MovieDO getMovie() {
        return this.movie;
    }

    public Date getAddingDate() {
        return this.addingDate;
    }

    public UserMovieSettings getSettings() {
        return this.settings;
    }

    public void setSettings(final UserMovieSettings settings) {
        this.settings = settings;
    }

    @Override
    public ExaneDataType getDataType() {
        return MovieOrganizerType.USER_MOVIE;
    }
}
