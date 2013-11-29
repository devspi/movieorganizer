package spi.movieorganizer.display.component;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import net.miginfocom.swing.MigLayout;
import spi.movieorganizer.controller.tmdb.TMDBRequestResult.TMDBRequestType;
import spi.movieorganizer.data.collection.CollectionDO;
import spi.movieorganizer.data.movie.MovieDO;
import spi.movieorganizer.data.movie.UserMovieSettings.MovieFormat;
import spi.movieorganizer.data.movie.UserMovieSettings.MovieResolution;
import spi.movieorganizer.display.MovieOrganizerSession;
import spi.movieorganizer.display.table.renderer.movie.MovieFormatRenderer;
import spi.movieorganizer.display.table.renderer.movie.MovieResolutionRenderer;
import spi.movieorganizer.display.view.detail.CollectionDetailPanel;
import spi.movieorganizer.display.view.detail.MovieDetailPanel;
import spi.movieorganizer.display.view.loadresult.SelectedItemData;
import exane.osgi.jexlib.common.swing.component.label.JHyperlinkLabel;
import exane.osgi.jexlib.core.action.Executable;

public abstract class AbstractSelectableItemPanel extends JPanel implements ItemListener {
    private final Color            selectedColor = new Color(60, 210, 90);
    private final JPanel           selectPanel;
    private final JCheckBox        seenCheckbox;
    private final JComboBox        formatComboBox;
    private final JComboBox        resolutionComboBox;
    private final JPanel           settingPanel;

    private final SelectedItemData itemData;

    protected final JLabel         posterLabel;
    protected final JLabel         titleLabel;
    protected final JCheckBox      selectCheckbox;

    public AbstractSelectableItemPanel() {
        this.itemData = new SelectedItemData();
        this.posterLabel = new JLabel();
        this.posterLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.posterLabel.setBackground(Color.BLACK);
        this.posterLabel.setOpaque(true);
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
        this.selectCheckbox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(final ItemEvent e) {
            }
        });
        this.selectCheckbox.setOpaque(false);
        this.selectCheckbox.setBorder(BorderFactory.createEtchedBorder());
        this.selectCheckbox.addItemListener(this);

        this.formatComboBox = new JComboBox<>(MovieFormat.values());
        this.formatComboBox.setRenderer(new MovieFormatRenderer());
        this.formatComboBox.setSelectedItem(this.itemData.getSettings().getMovieFormat());
        this.formatComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(final ItemEvent e) {
                if (ItemEvent.SELECTED == e.getStateChange())
                    AbstractSelectableItemPanel.this.itemData.getSettings().setMovieFormat((MovieFormat) e.getItem());
            }
        });
        final JLabel formatLabel = new JLabel("Format");

        this.resolutionComboBox = new JComboBox<>(MovieResolution.values());
        this.resolutionComboBox.setRenderer(new MovieResolutionRenderer());
        this.resolutionComboBox.setSelectedItem(this.itemData.getSettings().getMovieResolution());
        this.resolutionComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(final ItemEvent e) {
                if (ItemEvent.SELECTED == e.getStateChange())
                    AbstractSelectableItemPanel.this.itemData.getSettings().setMovieResolution((MovieResolution) e.getItem());
            }
        });
        final JLabel resolutionLabel = new JLabel("Resolution");

        this.seenCheckbox = new JCheckBox("I saw it !");
        this.seenCheckbox.setOpaque(false);
        this.seenCheckbox.setMargin(new Insets(0, 0, 0, 0));

        this.settingPanel = new JPanel(new MigLayout("ins 0 10 0 0, gap 0, wrap", "fill, grow", "[][][][][]"));
        this.settingPanel.setOpaque(false);
        this.settingPanel.setVisible(false);
        this.settingPanel.add(this.seenCheckbox);
        this.settingPanel.add(formatLabel);
        this.settingPanel.add(this.formatComboBox);
        this.settingPanel.add(resolutionLabel);
        this.settingPanel.add(this.resolutionComboBox);

        // select panel right from the poster
        this.selectCheckbox.setText("I have it");
        this.selectPanel = new JPanel(new MigLayout("ins 0, gap 0, hidemode 3", "[left]", "[top][top][top]"));
        this.selectPanel.add(this.titleLabel, "wrap");
        this.selectPanel.add(this.selectCheckbox, "wrap");
        this.selectPanel.add(this.settingPanel);
        this.selectPanel.setBackground(null);
        setLayout(new MigLayout("ins 0, gap 0", "[94px!][120px!]", "[140::, fill, grow, top]"));
        add(this.posterLabel);
        add(this.selectPanel, "growx");
    }

    public void setSelected(final boolean selected) {
        this.selectCheckbox.setSelected(selected);
    }

    public boolean isSelected() {
        return this.selectCheckbox.isSelected();
    }

    public SelectedItemData getItemData() {
        this.itemData.setItemId(getItemId());
        this.itemData.setRequestType(getType());
        return this.itemData;
    }

    @Override
    public void itemStateChanged(final ItemEvent e) {
        this.selectPanel.setBackground(this.selectCheckbox.isSelected() ? this.selectedColor : UIManager.getColor("panel.background"));
        this.settingPanel.setVisible(this.selectCheckbox.isSelected());
        AbstractSelectableItemPanel.this.itemData.getSettings().setSeen(AbstractSelectableItemPanel.this.selectCheckbox.isSelected());
        repaint();
    }

    abstract protected TMDBRequestType getType();

    abstract protected Integer getItemId();
}
