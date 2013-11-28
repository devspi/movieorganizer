package spi.movieorganizer.data.collection;

import java.util.Date;
import java.util.Locale;

import spi.movieorganizer.data.MovieOrganizerType;
import spi.movieorganizer.data.util.StringMultiLang;
import exane.osgi.jexlib.data.object.AbstractDataObject;
import exane.osgi.jexlib.data.object.ExaneDataType;

public class CollectionPartDO extends AbstractDataObject<Integer> {

    private final String          backdropPath;
    private final String          posterPath;
    private final Date            releaseDate;
    private final StringMultiLang title;

    public CollectionPartDO(final Integer key, final String backdropPath, final String posterPath, final Date releaseDate, final StringMultiLang title) {
        super(key);
        this.backdropPath = backdropPath;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.title = title;
    }

    public String getBackdropPath() {
        return this.backdropPath;
    }

    public String getPosterPath() {
        return this.posterPath;
    }

    public Date getReleaseDate() {
        return this.releaseDate;
    }

    public StringMultiLang getTitleMultiLang() {
        return this.title;
    }

    public String getTitle(final Locale locale) {
        return this.title.getValue(locale);

    }

    @Override
    public ExaneDataType getDataType() {
        return MovieOrganizerType.COLLECTION_PART;
    }

}
