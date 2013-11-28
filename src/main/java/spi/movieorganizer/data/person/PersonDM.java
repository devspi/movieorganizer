package spi.movieorganizer.data.person;

import spi.movieorganizer.data.MovieOrganizerType;
import exane.osgi.jexlib.data.manager.AbstractMutableListMapDM;
import exane.osgi.jexlib.data.object.ExaneDataType;

public class PersonDM extends AbstractMutableListMapDM<Integer, PersonDO> {

    @Override
    public ExaneDataType getDataType() {
        return MovieOrganizerType.PEOPLE;
    }

}
