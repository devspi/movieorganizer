package spi.movieorganizer.display.view.searchresult;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import spi.movieorganizer.controller.tmdb.TMDBRequestResult;
import spi.movieorganizer.controller.tmdb.TMDBRequestResult.TMDBRequestType;
import spi.movieorganizer.data.collection.CollectionDO;
import spi.movieorganizer.data.movie.MovieDO;

public class SearchResultPanel extends JPanel {

    public SearchResultPanel(final TMDBRequestResult result) {

        final JTabbedPane tabbedPane = new JTabbedPane();
        for (final TMDBRequestType type : TMDBRequestType.values())
            switch (type) {
                case Movies: {
                    final JPanel resultPanel = new JPanel(new MigLayout());
                    for (final MovieDO movieDO : result.getMovieDM())
                        resultPanel.add(new MovieSummaryPanel(movieDO), "wrap");
                    tabbedPane.insertTab(type.name() + " (" + result.getMovieDM().getDataObjectCount() + " results)", null, resultPanel, "", type.ordinal());
                    break;
                }
                case Collections: {
                    final JPanel resultPanel = new JPanel(new MigLayout());
                    for (final CollectionDO collectionDO : result.getCollectionDM())
                        resultPanel.add(new CollectionSummaryPanel(collectionDO), "wrap");
                    tabbedPane.insertTab(type.name() + " (" + result.getCollectionDM().getDataObjectCount() + " results)", null, resultPanel, "", type.ordinal());
                    if (result.getMovieDM().getDataObjectCount() == 0 && result.getCollectionDM().getDataObjectCount() > 0)
                        tabbedPane.setSelectedIndex(1);
                    break;
                }

            }

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
    }
}
