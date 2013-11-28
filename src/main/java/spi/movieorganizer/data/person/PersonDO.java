package spi.movieorganizer.data.person;

import spi.movieorganizer.data.MovieOrganizerType;
import exane.osgi.jexlib.data.object.AbstractDataObject;
import exane.osgi.jexlib.data.object.ExaneDataType;

public class PersonDO extends AbstractDataObject<Integer> {

    private final boolean adult;
    private final String  biography;
    private final String  birthday;
    private final String  deathday;
    private final String  homepage;
    private final String  name;
    private final String  placeOfBirth;
    private final String  profilePath;

    public PersonDO(final Integer key, final boolean adult, final String biography, final String birthday, final String deathday, final String homepage, final String name,
            final String placeOfBirth, final String profilePath) {
        super(key);
        this.adult = adult;
        this.biography = biography;
        this.birthday = birthday;
        this.deathday = deathday;
        this.homepage = homepage;
        this.name = name;
        this.placeOfBirth = placeOfBirth;
        this.profilePath = profilePath;
    }

    public boolean isAdult() {
        return this.adult;
    }

    public String getBiography() {
        return this.biography;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public String getDeathday() {
        return this.deathday;
    }

    public String getHomepage() {
        return this.homepage;
    }

    public String getName() {
        return this.name;
    }

    public String getPlaceOfBirth() {
        return this.placeOfBirth;
    }

    public String getProfilePath() {
        return this.profilePath;
    }

    @Override
    public ExaneDataType getDataType() {
        return MovieOrganizerType.PEOPLE;
    }
}
