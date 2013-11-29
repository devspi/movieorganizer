package spi.movieorganizer.display.table.renderer.movie;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import spi.movieorganizer.data.movie.UserMovieSettings.MovieFormat;

public class MovieFormatRenderer implements TableCellRenderer, ListCellRenderer {

    private final DefaultTableCellRenderer defaultTableCellRenderer;
    private final DefaultListCellRenderer  defaultListCellRenderer;

    public MovieFormatRenderer() {
        this.defaultTableCellRenderer = new DefaultTableCellRenderer();
        this.defaultListCellRenderer = new DefaultListCellRenderer();
    }

    @Override
    public Component getListCellRendererComponent(final JList list, Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        if (value instanceof MovieFormat)
            value = ((MovieFormat) value).getLabel();
        return this.defaultListCellRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        if (value instanceof MovieFormat)
            value = ((MovieFormat) value).getLabel();
        return this.defaultTableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
