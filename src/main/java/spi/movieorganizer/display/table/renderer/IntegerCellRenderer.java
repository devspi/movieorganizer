package spi.movieorganizer.display.table.renderer;

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import spi.movieorganizer.data.UnknownValue;
import spi.movieorganizer.data.util.FormatterFactory;
import exane.osgi.jexlib.common.swing.table.renderer.AbstractLabelCellRenderer;

/**
 * This class manages the renderer of a long value.
 * 
 * @author zigah_d
 * @author Tribondeau Brian
 */
public class IntegerCellRenderer extends AbstractLabelCellRenderer {
    private static final long   serialVersionUID         = 1L;

    public static DecimalFormat GROUP_3_DIGITS_FORMATTER = new DecimalFormat("#,##0", FormatterFactory.getDecimalFormatSymbols());

    private final DecimalFormat formatter;

    /**
     * Creates a renderer for long values.
     */
    public IntegerCellRenderer() {
        super();
        setHorizontalAlignment(SwingConstants.RIGHT);
        this.formatter = null;
    }

    public IntegerCellRenderer(final DecimalFormat formatter) {
        super();
        setHorizontalAlignment(SwingConstants.RIGHT);
        this.formatter = formatter;
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        final JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setToolTipText(null);

        if (value instanceof Integer) {
            final int intValue = (Integer) value;
            if (intValue == UnknownValue.UNKNOWN_INTEGER)
                label.setText(null);
            else if (this.formatter != null)
                label.setText(this.formatter.format(intValue));
        }

        return label;
    }
}
