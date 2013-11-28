package spi.movieorganizer.display.view.user;

import java.awt.BorderLayout;
import java.util.Locale;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import spi.movieorganizer.data.movie.MovieDO;
import spi.movieorganizer.data.movie.UserMovieDM;
import spi.movieorganizer.data.movie.UserMovieDO;
import spi.movieorganizer.display.MovieOrganizerSession;
import spi.movieorganizer.display.resources.MovieOrganizerStaticResources;
import spi.movieorganizer.display.table.column.movie.MovieOriginalTitleTableColumn;
import spi.movieorganizer.display.table.column.movie.MovieReleaseDateTableColumn;
import spi.movieorganizer.display.table.column.movie.MovieTitleTableColumn;
import spi.movieorganizer.display.view.detail.MovieDetailPanel;
import exane.osgi.jexlib.common.annotation.JexAction;
import exane.osgi.jexlib.common.annotation.injector.ActionInjector;
import exane.osgi.jexlib.common.swing.table.ExaneTableUtilities;
import exane.osgi.jexlib.common.swing.table.JExaneTable;
import exane.osgi.jexlib.common.swing.table.controller.TableControllerHelper;
import exane.osgi.jexlib.common.swing.table.decorator.StripedRowsColorCellDecorator;
import exane.osgi.jexlib.common.swing.table.header.filter.FilterHeaderTableModel;
import exane.osgi.jexlib.common.swing.table.header.filter.editor.DataListFilterEditor;
import exane.osgi.jexlib.common.swing.table.header.filter.editor.RegExpFilterEditor;
import exane.osgi.jexlib.common.swing.table.header.generic.JColumnTableHeader;
import exane.osgi.jexlib.common.swing.table.header.generic.MultiTableHeader;
import exane.osgi.jexlib.common.swing.table.header.generic.TableHeaderWrapper;
import exane.osgi.jexlib.core.action.Executable;
import exane.osgi.jexlib.core.action.Retrievable;
import exane.osgi.jexlib.data.manager.DataManagerProxy;
import exane.osgi.jexlib.data.manager.filter.DataObjectFilter.LogicalOperator;
import exane.osgi.jexlib.data.manager.filter.simple.LogicalDataObjectFilter;

public class UserMovieTablePanel extends JPanel {

    private MovieDetailPanel                             detailPanel;
    private UserMovieDataObjectFilter                    userMovieDataObjectFilter;
    private UserMovieTableModel                          userMovieTableModel;
    private JExaneTable                                  userMovieTable;

    private final LogicalDataObjectFilter<UserMovieDO>   compoundFilter;
    private final DataManagerProxy<Integer, UserMovieDO> userMovieDMProxy;
    private final UserMovieDM                            userMovieDM;

    public UserMovieTablePanel() {
        this.userMovieDM = MovieOrganizerSession.getSession().getDataManagerRepository().getUserMovieDM();

        this.userMovieDMProxy = new DataManagerProxy<>(this.userMovieDM);
        this.compoundFilter = new LogicalDataObjectFilter<>(LogicalOperator.AND);
        this.userMovieDMProxy.setDataObjectFilter(this.compoundFilter);

        this.compoundFilter.addDataObjectFilter(this.userMovieDataObjectFilter = new UserMovieDataObjectFilter());
        initComponents();
    }

    private void initComponents() {

        this.userMovieTableModel = new UserMovieTableModel(this.userMovieDMProxy, new Retrievable<Locale>() {

            @Override
            public Locale get() {
                return Locale.FRENCH;
            }
        });
        this.userMovieTableModel.setGlobalDecorator(new StripedRowsColorCellDecorator());

        this.userMovieTable = new JExaneTable(this.userMovieTableModel);
        this.userMovieTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(final ListSelectionEvent e) {
                if (!e.getValueIsAdjusting())
                    if (UserMovieTablePanel.this.userMovieTable.getSelectedRowCount() == 1) {
                        final UserMovieDO userMovieDO = UserMovieTablePanel.this.userMovieTableModel.getObjectAt(UserMovieTablePanel.this.userMovieTable
                                .convertRowIndexToModel(UserMovieTablePanel.this.userMovieTable.getSelectedRow()));
                        UserMovieTablePanel.this.detailPanel.lockLayer();
                        MovieOrganizerSession.getSession().getControllerRepository().getTmdbController()
                                .requestMovie(userMovieDO.getIdentifier().toString(), Locale.FRENCH, new Executable<MovieDO>() {

                                    @Override
                                    public void execute(final MovieDO arg0) {
                                        UserMovieTablePanel.this.detailPanel.setMovie(arg0);
                                    }
                                });
                    }
            }
        });

        ActionInjector.inject(this);
        final JPopupMenu menu = new JPopupMenu();
        menu.add(getActionMap().get("removeFromUserMovie"));

        this.userMovieTable.setComponentPopupMenu(menu);

        final TableControllerHelper tableControllerHelper = new TableControllerHelper(this.userMovieTable);

        final JScrollPane scrollPane = new JScrollPane(this.userMovieTable);
        ExaneTableUtilities.setupTableScrollPane(this.userMovieTable, scrollPane);
        scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, tableControllerHelper.createConfigPopupButton());
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        final MultiTableHeader multiHeader = new MultiTableHeader();
        multiHeader.addHeaderSection(new TableHeaderWrapper(this.userMovieTable.getTableHeader()));
        final FilterHeaderTableModel filterHeaderModel = new FilterHeaderTableModel();

        filterHeaderModel
                .addColumnHeader(new RegExpFilterEditor((TableColumn) this.userMovieTableModel.getColumn(this.userMovieTableModel.findColumn(MovieTitleTableColumn.class))));
        filterHeaderModel.addColumnHeader(new RegExpFilterEditor((TableColumn) this.userMovieTableModel.getColumn(this.userMovieTableModel
                .findColumn(MovieOriginalTitleTableColumn.class))));
        filterHeaderModel.addColumnHeader(new DataListFilterEditor((TableColumn) this.userMovieTableModel.getColumn(this.userMovieTableModel
                .findColumn(MovieReleaseDateTableColumn.class)), this.userMovieDMProxy));
        this.compoundFilter.addDataObjectFilter(filterHeaderModel);

        final JColumnTableHeader tableHeader = new JColumnTableHeader(this.userMovieTable, filterHeaderModel);
        multiHeader.addHeaderSection(tableHeader);

        scrollPane.setColumnHeaderView(multiHeader.getHeaderComponent());

        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, this.detailPanel = new MovieDetailPanel());

        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
    }

    public void setSelectedGenre(final Integer genreId) {
        this.userMovieDataObjectFilter.setSelectedGenre(genreId);

    }

    @JexAction(source = MovieOrganizerStaticResources.PROPERTIES_ACTIONS)
    private void removeFromUserMovie() {
        for (final Integer index : this.userMovieTable.getSelectedRows()) {
            final UserMovieDO userMovieDO = this.userMovieTableModel.getObjectAt(this.userMovieTable.convertRowIndexToModel(index));
            if (userMovieDO != null)
                MovieOrganizerSession.getSession().getControllerRepository().getUserMovieController().removeFromUserMovie(userMovieDO.getIdentifier());
        }
    }
}
