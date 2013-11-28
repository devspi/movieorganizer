package spi.movieorganizer.data.genre;

import spi.movieorganizer.data.MovieOrganizerType;
import exane.osgi.jexlib.data.manager.AbstractMutableListMapDM;
import exane.osgi.jexlib.data.object.ExaneDataType;

public class GenreDM extends AbstractMutableListMapDM<Integer, GenreDO> {

    @Override
    public ExaneDataType getDataType() {
        return MovieOrganizerType.GENRE;
    }

}
