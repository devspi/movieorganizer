package spi.movieorganizer.display.view.loadresult;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import net.miginfocom.swing.MigLayout;
import spi.movieorganizer.controller.tmdb.TMDBRequestResult;
import spi.movieorganizer.controller.tmdb.TMDBRequestResult.TMDBRequestType;
import spi.movieorganizer.data.movie.LoadMovie;
import spi.movieorganizer.display.MovieOrganizerSession;
import spi.movieorganizer.display.resources.MovieOrganizerStaticResources;
import exane.osgi.jexlib.common.annotation.JexAction;
import exane.osgi.jexlib.common.annotation.injector.ActionInjector;
import exane.osgi.jexlib.core.action.Executable;
import exane.osgi.jexlib.core.type.tuple.DoubleTuple;

public class LoadResultPanel extends JPanel {

    private final Map<String, LoadMoviePanel> loadMovieMap;
    private int                               count = 0;

    public LoadResultPanel(final List<String> movieList) {
        this.loadMovieMap = new HashMap<>();

        ActionInjector.inject(this);
        final JButton addSelectedButton = new JButton(getActionMap().get("addSelectedMovies"));
        final JPanel actionPanel = new JPanel(new MigLayout("ins 0", "5[]push[]"));
        actionPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        actionPanel.add(new JLabel(movieList.size() + " files requested"));
        actionPanel.add(addSelectedButton);

        final JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new MigLayout("ins 0", "fill, grow"));

        for (final String movieName : movieList) {
            final LoadMoviePanel panel = new LoadMoviePanel(movieName);
            this.loadMovieMap.put(movieName, panel);
            contentPanel.add(panel, "wrap");
        }

        final JScrollPane scrollPane = new JScrollPane(contentPanel);
        setLayout(new MigLayout("ins 0, gap 0", "fill, grow", "[][fill, grow]"));
        add(actionPanel, "wrap");
        add(scrollPane, BorderLayout.CENTER);

        MovieOrganizerSession.getSession().getControllerRepository().getUserMovieController().loadMovieList(movieList, new Executable<DoubleTuple<LoadMovie, TMDBRequestResult>>() {

            @Override
            public void execute(final DoubleTuple<LoadMovie, TMDBRequestResult> resultTuple) {
                LoadMoviePanel panel;
                if ((panel = LoadResultPanel.this.loadMovieMap.get(resultTuple.getFirstValue().getFileName())) != null) {
                    panel.updateContent(resultTuple.getSecondValue());
                    if (++LoadResultPanel.this.count == LoadResultPanel.this.loadMovieMap.size())
                        getActionMap().get("addSelectedMovies").setEnabled(true);
                }
            }
        });
    }

    @JexAction(enabledProperty = false, source = MovieOrganizerStaticResources.PROPERTIES_ACTIONS)
    private void addSelectedMovies() {
        for (final LoadMoviePanel panel : this.loadMovieMap.values())
            for (final DoubleTuple<TMDBRequestType, Integer> item : panel.getSelectedItems())
                MovieOrganizerSession.getSession().getControllerRepository().getUserMovieController().addToUserMovie(item.getFirstValue(), item.getSecondValue());
    }
}
