package spi.movieorganizer.display.component;

import java.util.Locale;

import spi.movieorganizer.controller.tmdb.TMDBController;
import spi.movieorganizer.controller.tmdb.TMDBRequestResult.TMDBRequestType;
import spi.movieorganizer.data.movie.LoadedMovieData;
import spi.movieorganizer.data.movie.MovieDO;

public class MovieSelectablePanel extends AbstractSelectableItemPanel {

    private final MovieDO movieDO;

    public MovieSelectablePanel(final LoadedMovieData loadMovie, final MovieDO movieDO) {
        super(loadMovie);
        this.movieDO = movieDO;
        final String src = movieDO.getPosterPath() != null ? TMDBController.BASE_URL + "w92" + movieDO.getPosterPath() : "http://d3a8mw37cqal2z.cloudfront.net/assets/e6497422f20fa74/images/no-poster-w92.jpg";
        this.posterLabel.setText("<html><img src=\"" + src + "\"></html>");
        this.titleLabel.setText("<html>" + movieDO.getTitle(Locale.FRENCH) + "</html>");
    }

    @Override
    protected TMDBRequestType getType() {
        return TMDBRequestType.Movies;
    }

    @Override
    protected Integer getItemId() {
        return this.movieDO.getIdentifier();
    }
}
