package spi.movieorganizer.display.component;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class JLabelGradientBackground extends JLabel {

    public JLabelGradientBackground() {
        setText(" ");
        setBorder(BorderFactory.createEtchedBorder());
        setOpaque(false);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        final Graphics2D g2 = (Graphics2D) g;

        final Color bck = getBackground();

        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setPaint(new GradientPaint(0, 0, getBackground(), getWidth(), getHeight(), new Color(bck.getRed(), bck.getGreen(), bck.getBlue(), 127)));
        g2.fillRect(0, 0, getWidth(), getHeight());

        super.paintComponent(g);
    }
}
