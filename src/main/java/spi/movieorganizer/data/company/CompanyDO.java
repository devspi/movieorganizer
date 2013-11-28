package spi.movieorganizer.data.company;

import spi.movieorganizer.data.MovieOrganizerType;
import exane.osgi.jexlib.data.object.AbstractDataObject;
import exane.osgi.jexlib.data.object.ExaneDataType;

public class CompanyDO extends AbstractDataObject<Integer> {

    private final String  name;
    private final String  description;
    private final String  homepage;
    private final String  logoPath;
    private final Integer parentCompany;

    public CompanyDO(final Integer key, final String name) {
        this(key, name, null, null, null, null);
    }

    public CompanyDO(final Integer key, final String name, final String description, final String homepage, final String logoPath, final Integer parentCompany) {
        super(key);
        this.name = name;
        this.description = description;
        this.homepage = homepage;
        this.logoPath = logoPath;
        this.parentCompany = parentCompany;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getHomepage() {
        return this.homepage;
    }

    public String getLogoPath() {
        return this.logoPath;
    }

    public Integer getParentCompany() {
        return this.parentCompany;
    }

    @Override
    public ExaneDataType getDataType() {
        return MovieOrganizerType.COMPANY;
    }
}
