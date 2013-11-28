package spi.movieorganizer.display.table.renderer;

import java.awt.Component;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import spi.movieorganizer.data.UnknownValue;
import spi.movieorganizer.data.util.FormatterFactory;
import exane.osgi.jexlib.common.swing.table.renderer.AbstractLabelCellRenderer;

/**
 * This class defines the renderer for the percent
 * 
 * @author zigah_d
 * @author Tribondeau Brian
 */
public class PercentCellRenderer extends AbstractLabelCellRenderer {
    private static final long  serialVersionUID = 1L;
    private final NumberFormat format;

    /**
     * This class creates a renderer for double values.
     */
    public PercentCellRenderer(final int precision) {
        super();
        String percStr = "0";
        if (precision > 0)
            percStr += ".";
        for (int precisionIndex = 0; precisionIndex < precision; precisionIndex++)
            percStr += "0";
        percStr += " %";

        this.format = new DecimalFormat(percStr, FormatterFactory.getDecimalFormatSymbols());
        setHorizontalAlignment(SwingConstants.RIGHT);
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        final JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setToolTipText(null);

        final Number numberValue = (Number) value;

        if (numberValue != null && numberValue instanceof Double) {
            final Double doubleValue = (Double) numberValue;
            if (doubleValue == UnknownValue.UNKNOWN_PERCENT || doubleValue == UnknownValue.UNKNOWN_DOUBLE)
                label.setText(null);
            else
                label.setText(this.format.format(doubleValue));
        } else if (numberValue != null && numberValue instanceof Integer) {
            final Integer intValue = (Integer) numberValue;
            if (intValue == UnknownValue.UNKNOWN_INTEGER)
                label.setText(null);
            else
                label.setText(intValue + "%");
        }

        return label;
    }
}
