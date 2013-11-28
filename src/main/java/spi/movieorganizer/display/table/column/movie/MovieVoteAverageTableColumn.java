package spi.movieorganizer.display.table.column.movie;

import spi.movieorganizer.data.movie.UserMovieDO;
import spi.movieorganizer.display.table.column.AbstractMovieOrganizerTableColumn;
import spi.movieorganizer.display.table.column.ColumnCategory;
import spi.movieorganizer.display.table.renderer.DoubleCellRenderer;

public class MovieVoteAverageTableColumn extends AbstractMovieOrganizerTableColumn<Double, UserMovieDO> {

    public MovieVoteAverageTableColumn() {
        setCellRenderer(new DoubleCellRenderer(1));
    }

    @Override
    public Class<Double> getColumnClass() {
        return Double.class;
    }

    @Override
    public Double getValue(final UserMovieDO movie) {
        return movie.getMovie().getVoteAverage();
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
