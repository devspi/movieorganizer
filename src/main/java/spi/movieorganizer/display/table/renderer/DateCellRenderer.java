package spi.movieorganizer.display.table.renderer;

import java.awt.Component;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import spi.movieorganizer.data.UnknownValue;
import spi.movieorganizer.data.util.TimeTools;
import exane.osgi.jexlib.common.swing.table.renderer.AbstractLabelCellRenderer;

/**
 * This class manages the renderer of a date value.
 * 
 * @author zigah_d
 */
public class DateCellRenderer extends AbstractLabelCellRenderer {
    private static final long serialVersionUID = 1L;

    private static DateFormat DATE_FORMAT      = TimeTools.getDateFormat(TimeTools.dd_MM_yyyy_PATTERN);

    public DateCellRenderer(final DateFormat dateFormat) {
        super();
        setHorizontalAlignment(SwingConstants.RIGHT);
        DateCellRenderer.DATE_FORMAT = dateFormat;
    }

    public DateCellRenderer() {
        super();
        setHorizontalAlignment(SwingConstants.RIGHT);
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        final JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value instanceof Date) {
            final Date date = (Date) value;
            if (!date.equals(UnknownValue.UNKNOWN_TIME))
                label.setText(DateCellRenderer.DATE_FORMAT.format(date));
            else
                label.setText("");
        } else
            label.setText("");

        return label;
    }
}
