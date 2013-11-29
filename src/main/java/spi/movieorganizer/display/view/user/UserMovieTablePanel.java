package spi.movieorganizer.display.view.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import net.miginfocom.swing.MigLayout;
import spi.movieorganizer.data.movie.MovieDO;
import spi.movieorganizer.data.movie.UserMovieDM;
import spi.movieorganizer.data.movie.UserMovieDO;
import spi.movieorganizer.display.MovieOrganizerSession;
import spi.movieorganizer.display.component.PaintSplitPane;
import spi.movieorganizer.display.resources.MovieOrganizerStaticResources;
import spi.movieorganizer.display.table.column.movie.MovieOriginalTitleTableColumn;
import spi.movieorganizer.display.table.column.movie.MovieReleaseYearTableColumn;
import spi.movieorganizer.display.table.column.movie.MovieTitleTableColumn;
import spi.movieorganizer.display.view.detail.MovieDetailPanel;
import exane.osgi.jexlib.common.annotation.JexAction;
import exane.osgi.jexlib.common.annotation.injector.ActionInjector;
import exane.osgi.jexlib.common.swing.component.panel.TableSelectionInformationPanel;
import exane.osgi.jexlib.common.swing.table.ExaneTableUtilities;
import exane.osgi.jexlib.common.swing.table.JExaneTable;
import exane.osgi.jexlib.common.swing.table.controller.TableControllerHelper;
import exane.osgi.jexlib.common.swing.table.controller.column.HorizontalScrollBarTableController;
import exane.osgi.jexlib.common.swing.table.controller.column.PackColumnsTableController;
import exane.osgi.jexlib.common.swing.table.controller.font.FontPoliceTableController;
import exane.osgi.jexlib.common.swing.table.controller.font.FontSizeTableController;
import exane.osgi.jexlib.common.swing.table.controller.font.FontStyleTableController;
import exane.osgi.jexlib.common.swing.table.controller.header.HeaderFilterTableController;
import exane.osgi.jexlib.common.swing.table.controller.header.HeaderGroupsTableController;
import exane.osgi.jexlib.common.swing.table.controller.header.HeaderSmallFontTableController;
import exane.osgi.jexlib.common.swing.table.decorator.StripedRowsColorCellDecorator;
import exane.osgi.jexlib.common.swing.table.header.filter.FilterHeaderTableModel;
import exane.osgi.jexlib.common.swing.table.header.filter.editor.DataListFilterEditor;
import exane.osgi.jexlib.common.swing.table.header.filter.editor.RegExpFilterEditor;
import exane.osgi.jexlib.common.swing.table.header.generic.JColumnTableHeader;
import exane.osgi.jexlib.common.swing.table.header.generic.MultiTableHeader;
import exane.osgi.jexlib.common.swing.table.header.generic.TableHeaderWrapper;
import exane.osgi.jexlib.common.swing.table.listener.table.SelectionOnRightMousePressedListener;
import exane.osgi.jexlib.core.action.Executable;
import exane.osgi.jexlib.core.action.Retrievable;
import exane.osgi.jexlib.data.manager.DataManagerProxy;
import exane.osgi.jexlib.data.manager.filter.DataObjectFilter.LogicalOperator;
import exane.osgi.jexlib.data.manager.filter.DataObjectFilterListener;
import exane.osgi.jexlib.data.manager.filter.simple.LogicalDataObjectFilter;

public class UserMovieTablePanel extends JPanel {

