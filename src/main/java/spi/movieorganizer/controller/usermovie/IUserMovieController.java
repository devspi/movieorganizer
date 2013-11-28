package spi.movieorganizer.controller.usermovie;

import java.util.List;

import spi.movieorganizer.controller.tmdb.TMDBRequestResult;
import spi.movieorganizer.data.collection.CollectionDO;
import spi.movieorganizer.data.movie.LoadMovie;
import spi.movieorganizer.data.movie.MovieDO;
import exane.osgi.jexlib.core.action.Executable;
import exane.osgi.jexlib.core.type.tuple.DoubleTuple;

public interface IUserMovieController {

    void addToUserMovie(MovieDO movieDO);

    void addToUserMovie(CollectionDO collectionDO);

    void removeFromUserMovie(Integer movieId);

    void loadMovieList(List<String> fileNames, Executable<DoubleTuple<LoadMovie, TMDBRequestResult>> callback);

}
