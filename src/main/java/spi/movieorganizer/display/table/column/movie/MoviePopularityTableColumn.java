package spi.movieorganizer.display.table.column.movie;

import spi.movieorganizer.data.movie.UserMovieDO;
import spi.movieorganizer.display.table.column.AbstractMovieOrganizerTableColumn;
import spi.movieorganizer.display.table.column.ColumnCategory;
import spi.movieorganizer.display.table.renderer.DoubleCellRenderer;

public class MoviePopularityTableColumn extends AbstractMovieOrganizerTableColumn<Double, UserMovieDO> {

    public MoviePopularityTableColumn() {
        setCellRenderer(new DoubleCellRenderer(3));
    }

    @Override
    public Class<Double> getColumnClass() {
        return Double.class;
    }

    @Override
    public Double getValue(final UserMovieDO movie) {
        return movie.getMovie().getPopularity();
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
