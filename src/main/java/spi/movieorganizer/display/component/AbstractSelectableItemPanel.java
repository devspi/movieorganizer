package spi.movieorganizer.display.component;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import net.miginfocom.swing.MigLayout;
import spi.movieorganizer.controller.tmdb.TMDBRequestResult.TMDBRequestType;
import spi.movieorganizer.data.collection.CollectionDO;
import spi.movieorganizer.data.movie.MovieDO;
import spi.movieorganizer.display.MovieOrganizerSession;
import spi.movieorganizer.display.view.detail.CollectionDetailPanel;
import spi.movieorganizer.display.view.detail.MovieDetailPanel;
import exane.osgi.jexlib.common.swing.component.label.JHyperlinkLabel;
import exane.osgi.jexlib.core.action.Executable;
import exane.osgi.jexlib.core.type.tuple.DoubleTuple;

public abstract class AbstractSelectableItemPanel extends JPanel implements ItemListener {
    protected final JLabel                                    posterLabel;
    protected final JLabel                                    titleLabel;
    protected final JCheckBox                                 selectCheckbox;
    private final JPanel                                      selectPanel;

    private final List<DoubleTuple<TMDBRequestType, Integer>> selectedItems;

    public AbstractSelectableItemPanel(final List<DoubleTuple<TMDBRequestType, Integer>> selectedItems) {
        this.selectedItems = selectedItems;
        this.posterLabel = new JLabel();
        this.posterLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.titleLabel = new JHyperlinkLabel(null, new Runnable() {

            @Override
            public void run() {
                switch (getType()) {
                    case Collections:
                        MovieOrganizerSession.getSession().getControllerRepository().getTmdbController()
                                .requestCollection(getItemId().toString(), Locale.FRENCH, new Executable<CollectionDO>() {

                                    @Override
                                    public void execute(final CollectionDO arg0) {
                                        final CollectionDetailPanel detailPanel = new CollectionDetailPanel(arg0);
                                        MovieOrganizerSession.getCenterPanel().setContent(detailPanel);
                                    }
                                });
                        break;
                    case Movies:
                        MovieOrganizerSession.getSession().getControllerRepository().getTmdbController()
                                .requestMovie(getItemId().toString(), Locale.FRENCH, new Executable<MovieDO>() {

                                    @Override
                                    public void execute(final MovieDO arg0) {
                                        final MovieDetailPanel detailPanel = new MovieDetailPanel();
                                        detailPanel.setMovie(arg0);
                                        MovieOrganizerSession.getCenterPanel().setContent(detailPanel);
                                    }
                                });
                        break;
                }
            }
        });
        this.titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
        this.titleLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        this.titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.selectCheckbox = new JCheckBox();
        this.selectCheckbox.setOpaque(false);
        this.selectCheckbox.setBorder(BorderFactory.createEtchedBorder());
        this.selectCheckbox.addItemListener(this);

        // setLayout(new MigLayout("ins 0, gap 0", "[][92px!, center, fill]", "[][]"));
        // add(this.selectCheckbox, "growy");
        // add(this.posterLabel, "wrap");
        // add(this.titleLabel, "skip 1");

        // select panel right from the poster
        this.selectCheckbox.setText("I have it");
        this.selectPanel = new JPanel(new MigLayout("ins 0, gap 0", "[left]", "[top]15[top]"));
        this.selectPanel.add(this.titleLabel, "wrap");
        this.selectPanel.add(this.selectCheckbox);
        setLayout(new MigLayout("ins 0, gap 0", "[94px!][94px!]", "[fill, grow, top]"));
        add(this.posterLabel);
        add(this.selectPanel);

        // // select panel bottom of poster
        // this.selectPanel = new JPanel(new MigLayout("ins 0, gap 0", "[][][center, fill]", "fill ,grow"));
        // this.selectPanel.setBorder(BorderFactory.createEtchedBorder());
        // this.selectPanel.add(this.selectCheckbox);
        // this.selectPanel.add(new JSeparator(SwingConstants.VERTICAL), "growy");
        // this.selectPanel.add(this.titleLabel);
        // setLayout(new MigLayout("ins 0, gap 0", "[94px!, center, fill]", "[][top, fill, grow]"));
        // add(this.posterLabel, "wrap");
        // add(this.selectPanel);

    }

    @Override
    public void itemStateChanged(final ItemEvent e) {
        final DoubleTuple<TMDBRequestType, Integer> item = new DoubleTuple<TMDBRequestType, Integer>(getType(), getItemId());
        if (e.getStateChange() == ItemEvent.DESELECTED) {
            this.selectedItems.remove(item);
            this.selectPanel.setBackground(UIManager.getColor("panel.background"));
        } else if (e.getStateChange() == ItemEvent.SELECTED) {
            this.selectedItems.add(item);
            this.selectPanel.setBackground(Color.GREEN.brighter());
        }
        repaint();
    }

    abstract protected TMDBRequestType getType();

    abstract protected Integer getItemId();
}
