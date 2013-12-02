package spi.movieorganizer.display.view.loadresult;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.jxlayer.JXLayer;

import spi.movieorganizer.controller.tmdb.TMDBRequestResult;
import spi.movieorganizer.data.movie.LoadedMovieData;
import spi.movieorganizer.display.MovieOrganizerSession;
import spi.movieorganizer.display.component.AbstractSelectableItemPanel;
import spi.movieorganizer.display.resources.MovieOrganizerStaticResources;
import exane.osgi.jexlib.common.annotation.JexAction;
import exane.osgi.jexlib.common.annotation.injector.ActionInjector;
import exane.osgi.jexlib.common.swing.component.lockableui.BusyPainterUI;
import exane.osgi.jexlib.core.action.Executable;
import exane.osgi.jexlib.core.type.mutable.MutableInteger;
import exane.osgi.jexlib.core.type.tuple.DoubleTuple;

public class LoadResultPanel extends JPanel {

    private final Map<String, LoadMoviePanel> loadMovieMap;
    private int                               count = 0;
    private final JXLayer<JComponent>         globalLayer;
    private BusyPainterUI                     busyPainterUI;

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

        final JPanel layerContent = new JPanel(new MigLayout("ins 0, gap 0", "fill, grow", "[][fill, grow]"));
        layerContent.add(actionPanel, "wrap");
        layerContent.add(scrollPane, BorderLayout.CENTER);

        this.globalLayer = new JXLayer<>(layerContent, this.busyPainterUI = new BusyPainterUI());

        setLayout(new BorderLayout());
        add(this.globalLayer);

        MovieOrganizerSession.getSession().getControllerRepository().getUserMovieController()
                .loadMovieList(movieList, new Executable<DoubleTuple<LoadedMovieData, TMDBRequestResult>>() {

                    @Override
                    public void execute(final DoubleTuple<LoadedMovieData, TMDBRequestResult> resultTuple) {
                        LoadMoviePanel panel;
                        if ((panel = LoadResultPanel.this.loadMovieMap.get(resultTuple.getFirstValue().getFileName())) != null) {
                            panel.updateContent(resultTuple);
                            if (++LoadResultPanel.this.count == LoadResultPanel.this.loadMovieMap.size())
                                getActionMap().get("addSelectedMovies").setEnabled(true);
                        }
                    }
                });

    }

    @JexAction(enabledProperty = false, source = MovieOrganizerStaticResources.PROPERTIES_ACTIONS)
    private void addSelectedMovies() {
        this.busyPainterUI.setLocked(true);
        final MutableInteger totalCount = new MutableInteger(0);
        final MutableInteger index = new MutableInteger(0);
        for (final LoadMoviePanel panel : this.loadMovieMap.values()) {
            final List<AbstractSelectableItemPanel> itemPanels = panel.getSelectedItemPanels();
            totalCount.increment(itemPanels.size());
            for (final AbstractSelectableItemPanel itemPanel : itemPanels) {
                final SelectedItemData itemData = itemPanel.getItemData();
                MovieOrganizerSession.getSession().getControllerRepository().getUserMovieController()
                        .addToUserMovie(itemData.getRequestType(), itemData.getItemId(), itemData.getSettings(), new Runnable() {

                            @Override
                            public void run() {
                                switch (itemData.getRequestType()) {
                                    case Collections:
                                        itemPanel.fireItemAdded("<html>Collection successfully added !</html>");
                                        break;
                                    case Movies:
                                        itemPanel.fireItemAdded("<html>Movie successfully added !</html>");
                                        break;
                                }
                                index.increment(1);
                                if (index.get() == totalCount.get())
                                    LoadResultPanel.this.busyPainterUI.setLocked(false);
                            }
                        });
            }
        }
    }
}
