package spi.movieorganizer.data.collection;

import spi.movieorganizer.data.MovieOrganizerType;
import exane.osgi.jexlib.data.manager.AbstractMutableListMapDM;
import exane.osgi.jexlib.data.object.ExaneDataType;

public class CollectionPartDM extends AbstractMutableListMapDM<Integer, CollectionPartDO> {

    @Override
    public ExaneDataType getDataType() {
        return MovieOrganizerType.COLLECTION_PART;
    }

}
