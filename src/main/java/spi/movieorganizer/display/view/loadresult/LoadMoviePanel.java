package spi.movieorganizer.display.view.loadresult;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.jxlayer.JXLayer;

import spi.movieorganizer.controller.tmdb.TMDBRequestResult;
import spi.movieorganizer.controller.tmdb.TMDBRequestResult.TMDBRequestType;
import spi.movieorganizer.data.collection.CollectionDM;
import spi.movieorganizer.data.collection.CollectionDO;
import spi.movieorganizer.data.movie.MovieDM;
import spi.movieorganizer.data.movie.MovieDO;
import spi.movieorganizer.display.component.CollectionSelectablePanel;
import spi.movieorganizer.display.component.MovieSelectablePanel;
import exane.osgi.jexlib.common.swing.component.lockableui.BusyPainterUI;
import exane.osgi.jexlib.core.type.tuple.DoubleTuple;

public class LoadMoviePanel extends JPanel {

    private final String                        fileName;
    private final BusyPainterUI                 busyPainterUI;
    private final JPanel                        resultPanel;

    List<DoubleTuple<TMDBRequestType, Integer>> selectedItems;

    public LoadMoviePanel(final String fileName) {
        this.selectedItems = new ArrayList<>();
        final JLabel fileLabel = new JLabel(this.fileName = fileName);
        fileLabel.setBorder(BorderFactory.createEtchedBorder());

        this.resultPanel = new JPanel(new MigLayout("ins 0", "fill, grow", "[:168:, top]"));
        this.resultPanel.setBorder(BorderFactory.createEtchedBorder());

        final JXLayer<JComponent> resultLayer = new JXLayer<>(this.resultPanel, this.busyPainterUI = new BusyPainterUI());

        setLayout(new MigLayout("ins 0, gap 0", "fill, grow", "[top][top]"));
        add(fileLabel, "wrap");
        add(resultLayer, "wrap");

        this.busyPainterUI.setLocked(true);
    }

    public void updateContent(final TMDBRequestResult result) {
        String colConstraint = "";
        if (result.getMovieDM() != null && result.getMovieDM().getDataObjectCount() > 0) {
            colConstraint += "[][]";
            ((MigLayout) this.resultPanel.getLayout()).setColumnConstraints(colConstraint);
            this.resultPanel.add(new JLabel(result.getMovieDM().getDataObjectCount() + " movies found"), "wrap");
            final MovieDM movieDM = result.getMovieDM();
            if (movieDM.getDataObjectCount() == 1)
                this.resultPanel.add(new MovieSelectablePanel(this.selectedItems, movieDM.getDataObjectAt(0)));
            else
                for (final MovieDO movieDO : movieDM)
                    this.resultPanel.add(new MovieSelectablePanel(this.selectedItems, movieDO));
        }
        if (result.getCollectionDM() != null && result.getCollectionDM().getDataObjectCount() > 0) {
            colConstraint += colConstraint.isEmpty() ? "[][]" : "10[][]";
            ((MigLayout) this.resultPanel.getLayout()).setColumnConstraints(colConstraint);
            this.resultPanel.add(new JLabel(result.getCollectionDM().getDataObjectCount() + " collections found"), "newline, wrap");
            final CollectionDM collectionDM = result.getCollectionDM();
            if (collectionDM.getDataObjectCount() == 1)
                this.resultPanel.add(new CollectionSelectablePanel(this.selectedItems, collectionDM.getDataObjectAt(0)));
            else
                for (final CollectionDO collectionDO : collectionDM)
                    this.resultPanel.add(new CollectionSelectablePanel(this.selectedItems, collectionDO));
        }
        if (colConstraint.isEmpty())
            this.resultPanel.add(new JLabel("No result"));
        this.busyPainterUI.setLocked(false);
        this.resultPanel.setPreferredSize(this.resultPanel.getMinimumSize());
        revalidate();
    }

    public List<DoubleTuple<TMDBRequestType, Integer>> getSelectedItems() {
        return this.selectedItems;
    }

    public String getFileName() {
        return this.fileName;
    }
}
