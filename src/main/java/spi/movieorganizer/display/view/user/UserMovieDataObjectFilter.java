package spi.movieorganizer.display.view.user;

import spi.movieorganizer.data.movie.UserMovieDO;
import exane.osgi.jexlib.data.manager.filter.AbstractDataObjectFilter;

public class UserMovieDataObjectFilter extends AbstractDataObjectFilter<UserMovieDO> {

    private Integer selectedGenreId;

    public UserMovieDataObjectFilter() {
        this.selectedGenreId = null;
    }

    @Override
    public void clearFilter() {
        this.selectedGenreId = null;
    }

    @Override
    public boolean isIncluded(final UserMovieDO userMovieDO) {
        if (this.selectedGenreId == null || userMovieDO.getMovie().getGenres().contains(this.selectedGenreId))
            return true;
        return false;
    }

    public void setSelectedGenre(final Integer genreId) {
        if (this.selectedGenreId != null && this.selectedGenreId.equals(genreId) == false || this.selectedGenreId == null && genreId != null) {
            this.selectedGenreId = genreId;
            fireDataObjectFilterUpdated();
        }
    }
}
