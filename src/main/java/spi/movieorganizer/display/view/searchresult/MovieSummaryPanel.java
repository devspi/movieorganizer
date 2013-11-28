package spi.movieorganizer.display.view.searchresult;

import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import spi.movieorganizer.controller.tmdb.TMDBController;
import spi.movieorganizer.data.movie.MovieDO;
import spi.movieorganizer.data.movie.UserMovieDM;
import spi.movieorganizer.data.util.TimeTools;
import spi.movieorganizer.display.MovieOrganizerSession;
import spi.movieorganizer.display.resources.MovieOrganizerStaticResources;
import spi.movieorganizer.display.view.detail.MovieDetailPanel;
import exane.osgi.jexlib.common.annotation.JexAction;
import exane.osgi.jexlib.common.annotation.injector.ActionInjector;
import exane.osgi.jexlib.common.swing.component.label.JHyperlinkLabel;
import exane.osgi.jexlib.core.action.Executable;

public class MovieSummaryPanel extends JPanel {

    private final JLabel      titleLabel;
    private final JLabel      releaseDateLabel;
    private final JLabel      voteAverageLabel;
    private final JLabel      posterLabel;
    private final JButton     addButton;

    private final MovieDO     summaryMovieDO;
    private final UserMovieDM userMovieDM;

    public MovieSummaryPanel(final MovieDO movieDO) {
        this.userMovieDM = MovieOrganizerSession.getSession().getDataManagerRepository().getUserMovieDM();
        this.summaryMovieDO = movieDO;
        this.titleLabel = new JHyperlinkLabel(movieDO.getTitle(Locale.FRENCH), new Runnable() {

            @Override
            public void run() {
                MovieOrganizerSession.getSession().getControllerRepository().getTmdbController()
                        .requestMovie(movieDO.getIdentifier().toString(), Locale.FRENCH, new Executable<MovieDO>() {

                            @Override
                            public void execute(final MovieDO arg0) {
                                final MovieDetailPanel movieDetailPanel = new MovieDetailPanel();
                                movieDetailPanel.setMovie(arg0);
                                MovieOrganizerSession.getCenterPanel().setContent(movieDetailPanel);
                            }
                        });
            }
        });
        this.releaseDateLabel = new JLabel(TimeTools.format(TimeTools.dd_MM_yyyy_PATTERN, movieDO.getReleaseDate()));
        this.voteAverageLabel = new JLabel(String.valueOf(movieDO.getVoteAverage()) + " (" + movieDO.getVoteCount() + " votes)");

        final String src = movieDO.getPosterPath() != null ? TMDBController.BASE_URL + "w92" + movieDO.getPosterPath() : "http://d3a8mw37cqal2z.cloudfront.net/assets/e6497422f20fa74/images/no-poster-w92.jpg";
        this.posterLabel = new JLabel("<html><img src=\"" + src + "\"></html>");

        ActionInjector.inject(this);

        if (this.userMovieDM.hasDataObjectKey(movieDO.getIdentifier()))
            this.addButton = new JButton(getActionMap().get("removeFromUserMovie"));
        else
            this.addButton = new JButton(getActionMap().get("addToUserMovie"));

        setLayout(new MigLayout("ins 0", "[][]", "[][][]"));
        add(this.posterLabel, "spany 4");
        add(this.titleLabel, "wrap");
        add(this.releaseDateLabel, "skip 1, wrap");
        add(this.voteAverageLabel, "skip 1, wrap");
        add(this.addButton, "skip 1");
    }

    @JexAction(source = MovieOrganizerStaticResources.PROPERTIES_ACTIONS)
    private void addToUserMovie() {
        MovieOrganizerSession.getSession().getControllerRepository().getTmdbController()
                .requestMovie(this.summaryMovieDO.getIdentifier().toString(), Locale.FRENCH, new Executable<MovieDO>() {

                    @Override
                    public void execute(final MovieDO movieDO) {
                        MovieOrganizerSession.getSession().getControllerRepository().getUserMovieController().addToUserMovie(movieDO);
                        MovieSummaryPanel.this.addButton.setAction(getActionMap().get("removeFromUserMovie"));
                    }
                });
    }

    @JexAction(source = MovieOrganizerStaticResources.PROPERTIES_ACTIONS)
    private void removeFromUserMovie() {
        MovieOrganizerSession.getSession().getControllerRepository().getUserMovieController().removeFromUserMovie(this.summaryMovieDO.getIdentifier());
        this.addButton.setAction(getActionMap().get("addToUserMovie"));
    }
}
