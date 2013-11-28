package spi.movieorganizer.data.company;

import spi.movieorganizer.data.MovieOrganizerType;
import exane.osgi.jexlib.data.manager.AbstractMutableListMapDM;
import exane.osgi.jexlib.data.object.ExaneDataType;

public class CompanyDM extends AbstractMutableListMapDM<Integer, CompanyDO> {

    @Override
    public ExaneDataType getDataType() {
        return MovieOrganizerType.COMPANY;
    }
}
