package spi.movieorganizer.display.view.loadresult;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;
import spi.movieorganizer.controller.tmdb.TMDBRequestResult;
import spi.movieorganizer.data.movie.LoadMovie;
import spi.movieorganizer.display.MovieOrganizerSession;
import exane.osgi.jexlib.core.action.Executable;
import exane.osgi.jexlib.core.type.tuple.DoubleTuple;

public class LoadResultPanel extends JPanel {

    private final Map<String, LoadMoviePanel> loadMovieMap;

    public LoadResultPanel(final List<String> movieList) {
        this.loadMovieMap = new HashMap<>();

        final JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new MigLayout("ins 0", "fill, grow"));

        for (final String movieName : movieList) {
            final LoadMoviePanel panel = new LoadMoviePanel(movieName);
            this.loadMovieMap.put(movieName, panel);
            contentPanel.add(panel, "wrap");
        }

        final JScrollPane scrollPane = new JScrollPane(contentPanel);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        MovieOrganizerSession.getSession().getControllerRepository().getUserMovieController().loadMovieList(movieList, new Executable<DoubleTuple<LoadMovie, TMDBRequestResult>>() {

            @Override
            public void execute(final DoubleTuple<LoadMovie, TMDBRequestResult> resultTuple) {
                LoadMoviePanel panel;
                if ((panel = LoadResultPanel.this.loadMovieMap.get(resultTuple.getFirstValue().getFileName())) != null)
                    panel.updateContent(resultTuple.getSecondValue());
            }
        });
    }
}
