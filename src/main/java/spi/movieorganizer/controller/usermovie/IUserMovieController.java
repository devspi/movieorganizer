package spi.movieorganizer.controller.usermovie;

import java.util.List;

import spi.movieorganizer.controller.tmdb.TMDBRequestResult;
import spi.movieorganizer.controller.tmdb.TMDBRequestResult.TMDBRequestType;
import spi.movieorganizer.data.movie.LoadMovie;
import exane.osgi.jexlib.core.action.Executable;
import exane.osgi.jexlib.core.type.tuple.DoubleTuple;

public interface IUserMovieController {

    void loadMovieList(List<String> fileNames, Executable<DoubleTuple<LoadMovie, TMDBRequestResult>> callback);

    void addToUserMovie(TMDBRequestType type, Integer itemId);

    void removeFromUserMovie(List<Integer> movieIds);

}
