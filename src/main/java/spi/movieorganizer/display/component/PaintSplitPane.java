package spi.movieorganizer.display.component;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JSplitPane;

public class PaintSplitPane extends JSplitPane {

    private final float dividerLocation;
    private boolean     isPaint = false;

    public PaintSplitPane(final int orientation, final Component newLeftComponent, final Component newRightComponent, final float dividerLocation) {
        super(orientation, newLeftComponent, newRightComponent);
        this.dividerLocation = dividerLocation;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        if (this.isPaint == false) {
            setDividerLocation(this.dividerLocation);
            this.isPaint = true;
        }
        super.paintComponent(g);
    }
}
