package spi.movieorganizer.display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import spi.movieorganizer.display.view.CenterPanel;
import spi.movieorganizer.display.view.LeftPanel;
import spi.movieorganizer.display.view.SearchPanel;

public class MovieOrganizer {

    private final JFrame mainFrame;

    private SearchPanel  searchPanel;
    private LeftPanel    leftPanel;
    private CenterPanel  centerPanel;

    public MovieOrganizer() {
        com.jidesoft.utils.Lm.verifyLicense("EXANE SA", "GALAHAD", "g5iB1zXHpS0ISu9k08PR3oBt8kSWAa33");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.getLookAndFeelDefaults().put("ClassLoader", MovieOrganizer.class.getClassLoader());
        } catch (final Exception e) {
            e.printStackTrace();
        }
        UIManager.put("Table.alternateRowColor", new Color(210, 225, 240));
        UIManager.put("List.selectionBackground", new Color(10, 36, 106));
        UIManager.put("Table.gridColor", Color.LIGHT_GRAY);
        UIManager.put("Table.selectionBackground", new Color(170, 185, 205));
        UIManager.put("Table.blinkingBackground", new Color(40, 77, 117));
        UIManager.put("Table.blinkingForeground", Color.WHITE);
        UIManager.put("Table.blinkingDelay", Integer.valueOf(400));

        this.mainFrame = new JFrame();
        this.mainFrame.setPreferredSize(new Dimension(800, 600));

        final MovieOrganizerClient session = new MovieOrganizerClient();
        MovieOrganizerSession.setSession(session);

        final JPanel pane = new JPanel(new BorderLayout());
        pane.add(this.searchPanel = new SearchPanel(), BorderLayout.NORTH);
        pane.add(this.leftPanel = new LeftPanel(), BorderLayout.WEST);
        pane.add(this.centerPanel = new CenterPanel(), BorderLayout.CENTER);
        this.mainFrame.getContentPane().add(pane);

        MovieOrganizerSession.setCenterPanel(this.centerPanel);

        this.mainFrame.pack();
        this.mainFrame.setVisible(true);
    }

    public JFrame getWindow() {
        return this.mainFrame;
    }
}
