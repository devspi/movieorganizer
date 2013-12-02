package spi.movieorganizer.display.view.detail;

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;
import spi.movieorganizer.controller.tmdb.TMDBController;
import spi.movieorganizer.data.collection.CollectionDO;
import spi.movieorganizer.data.collection.CollectionPartDO;
import spi.movieorganizer.data.movie.MovieDO;
import spi.movieorganizer.display.MovieOrganizerSession;
import exane.osgi.jexlib.common.swing.component.label.JHyperlinkLabel;
import exane.osgi.jexlib.core.action.Executable;

public class CollectionDetailPanel extends JPanel {

    private final SimpleDateFormat yearDateFormat = new SimpleDateFormat("yyyy");

    private final JLabel           nameLabel;
    private final JLabel           posterLabel;

    public CollectionDetailPanel(final CollectionDO collectionDO) {
        this.nameLabel = new JLabel(collectionDO.getName(Locale.FRENCH));

        final String src = collectionDO.getPosterPath() != null ? TMDBController.BASE_URL + "w185" + collectionDO.getPosterPath() : "http://d3a8mw37cqal2z.cloudfront.net/assets/e6497422f20fa74/images/no-poster-w185.jpg";
        this.posterLabel = new JLabel("<html><img src=\"" + src + "\"></html>");

        final JPanel contentPanel = new JPanel(new MigLayout("ins 0", "[][]", "[][][]"));
        contentPanel.add(this.posterLabel, "spany 1");
        contentPanel.add(this.nameLabel);

        final JPanel partPanel = new JPanel(new MigLayout());
        partPanel.add(new JLabel("Collection parts"), "wrap");
        for (final CollectionPartDO collectionPartDO : collectionDO.getCollectionPartDM())
            partPanel.add(new CollectionPartPanel(collectionPartDO), "wrap");

        contentPanel.add(partPanel, "newline");

        final JScrollPane scrollPane = new JScrollPane(contentPanel);
        setLayout(new BorderLayout());
        add(scrollPane);
    }

    private class CollectionPartPanel extends JPanel {
        private final JHyperlinkLabel titleLabel;
        private final JLabel          posterLabel;
        private final JLabel          releaseDateLabel;

        public CollectionPartPanel(final CollectionPartDO partDO) {
            this.titleLabel = new JHyperlinkLabel(partDO.getTitle(Locale.FRENCH), new Runnable() {

                @Override
                public void run() {
                    MovieOrganizerSession.getSession().getControllerRepository().getTmdbController()
                            .requestMovie(partDO.getIdentifier().toString(), Locale.FRENCH, new Executable<MovieDO>() {

                                @Override
                                public void execute(final MovieDO arg0) {
                                    final MovieDetailPanel movieDetailPanel = new MovieDetailPanel();
                                    movieDetailPanel.setMovie(arg0);
                                    MovieOrganizerSession.getCenterPanel().setContent(movieDetailPanel);
                                }
                            }, true);
                }
            });
            this.releaseDateLabel = new JLabel("(" + CollectionDetailPanel.this.yearDateFormat.format(partDO.getReleaseDate()) + ")");

            final String src = partDO.getPosterPath() != null ? TMDBController.BASE_URL + "w92" + partDO.getPosterPath() : "http://d3a8mw37cqal2z.cloudfront.net/assets/e6497422f20fa74/images/no-poster-w92.jpg";
            this.posterLabel = new JLabel("<html><img src=\"" + src + "\"></html>");

            setLayout(new MigLayout("ins 0", "[][][]", ""));
            add(this.posterLabel);
            add(this.titleLabel);
            add(this.releaseDateLabel);
        }
    }
}
