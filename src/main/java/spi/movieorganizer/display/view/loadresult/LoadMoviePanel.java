package spi.movieorganizer.display.view.loadresult;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.jxlayer.JXLayer;

import spi.movieorganizer.controller.tmdb.TMDBRequestResult;
import spi.movieorganizer.data.collection.CollectionDM;
import spi.movieorganizer.data.collection.CollectionDO;
import spi.movieorganizer.data.movie.LoadMovie;
import spi.movieorganizer.data.movie.MovieDM;
import spi.movieorganizer.data.movie.MovieDO;
import spi.movieorganizer.display.component.AbstractSelectableItemPanel;
import spi.movieorganizer.display.component.CollectionSelectablePanel;
import spi.movieorganizer.display.component.JLabelGradientBackground;
import spi.movieorganizer.display.component.MovieSelectablePanel;
import exane.osgi.jexlib.common.swing.component.lockableui.BusyPainterUI;
import exane.osgi.jexlib.core.type.tuple.DoubleTuple;

public class LoadMoviePanel extends JPanel {

    private final String                            fileName;
    private final BusyPainterUI                     busyPainterUI;
    private final JPanel                            resultPanel;

    private final List<AbstractSelectableItemPanel> selectablePanels;

    public LoadMoviePanel(final String fileName) {
        this.selectablePanels = new ArrayList<>();
        final JLabel fileLabel = new JLabelGradientBackground();
        fileLabel.setText(this.fileName = fileName);
        fileLabel.setBackground(Color.BLUE);
        fileLabel.setForeground(Color.WHITE);
        fileLabel.setFont(fileLabel.getFont().deriveFont(Font.BOLD, 14f));

        this.resultPanel = new JPanel(new MigLayout("ins 0", "fill, grow", "[:168:, top]"));
        this.resultPanel.setBorder(BorderFactory.createEtchedBorder());
        this.resultPanel.setBackground(new Color(220, 230, 255));
        final JXLayer<JComponent> resultLayer = new JXLayer<>(this.resultPanel, this.busyPainterUI = new BusyPainterUI());

        setLayout(new MigLayout("ins 0, gap 0", "fill, grow", "[top][top]"));
        add(fileLabel, "wrap");
        add(resultLayer, "wrap");

        this.busyPainterUI.setLocked(true);
    }

    public void updateContent(final DoubleTuple<LoadMovie, TMDBRequestResult> resultTuple) {
        String colConstraint = "";

        final JLabel resultLabel = new JLabelGradientBackground();
        resultLabel.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));
        resultLabel.setBackground(Color.BLACK);
        resultLabel.setForeground(Color.WHITE);

        final LoadMovie loadMovie = resultTuple.getFirstValue();
        final TMDBRequestResult requestResult = resultTuple.getSecondValue();

        if (requestResult.getMovieDM() != null && requestResult.getMovieDM().getDataObjectCount() > 0) {
            colConstraint += "[][]";
            ((MigLayout) this.resultPanel.getLayout()).setColumnConstraints(colConstraint);
            resultLabel.setText(requestResult.getMovieDM().getDataObjectCount() + " movies found");
            this.resultPanel.add(resultLabel, "spanx, growx, wrap");
            final MovieDM movieDM = requestResult.getMovieDM();
            for (final MovieDO movieDO : movieDM) {
                final MovieSelectablePanel panel = new MovieSelectablePanel(movieDO);
                this.resultPanel.add(panel);
                this.selectablePanels.add(panel);
                panel.setSelected(movieDM.getDataObjectCount() == 1);
            }
        }
        if (requestResult.getCollectionDM() != null && requestResult.getCollectionDM().getDataObjectCount() > 0) {
            colConstraint += colConstraint.isEmpty() ? "[][]" : "10[][]";
            ((MigLayout) this.resultPanel.getLayout()).setColumnConstraints(colConstraint);
            resultLabel.setText(requestResult.getCollectionDM().getDataObjectCount() + " collections found");
            this.resultPanel.add(resultLabel, "spanx, growx, wrap");
            final CollectionDM collectionDM = requestResult.getCollectionDM();
            for (final CollectionDO collectionDO : collectionDM) {
                final CollectionSelectablePanel panel = new CollectionSelectablePanel(collectionDO);
                this.resultPanel.add(panel);
                this.selectablePanels.add(panel);
                panel.setSelected(collectionDM.getDataObjectCount() == 1);
            }
        }
        if (colConstraint.isEmpty()) {
            resultLabel.setText("No result");
            this.resultPanel.add(resultLabel);
        }
        this.busyPainterUI.setLocked(false);
        this.resultPanel.setPreferredSize(this.resultPanel.getMinimumSize());
        revalidate();
    }

    public List<SelectedItemData> getSelectedItemsData() {
        final List<SelectedItemData> itemDatas = new ArrayList<>();
        for (final AbstractSelectableItemPanel panel : this.selectablePanels)
            if (panel.isSelected())
                itemDatas.add(panel.getItemData());
        return itemDatas;
    }

    public String getFileName() {
        return this.fileName;
    }
}
