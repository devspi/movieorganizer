package spi.movieorganizer.display.view;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;
import spi.movieorganizer.controller.tmdb.TMDBRequestResult;
import spi.movieorganizer.display.MovieOrganizerSession;
import spi.movieorganizer.display.resources.MovieOrganizerStaticResources;
import spi.movieorganizer.display.view.loadresult.LoadResultPanel;
import spi.movieorganizer.display.view.searchresult.SearchResultPanel;
import spi.movieorganizer.repository.MovieOrganizerConstant;
import exane.osgi.jexlib.common.annotation.JexAction;
import exane.osgi.jexlib.common.annotation.injector.ActionInjector;
import exane.osgi.jexlib.core.action.Executable;

public class SearchPanel extends JPanel {

    private final JTextField searchTextField;
    private final JButton    searchButton;
    private final JButton    previousButton;
    private final JButton    nextButton;
    private final JButton    loadButton;

    public SearchPanel() {
        ActionInjector.inject(this);
        this.searchTextField = new JTextField();
        this.searchButton = new JButton(getActionMap().get("search"));
        this.previousButton = new JButton(getActionMap().get("previousView"));
        this.nextButton = new JButton(getActionMap().get("nextView"));
        this.loadButton = new JButton(getActionMap().get("loadFolder"));
        setLayout(new MigLayout());
        add(this.searchTextField, "width 100px!");
        add(this.searchButton);

        add(this.previousButton);
        add(this.nextButton);
        add(this.loadButton);
    }

    @JexAction(source = MovieOrganizerStaticResources.PROPERTIES_ACTIONS)
    private void search() {
        MovieOrganizerSession.getSession().getControllerRepository().getTmdbController().search(this.searchTextField.getText(), Locale.FRENCH, new Executable<TMDBRequestResult>() {

            @Override
            public void execute(final TMDBRequestResult result) {
                MovieOrganizerSession.getCenterPanel().setContent(new SearchResultPanel(result));
            }
        });
    }

    @JexAction(source = MovieOrganizerStaticResources.PROPERTIES_ACTIONS)
    private void previousView() {
        MovieOrganizerSession.getCenterPanel().previousView();
    }

    @JexAction(source = MovieOrganizerStaticResources.PROPERTIES_ACTIONS)
    private void nextView() {
        MovieOrganizerSession.getCenterPanel().nextView();
    }

    @JexAction(source = MovieOrganizerStaticResources.PROPERTIES_ACTIONS)
    private void loadFolder() {
        final JFileChooser fileChooser = new JFileChooser(MovieOrganizerConstant.HARD_DRIVE_MACOSX);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setFileFilter(new FileNameExtensionFilter("txt files and directory", "txt"));

        final int returnVal = fileChooser.showDialog(MovieOrganizerSession.getCenterPanel(), "Open");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File file = fileChooser.getSelectedFile();
            if (file.isDirectory()) {
            	System.out.println(file.getAbsolutePath());
                final List<String> moveList = new LinkedList<>();
                for (final File content : file.listFiles())
                    if (content.isDirectory())
                        moveList.add(content.getName());
                MovieOrganizerSession.getCenterPanel().setContent(new LoadResultPanel(moveList));
            }
        }
    }
}
