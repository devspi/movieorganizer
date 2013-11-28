package spi.movieorganizer.repository;

import spi.movieorganizer.data.genre.GenreDM;
import spi.movieorganizer.data.movie.UserMovieDM;

public class MovieOrganizerDataManagerRepository {

    private final GenreDM     genreDM;
    private final UserMovieDM userMovieDM;

    public MovieOrganizerDataManagerRepository() {
        this.genreDM = new GenreDM();
        this.userMovieDM = new UserMovieDM();
    }

    public GenreDM getGenreDM() {
        return this.genreDM;
    }

    public UserMovieDM getUserMovieDM() {
        return this.userMovieDM;
    }

}
