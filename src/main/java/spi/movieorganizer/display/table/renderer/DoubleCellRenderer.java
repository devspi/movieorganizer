package spi.movieorganizer.display.table.renderer;

import java.awt.Component;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import spi.movieorganizer.data.UnknownValue;
import spi.movieorganizer.data.util.FormatterFactory;
import exane.osgi.jexlib.common.swing.table.renderer.AbstractLabelCellRenderer;

/**
 * This class manages the renderer of a double value.
 * 
 * @author zigah_d
 */
public class DoubleCellRenderer extends AbstractLabelCellRenderer {
    private static final long                        serialVersionUID = 1L;

    private static final Map<Integer, DecimalFormat> decimalFormats   = new HashMap<Integer, DecimalFormat>();
    private DecimalFormat                            decimalFormat;
    private Integer                                  currentPrecision = null;

    /**
     * Creates a renderer for double values with a precision of 2 digits.
     */
    public DoubleCellRenderer() {
        this(2);
    }

    /**
     * Creates a renderer for double values.
     * 
     * @param precision
     *            The precision of the renderer number decimals will be displayed with <code>precision</code> digits, if it's positive. number decimals will be displayed with
     *            <code>precision</code> digits at most, if it's negative,
     */

    public DoubleCellRenderer(final int precision) {
        super();
        setHorizontalAlignment(SwingConstants.RIGHT);
        setPrecision(precision);
    }

    public void setGroupingUsed(final boolean grouping) {
        this.decimalFormat.setGroupingUsed(grouping);
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        final JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setToolTipText(null);

        if (value instanceof Double) {
            final Double doubleValue = (Double) value;
            if (doubleValue == UnknownValue.UNKNOWN_DOUBLE)
                label.setText(null);
            else
                label.setText(this.decimalFormat.format(doubleValue));
        } else if (value instanceof Integer) {
            final int intValue = (Integer) value;
            if (intValue == UnknownValue.UNKNOWN_INTEGER)
                label.setText(null);
        }

        return label;
    }

    /**
     * Sets the new precision.
     */
    public void setPrecision(int precision) {
        if (this.currentPrecision == null || this.currentPrecision != precision) {

            if (precision == UnknownValue.UNKNOWN_INTEGER)
                precision = 2;
            else if (precision > 6)
                // Display precision is limited to maximum 6 digits
                precision = 6;

            DecimalFormat decimalFormat = DoubleCellRenderer.decimalFormats.get(precision);
            if (decimalFormat == null) {
                String pattern = "#,##0.";
                for (int i = 0; i < Math.abs(precision); i++)
                    pattern += precision < 0 ? "#" : "0";
                decimalFormat = new DecimalFormat(pattern, FormatterFactory.getDecimalFormatSymbols());
                // Store the decimal format in the map for future use
                DoubleCellRenderer.decimalFormats.put(precision, decimalFormat);
            }

            this.currentPrecision = precision;
            this.decimalFormat = decimalFormat;
        }
    }

    public Integer getCurrentPrecision() {
        return this.currentPrecision;
    }

    public DecimalFormat getDecimalFormat() {
        return this.decimalFormat;
    }
}
