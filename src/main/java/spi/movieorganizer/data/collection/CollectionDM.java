package spi.movieorganizer.data.collection;

import spi.movieorganizer.data.MovieOrganizerType;
import exane.osgi.jexlib.data.manager.AbstractMutableListMapDM;
import exane.osgi.jexlib.data.object.ExaneDataType;

public class CollectionDM extends AbstractMutableListMapDM<Integer, CollectionDO> {

    @Override
    public ExaneDataType getDataType() {
        return MovieOrganizerType.COLLECTION;
    }

}
