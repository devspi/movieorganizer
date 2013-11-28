import javax.swing.SwingUtilities;

import spi.movieorganizer.display.MovieOrganizer;
import exane.osgi.jexlib.data.manager.AbstractExaneDataManager;

public class MainClass {

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                AbstractExaneDataManager.setDefaultThreadOwnerPolicy(null);
                new MovieOrganizer();
            }
        });
    }
}