    private TableSelectionInformationPanel               selectionInfoPanel;
    private JSplitPane                                   contentSplitPane;
    private JToggleButton                                splitScreenButton;
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
        this.compoundFilter.addDataObjectFilterListener(new DataObjectFilterListener() {

            @Override
            public void onDataObjectFilterUpdate() {
                UserMovieTablePanel.this.selectionInfoPanel.updateCount();
            }
        });
        initComponents();
    }

    private void initComponents() {
        this.selectionInfoPanel = new TableSelectionInformationPanel(this.userMovieDM, this.userMovieDMProxy);
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
                if (!e.getValueIsAdjusting()) {
                    UserMovieTablePanel.this.selectionInfoPanel.setSelectedRowCount(UserMovieTablePanel.this.userMovieTable.getSelectedRowCount());
                    if (UserMovieTablePanel.this.splitScreenButton.isSelected())
                        loadMovieDetail();
                }
            }
        });

        ActionInjector.inject(this);
        final JPopupMenu menu = new JPopupMenu();
        menu.add(getActionMap().get("removeFromUserMovie"));

        this.userMovieTable.setComponentPopupMenu(menu);
        this.userMovieTable.addMouseListener(new SelectionOnRightMousePressedListener());

        final TableRowSorter<UserMovieTableModel> tableRowSorter = new TableRowSorter<UserMovieTableModel>(this.userMovieTableModel);
        this.userMovieTable.setRowSorter(tableRowSorter);
        this.userMovieTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        final TableControllerHelper tableControllerHelper = new TableControllerHelper(this.userMovieTable);
        tableControllerHelper.addTableController(new PackColumnsTableController());
        tableControllerHelper.addTableController(new HorizontalScrollBarTableController(true));
        tableControllerHelper.addTableController(new HeaderGroupsTableController());
        tableControllerHelper.addTableController(new HeaderSmallFontTableController());
        tableControllerHelper.addSeparator();
        tableControllerHelper.addTableController(new FontSizeTableController());
        tableControllerHelper.addTableController(new FontStyleTableController());
        tableControllerHelper.addTableController(new FontPoliceTableController());

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
                .findColumn(MovieReleaseYearTableColumn.class)), this.userMovieDM));
        this.compoundFilter.addDataObjectFilter(filterHeaderModel);

        final JColumnTableHeader tableHeader = new JColumnTableHeader(this.userMovieTable, filterHeaderModel);
        multiHeader.addHeaderSection(tableHeader);

        scrollPane.setColumnHeaderView(multiHeader.getHeaderComponent());

        tableControllerHelper.addTableController(new HeaderFilterTableController(filterHeaderModel, multiHeader, tableHeader));

        this.contentSplitPane = new PaintSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, this.detailPanel = new MovieDetailPanel(), 1f);

        this.splitScreenButton = new JToggleButton(getActionMap().get("switchSplitScreen"));

        final JPanel selectionPanel = new JPanel(new MigLayout("ins 0, gap 0", "[]push[]", "[fill, grow, center]"));
        selectionPanel.add(this.splitScreenButton);
        selectionPanel.add(this.selectionInfoPanel);

        setLayout(new MigLayout("ins 0, gap 0", "fill, grow", "[][][fill, grow]"));
        add(new JSeparator(SwingConstants.HORIZONTAL), "spanx, growx");
        add(selectionPanel, "wrap");
        add(this.contentSplitPane);
    }

    public void setSelectedGenre(final Integer genreId) {
        this.userMovieDataObjectFilter.setSelectedGenre(genreId);
    }

    private void loadMovieDetail() {
        if (this.userMovieTable.getSelectedRowCount() == 1) {
            final UserMovieDO userMovieDO = UserMovieTablePanel.this.userMovieTableModel.getObjectAt(UserMovieTablePanel.this.userMovieTable
                    .convertRowIndexToModel(UserMovieTablePanel.this.userMovieTable.getSelectedRow()));
            if (userMovieDO != null)
                if (this.detailPanel.getMovieDO() == null || this.detailPanel.getMovieDO().getIdentifier().equals(userMovieDO.getIdentifier()) == false) {
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
    }

    @JexAction(source = MovieOrganizerStaticResources.PROPERTIES_ACTIONS)
    private void switchSplitScreen() {
        this.contentSplitPane.setDividerLocation(this.splitScreenButton.isSelected() ? 0.5f : 1f);
        loadMovieDetail();
    }

    @JexAction(source = MovieOrganizerStaticResources.PROPERTIES_ACTIONS)
    private void removeFromUserMovie() {
        final List<Integer> selectedMovieIds = new ArrayList<>();
        for (final Integer index : this.userMovieTable.getSelectedRows()) {
            final UserMovieDO userMovieDO = this.userMovieTableModel.getObjectAt(this.userMovieTable.convertRowIndexToModel(index));
            if (userMovieDO != null)
                selectedMovieIds.add(userMovieDO.getIdentifier());
        }
        MovieOrganizerSession.getSession().getControllerRepository().getUserMovieController().removeFromUserMovie(selectedMovieIds);
    }
}
