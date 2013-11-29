package spi.movieorganizer.display.view.detail;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.jxlayer.JXLayer;

import spi.movieorganizer.controller.tmdb.TMDBController;
import spi.movieorganizer.data.movie.MovieDO;
import spi.movieorganizer.data.util.TimeTools;
import spi.movieorganizer.display.component.StarRater;
import exane.osgi.jexlib.common.swing.component.lockableui.BusyPainterUI;

public class MovieDetailPanel extends JPanel {

    private final StarRater           starRater;
    private final JLabel              titleLabel;
    private final JLabel              releaseDateLabel;
    private final JLabel              voteCountLabel;
    private final JLabel              posterLabel;
    private final JTextArea           overviewLabel;
    private final JScrollPane         scrollPane;
    private final JXLayer<JComponent> lockedLayer;
    private final BusyPainterUI       busyPainterUI;

    private MovieDO                   movieDO;

    public MovieDetailPanel() {
        this.titleLabel = new JLabel();
        this.titleLabel.setFont(this.titleLabel.getFont().deriveFont(Font.BOLD, 20f));

        this.overviewLabel = new JTextArea();
        this.overviewLabel.setFont(UIManager.getFont("Label.font"));
        this.overviewLabel.setLineWrap(true);
        this.overviewLabel.setWrapStyleWord(true);
        this.overviewLabel.setBackground(null);
        this.overviewLabel.setBorder(null);

        this.releaseDateLabel = new JLabel();
        this.releaseDateLabel.setFont(this.releaseDateLabel.getFont().deriveFont(Font.BOLD, 13f));
        this.releaseDateLabel.setForeground(Color.DARK_GRAY);
        this.releaseDateLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        this.releaseDateLabel.setVerticalAlignment(SwingConstants.BOTTOM);

        this.voteCountLabel = new JLabel();
        this.posterLabel = new JLabel();
        this.posterLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.starRater = new StarRater(10, 0, 0);
        this.starRater.setEnabled(false);

        final JLabel overviewTitleLabel = new JLabel("Overview");
        overviewTitleLabel.setFont(overviewTitleLabel.getFont().deriveFont(Font.BOLD));

        final JPanel posterPanel = new JPanel(new MigLayout("ins 0, gap 0", "fill, grow", "fill, grow"));
        posterPanel.setBackground(null);
        posterPanel.add(this.posterLabel, BorderLayout.NORTH);

        final JPanel contentPanel = new JPanel(new MigLayout("ins 0", "left", "[top][top]15[top][top]"));
        contentPanel.setBackground(null);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 10));
        contentPanel.add(this.titleLabel, "split 2");
        contentPanel.add(this.releaseDateLabel, "bottom, gapbottom 2px, wrap");
        contentPanel.add(this.starRater, "split 2");
        contentPanel.add(this.voteCountLabel, "wrap");
        contentPanel.add(overviewTitleLabel, "spanx, wrap");
        contentPanel.add(this.overviewLabel, "spanx, growx");

        this.scrollPane = new JScrollPane(contentPanel);
        this.scrollPane.setBackground(null);
        this.scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.scrollPane.setBorder(null);

        final JPanel globalPanel = new JPanel(new MigLayout("ins 0, gap 0", "[][fill, grow]", "fill, grow"));
        globalPanel.setBackground(new Color(234, 234, 234));
        globalPanel.add(posterPanel);
        globalPanel.add(this.scrollPane);

        this.lockedLayer = new JXLayer<>(globalPanel, this.busyPainterUI = new BusyPainterUI());
        this.lockedLayer.getView().setVisible(false);
        setLayout(new BorderLayout());
        add(this.lockedLayer, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(final ComponentEvent e) {
                resizeContent();
            }
        });
    }

    public void lockLayer() {
        this.busyPainterUI.setLocked(true);
    }

    private void resizeContent() {
        final Dimension dimension = new Dimension(this.scrollPane.getViewport().getSize().width - 15, MovieDetailPanel.this.overviewLabel.getMinimumSize().height);
        this.overviewLabel.setSize(dimension);
        this.overviewLabel.setPreferredSize(dimension);
    }

    public void setMovie(final MovieDO movieDO) {
        this.movieDO = movieDO;
        this.titleLabel.setText(movieDO.getTitle(Locale.FRENCH));
        this.releaseDateLabel.setText("(" + TimeTools.format(TimeTools.yyyy_PATTERN, movieDO.getReleaseDate()) + ")");
        this.releaseDateLabel.setToolTipText(TimeTools.format(TimeTools.dd_MM_yyyy_PATTERN, movieDO.getReleaseDate()));
        this.voteCountLabel.setText(movieDO.getVoteAverage() + "/10 (" + movieDO.getVoteCount() + " votes)");
        this.overviewLabel.setText(movieDO.getOverview(Locale.FRENCH));
        final String src = movieDO.getPosterPath() != null ? TMDBController.BASE_URL + "w185" + movieDO.getPosterPath() : "http://d3a8mw37cqal2z.cloudfront.net/assets/e6497422f20fa74/images/no-poster-w185.jpg";
        this.posterLabel.setText("<html><img src=\"" + src + "\"></html>");

        this.starRater.setRating(movieDO.getVoteAverage().floatValue());
        this.lockedLayer.getView().setVisible(true);
        this.busyPainterUI.setLocked(false);

        repaint();
        revalidate();

        this.shouldResize = true;
    }

    public MovieDO getMovieDO() {
        return this.movieDO;
    }

    private boolean shouldResize = false;

    @Override
    protected void paintComponent(final Graphics g) {
        if (this.shouldResize) {
            resizeContent();
            this.shouldResize = false;
        }
        super.paintComponent(g);
    }
}
