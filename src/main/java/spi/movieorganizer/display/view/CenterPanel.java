package spi.movieorganizer.display.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.util.LinkedList;

import javax.swing.JPanel;

public class CenterPanel extends JPanel {

    private final LinkedList<Container> contentHistory;
    private int                         viewIndex;
    private Container                   currentContent;

    public CenterPanel() {
        this.contentHistory = new LinkedList<>();
        setLayout(new BorderLayout());
        setBackground(Color.BLUE);
    }

    public void setContent(final Container content) {
        if (this.currentContent != null)
            this.contentHistory.add(this.currentContent);
        removeAll();
        add(this.currentContent = content, BorderLayout.CENTER);
        this.viewIndex = this.contentHistory.size();
        revalidate();
    }

    public Container getContent() {
        return this.currentContent;
    }

    public int getHistoryCount() {
        return this.contentHistory.size();
    }

    public void previousView() {
        if (this.viewIndex > 0) {
            if (this.contentHistory.contains(this.currentContent) == false)
                this.contentHistory.add(this.currentContent);
            removeAll();
            add(this.currentContent = this.contentHistory.get(--this.viewIndex));
            revalidate();
            repaint();
        }
    }

    public void nextView() {
        if (this.viewIndex < this.contentHistory.size() - 1) {
            removeAll();
            add(this.currentContent = this.contentHistory.get(++this.viewIndex));
            revalidate();
            repaint();
        }
    }
}
