package spi.movieorganizer.display.view.user;

import java.util.Locale;

import spi.movieorganizer.data.movie.UserMovieDO;
import spi.movieorganizer.display.table.column.movie.MovieAddingDateTableColumn;
import spi.movieorganizer.display.table.column.movie.MovieOriginalTitleTableColumn;
import spi.movieorganizer.display.table.column.movie.MoviePopularityTableColumn;
import spi.movieorganizer.display.table.column.movie.MovieReleaseYearTableColumn;
import spi.movieorganizer.display.table.column.movie.MovieTitleTableColumn;
import spi.movieorganizer.display.table.column.movie.MovieVoteAverageTableColumn;
import exane.osgi.jexlib.common.swing.table.model.ExaneDataManagerTableModel;
import exane.osgi.jexlib.core.action.Retrievable;
import exane.osgi.jexlib.data.manager.properties.ListableDataManager;

public class UserMovieTableModel extends ExaneDataManagerTableModel<UserMovieDO> {

    public UserMovieTableModel(final ListableDataManager<?, UserMovieDO> dataManager, final Retrievable<Locale> languageRetriever) {
        super(dataManager);

        addColumn(new MovieTitleTableColumn(languageRetriever));
        addColumn(new MovieReleaseYearTableColumn());
        addColumn(new MovieOriginalTitleTableColumn());
        addColumn(new MovieVoteAverageTableColumn());
        addColumn(new MoviePopularityTableColumn());
        addColumn(new MovieAddingDateTableColumn());
    }

}
