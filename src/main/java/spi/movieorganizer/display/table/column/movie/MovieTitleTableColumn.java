package spi.movieorganizer.display.table.column.movie;

import java.util.Locale;

import spi.movieorganizer.data.movie.UserMovieDO;
import spi.movieorganizer.display.table.column.AbstractMovieOrganizerTableColumn;
import spi.movieorganizer.display.table.column.ColumnCategory;
import exane.osgi.jexlib.common.swing.table.renderer.basic.DefaultTextCellRenderer;
import exane.osgi.jexlib.core.action.Retrievable;

public class MovieTitleTableColumn extends AbstractMovieOrganizerTableColumn<String, UserMovieDO> {

    private final Retrievable<Locale> languageRetriever;

    public MovieTitleTableColumn(final Retrievable<Locale> languageRetriever) {
        this.languageRetriever = languageRetriever;
        setCellRenderer(new DefaultTextCellRenderer());
    }

    @Override
    public Class<String> getColumnClass() {
        return String.class;
    }

    @Override
    public String getValue(final UserMovieDO movie) {
        if (this.languageRetriever != null)
            return movie.getMovie().getTitle(this.languageRetriever.get());
        return movie.getMovie().getTitle(Locale.FRENCH);
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
