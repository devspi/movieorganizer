package spi.movieorganizer.display;

import spi.movieorganizer.display.view.CenterPanel;

public class MovieOrganizerSession {

    private static CenterPanel          centerPanel;
    private static MovieOrganizerClient session;

    static void setCenterPanel(final CenterPanel centerPanel) {
        MovieOrganizerSession.centerPanel = centerPanel;
    }

    public static CenterPanel getCenterPanel() {
        return MovieOrganizerSession.centerPanel;
    }

    static void setSession(final MovieOrganizerClient session) {
        MovieOrganizerSession.session = session;
    }

    public static MovieOrganizerClient getSession() {
        return MovieOrganizerSession.session;
    }
}
