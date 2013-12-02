package spi.movieorganizer.controller.tmdb;

import java.util.Locale;

import spi.movieorganizer.data.collection.CollectionDM;
import spi.movieorganizer.data.collection.CollectionDO;
import spi.movieorganizer.data.movie.MovieDM;
import spi.movieorganizer.data.movie.MovieDO;
import exane.osgi.jexlib.core.action.Executable;

public interface ITMDBController {

    void requestMovie(String movieId, Locale locale, Executable<MovieDO> callback, boolean executeCallbackInEDT);

    void requestCollection(String collectionId, Locale locale, Executable<CollectionDO> callback, boolean executeCallbackInEDT);

    void searchMovie(String query, Locale locale, Executable<MovieDM> callback, boolean executeCallbackInEDT);

    void search(String query, Locale locale, Executable<TMDBRequestResult> callback);

    void searchCollection(String query, Locale locale, Executable<CollectionDM> callback, boolean executeCallbackInEDT);

}
