package spi.movieorganizer.data.movie;

import spi.movieorganizer.data.MovieOrganizerType;
import exane.osgi.jexlib.data.manager.AbstractMutableListMapDM;
import exane.osgi.jexlib.data.object.ExaneDataType;

public class MovieDM extends AbstractMutableListMapDM<Integer, MovieDO> {

    @Override
    public ExaneDataType getDataType() {
        return MovieOrganizerType.MOVIE;
    }

}
