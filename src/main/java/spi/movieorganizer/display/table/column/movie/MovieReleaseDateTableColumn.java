package spi.movieorganizer.display.table.column.movie;

import java.util.Date;

import spi.movieorganizer.data.movie.UserMovieDO;
import spi.movieorganizer.display.table.column.AbstractMovieOrganizerTableColumn;
import spi.movieorganizer.display.table.column.ColumnCategory;
import spi.movieorganizer.display.table.renderer.DateCellRenderer;

public class MovieReleaseDateTableColumn extends AbstractMovieOrganizerTableColumn<Date, UserMovieDO> {

    public MovieReleaseDateTableColumn() {
        setCellRenderer(new DateCellRenderer());
    }

    @Override
    public Class<Date> getColumnClass() {
        return Date.class;
    }

    @Override
    public Date getValue(final UserMovieDO movie) {
        return movie.getMovie().getReleaseDate();
    }

    @Override
    public boolean isUpdateNeeded(final Object arg0, final Object arg1) {
        return false;
    }

    @Override
    public ColumnCategory getCategory() {
        return ColumnCategory.Movie;
    }

}
