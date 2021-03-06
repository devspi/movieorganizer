package spi.movieorganizer.display.view.searchresult;

import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import spi.movieorganizer.controller.tmdb.TMDBController;
import spi.movieorganizer.controller.tmdb.TMDBRequestResult.TMDBRequestType;
import spi.movieorganizer.data.collection.CollectionDO;
import spi.movieorganizer.data.movie.UserMovieSettings;
import spi.movieorganizer.display.MovieOrganizerSession;
import spi.movieorganizer.display.resources.MovieOrganizerStaticResources;
import spi.movieorganizer.display.view.detail.CollectionDetailPanel;
import exane.osgi.jexlib.common.annotation.JexAction;
import exane.osgi.jexlib.common.annotation.injector.ActionInjector;
import exane.osgi.jexlib.common.swing.component.label.JHyperlinkLabel;
import exane.osgi.jexlib.core.action.Executable;

public class CollectionSummaryPanel extends JPanel {

    private final JLabel       nameLabel;
    private final JLabel       posterLabel;
    private final JButton      addButton;

    private final CollectionDO summaryCollectionDO;

    public CollectionSummaryPanel(final CollectionDO collectionDO) {
        this.summaryCollectionDO = collectionDO;
        this.nameLabel = new JHyperlinkLabel(collectionDO.getName(Locale.FRENCH), new Runnable() {

            @Override
            public void run() {
                MovieOrganizerSession.getSession().getControllerRepository().getTmdbController()
                        .requestCollection(collectionDO.getIdentifier().toString(), Locale.FRENCH, new Executable<CollectionDO>() {

                            @Override
                            public void execute(final CollectionDO arg0) {
                                MovieOrganizerSession.getCenterPanel().setContent(new CollectionDetailPanel(arg0));
                            }
                        }, true);
            }
        });

        final String src = collectionDO.getPosterPath() != null ? TMDBController.BASE_URL + "w92" + collectionDO.getPosterPath() : "http://d3a8mw37cqal2z.cloudfront.net/assets/e6497422f20fa74/images/no-poster-w92.jpg";
        this.posterLabel = new JLabel("<html><img src=\"" + src + "\"></html>");

        ActionInjector.inject(this);

        this.addButton = new JButton(getActionMap().get("addCollectionToUserMovie"));

        setLayout(new MigLayout("ins 0", "[][]", "[][][]"));
        add(this.posterLabel, "spany 4");
        add(this.nameLabel, "wrap");
        add(this.addButton, "skip 1");
    }

    @JexAction(source = MovieOrganizerStaticResources.PROPERTIES_ACTIONS)
    private void addCollectionToUserMovie() {
        MovieOrganizerSession.getSession().getControllerRepository().getUserMovieController()
                .addToUserMovie(TMDBRequestType.Collections, this.summaryCollectionDO.getIdentifier(), UserMovieSettings.createUnknownSettings(), new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                    }
                });
    }
}
