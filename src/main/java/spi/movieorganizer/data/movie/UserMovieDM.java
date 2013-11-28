package spi.movieorganizer.data.movie;

import java.util.HashMap;
import java.util.Map;

import spi.movieorganizer.data.MovieOrganizerType;
import exane.osgi.jexlib.data.manager.AbstractMutableListMapDM;
import exane.osgi.jexlib.data.manager.listener.DataManagerListener;
import exane.osgi.jexlib.data.object.ExaneDataType;

public class UserMovieDM extends AbstractMutableListMapDM<Integer, UserMovieDO> {

    private final Map<Integer, Integer> countMovieByGenre;

    public UserMovieDM() {
        this.countMovieByGenre = new HashMap<>();
        addDataManagerListener(new UserMovieDMListener());
    }

    public Integer countMovieForGenre(final Integer genreId) {
        return this.countMovieByGenre.get(genreId);
    }

    private class UserMovieDMListener implements DataManagerListener<UserMovieDM, UserMovieDO> {

        @Override
        public void onDataManagerDelete(final UserMovieDM userMovieDM, final Integer index, final UserMovieDO userMovieDO) {
            for (final Integer genreId : userMovieDO.getMovie().getGenres())
                UserMovieDM.this.countMovieByGenre.put(genreId, UserMovieDM.this.countMovieByGenre.get(genreId) - 1);

        }

        @Override
        public void onDataManagerInsert(final UserMovieDM userMovieDM, final Integer index, final UserMovieDO userMovieDO) {
            for (final Integer genreId : userMovieDO.getMovie().getGenres()) {
                Integer count = 0;
                if ((count = UserMovieDM.this.countMovieByGenre.get(genreId)) == null)
                    UserMovieDM.this.countMovieByGenre.put(genreId, count = 0);
                UserMovieDM.this.countMovieByGenre.put(genreId, ++count);
            }
        }

        @Override
        public void onDataManagerUpdate(final UserMovieDM userMovieDM, final Integer index, final ExaneDataType arg2, final UserMovieDO userMovieDO, final Object arg4,
                final Object arg5) {
        }
    }

    @Override
    public ExaneDataType getDataType() {
        return MovieOrganizerType.USER_MOVIE;
    }

}
