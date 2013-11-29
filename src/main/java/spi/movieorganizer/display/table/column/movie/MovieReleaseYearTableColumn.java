package spi.movieorganizer.display.table.column.movie;

import spi.movieorganizer.data.movie.UserMovieDO;
import spi.movieorganizer.data.util.TimeTools;
import spi.movieorganizer.display.table.column.AbstractMovieOrganizerTableColumn;
import spi.movieorganizer.display.table.column.ColumnCategory;
import exane.osgi.jexlib.common.swing.table.renderer.basic.DefaultTextCellRenderer;

public class MovieReleaseYearTableColumn extends AbstractMovieOrganizerTableColumn<String, UserMovieDO> {

    public MovieReleaseYearTableColumn() {
        setCellRenderer(new DefaultTextCellRenderer());
    }

    @Override
    public Class<String> getColumnClass() {
        return String.class;
    }

    @Override
    public String getValue(final UserMovieDO movie) {
        return TimeTools.format(TimeTools.yyyy_PATTERN, movie.getMovie().getReleaseDate());
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
