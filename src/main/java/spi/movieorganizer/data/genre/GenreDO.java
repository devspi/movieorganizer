package spi.movieorganizer.data.genre;

import java.util.Locale;

import spi.movieorganizer.data.MovieOrganizerType;
import spi.movieorganizer.data.util.StringMultiLang;
import exane.osgi.jexlib.data.object.AbstractDataObject;
import exane.osgi.jexlib.data.object.ExaneDataType;

public class GenreDO extends AbstractDataObject<Integer> {

    private final StringMultiLang name;

    public GenreDO(final int id, final StringMultiLang name) {
        super(id);
        this.name = name;
    }

    public String getName(final Locale locale) {
        return this.name.getValue(locale);
    }

    public void setName(final Locale locale, final String value) {
        this.name.addValue(locale, value);
    }

    public StringMultiLang getNameMultiLang() {
        return this.name;
    }

    @Override
    public String toString() {
        return "id: " + getIdentifier() + " " + this.name;
    }

    @Override
    public ExaneDataType getDataType() {
        return MovieOrganizerType.GENRE;
    }
}
