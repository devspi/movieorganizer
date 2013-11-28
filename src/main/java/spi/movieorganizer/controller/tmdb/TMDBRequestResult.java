package spi.movieorganizer.controller.tmdb;

import spi.movieorganizer.data.collection.CollectionDM;
import spi.movieorganizer.data.movie.MovieDM;

public class TMDBRequestResult {

    public static enum TMDBRequestType {
        Movies,
        Collections;
    }

    private MovieDM      movieDM;
    private CollectionDM collectionDM;

    public void setMovieDM(final MovieDM movieDM) {
        this.movieDM = movieDM;
    }

    public void setCollectionDM(final CollectionDM collectionDM) {
        this.collectionDM = collectionDM;
    }

    public MovieDM getMovieDM() {
        return this.movieDM;
    }

    public CollectionDM getCollectionDM() {
        return this.collectionDM;
    }
}
