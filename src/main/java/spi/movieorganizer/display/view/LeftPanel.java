package spi.movieorganizer.display.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import spi.movieorganizer.data.genre.GenreDM;
import spi.movieorganizer.data.genre.GenreDO;
import spi.movieorganizer.data.movie.UserMovieDM;
import spi.movieorganizer.data.movie.UserMovieDO;
import spi.movieorganizer.display.MovieOrganizerSession;
import spi.movieorganizer.display.view.user.UserMovieTablePanel;
import exane.osgi.jexlib.data.manager.listener.DataManagerListener;
import exane.osgi.jexlib.data.object.ExaneDataType;

public class LeftPanel extends JPanel {

    private final List<Integer> userGenreList;

    private final JTree         tree;
    private final GenreDM       genreDM;
    private final UserMovieDM   userMovieDM;

    public LeftPanel() {
        this.genreDM = MovieOrganizerSession.getSession().getDataManagerRepository().getGenreDM();
        this.userMovieDM = MovieOrganizerSession.getSession().getDataManagerRepository().getUserMovieDM();

        final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("My movies");
        this.tree = new JTree(new DefaultTreeModel(rootNode));
        this.tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(final TreeSelectionEvent e) {
                final TreePath path = e.getPath();
                if (path.getLastPathComponent() != null) {
                    final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    Container container = MovieOrganizerSession.getCenterPanel().getContent();
                    if (container == null || container instanceof UserMovieTablePanel == false)
                        MovieOrganizerSession.getCenterPanel().setContent(container = new UserMovieTablePanel());

                    if (node.getUserObject() instanceof Integer)
                        ((UserMovieTablePanel) container).setSelectedGenre((Integer) node.getUserObject());
                    else
                        ((UserMovieTablePanel) container).setSelectedGenre(null);
                }
            }
        });
        this.tree.setCellRenderer(new DefaultTreeCellRenderer() {

            @Override
            public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean selected, final boolean expanded, final boolean leaf, final int row,
                    final boolean hasFocus) {
                final JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

                final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

                if (node.getUserObject() instanceof Integer) {
                    final Integer genreId = (Integer) node.getUserObject();
                    final GenreDO genreDO = LeftPanel.this.genreDM.getDataObject(genreId);
                    if (genreDO != null)
                        label.setText(genreDO.getName(Locale.FRENCH) + " (" + LeftPanel.this.userMovieDM.countMovieForGenre(genreId) + ")");
                }
                return label;
            }
        });

        final JScrollPane scrollPane = new JScrollPane(this.tree);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(150, 0));
        add(scrollPane, BorderLayout.CENTER);

        this.userMovieDM.addDataManagerListener(new UserMovieDMListener());
        this.genreDM.addDataManagerListener(new GenreDMListener());
        this.userGenreList = new ArrayList<>();
    }

    private class UserMovieDMListener implements DataManagerListener<UserMovieDM, UserMovieDO> {

        @Override
        public void onDataManagerDelete(final UserMovieDM userMovieDM, final Integer index, final UserMovieDO userMovieDO) {
            final DefaultTreeModel model = (DefaultTreeModel) LeftPanel.this.tree.getModel();
            for (final Integer genreId : userMovieDO.getMovie().getGenres())
                if (userMovieDM.countMovieForGenre(genreId) == 0)
                    for (int i = 0; i < model.getChildCount(model.getRoot()); i++) {
                        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) model.getChild(model.getRoot(), i);
                        if (node.getUserObject().equals(genreId)) {
                            model.removeNodeFromParent(node);
                            break;
                        }
                    }
        }

        @Override
        public void onDataManagerInsert(final UserMovieDM userMovieDM, final Integer index, final UserMovieDO userMovieDO) {
            final DefaultTreeModel model = (DefaultTreeModel) LeftPanel.this.tree.getModel();
            for (final Integer genreId : userMovieDO.getMovie().getGenres())
                if (LeftPanel.this.userGenreList.contains(genreId) == false) {
                    model.insertNodeInto(new DefaultMutableTreeNode(genreId), (MutableTreeNode) model.getRoot(), model.getChildCount(model.getRoot()));
                    LeftPanel.this.userGenreList.add(genreId);
                    ((DefaultTreeModel) LeftPanel.this.tree.getModel()).reload();
                }
        }

        @Override
        public void onDataManagerUpdate(final UserMovieDM userMovieDM, final Integer index, final ExaneDataType arg2, final UserMovieDO userMovieDO, final Object arg4,
                final Object arg5) {
            // TODO Auto-generated method stub

        }
    }

    private class GenreDMListener implements DataManagerListener<GenreDM, GenreDO> {

        @Override
        public void onDataManagerDelete(final GenreDM arg0, final Integer arg1, final GenreDO arg2) {
            // nothing todo
        }

        @Override
        public void onDataManagerInsert(final GenreDM arg0, final Integer arg1, final GenreDO arg2) {
            ((DefaultTreeModel) LeftPanel.this.tree.getModel()).reload();
        }

        @Override
        public void onDataManagerUpdate(final GenreDM arg0, final Integer arg1, final ExaneDataType arg2, final GenreDO arg3, final Object arg4, final Object arg5) {
            // nothing todo
        }

    }

}
