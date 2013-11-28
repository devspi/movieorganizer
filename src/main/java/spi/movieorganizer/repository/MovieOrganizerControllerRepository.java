package spi.movieorganizer.repository;

import spi.movieorganizer.controller.tmdb.ITMDBController;
import spi.movieorganizer.controller.tmdb.TMDBController;
import spi.movieorganizer.controller.usermovie.IUserMovieController;
import spi.movieorganizer.controller.usermovie.UserMovieController;
import spi.movieorganizer.display.MovieOrganizerClient;
import spi.movieorganizer.display.MovieOrganizerClient.ActionExecutor;

public class MovieOrganizerControllerRepository {

    private final ITMDBController      tmdbController;
    private final IUserMovieController userMovieController;

    public MovieOrganizerControllerRepository(final MovieOrganizerClient session) {
        this.tmdbController = new TMDBController(session, new ActionExecutor());
        this.userMovieController = new UserMovieController(session, new ActionExecutor());
    }

    public ITMDBController getTmdbController() {
        return this.tmdbController;
    }

    public IUserMovieController getUserMovieController() {
        return this.userMovieController;
    }
}
