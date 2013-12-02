package spi.movieorganizer.controller.usermovie;

import java.util.List;

import spi.movieorganizer.controller.tmdb.TMDBRequestResult;
import spi.movieorganizer.controller.tmdb.TMDBRequestResult.TMDBRequestType;
import spi.movieorganizer.data.movie.LoadedMovieData;
import spi.movieorganizer.data.movie.UserMovieSettings;
import exane.osgi.jexlib.core.action.Executable;
import exane.osgi.jexlib.core.type.tuple.DoubleTuple;

public interface IUserMovieController {

    void loadMovieList(List<String> fileNames, Executable<DoubleTuple<LoadedMovieData, TMDBRequestResult>> callback);

    void removeFromUserMovie(List<Integer> movieIds);

    void addToUserMovie(TMDBRequestType type, Integer itemId, UserMovieSettings settings);

}
